package com.cardcoupon.passbook.log;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;

@Slf4j
public class LogGenerator {
    public static void genLog(HttpServletRequest request, Long userId, String action, Object info){
        LogObject logObject = new LogObject(action, userId, System.currentTimeMillis(), request.getRemoteAddr(), info);
        log.info(JSON.toJSONString(logObject));
    }
}
