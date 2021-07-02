package com.cardcoupon.passbook.service;

import com.cardcoupon.passbook.vo.GainPassTemplateRequest;
import com.cardcoupon.passbook.vo.Response;

/**
 * The interface for user to gain a pass template
 */
public interface IGainPassTemplateService {
    /**
     * User gain a pass template by sending GainPassTemplateRequest
     * @param request {@link GainPassTemplateRequest}
     * @return Response {@link Response}
     */
    Response gainPassTemplate(GainPassTemplateRequest request) throws Exception;
}
