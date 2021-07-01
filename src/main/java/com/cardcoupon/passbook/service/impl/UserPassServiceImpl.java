package com.cardcoupon.passbook.service.impl;

import com.alibaba.fastjson.JSON;
import com.cardcoupon.passbook.constant.Constants;
import com.cardcoupon.passbook.constant.PassStatus;
import com.cardcoupon.passbook.dao.MerchantsDao;
import com.cardcoupon.passbook.entity.Merchants;
import com.cardcoupon.passbook.mapper.PassRowMapper;
import com.cardcoupon.passbook.service.IUserPassService;
import com.cardcoupon.passbook.vo.Pass;
import com.cardcoupon.passbook.vo.PassInfo;
import com.cardcoupon.passbook.vo.PassTemplate;
import com.cardcoupon.passbook.vo.Response;
import com.spring4all.spring.boot.starter.hbase.api.HbaseTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserPassServiceImpl implements IUserPassService {
    /** HBase client side */
    private final HbaseTemplate hbaseTemplate;
    /** MySQL dao for merchants table */
    private final MerchantsDao merchantsDao;

    public UserPassServiceImpl(HbaseTemplate hbaseTemplate, MerchantsDao merchantsDao) {
        this.hbaseTemplate = hbaseTemplate;
        this.merchantsDao = merchantsDao;
    }

    @Override
    public Response getUserPassInfo(Long userId) throws Exception {
        return getPassInfoByStatus(userId,PassStatus.UNUSED);
    }

    @Override
    public Response getUserUsedPassInfo(Long userId) throws Exception {
        return getPassInfoByStatus(userId,PassStatus.USED);
    }

    @Override
    public Response getUserAllPassInfo(Long userId) throws Exception {
        return getPassInfoByStatus(userId,PassStatus.ALL);
    }

    @Override
    public Response userUsePass(Pass pass) {
        byte[] FAMILY_I = Constants.PassTable.FAMILY_I.getBytes();
        byte[] TEMPLATE_ID = Constants.PassTable.TEMPLATE_ID.getBytes();
        byte[] CON_DATE = Constants.PassTable.CON_DATE.getBytes();

        byte[] rowPrefix = Bytes.toBytes(
                new StringBuilder(String.valueOf(pass.getUserId()))
                .reverse().toString()
        );

        Scan scan = new Scan();
        List<Filter> filters = new ArrayList<>();
        // filter 1: Prefix Filter
        filters.add(new PrefixFilter(rowPrefix));
        // filter 2: Single Column Value Filter,
        // the TEMPLATE_ID column should match the pass_template_id of the pass.
        filters.add(new SingleColumnValueFilter(
                FAMILY_I,
                TEMPLATE_ID,
                CompareFilter.CompareOp.EQUAL,
                Bytes.toBytes(pass.getTemplateId())
        ));
        // filter 3: Single Column Value Filter,
        // the CON_DATE should be '-1', AKA hasn't been consumed yet.
        filters.add(new SingleColumnValueFilter(
                FAMILY_I,
                CON_DATE,
                CompareFilter.CompareOp.EQUAL,
                Bytes.toBytes("-1")
        ));

        scan.setFilter(new FilterList(filters));
        List<Pass> passes = hbaseTemplate.find(
                Constants.PassTable.TABLE_NAME,scan, new PassRowMapper());

        if(passes == null || passes.size() != 1){
            log.error("User Use Pass Error:{}", JSON.toJSONString(pass));
            return Response.failure("User Use Pass Error");
        }

        // find the only pass we are searching for, the List is actually one object
        List<Mutation> mutations = new ArrayList<>();

        Put put = new Put(Bytes.toBytes(passes.get(0).getRowKey()));
        String today = DateFormatUtils.ISO_DATE_FORMAT.format(new Date());
        put.addColumn(FAMILY_I,CON_DATE, Bytes.toBytes(today));
        mutations.add(put);

        hbaseTemplate.saveOrUpdates(Constants.PassTable.TABLE_NAME,mutations);

        return Response.success();
    }

    /**
     * filter passes by statue(used, unused, all)
     * @param userId the User ID
     * @param status {@link PassStatus} to be filtered
     * @return Response {@link Response}
     * @throws Exception
     */
    private Response getPassInfoByStatus(Long userId, PassStatus status) throws Exception{
        byte[] rowPrefix = Bytes.toBytes(new StringBuilder(String.valueOf(userId)).reverse().toString());

        Scan scan = new Scan();
        List<Filter> filters = new ArrayList<>();

        byte[] FAMILY_I = Constants.PassTable.FAMILY_I.getBytes();
        byte[] CON_DATE = Constants.PassTable.CON_DATE.getBytes();
        CompareFilter.CompareOp compareOp = (status == PassStatus.UNUSED) ?
                        CompareFilter.CompareOp.EQUAL:CompareFilter.CompareOp.NOT_EQUAL;

        /** ADD FILTERS*/
        /** Filter 1: if what we are searching ALL
         * find passes of a certain user, adopting PrefixFilter*/
        filters.add(new PrefixFilter(rowPrefix));

        /** Filter 2: if what we are searching is not ALL
         * find all passes that haven't been used
         * single column filter by CON_DATE
         * if <code>CON_DATE == "-1"</code>, the pass hasn't been consumed
         * if <code>CON_DATE != "-1"</code>, the pass has been consumed
         * if <code>status == PassStatus.UNUSED</code>, unused, <code>CON_DATE == "-1"</code>
         * if <code>status == PassStatus.USED</code>, used, <code>CON_DATE != "-1"</code>
         *
         * <p> see also a simplified logic:
         *     <code>
         *         if(status != PassStatus.ALL){
         *             if(status == PassStatus.UNUSED){
         *                 filters.add(new SingleColumnValueFilter(
         *                         FAMILY_I,CON_DATE,CompareFilter.CompareOp.EQUAL,Bytes.toBytes("-1")
         *                 ));
         *             }
         *             else{
         *                 filters.add(new SingleColumnValueFilter(
         *                         FAMILY_I,CON_DATE,CompareFilter.CompareOp.NOT_EQUAL,Bytes.toBytes("-1")
         *                 ));
         *             }
         *         }
         *     </code>
         * </p>
         * */
        if(status != PassStatus.ALL){
            filters.add(new SingleColumnValueFilter(
                    FAMILY_I,CON_DATE,compareOp,Bytes.toBytes("-1")
            ));
        }

        /** find in hbase by filter, FilterList {@link FilterList} is a subclass of Filter */
        scan.setFilter(new FilterList(filters));
        List<Pass> passes = hbaseTemplate.find(
                Constants.PassTable.TABLE_NAME,scan, new PassRowMapper());
        /** build PassTemplate Map */
        Map<String, PassTemplate> passTemplateMap = buildPassTemplateMap(passes);

        /** get passTemplates lists */
        List<PassTemplate> passTemplates = new ArrayList<>(passTemplateMap.values());
        /** build PassTemplate Map */
        Map<Integer,Merchants> merchantsMap = buildMerchantsMap(passTemplates);

        List<PassInfo> passInfos = new ArrayList<>();

        for(Pass pass : passes){
            /** find the PassTemplate of this pass */
            PassTemplate passTemplate = passTemplateMap.getOrDefault(
                    pass.getTemplateId(),null);
            if( passTemplate == null ){
                log.error("PassTemplate of Pass {} is Null , TemplateId : {}", JSON.toJSONString(pass), pass.getTemplateId());
                continue;
            }

            /** find the Merchants of this PassTemplate */
            Merchants merchants = merchantsMap.getOrDefault(passTemplate.getId() , null);
            if( merchants == null){
                log.error("the Merchant of passTemplate {} is Null : MerchantID {}", JSON.toJSONString(passTemplate) ,passTemplate.getId() );
                continue;
            }

            passInfos.add(new PassInfo(pass, passTemplate, merchants ));
        }

        return new Response(passInfos);
    }

    /**
     * build a map by passes a user has obtained
     * @param passes a list of Passes {@link Pass}
     * @return Map {@link PassTemplate}
     * @throws Exception
     */
    private Map<String, PassTemplate> buildPassTemplateMap(List<Pass> passes) throws Exception{
        String[] datePattern = new String[]{"yyyy-MM-dd"};
        /** FAMILY_B */
        byte[] FAMILY_B = Constants.PassTemplateTable.FAMILY_B.getBytes();
        byte[] ID = Constants.PassTemplateTable.ID.getBytes();
        byte[] TITLE = Constants.PassTemplateTable.TITLE.getBytes();
        byte[] SUMMARY = Constants.PassTemplateTable.SUMMARY.getBytes();
        byte[] DESC = Constants.PassTemplateTable.DESC.getBytes();
        byte[] HAS_TOKEN = Constants.PassTemplateTable.HAS_TOKEN.getBytes();
        byte[] BACKGROUND = Constants.PassTemplateTable.BACKGROUND.getBytes();
        /** FAMILY_C */
        byte[] FAMILY_C = Constants.PassTemplateTable.FAMILY_C.getBytes();
        byte[] LIMIT = Constants.PassTemplateTable.LIMIT.getBytes();
        byte[] START = Constants.PassTemplateTable.START.getBytes();
        byte[] END = Constants.PassTemplateTable.END.getBytes();

        List<String> templateIds = passes.stream().map(Pass::getTemplateId).collect(Collectors.toList());
        List<Get> templateGets = new ArrayList<Get>(templateIds.size());
        templateIds.forEach(t -> templateGets.add(new Get(Bytes.toBytes(t))));

        Result[] templateResults = hbaseTemplate.getConnection()
                .getTable(TableName.valueOf(Constants.PassTable.TABLE_NAME))
                .get(templateGets);

        /** construct PassTemplateId -> PassTemplate Object, for passInfo */
        Map<String,PassTemplate> templateId2Object = new HashMap<>();
        for (Result item : templateResults){
            // create PassTemplate according to each Result
            PassTemplate passTemplate = new PassTemplate();

            passTemplate.setId(Bytes.toInt(item.getValue(FAMILY_B,ID)));
            passTemplate.setTitle(Bytes.toString(item.getValue(FAMILY_B,TITLE)));
            passTemplate.setSummary(Bytes.toString(item.getValue(FAMILY_B,SUMMARY)));
            passTemplate.setDesc(Bytes.toString(item.getValue(FAMILY_B,DESC)));
            passTemplate.setHasToken(Bytes.toBoolean(item.getValue(FAMILY_B,HAS_TOKEN)));
            passTemplate.setBackground(Bytes.toInt(item.getValue(FAMILY_B,BACKGROUND)));

            passTemplate.setLimit(Bytes.toLong(item.getValue(FAMILY_C,LIMIT)));
            passTemplate.setStart(
                    DateUtils.parseDate(
                            Bytes.toString(item.getValue(FAMILY_C,START)),datePattern
                    )
            );
            passTemplate.setEnd(
                    DateUtils.parseDate(
                            Bytes.toString(item.getValue(FAMILY_C,END)),datePattern
                    )
            );

            // put to map
            templateId2Object.put(
                    Bytes.toString(item.getRow()) , passTemplate
            );
        }

        return templateId2Object;
    }

    /**
     * construct passTemplatess -> Merchants,
     * @param passTemplates {@link PassTemplate}
     * @return Map {@link Merchants}
     */
    private Map<Integer, Merchants> buildMerchantsMap(List<PassTemplate> passTemplates){
        Map<Integer, Merchants> merchantsMap = new HashMap<>();

        List<Integer> merchantsIds = passTemplates.stream().map(PassTemplate::getId).collect(Collectors.toList());
        List<Merchants> merchants = merchantsDao.findByIdIn(merchantsIds);

        merchants.forEach(m -> merchantsMap.put(m.getId(),m));

        return merchantsMap;

    }
}
