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
    private Long userId;
    private String rowKey;
    private String templateId;
    private String token;
    private Date assignedDate;
    private Date conDate;
}
