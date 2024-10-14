package com.my.gateway.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Author: 温陆城
 * @Date: 2024-10-14 14:40:31
 */
@ConfigurationProperties(prefix = "my-encrypt")
@Component
@Data
@RefreshScope
public class EncryptProperties {


    /**
     * AES秘钥
     */
    private String aesKey;

    /**
     * 公钥
     */
    private String rsaPubKey;

    /**
     * 私钥
     */
    private String rsaPriKey;

}
