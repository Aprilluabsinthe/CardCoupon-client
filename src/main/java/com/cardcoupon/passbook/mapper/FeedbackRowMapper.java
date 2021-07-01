package com.cardcoupon.passbook.mapper;

import com.cardcoupon.passbook.constant.Constants;
import com.cardcoupon.passbook.vo.Feedback;
import com.spring4all.spring.boot.starter.hbase.api.RowMapper;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

/**
 * Feedback Hbase Row To Object
 */
public class FeedbackRowMapper implements RowMapper<Feedback> {
    private static byte[] FAMILY_I = Constants.Feedback.FAMILY_I.getBytes();
    private static byte[] USER_ID = Constants.Feedback.USER_ID.getBytes();
    private static byte[] TYPE = Constants.Feedback.TYPE.getBytes();
    private static byte[] TEMPLATE_ID = Constants.Feedback.TEMPLATE_ID.getBytes();
    private static byte[] COMMENT = Constants.Feedback.COMMENT.getBytes();

    /**
     * override Map Row
     * @param result {@link Result}
     * @param rowNum Row Number in Hbase
     * @return Feedback object {@link Feedback}
     * @throws Exception
     */
    @Override
    public Feedback mapRow(Result result, int rowNum ) throws Exception {
        Feedback feedback = new Feedback();

        feedback.setUserId(Bytes.toLong(result.getValue(FAMILY_I,USER_ID)));
        feedback.setType(Bytes.toString(result.getValue(FAMILY_I,TYPE)));
        feedback.setTemplateId(Bytes.toString(result.getValue(FAMILY_I,TEMPLATE_ID)));
        feedback.setComment(Bytes.toString(result.getValue(FAMILY_I,COMMENT)));

        return feedback;
    }
}
