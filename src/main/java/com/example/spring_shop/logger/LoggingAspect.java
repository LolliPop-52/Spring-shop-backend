package com.example.spring_shop.logger;

import com.example.spring_shop.dto.CreatorNewOrderDTO;
import com.example.spring_shop.dto.OrderDTO;
import com.example.spring_shop.dto.ProductDTO;
import com.example.spring_shop.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LoggingAspect {

    @Pointcut("@annotation(org.springframework.web.bind.annotation.ExceptionHandler)")
    public void exceptionHandlerMethods() {}

    @Around("exceptionHandlerMethods()")
    public Object logExceptionHandlerAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();

        Object[] args = joinPoint.getArgs();
        Throwable caughtException = (args.length > 0 && args[0] instanceof Throwable t) ? t : null;
        String exName = caughtException != null ? caughtException.getClass().getSimpleName() : "UnknownException";
        String exMessage = caughtException != null ? caughtException.getMessage() : "";

        Object result = joinPoint.proceed();

        if (result instanceof ResponseEntity<?> responseEntity) {
            if (responseEntity.getStatusCode().is5xxServerError()) {
                log.error("!<== [EX-HANDLER {}] Метод '{}' перехватил {}: {} | Ответ клиенту: {}",
                        responseEntity.getStatusCode().value(),
                        methodName,
                        exName,
                        exMessage,
                        responseEntity.getBody(),
                        caughtException);
            } else {
                log.warn("<== [EX-HANDLER {}] Метод '{}' перехватил {}: {} | Ответ клиенту: {}",
                        responseEntity.getStatusCode().value(),
                        methodName,
                        exName,
                        exMessage,
                        responseEntity.getBody());
            }
        }
        return result;
    }

    @AfterReturning("execution(public * com.example.spring_shop.service.impl.UserServiceImpl.signUp(..))")
    public void logUserSignUp(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] instanceof UserDTO dto) {
            log.info("==> [UserService.signUp() 201] Пользователь {Имя = '{}', Почта = '{}'} успешно зарегистрирован",
                    dto.getName(),
                    dto.getEmail()
            );
        }
    }

    @AfterReturning("execution(public * com.example.spring_shop.service.impl.UserServiceImpl.signIn(..))")
    public void logUserSignIn(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] instanceof UserDTO dto) {
            log.info("==> [UserService.signIn() 200] Пользователь {Почта = '{}'} успешно вошел в систему",
                    dto.getEmail()
            );
        }
    }

    @AfterReturning("execution(public * com.example.spring_shop.service.impl.ProductServiceImpl.addProduct(..))")
    public void logAddProduct(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] instanceof ProductDTO dto) {
            log.info("==> [ProductService.addProduct() 201] Добавлен новый товар {Название = '{}', Цена = '{}'}",
                    dto.getTitle(),
                    dto.getPrice()
            );
        }
    }

    @AfterReturning(
            pointcut = "execution(public * com.example.spring_shop.service.impl.OrderServiceImpl.createOrder(..))",
            returning = "result"
    )
    public void logCreateOrder(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] instanceof CreatorNewOrderDTO req && result instanceof OrderDTO res) {
            log.info("==> [OrderService.createOrder() 200] Создан заказ {OrderId = '{}', Почта = '{}', Сумма = '{}'}",
                    res.getId(),
                    req.getUserEmail(),
                    res.getTotalSum()
            );
        }
    }

    @AfterReturning("execution(public * com.example.spring_shop.service.impl.UserServiceImpl.deleteUserById(..))")
    public void logDeleteUserById(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] instanceof Long userId) {
            log.info("==> [UserService.deleteUserById() 200] Пользователь {ID = '{}'} успешно удален",
                    userId
            );
        }
    }
}