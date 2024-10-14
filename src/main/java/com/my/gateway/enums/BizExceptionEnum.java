package com.my.gateway.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description:
 * @Author: 温陆城
 * @Date: 2024-10-14 14:31:40
 */
@AllArgsConstructor
@Getter
public enum BizExceptionEnum {

    /**
     *
     */
    SUCCESS(200, "success"),
    /**
     * 用户信息异常提示
     */
    SYS_ERROR(2001000, " system error ");

    private Integer code;
    private String message;

}
