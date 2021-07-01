package com.cardcoupon.passbook.advice;

import com.cardcoupon.passbook.vo.ErrorInfo;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Global Error Handling
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    /**
     * a method return value should be bound to the web response body.
     * handling exceptions in specific handler classes and/or handler methods.
     * This also serves as a mapping hint
     * if the annotation itself does not narrow the exception types through its value().
     * @param request {@link HttpServletRequest}
     * @param e {@link Exception}
     * @return {@link ErrorInfo}
     * @throws Exception {@link Exception}
     */
    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public ErrorInfo<String> errorHandler(HttpServletRequest request, Exception e) throws Exception {
        ErrorInfo<String> info = new ErrorInfo<String>();
        info.setCode(ErrorInfo.ERROR);
        info.setMessage(e.getMessage());
        info.setData("Having No Return Data.");
        info.setUrl(request.getRequestURL().toString());
        return info;
    }
}
