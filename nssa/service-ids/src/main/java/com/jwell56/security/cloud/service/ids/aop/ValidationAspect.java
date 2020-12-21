package com.jwell56.security.cloud.service.ids.aop;

import com.alibaba.fastjson.JSONObject;
import com.jwell56.security.cloud.common.ResultObject;
import com.jwell56.security.cloud.service.ids.annotation.Validation;
import com.jwell56.security.cloud.service.ids.common.ReflectUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author wsg
 * @since 2020/11/15
 */

@Aspect
@Component
public class ValidationAspect {

    @Around("@annotation(org.springframework.web.bind.annotation.GetMapping)")
    public Object doAroundMethod(ProceedingJoinPoint joinPoint) {
        return validationCheck(joinPoint);
    }

    private Object validationCheck(ProceedingJoinPoint joinPoint) {
        for (Object arg : joinPoint.getArgs()) {
            ResultObject validation = validationCheck(arg);
            if (!validation.getSuccess()) return validation;
        }
        try {
            return joinPoint.proceed(joinPoint.getArgs());
        } catch (Exception e) {
            return ResultObject.exception(e);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return ResultObject.badRequest(throwable.getMessage());
        }
    }

    /**
     * 基于注解的输入验证
     * 1.判断是否为空
     * 2.判断字符长度
     * 3.基于正则表达式验证
     * 4.判断限定值
     */
    private ResultObject validationCheck(Object input) {
        for (Field field : ReflectUtil.getFieldsWithSupper(input.getClass())) {
            Validation annotation = field.getAnnotation(Validation.class);
            if (annotation != null) {
                Object object = null;
                try {
                    field.setAccessible(true);
                    object = field.get(input);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //非空验证
                if (!annotation.allowEmpty() &&
                        (object == null || (object instanceof String && ((String) object).isEmpty()))) {
                    return ResultObject.badRequest(field.getName() + "字段不能为空");
                }

                //正则验证
                if (!annotation.regex().isEmpty() && object instanceof String) {
                    if (!Pattern.matches(annotation.regex(), (String) object)) {
                        return ResultObject.badRequest(annotation.regexMessage());
                    }
                }

                //长度验证
                if (annotation.length() > 0 && object instanceof String) {
                    if (((String) object).length() < annotation.length()) {
                        return ResultObject.badRequest(field.getName() + "字段长度不得低于" + annotation.length());
                    }
                }

                //定值验证
                if (!annotation.enumValue().isEmpty() && object instanceof String) {
                    List<String> split = Arrays.asList(annotation.enumValue().split(","));
                    if (!split.contains(object)) {
                        return ResultObject.badRequest(field.getName() + "字段值仅支持：" + annotation.enumValue());
                    }
                }
            }
        }
        return ResultObject.message("验证通过");
    }
}
