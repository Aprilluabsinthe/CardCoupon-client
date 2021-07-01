package com.cardcoupon.passbook.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PassTemplate {
    /** Merchants' Id */
    private Integer id;
    /** title of coupon */
    private String title;
    /** brief summary of coupon */
    private String summary;
    /** detailed description of coupon */
    private String desc;
    /** limit counting of coupon */
    private Long limit;
    /** coupon hasToken or not */
    private Boolean hasToken;
    /** coupon background color */
    private Integer background;
    /** coupon start date */
    private Date start;
    /** coupon end date */
    private Date end;
}
