package com.cardcoupon.passbook.service.impl;

import com.alibaba.fastjson.JSON;
import com.cardcoupon.passbook.constant.Constants;
import com.cardcoupon.passbook.mapper.PassTemplateRowMapper;
import com.cardcoupon.passbook.service.IGainPassTemplateService;
import com.cardcoupon.passbook.utils.RowKeyGenUtil;
import com.cardcoupon.passbook.vo.GainPassTemplateRequest;
import com.cardcoupon.passbook.vo.PassTemplate;
import com.cardcoupon.passbook.vo.Response;
import com.spring4all.spring.boot.starter.hbase.api.HbaseTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.hadoop.hbase.client.Mutation;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class GainPassTemplateServiceImpl implements IGainPassTemplateService {
    /** HBase client side */
    private final HbaseTemplate hbaseTemplate;
    /** redis client side */
    private final StringRedisTemplate redisTemplate;

    @Autowired
    public GainPassTemplateServiceImpl(HbaseTemplate hbaseTemplate,
                                       StringRedisTemplate redisTemplate) {
        this.hbaseTemplate = hbaseTemplate;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Response gainPassTemplate(GainPassTemplateRequest request) throws Exception {
        PassTemplate passTemplate;
        String passTemplateId = RowKeyGenUtil.genPassTemplateRowKey(
                request.getPassTemplate()
        );

        try{
            passTemplate = hbaseTemplate.get(
                    Constants.PassTemplateTable.TABLE_NAME,
                    passTemplateId,
                    new PassTemplateRowMapper()
            );
        }catch(Exception e){
            log.error("Gain PassTemplate Error:[}", JSON.toJSONString(request.getPassTemplate()));
            return Response.failure("Gain PassTemplate Error");
        }

        // error 1: limit exceeds
        if(passTemplate.getLimit() <= 1 && passTemplate.getLimit()!=-1){
            log.error("PassTemplate LIMIT reaching Max :[}",
                    JSON.toJSONString(request.getPassTemplate()));
            return Response.failure("PassTemplate LIMIT reaching Max.");
        }

        // error 2: time expires
        Date cur = new Date();
        if(cur.getTime() < passTemplate.getStart().getTime()
                && cur.getTime() >= passTemplate.getEnd().getTime()){
            log.error("PassTemplate Valid Time Error or has expired :[}",
                    JSON.toJSONString(request.getPassTemplate()));
            return Response.failure("assTemplate Valid Time Error or has expired.");
        }

        if(passTemplate.getLimit() != -1){
            List<Mutation> mutations = new ArrayList<>();
            byte[] FAMILY_C = Constants.PassTemplateTable.FAMILY_C.getBytes();
            byte[] LIMIT = Constants.PassTemplateTable.LIMIT.getBytes();
            byte[] newLimit = Bytes.toBytes(passTemplate.getLimit()-1);
            Put put = new Put(Bytes.toBytes(passTemplateId));
            put.addColumn(FAMILY_C,LIMIT,newLimit);
            mutations.add(put);
            hbaseTemplate.saveOrUpdates(Constants.PassTemplateTable.TABLE_NAME,mutations);
        }

        // error 3: add pass to user failure
        if(!addPassForUser(request,passTemplate.getId(),passTemplateId)){
            return Response.failure("Add Pass For User Error");
        }

        return Response.success();
    }

    /**
     * Add pass to User helper function
     * @param request {@link GainPassTemplateRequest}
     * @param merchantsId Id of merchants
     * @param passTemplateId Id of passTemplate
     * @return true/false for success and failuer
     * @throws Exception from recordTokenToFile IOException
     */
    private boolean addPassForUser(GainPassTemplateRequest request,
                                   Integer merchantsId,
                                   String passTemplateId) throws Exception {
        byte[] FAMILY_I = Constants.PassTable.FAMILY_I.getBytes();
        byte[] USER_ID = Constants.PassTable.USER_ID.getBytes();
        byte[] TEMPLATE_ID = Constants.PassTable.TEMPLATE_ID.getBytes();
        byte[] TOKEN = Constants.PassTable.TOKEN.getBytes();
        byte[] ASSIGNED_DATE = Constants.PassTable.ASSIGNED_DATE.getBytes();
        byte[] CON_DATE = Constants.PassTable.CON_DATE.getBytes();

        List<Mutation> mutations = new ArrayList<>();
        Put put = new Put(Bytes.toBytes(
                RowKeyGenUtil.genPassRowKey(request)
        ));

        // USER_ID
        put.addColumn(FAMILY_I, USER_ID,
                Bytes.toBytes(request.getUserId()));
        // TEMPLATE_ID
        put.addColumn(FAMILY_I, TEMPLATE_ID,
                Bytes.toBytes(passTemplateId));

        // TOKEN
        if(request.getPassTemplate().getHasToken()){
            String token = redisTemplate.opsForSet().pop(passTemplateId);
            if(token == null){
                log.error("Token does not exist:{}", passTemplateId);
                return false;
            }
            recordTokenToFile(merchantsId,passTemplateId,token);
            put.addColumn(FAMILY_I,TOKEN,Bytes.toBytes(token));
        }
        else{
            put.addColumn(FAMILY_I,TOKEN,Bytes.toBytes("-1"));
        }

        // ASSIGNED_DATE
        put.addColumn(FAMILY_I,ASSIGNED_DATE,Bytes.toBytes(
                DateFormatUtils.ISO_DATE_FORMAT.format(new Date())
        ));
        // CON_DATE
        put.addColumn(FAMILY_I,CON_DATE,Bytes.toBytes("-1"));

        mutations.add(put);
        hbaseTemplate.saveOrUpdates(Constants.PassTable.TABLE_NAME, mutations);
        return true;
    }
    /**
     * Write USED token to files
     * @param merchantsId Id of the merchant
     * @param passTemplateId passTemplate
     * @param token distributed pass
     * @throws IOException file write
     */
    private void recordTokenToFile(Integer merchantsId,
                                   String passTemplateId, String token) throws IOException {

        // path => /TOKEN_DIR/merchantsId/passTemplateId_
        Path path = Paths.get(Constants.TOKEN_DIR,
                String.valueOf(merchantsId),
                passTemplateId+Constants.USED_TOKEN_SUFFIX);
        // byte => /token + "\n"
        byte[] tokenBytes = (token + "\n").getBytes();

        Files.write(path,tokenBytes,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }
}
