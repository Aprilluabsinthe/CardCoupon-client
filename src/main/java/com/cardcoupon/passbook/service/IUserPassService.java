package com.cardcoupon.passbook.service;

import com.cardcoupon.passbook.vo.Pass;
import com.cardcoupon.passbook.vo.Response;

/**
 * The core interface for manipulating user's pass information
 */
public interface IUserPassService {
    /**
     * get pass info that haven't been used
     * @param userId userID
     * @return {@link Response}
     * @throws Exception
     */
    Response getUserPassInfo(Long userId) throws Exception;

    /**
     * get the used pass template
     * @param userId userID
     * @return {@link Response}
     * @throws Exception
     */
    Response getUserUsedPassInfo(Long userId) throws Exception;

    /**
     * get all the pass template
     * should be the union of <code>getUserPassInfo</code> and <code>getUserUsedPassInfo</code>
     * @param userId userID
     * @return {@link Response}
     * @throws Exception
     */
    Response getUserAllPassInfo(Long userId) throws Exception;

    /**
     * <b>Action</b>
     * The user consume/use the pass
     * @param pass the pass to be consumed {@link Pass}
     * @return {@link Response}
     * @throws Exception
     */
    Response userUsePass(Pass pass);

}
