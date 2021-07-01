package com.cardcoupon.passbook.service.impl;

import com.alibaba.fastjson.JSON;
import com.cardcoupon.passbook.constant.Constants;
import com.cardcoupon.passbook.dao.MerchantsDao;
import com.cardcoupon.passbook.entity.Merchants;
import com.cardcoupon.passbook.mapper.PassTemplateRowMapper;
import com.cardcoupon.passbook.service.IInventoryService;
import com.cardcoupon.passbook.service.IUserPassService;
import com.cardcoupon.passbook.utils.RowKeyGenUtil;
import com.cardcoupon.passbook.vo.*;
import com.spring4all.spring.boot.starter.hbase.api.HbaseTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The implementation to get inventory pass
 * should only return those passes a user hasn't obtained
 */
@Slf4j
@Service
public class InventoryServiceImpl implements IInventoryService {

    /** Hbase client side */
    private final HbaseTemplate hbaseTemplate;
    /** MySQL Dao Interface */
    private final MerchantsDao merchantsDao;
    /** UserPassService Interface */
    private final IUserPassService userPassService;

    @Autowired
    public InventoryServiceImpl(HbaseTemplate hbaseTemplate,
                                MerchantsDao merchantsDao,
                                IUserPassService userPassService) {
        this.hbaseTemplate = hbaseTemplate;
        this.merchantsDao = merchantsDao;
        this.userPassService = userPassService;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Response getInventoryInfo(Long userId) throws Exception {
        Response allUserPass = userPassService.getUserAllPassInfo(userId);
        List<PassInfo> passInfos = (List<PassInfo>)allUserPass.getData();
        List<PassTemplate> excludeObject = passInfos.stream()
                .map(PassInfo::getPassTemplate).collect(Collectors.toList());
        List<String> excludeIds = new ArrayList<>();
        excludeObject.forEach(eo -> excludeIds.add(
                RowKeyGenUtil.genPassTemplateRowKey(eo)
        ));

        // build return value
        List<PassTemplate> availablePTs = getAvailablePassTemplate(excludeIds);
        List<PassTemplateInfo> availablePTInfos = buildPassTemplateInfo(availablePTs);
        InventoryResponse inventoryResponse = new InventoryResponse(userId,availablePTInfos);
        return new Response(inventoryResponse);
    }

    /**
     * Get all available PasTemplates in a system
     * @param excludeIds IDs of PassTemplates we would like to exclude
     * @return List<PassTemplate> {@link PassTemplate}
     */
    private List<PassTemplate> getAvailablePassTemplate(List<String> excludeIds){
        byte[] FAMILY_C = Constants.PassTemplateTable.FAMILY_C.getBytes();
        byte[] LIMIT = Constants.PassTemplateTable.LIMIT.getBytes();

        // FilterList, combining filters by OR
        FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ONE);

        // filter 1
        filterList.addFilter(
                new SingleColumnValueFilter(
                        FAMILY_C, LIMIT, CompareFilter.CompareOp.GREATER, Bytes.toBytes(0L)
                )
        );

        // OR filter 2
        // no limit
        filterList.addFilter(
                new SingleColumnValueFilter(
                        FAMILY_C, LIMIT, CompareFilter.CompareOp.EQUAL, Bytes.toBytes("-1")
                )
        );

        Scan scan = new Scan();
        scan.setFilter(filterList);

        List<PassTemplate> validTemplates = hbaseTemplate.find(
                Constants.PassTemplateTable.TABLE_NAME, scan ,new PassTemplateRowMapper()
        );

        // return value
        List<PassTemplate> availableTemplates = new ArrayList<>();
        Date current = new Date();

        for(PassTemplate vt : validTemplates){
            if(excludeIds.contains(RowKeyGenUtil.genPassTemplateRowKey(vt))){
                continue;
            }

            if(current.getTime() >= vt.getStart().getTime() && current.getTime() <= vt.getEnd().getTime()){
                availableTemplates.add(vt);
            }
        }
        return availableTemplates;

    }

    /**
     * Build a list of PassTemplateInfo from a list of PassTemplate
     * @param passTemplates {@link PassTemplate}
     * @return a List of PassTemplateInfo {@link PassTemplateInfo}
     */
    private List<PassTemplateInfo> buildPassTemplateInfo(List<PassTemplate> passTemplates){
        Map<Integer, Merchants> merchantsMap = new HashMap<>();
        List<Integer> merchantsIds = passTemplates.stream()
                .map(PassTemplate::getId).collect(Collectors.toList());
        List<Merchants> merchants = merchantsDao.findByIdIn(merchantsIds);
        merchants.forEach( m -> merchantsMap.put(m.getId(), m));

        List<PassTemplateInfo> passTemplateInfos = new ArrayList<>();
        for(PassTemplate pt : passTemplates){
            Merchants mct = merchantsMap.getOrDefault(pt.getId(),null);
            if(mct == null){
                log.error("Can not find Merchants for PassTemplate {} : MerchantsId {}",
                        JSON.toJSONString(pt),pt.getId() );
                continue;
            }
            passTemplateInfos.add(new PassTemplateInfo(pt,mct));
        }
        return passTemplateInfos;
    }
}
