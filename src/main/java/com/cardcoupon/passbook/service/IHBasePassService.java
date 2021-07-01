package com.cardcoupon.passbook.service;

import com.cardcoupon.passbook.vo.PassTemplate;

public interface IHBasePassService {
    /**
     * The function to write passTemplate into HBase passTemplate Table
     * @param passTemplate {@link PassTemplate}
     * @return true if succeed, false if not
     */
    boolean dropPassTemplateToHBase(PassTemplate passTemplate);
}
