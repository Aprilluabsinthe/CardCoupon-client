package com.cardcoupon.passbook.service;

import com.cardcoupon.passbook.vo.Feedback;
import com.cardcoupon.passbook.vo.Response;

/**
 * Client Feedback function
 */
public interface IFeedbackService {
    /**
     * Create a feed back interface
     * @param feedback {@link Feedback}
     * @return {@link Response}
     */
    Response createFeedback(Feedback feedback);

    /**
     * get the feedbacks from a certain user
     * @param userId User's ID, Long
     * @return {@link Response}
     */
    Response getFeedback(Long userId);
}
