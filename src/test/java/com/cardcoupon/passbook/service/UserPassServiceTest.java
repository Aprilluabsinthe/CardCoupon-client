package com.cardcoupon.passbook.service;


import com.cardcoupon.passbook.vo.Pass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserPassServiceTest extends AbstractServiceTest{

    @Autowired
    private IUserPassService userPassService;

    @Test
    public void testGetUserPassInfo() throws Exception {
        System.out.println(
                userPassService.getUserPassInfo(userId)
        );
    }

    @Test
    public void testGetUserAllPassInfo() throws Exception {
        System.out.println(
                userPassService.getUserAllPassInfo(userId)
        );
    }

    @Test
    public void testUserusePass() throws Exception {
        Pass pass = new Pass();
        pass.setUserId(userId);
        pass.setTemplateId("1L");

        System.out.println(
                userPassService.userUsePass(pass)
        );
    }
}
