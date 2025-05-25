package ru.yandex.practicum.filmorate.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Aspect
@Component
@Slf4j
public class ServiceLogAspect {

    @Around("@within(org.springframework.stereotype.Service)")
    public Object logServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String method = joinPoint.getSignature().toShortString();

        log.trace("Вызов: {}", method);

        try {
            Object result = joinPoint.proceed();

            if (result instanceof Optional<?> optional && optional.isEmpty()) {
                logOptional(joinPoint, method);
                return result;
            }

            log.trace("Успешно: {}", method);
            return result;

        } catch (RuntimeException ex) {
            log.error("Ошибка в методе {}: {}", method, ex.getMessage(), ex);
            throw ex;
        }
    }

    private void logOptional(ProceedingJoinPoint joinPoint, String method) {
        Object[] paramValues = joinPoint.getArgs();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = signature.getParameterNames();
        String joined = IntStream.range(0, paramNames.length)
                .mapToObj(i -> paramNames[i] + "=" + paramValues[i])
                .collect(Collectors.joining(", "));
        log.info("Метод {} вернул пустой Optional при аргументах {}", method, joined);
    }
}
