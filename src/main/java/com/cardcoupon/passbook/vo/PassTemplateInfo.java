package com.cardcoupon.passbook.vo;

import com.cardcoupon.passbook.entity.Merchants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * coupon passTemplate information for object communication
 */
@EqualsAndHashCode
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PassTemplateInfo extends PassTemplate{
    /**
     * pass template
     */
    private PassTemplate passTemplate;
    /**
     * merchants entity to this pass template
     */
    private Merchants merchants;
}
