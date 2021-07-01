package com.cardcoupon.passbook.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User Object
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    private BaseInfo baseInfo;
    private OtherInfo otherInfo;

    /**
     * Base Information inner class
     * HBase family-b
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BaseInfo {
        private String name;
        private Integer age;
        private String sex;
    }

    /**
     * Other Information inner class
     * HBase family-i
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OtherInfo {
        private String phone;
        private String address;
    }
}
