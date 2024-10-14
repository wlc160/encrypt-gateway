package com.my.gateway.exception;

import com.my.gateway.enums.BizExceptionEnum;
import lombok.*;

/**
 * @Description:
 * @Author: 温陆城
 * @Date: 2024-10-14 14:33:02
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BizException extends RuntimeException{

    private BizExceptionEnum businessExceptionEnum;
    private ExceptionInterface exceptionInterface;
    private String message;

    public BizException(BizExceptionEnum businessExceptionEnum, String message) {
        this.businessExceptionEnum = businessExceptionEnum;
        this.exceptionInterface = null;
        this.message = message;
    }

    public static void error(BizExceptionEnum exceptionEnum) {
        throw builder().message(exceptionEnum.getMessage()).businessExceptionEnum(exceptionEnum).build();
    }
}
