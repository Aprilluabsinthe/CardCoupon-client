package com.cardcoupon.passbook.service;

import com.cardcoupon.passbook.vo.Response;

/**
 * get informtion about inventory
 * should only show passes/passtemplates that a user haven't yet acquired.
 */
public interface IInventoryService {
    /**
     * get inventory for a certain user
     * @param userId the ID of the user
     * @return {@link Response}
     * @throws Exception
     */
    Response getInventory(Long userId) throws Exception;

}
