package com.cardcoupon.passbook.utils;

import com.cardcoupon.passbook.vo.Feedback;
import com.cardcoupon.passbook.vo.GainPassTemplateRequest;
import com.cardcoupon.passbook.vo.PassTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

@Slf4j
public class RowKeyGenUtil {
    /**
     * Generate RowKey according to PassTemplate
     * @param passTemplate {@link PassTemplate} the passTemplate to generate rowkey
     * @return encoded md5Hex String carrying passTemplate information
     */
    public static String genPassTemplateRowKey(PassTemplate passTemplate){
        String passInfo = String.valueOf(passTemplate.getId()) + '_' + passTemplate.getTitle();
        String rowKey = DigestUtils.md5Hex(passInfo);
        log.info("Gen PassTemplate RowKey : {}, {}", passInfo, rowKey);
        return rowKey;
    }

    /**
     * Generate RowKey according to Feedback
     *      <p>FeedBack is not a fixed content, we would like feedback from the same
     *      userID be stored closely, thus we can use UserID as RowKey</p>
     *
     *      <p>If the number Users are huge, the higher digits will be the same for a long time,
     *      we can reverse the UserID to make the number more randomly distributed</p>
     *
     *      <p>The time stamp is the time we store the Feedback. if B is stored after A,
     *      B's number suffix will become smaller than A.
     *      thus, The last Feedback will be shown at the top</p>
     * @param feedback {@link Feedback} the Feedback to generate rowkey
     * @return String Key containing UserID and timestamp
     */
    public static String genFeedbackRowKey(Feedback feedback){
        return new StringBuilder(String.valueOf(feedback.getUserId())).reverse().toString()
                + (Long.MAX_VALUE - System.currentTimeMillis());
    }

    /**
     * Generate RowKey according to the action of gaining a pass template
     *      <p>We would like the gainningrequest from the same
     *      userID be stored closely, thus we can use UserID as RowKey</p>
     *
     *      <p>If the number Users are huge, the higher digits will be the same for a long time,
     *      we can reverse the UserID to make the number more randomly distributed</p>
     *
     *      <p>The timestamp is the time we store the Feedback. if B is stored after A,
     *      B's number suffix will become smaller than A.
     *      thus, The last Feedback will be shown at the top</p>
     *
     *      <p>The PassTemplate gained at this time should also be stored</p>
     * @param request {@link GainPassTemplateRequest}
     * @return
     */
    public static String genPassRowKey(GainPassTemplateRequest request){
        return new StringBuilder(String.valueOf(request.getUserId())).reverse().toString()
                + (Long.MAX_VALUE - System.currentTimeMillis())
                + genPassTemplateRowKey(request.getPassTemplate());

    }




}
