package com.cardcoupon.passbook.constant;

/**
 * Enum type for PassTemplate object
 */
public enum PassStatus {
    UNUSED(1,"Unused coupon"),
    USED(2,"Used coupon"),
    ALL(3,"All coupon used");

    private Integer code;
    private String desc;

    PassStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() { return this.code; }

    public String getDesc() { return this.desc; }
}
