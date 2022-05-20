package com.example.ecom.aspects;

import com.example.ecom.services.ThrottleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class ThrottleByIpAspect {

    private final ThrottleService throttleService;

    @Before("execution(public * *(..)) && within(@com.example.ecom.aspects.annotations.ThrottleByIp *) && args(httpRequest, ..)")
    public void logExecutionTimeType(HttpServletRequest httpRequest) throws Throwable {
        String ip = readUserIp(httpRequest);
        log.info("Ip: {}", ip);
        throttleService.isAcceptable(ip);
    }

    private String readUserIp(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-REAL-IP");
        if (ipAddress == null || ipAddress.isBlank()) {
            ipAddress = request.getHeader("X-FORWARDED-FOR");
        }
        if (ipAddress == null || ipAddress.isBlank()) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }

}
