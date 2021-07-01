package com.cardcoupon.passbook.service.impl;

import com.alibaba.fastjson.JSON;
import com.cardcoupon.passbook.constant.Constants;
import com.cardcoupon.passbook.dao.MerchantsDao;
import com.cardcoupon.passbook.entity.Merchants;
import com.cardcoupon.passbook.mapper.PassTemplateRowMapper;
import com.cardcoupon.passbook.service.IInventoryService;
import com.cardcoupon.passbook.utils.RowKeyGenUtil;
import com.cardcoupon.passbook.vo.PassTemplate;
import com.cardcoupon.passbook.vo.PassTemplateInfo;
import com.cardcoupon.passbook.vo.Response;
import com.spring4all.spring.boot.starter.hbase.api.HbaseTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;
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

    private final HbaseTemplate hbaseTemplate;
    private final MerchantsDao merchantsDao;

    public InventoryServiceImpl(HbaseTemplate hbaseTemplate, MerchantsDao merchantsDao) {
        this.hbaseTemplate = hbaseTemplate;
        this.merchantsDao = merchantsDao;
    }

    @Override
    public Response getInventoryInfo(Long userId) throws Exception {
        return null;
    }

    /**
     * Get all available PasTemplates in a system
     * @param excludeIds IDs of PassTemplates we would like to exclude
     * @return List<PassTemplate> {@link PassTemplate}
     */
    private List<PassTemplate> getAvailablePassTemplate(List<String> excludeIds){
        byte[] FAMILY_C = Constants.PassTemplateTable.FAMILY_C.getBytes();
        byte[] LIMIT = Constants.PassTemplateTable.LIMIT.getBytes();
        byte[] START = Constants.PassTemplateTable.START.getBytes();
        byte[] END = Constants.PassTemplateTable.END.getBytes();

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

    private List<PassTemplateInfo> buildPassTemplateInfo(List<PassTemplate> passTemplates){
        Map<Integer, Merchants> merchantsMap = new HashMap<>();
        List<Integer> merchantsIds = passTemplates.stream().map(PassTemplate::getId).collect(Collectors.toList());
        List<Merchants> merchants = merchantsDao.findByIdIn(merchantsIds);
        merchants.forEach( m -> merchantsMap.put(m.getId(), m));

        List<PassTemplateInfo> passTemplateInfos = new ArrayList<>();

        for(PassTemplate pt : passTemplates){
            Merchants mct = merchantsMap.getOrDefault(pt.getId(),null);
            if(mct == null){
                log.error("Can not find Merchants for PassTemplate {} : MerchantsId {}", JSON.toJSONString(pt),pt.getId() );
                continue;
            }
            passTemplateInfos.add(new PassTemplateInfo(pt,mct));
        }
        return passTemplateInfos;
    }
}
