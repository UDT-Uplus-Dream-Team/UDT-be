package com.example.udtbe.global.log;

import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.global.dto.CursorPageResponse;
import com.example.udtbe.global.log.annotation.LogReturn;
import com.example.udtbe.global.token.cookie.CookieUtil;
import com.example.udtbe.global.token.service.TokenProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Slf4j
@Component
@RequiredArgsConstructor
public class ApiTraceAspect {

    private final ObjectMapper objectMapper;
    private final TokenProvider tokenProvider;
    private final CookieUtil cookieUtil;

    @Pointcut("execution(* com.example.udtbe..controller..*(..)) &&" +
            "!within(com.example.udtbe.domain.file..*)")
    public void controllerPointcut() {
    }

    @Pointcut("execution(* com.example.udtbe..service..*(..)) && " +
            "!within(com.example.udtbe.global.security.service..*) && " +
            "!within(com.example.udtbe.global.token.service..*) && " +
            "!within(com.example.udtbe.domain.file..*)")
    public void servicePointcut() {
    }

    @Pointcut("@annotation(com.example.udtbe.global.log.annotation.LogReturn)")
    public void annotationLogReturn() {
    }

    @Around("controllerPointcut() || servicePointcut()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        String fullClassName = joinPoint.getSignature().getDeclaringTypeName();
        String simpleClassName = fullClassName.substring(fullClassName.lastIndexOf('.') + 1);
        String methodName = getMethodName(joinPoint);
        log.info("[START] {}.{}()", simpleClassName, methodName);
        try {
            return joinPoint.proceed();
        } finally {
            long end = System.currentTimeMillis();
            long timeMs = end - start;
            log.info("[END] {}.{}() ===> {}ms", simpleClassName, methodName, timeMs);
        }
    }

    @Before("controllerPointcut()")
    public void logControllerRequest(JoinPoint joinPoint) {
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        String token = cookieUtil.getCookieValue(request);
        String memberId = tokenProvider.getMemberId(token);

        if (!StringUtils.hasText(memberId)) {
            return;
        }

        logRequestMetadata(memberId, request);
        logRequestArguments(joinPoint);
    }

    private void logRequestMetadata(String memberId, HttpServletRequest request) {
        String uri = request.getRequestURI();
        String method = request.getMethod();
        String clientIp = request.getRemoteAddr();

        log.info("[Request] {} {} from IP: {}, Agent: {}", method, uri, clientIp, memberId);
    }

    private void logRequestArguments(JoinPoint joinPoint) {
        String methodName = getMethodName(joinPoint);
        log.info("===== {} Request Detail START =====", methodName);
        Arrays.stream(joinPoint.getArgs())
                .filter(arg -> Objects.nonNull(arg) && !(arg instanceof Member))
                .forEach(arg -> {
                    try {
                        String json = objectMapper.writeValueAsString(arg);
                        log.info("arg: {}", json);
                    } catch (JsonProcessingException e) {
                        log.warn("JSON 직렬화 실패: {}", arg);
                    }
                });
        log.info("===== {} Request Detail END =====", methodName);
    }

    @AfterReturning(value = "servicePointcut() && annotationLogReturn()", returning = "returnValue")
    public void logResponseDetails(JoinPoint joinPoint, Object returnValue) {
        String methodName = getMethodName(joinPoint);
        log.info("===== {} Response Detail START =====", methodName);

        if (returnValue == null) {
            log.error("null 값 반환 오류 발생!!!");
            return;
        }

        if (isSummaryOnly(joinPoint) && isCursorPageResponseType(returnValue)) {
            logCursorPageSummary((CursorPageResponse<?>) returnValue);
            return;
        }

        try {
            String resultJson = objectMapper.writeValueAsString(returnValue);
            log.info("Response: {}", resultJson);
        } catch (JsonProcessingException e) {
            log.warn("응답 JSON 직렬화 실패: {}", returnValue.toString());
        }

        log.info("===== {} Response Detail END =====", methodName);

    }

    private void logCursorPageSummary(CursorPageResponse<?> response) {
        log.info("[CursorPageResponse] size = {}, cursor = {}, hasNext = {}",
                response.item().size(),
                response.nextCursor(),
                response.hasNext());
    }

    private boolean isCursorPageResponseType(Object returnValue) {
        return returnValue instanceof CursorPageResponse<?> response;
    }

    private String getMethodName(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getMethod().getName();
    }

    private boolean isSummaryOnly(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        LogReturn logReturn = signature.getMethod().getAnnotation(LogReturn.class);

        if (Objects.nonNull(logReturn) && logReturn.summaryOnly()) {
            return true;
        }

        return false;
    }
}
