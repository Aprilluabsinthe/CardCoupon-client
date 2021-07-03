package com.cardcoupon.passbook.controller;

import com.cardcoupon.passbook.constant.Constants;
import com.cardcoupon.passbook.log.LogConstants;
import com.cardcoupon.passbook.log.LogGenerator;
import com.cardcoupon.passbook.service.IFeedbackService;
import com.cardcoupon.passbook.service.IGainPassTemplateService;
import com.cardcoupon.passbook.service.IInventoryService;
import com.cardcoupon.passbook.service.IUserPassService;
import com.cardcoupon.passbook.vo.Feedback;
import com.cardcoupon.passbook.vo.GainPassTemplateRequest;
import com.cardcoupon.passbook.vo.Pass;
import com.cardcoupon.passbook.vo.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Controller for User/Inventory/Feedback services
 */
@Slf4j
@RestController
@RequestMapping("/passbook")
public class PassbookController {
    /** user pass service */
    private final IUserPassService userPassService;
    /** Inventory service */
    private final IInventoryService inventoryService;
    /** Gain Pass Template service */
    private final IGainPassTemplateService gainPassTemplateService;
    /** Feedback service */
    private final IFeedbackService feedbackService;
    /** HttpServletRequest */
    private final HttpServletRequest httpServletRequest;

    @Autowired
    public PassbookController(IUserPassService userPassService, IInventoryService inventoryService, IGainPassTemplateService gainPassTemplateService, IFeedbackService feedbackService, HttpServletRequest httpServletRequest) {
        this.userPassService = userPassService;
        this.inventoryService = inventoryService;
        this.gainPassTemplateService = gainPassTemplateService;
        this.feedbackService = feedbackService;
        this.httpServletRequest = httpServletRequest;
    }

    /**
     * Gain pass information of a certain user
     * @param userId the ID of the user
     * @return {@link Response}
     * @throws Exception
     */
    @ResponseBody
    @GetMapping("/userpassinfo")
    Response userPassInfo(Long userId) throws Exception {
        LogGenerator.genLog(
                httpServletRequest,
                userId,
                LogConstants.ActionName.USER_PASS_INFO,
                null);
        return userPassService.getUserPassInfo(userId);
    }

    /**
     * Gain used pass information of a certain user
     * @param userId the ID of the user
     * @return {@link Response}
     * @throws Exception
     */
    @ResponseBody
    @GetMapping("/userusedpassinfo")
    Response userUsedPassInfo(Long userId) throws Exception {
        LogGenerator.genLog(
                httpServletRequest,
                userId,
                LogConstants.ActionName.USER_USED_PASS_INFO,
                null);
        return userPassService.getUserUsedPassInfo(userId);
    }

    /**
     * Gain used pass information of a certain user
     * @param pass {@link Pass}
     * @return {@link Response}
     * @throws Exception
     */
    @ResponseBody
    @GetMapping("/userusepass")
    Response userUsePass(@RequestBody Pass pass) throws Exception {
        LogGenerator.genLog(
                httpServletRequest,
                pass.getUserId(),
                LogConstants.ActionName.USER_USE_PASS,
                pass);
        return userPassService.userUsePass(pass);
    }

    /**
     * Gain inventory information of a certain user
     * @param userId the ID of the user
     * @return {@link Response}
     * @throws Exception
     */
    @ResponseBody
    @GetMapping("/inventoryinfo")
    Response inventoryInfo(Long userId) throws Exception {
        LogGenerator.genLog(
                httpServletRequest,
                userId,
                LogConstants.ActionName.INVENTORY_INFO,
                null);
        return inventoryService.getInventoryInfo(userId);
    }

    /**
     * Endpoint for gainning a pass template
     * @param request
     * @return
     * @throws Exception
     */
    @ResponseBody
    @GetMapping("/gainpasstemplate")
    Response gainPassTemplate(@RequestBody GainPassTemplateRequest request) throws Exception {
        LogGenerator.genLog(
                httpServletRequest,
                request.getUserId(),
                LogConstants.ActionName.GAIN_PASS_TEMPLATE,
                request);
        return gainPassTemplateService.gainPassTemplate(request);
    }

    /**
     * Endpoint for creating a Feedback
     * @param feedback {@link Feedback}
     * @return {@link Response}
     */
    @ResponseBody
    @GetMapping("/createfeedback")
    Response createFeedback(@RequestBody Feedback feedback){
        LogGenerator.genLog(
                httpServletRequest,
                feedback.getUserId(),
                LogConstants.ActionName.CREATE_FEEDBACK,
                feedback);
        return feedbackService.createFeedback(feedback);
    }

    /**
     * Endpoint for getting all feedbacks from a user
     * @param userId the Id of the user
     * @return {@link Response}
     */
    @ResponseBody
    @GetMapping("/getfeedback")
    Response getFeedback(Long userId){
        LogGenerator.genLog(
                httpServletRequest,
                userId,
                LogConstants.ActionName.GET_FEEDBACK,
                null);
        return feedbackService.getFeedback(userId);
    }

    /**
     * ENdpoint for exception
     * @return {@link Response}
     * @throws Exception
     */
    @ResponseBody
    @GetMapping("/exception")
    Response exception() throws Exception{
        throw new Exception("Welcome to Client-side Coupon Passbook System");
    }
}
