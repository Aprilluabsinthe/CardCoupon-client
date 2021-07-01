package com.cardcoupon.passbook.vo;

import lombok.*;

import java.util.Date;

/**
 * The Passes the user has obtained
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pass {
    /** User ID */
    private Long userId;
    /** rowKey in HBase for pass */
    private String rowKey;
    /** rowKey in HBase for passTemplate */
    private String templateId;
    /** token can be null(-1) */
    private String token;
    /** the date the coupon is assigned */
    private Date assignedDate;
    /** the date the coupon is consumed */
    private Date conDate;
}
