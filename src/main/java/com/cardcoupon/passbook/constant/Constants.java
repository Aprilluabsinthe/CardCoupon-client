package com.cardcoupon.passbook.constant;

public class Constants {
    /** merchants Kafka topic */
    public static final String TEMPLATE_TOPIC = "merchants-template";
    /** token storage directory */
    public static final String TOKEN_DIR = "/tmp/token";
    /** the used token has a special suffic "_" */
    public static final String USED_TOKEN_SUFFIX = "_";
    /** redis key for user counting */
    public static final String USE_COUNT_REDIS_KEY = "user-count";

    /**
     * User HBase Table
     */
    public class UserTable{
        /** User Hbase table name */
        public static final String TABLE_NAME = "pb:user";

        /**
         * basic column family
         */
        public static final String FAMILY_B = "b";
        /** user name */
        public static final String NAME = "name";
        /** user age */
        public static final String AGE = "age";
        /** user sex */
        public static final String SEX = "sex";

        /**
         * Optional column family
         */
        public static final String FAMILY_O = "o";
        /** user phone */
        public static final String PHONE = "phone";
        /** user address */
        public static final String ADDRESS = "address";
    }

    /**
     * HBase Table for Pass Template
     */
    public class PassTemplateTable{
        /** PassTemplateTable Hbase table name */
        public static final String TABLE_NAME = "pb:passtemplate";
        /**
         * basic column family
         */
        public static final String FAMILY_B = "b";
        /** merchant's id */
        public static final String ID = "id";
        /** pass template's id */
        public static final String TITLE = "title";
        /** pass template's brief summary */
        public static final String SUMMARY = "summary";
        /** pass template's detailed description */
        public static final String DESC = "desc";
        /** pass template's has token or has_token */
        public static final String HAS_TOKEN = "has_token";
        /** pass template's background color */
        public static final String BACKGROUND = "background";

        /**
         * pass template's color family column
         */
        public static final String FAMILY_C = "c";
        /** pass template's limit */
        public static final String LIMIT = "limit";
        /** pass template's start time */
        public static final String START = "start";
        /** pass template's end time */
        public static final String END = "end";
    }

    /**
     * HBase Table for Passes
     */
    public class PassTable{
        /** Pass HBase Table name */
        public static final String TABLE_NAME = "pb:pass";

        /**
         * basic information column family
         */
        public static final String FAMILY_I = "i";
        /** User ID */
        public static final String USER_ID = "user_id";
        /** pass template ID */
        public static final String TEMPLATE_ID = "template_id";
        /** token for pass coupon */
        public static final String TOKEN = "token";
        /** the date the pass is assign */
        public static final String ASSIGNED_DATE = "assigned_data";
        /** the date the pass is consumed */
        public static final String CON_DATE = "con_date";
    }

    /**
     * HBase Table for FeedBack
     */
    public class Feedback{
        /** Feedback HBase Table */
        public static final String TABLE_NAME = "pb:feedback";

        /** Feedback HBase information column family */
        public static final String FAMILY_I = "i";
        /** User id */
        public static final String USER_ID = "user_id";
        /** type of the feed back(pass or app) */
        public static final String TYPE = "type";
        /** PassTemplate RowKey, if the feed back is for app, it should be -1 */
        public static final String TEMPLATE_ID = "template_id";
        /** comment content */
        public static final String COMMENT = "comment";
    }
}
