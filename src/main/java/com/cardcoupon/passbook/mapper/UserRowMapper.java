package com.cardcoupon.passbook.mapper;

import com.cardcoupon.passbook.constant.Constants;
import com.cardcoupon.passbook.vo.User;
import com.spring4all.spring.boot.starter.hbase.api.RowMapper;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

/**
 * User Hbase Row To Object
 */
public class UserRowMapper implements RowMapper<User> {
    private static byte[] FAMILY_B = Constants.UserTable.FAMILY_B.getBytes();
    private static byte[] NAME = Constants.UserTable.NAME.getBytes();
    private static byte[] AGE = Constants.UserTable.AGE.getBytes();
    private static byte[] SEX = Constants.UserTable.SEX.getBytes();

    private static byte[] FAMILY_O = Constants.UserTable.FAMILY_O.getBytes();
    private static byte[] ADDRESS = Constants.UserTable.ADDRESS.getBytes();
    private static byte[] PHONE = Constants.UserTable.PHONE.getBytes();

    /**
     * override Map Row
     * @param result {@link Result}
     * @param rowNum Row Number in Hbase
     * @return User object {@link User}
     * @throws Exception
     */
    @Override
    public User mapRow(Result result, int rowNum) throws Exception {
        User.BaseInfo baseInfo = new User.BaseInfo(
                Bytes.toString(result.getValue(FAMILY_B,NAME)),
                Bytes.toInt(result.getValue(FAMILY_B,AGE)),
                Bytes.toString(result.getValue(FAMILY_B,SEX))
        );
        User.OtherInfo otherInfo = new User.OtherInfo(
                Bytes.toString(result.getValue(FAMILY_O,PHONE)),
                Bytes.toString(result.getValue(FAMILY_O,ADDRESS))
        );
        return new User(
                Bytes.toLong(result.getRow()),baseInfo,otherInfo
        );
    }
}
