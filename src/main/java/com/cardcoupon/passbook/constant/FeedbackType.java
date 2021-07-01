package com.cardcoupon.passbook.constant;

/**
 * Enum type for Feedback object
 */
public enum FeedbackType {
    PASS("pass","Feedback on Passes"),
    APP("pass","Feedback on Apps");

    private String code;
    private String desc;

    FeedbackType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode(){return code;}

    public String getDesc(){return desc;}
}
