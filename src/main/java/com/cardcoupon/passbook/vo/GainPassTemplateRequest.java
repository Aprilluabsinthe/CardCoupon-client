package com.cardcoupon.passbook.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GainPassTemplateRequest {
    /** User's ID */
    private Long userId;
    /** PassTemplate's object */
    private PassTemplate passTemplate;
}
