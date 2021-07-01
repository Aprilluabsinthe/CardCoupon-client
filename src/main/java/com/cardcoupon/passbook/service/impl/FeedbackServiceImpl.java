package com.cardcoupon.passbook.service.impl;

import com.alibaba.fastjson.JSON;
import com.cardcoupon.passbook.constant.Constants;
import com.cardcoupon.passbook.mapper.FeedbackRowMapper;
import com.cardcoupon.passbook.service.IFeedbackService;
import com.cardcoupon.passbook.utils.RowKeyGenUtil;
import com.cardcoupon.passbook.vo.Feedback;
import com.cardcoupon.passbook.vo.Response;
import com.spring4all.spring.boot.starter.hbase.api.HbaseTemplate;
import com.spring4all.spring.boot.starter.hbase.api.RowMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation for Feedback Service
 */
@Slf4j
@Service
public class FeedbackServiceImpl implements IFeedbackService {
    /** Hbase client side*/
    private final HbaseTemplate hbaseTemplate;

    @Autowired
    public FeedbackServiceImpl(HbaseTemplate hbaseTemplate) {
        this.hbaseTemplate = hbaseTemplate;
    }

    @Override
    public Response createFeedback(Feedback feedback) {
        byte[] FAMILY_I = Constants.Feedback.FAMILY_I.getBytes();
        byte[] USER_ID = Constants.Feedback.USER_ID.getBytes();
        byte[] TYPE = Constants.Feedback.TYPE.getBytes();
        byte[] TEMPLATE_ID = Constants.Feedback.TEMPLATE_ID.getBytes();
        byte[] COMMENT = Constants.Feedback.COMMENT.getBytes();

        if(!feedback.validate()){
            log.error("Feedback invalid: {}", JSON.toJSONString(feedback));
            return Response.failure("Feedback Invalid");
        }

        Put put = new Put(Bytes.toBytes(RowKeyGenUtil.genFeedbackRowKey(feedback)));

        /** put and add column */
        put.addColumn(FAMILY_I,USER_ID,Bytes.toBytes(feedback.getUserId()));
        put.addColumn(FAMILY_I,TYPE,Bytes.toBytes(feedback.getType()));
        put.addColumn(FAMILY_I,TEMPLATE_ID,Bytes.toBytes(feedback.getTemplateId()));
        put.addColumn(FAMILY_I,COMMENT,Bytes.toBytes(feedback.getComment()));

        hbaseTemplate.saveOrUpdate(Constants.Feedback.TABLE_NAME,put);
        return Response.success();
    }

    @Override
    public Response getFeedback(Long userId) {
        String reversedId = new StringBuilder(String.valueOf(userId)).reverse().toString();
        byte[] reverseUserId = reversedId.getBytes();

        /** find feedbacks by prefix filter */
        Scan scan = new Scan();
        scan.setFilter(new PrefixFilter(reverseUserId));

        /** find feedbacks by prefix filter */
        List<Feedback> feedbacks = hbaseTemplate.find(
                Constants.Feedback.TABLE_NAME,
                scan,
                new FeedbackRowMapper()
        );
        return new Response(feedbacks);
    }
}
