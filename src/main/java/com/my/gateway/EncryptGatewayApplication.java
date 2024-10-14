package com.my.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @Description:
 * @Author: 温陆城
 * @Date: 2024-10-14 17:17:21
 */
@SpringBootApplication
@EnableFeignClients
public class EncryptGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(EncryptGatewayApplication.class, args);
    }

}
