package com.cardcoupon.passbook.service;

import com.alibaba.fastjson.JSON;
import com.cardcoupon.passbook.vo.GainPassTemplateRequest;
import com.cardcoupon.passbook.vo.PassTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GainPassTemplateServiceTest extends AbstractServiceTest{
    @Autowired
    private IGainPassTemplateService gainPassTemplateService;

    @Test
    public void tsetGainPassTemplate() throws Exception {
        PassTemplate target = new PassTemplate();
        target.setId(9);
        target.setTitle("Cheese Cake Factory");
        target.setHasToken(true);

        System.out.println(JSON.toJSONString(
                gainPassTemplateService.gainPassTemplate(
                        new GainPassTemplateRequest(userId, target)
                )
        ));

    }

}
