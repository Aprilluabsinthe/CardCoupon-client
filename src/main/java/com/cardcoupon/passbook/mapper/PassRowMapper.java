package com.cardcoupon.passbook.mapper;

import com.cardcoupon.passbook.constant.Constants;
import com.cardcoupon.passbook.vo.Pass;
import com.spring4all.spring.boot.starter.hbase.api.RowMapper;
import org.apache.commons.lang.time.DateUtils;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

/**
 * Mapping for PassRowMapper
 */
public class PassRowMapper implements RowMapper<Pass> {
    private static byte[] FAMILY_I = Constants.PassTable.FAMILY_I.getBytes();
    private static byte[] USER_ID = Constants.PassTable.USER_ID.getBytes();
    private static byte[] TEMPLATE_ID = Constants.PassTable.TEMPLATE_ID.getBytes();
    private static byte[] TOKEN = Constants.PassTable.TOKEN.getBytes();
    private static byte[] ASSIGNED_DATE = Constants.PassTable.ASSIGNED_DATE.getBytes();
    private static byte[] CON_DATE = Constants.PassTable.CON_DATE.getBytes();

    /**
     * mapRow for PassRowMapper
     * @param result org.apache.hadoop.hbase.client
     * @param rowNum int
     * @return Pass
     * @throws Exception
     */
    @Override
    public Pass mapRow(Result result, int rowNum) throws Exception {
        Pass pass = new Pass();
        pass.setUserId(Bytes.toLong(result.getValue(FAMILY_I,USER_ID)));
        pass.setTemplateId(Bytes.toString(result.getValue(FAMILY_I,TEMPLATE_ID)));
        pass.setToken(Bytes.toString(result.getValue(FAMILY_I,TOKEN)));

        String[] patterns = new String[]{"yyyy-MM-dd"};

        String assignedDateStr = Bytes.toString(result.getValue(FAMILY_I,ASSIGNED_DATE));
        pass.setAssignedDate(DateUtils.parseDate(assignedDateStr, patterns));

        String conDateStr = Bytes.toString(result.getValue(FAMILY_I,CON_DATE));
        if(conDateStr.equals("-1")){
            pass.setConDate(null);
        }else{
            pass.setConDate(DateUtils.parseDate(conDateStr,patterns));
        }

        pass.setRowKey(Bytes.toString(result.getRow()));

        return pass;
    }
}
