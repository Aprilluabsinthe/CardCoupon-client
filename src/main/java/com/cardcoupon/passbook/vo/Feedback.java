package com.cardcoupon.passbook.vo;

import com.cardcoupon.passbook.constant.FeedbackType;
import com.google.common.base.Enums;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Feedback {
    /** User's ID */
    private Long userId;
    /** Comment's type */
    private String type;
    /** PassTemplate RowKey, if the comment is for APP, the String can be null */
    private String templateId;
    /** comment content */
    private String comment;

    /** Comment's content */
    public boolean validate(){

        /** Feedback can only be FeedbackType(APP or PASS) */
        FeedbackType feedbackType = Enums.getIfPresent(
                FeedbackType.class, this.type.toUpperCase()
        ).orNull();

        /** Feedback can not be null */
        return !(null == feedbackType || null == comment);
    }
}
