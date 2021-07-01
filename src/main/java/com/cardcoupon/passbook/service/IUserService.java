package com.cardcoupon.passbook.service;

import com.cardcoupon.passbook.vo.Response;
import com.cardcoupon.passbook.vo.User;

/**
 * Service to create User Service
 */
public interface IUserService {
    Response createUser(User user) throws Exception;
}
