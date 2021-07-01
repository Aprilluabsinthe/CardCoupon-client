package com.cardcoupon.passbook.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Global Error Info Class
 * Generic class T
 * */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorInfo<T> {
    /** Error Code */
    public static final Integer ERROR = -1;
    /** the code number indicating ERROR type*/
    private Integer code;
    /** Error message */
    private String message;
    /** url of requesting */
    private String url;
    /** data that was requested */
    private T data;
}
