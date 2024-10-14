package com.my.gateway.exception;

/**
 * @Description:
 * @Author: 温陆城
 * @Date: 2024-10-14 14:34:19
 */
public interface ExceptionInterface {

    /**
     * 功能描述:
     *
     * @return {@link Integer }
     * @author 温陆城
     * @date 2024-10-14 14:34:15
     */
    Integer getErrorCode();

    /**
     * 功能描述:
     *
     * @return {@link String }
     * @author 温陆城
     * @date 2024-10-14 14:34:17
     */
    String getErrorMessage();

}
