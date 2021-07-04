package com.cardcoupon.passbook.service;

import com.alibaba.fastjson.JSON;
import com.cardcoupon.passbook.constant.FeedbackType;
import com.cardcoupon.passbook.vo.Feedback;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FeedbackServiceTest extends AbstractServiceTest{
    @Autowired
    private IFeedbackService feedbackService;

    @Test
    public void testCreateFeedback(){
        Feedback appFeedback = new Feedback();
        appFeedback.setUserId(userId);
        appFeedback.setType(FeedbackType.APP.getCode());
        appFeedback.setTemplateId("-1");
        appFeedback.setComment("A distributed sub system for client.");

        System.out.println(
                JSON.toJSONString(
                        feedbackService.createFeedback(appFeedback)
                )
        );

        Feedback passFeedback = new Feedback();
        passFeedback.setUserId(userId);
        passFeedback.setType(FeedbackType.PASS.getCode());
        passFeedback.setTemplateId("1L");
        passFeedback.setComment("A comment for PASS:1L.");

        System.out.println(
                JSON.toJSONString(
                        feedbackService.createFeedback(appFeedback)
                )
        );
    }

    @Test
    public void testGetFeedback(){
        System.out.println(
                JSON.toJSONString(
                        feedbackService.getFeedback(userId)
                )
        );
    }
}
