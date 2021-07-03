package com.cardcoupon.passbook.controller;

import com.cardcoupon.passbook.log.LogConstants;
import com.cardcoupon.passbook.log.LogGenerator;
import com.cardcoupon.passbook.service.IUserService;
import com.cardcoupon.passbook.vo.Response;
import com.cardcoupon.passbook.vo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Controller for creating a user
 */
@Slf4j
@RestController
@RequestMapping("/passbook")
public class CreateUserController {
    /** service to create a user */
    private final IUserService userService;
    /** HttpServletRequest */
    private final HttpServletRequest httpServletRequest;

    @Autowired
    public CreateUserController(IUserService userService,
                                HttpServletRequest httpServletRequest) {
        this.userService = userService;
        this.httpServletRequest = httpServletRequest;
    }

    /**
     * endpoint for creating a user
     * @param user {@link User}
     * @return {@link Response}
     * @throws Exception
     */
    @ResponseBody
    @PostMapping("/createuser")
    Response createUser(@RequestBody User user) throws Exception {
        LogGenerator.genLog(
                httpServletRequest,
                -1L,
                LogConstants.ActionName.CREATE_USER,
                user
        );
        return userService.createUser(user);
    }
}
