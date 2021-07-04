package com.cardcoupon.passbook.service;

import com.alibaba.fastjson.JSON;
import com.cardcoupon.passbook.vo.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * test for creating test
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {

    @Autowired
    private IUserService userService;

    @Test
    public void testCreaterUser() throws Exception {
        User user = new User();
        user.setBaseInfo(
                new User.BaseInfo("Cheese Cake Factory",10,"M")
        );
        user.setOtherInfo(
                new User.OtherInfo("412-010-1045","Pittsburgh, PA")
        );

        System.out.println(JSON.toJSONString(userService.createUser(user)));

    }
}
