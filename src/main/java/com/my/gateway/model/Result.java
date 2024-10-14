package com.my.gateway.model;

import com.my.gateway.enums.CodeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Description:
 * @Author: 温陆城
 * @Date: 2024-10-14 14:38:45
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Result implements Serializable {

    private Object datas;
    private Integer resp_code;
    private String resp_msg;


    public static Result of(Object datas, Integer code, String msg) {
        return new Result(datas, code, msg);
    }
    public static Result failed(String msg) {
        return new Result(null, CodeEnum.ERROR.getCode(), msg);
    }

}
