package com.cardcoupon.passbook.service.impl;

import com.cardcoupon.passbook.constant.Constants;
import com.cardcoupon.passbook.service.IUserService;
import com.cardcoupon.passbook.vo.Response;
import com.cardcoupon.passbook.vo.User;
import com.spring4all.spring.boot.starter.hbase.api.HbaseTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.hadoop.hbase.client.Mutation;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Implementation for create User Service
 * Interface {@link IUserService}
 */
@Slf4j
@Service
public class UserServiceImpl implements IUserService {
    /** HBase client side */
    private final HbaseTemplate hbaseTemplate;
    /** redis client side */
    private final StringRedisTemplate redisTemplate;

    /***
     * Constructor
     * @param hbaseTemplate {@link HbaseTemplate}
     * @param redisTemplate {@link StringRedisTemplate}
     */
    @Autowired
    public UserServiceImpl(HbaseTemplate hbaseTemplate, StringRedisTemplate redisTemplate) {
        this.hbaseTemplate = hbaseTemplate;
        this.redisTemplate = redisTemplate;
    }

    /**
     * UserId generator
     * @param prefix the current User Counts
     * @return Long UserID
     */
    private Long genUserId(Long prefix){
        /** random */
        String suffix = RandomStringUtils.randomNumeric(5);
        return Long.valueOf(prefix+suffix);
    }

    /**
     * The implementation for create User service
     * @param user the User Object {@link User} to add to HBase
     * @return {@link Response} Response carrying User info
     * @throws Exception {@link Exception}
     */
    @Override
    public Response createUser(User user) throws Exception {
        /** basic column family */
        byte[] FAMILY_B = Constants.UserTable.FAMILY_B.getBytes();
        byte[] NAME = Constants.UserTable.NAME.getBytes();
        byte[] AGE = Constants.UserTable.AGE.getBytes();
        byte[] SEX = Constants.UserTable.SEX.getBytes();
        /** Optional column family */
        byte[] FAMILY_O = Constants.UserTable.FAMILY_O.getBytes();
        byte[] PHONE = Constants.UserTable.PHONE.getBytes();
        byte[] ADDRESS = Constants.UserTable.ADDRESS.getBytes();

        /** generate user ID(rowKey) */
        Long curCount = redisTemplate.opsForValue().increment(Constants.USE_COUNT_REDIS_KEY,1);
        Long userId = genUserId(curCount);

        /** Mutation is subclass for PUT and DELETE */
        List<Mutation> mutationList = new ArrayList<Mutation>();
        Put put = new Put(Bytes.toBytes(userId));

        /** add to basic column family */
        put.addColumn(
                FAMILY_B, NAME, Bytes.toBytes(user.getBaseInfo().getName())
        );
        put.addColumn(
                FAMILY_B, AGE, Bytes.toBytes(user.getBaseInfo().getAge())
        );
        put.addColumn(
                FAMILY_B, SEX, Bytes.toBytes(user.getBaseInfo().getSex())
        );
        /** add to Optional column family */
        put.addColumn(
                FAMILY_O, PHONE, Bytes.toBytes(user.getOtherInfo().getPhone())
        );
        put.addColumn(
                FAMILY_O, ADDRESS, Bytes.toBytes(user.getOtherInfo().getAddress())
        );

        /** update HBase by lists */
        mutationList.add(put);
        hbaseTemplate.saveOrUpdates(Constants.UserTable.TABLE_NAME,mutationList);

        /** return Response of USER object */
        user.setId(userId);
        return new Response(user);
    }
}
