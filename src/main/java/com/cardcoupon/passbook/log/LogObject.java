package com.cardcoupon.passbook.log;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Log Object Definition
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogObject {
    /** User's action to write into log */
    private String action;
    /** User's ID to write into log */
    private Long userId;
    /** User's timestamp to write into log */
    private Long timestamp;
    /** User's remote host Ip */
    private String remoteIp;
    /** Log info*/
    private Object info = null;
}
