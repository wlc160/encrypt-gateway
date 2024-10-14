package com.my.gateway.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description:
 * @Author: 温陆城
 * @Date: 2024-10-14 14:32:17
 */
@AllArgsConstructor
@Getter
public enum CodeEnum {

    /**
     *
     */
    SUCCESS(200),
    /**
     *
     */
    ERROR(500);
    /**
     *
     */
    private Integer code;
}
