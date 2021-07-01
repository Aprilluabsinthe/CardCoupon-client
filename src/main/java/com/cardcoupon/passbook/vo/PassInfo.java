package com.cardcoupon.passbook.vo;

import com.cardcoupon.passbook.entity.Merchants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The pass the User has gained
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PassInfo {
    /** the pass itself */
    private Pass pass;

    /** the PassTemplate for this pass */
    private PassTemplate passTemplate;

    /** the Merchants for this pass */
    private Merchants merchants;
}
