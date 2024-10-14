### 网关加解密 请求响应参数 解析
### 
启动参数
***
````
-Xmx240m
-Xms240m
-Dspring.profiles.active=dev
-Dfile.encoding=utf-8
-Dnacos.server=http://127.0.0.1:8848/nacos
-Dnacos.username=
-Dnacos.password=
-Dnacos.discovery.server.namespace=public
-Dnacos.config.server.namespace=public 

````
encrypt-gateway.yaml 配置 

````
spring:
  cloud:
    gateway:
      discovery:
        locator:
          lowerCaseServiceId: true
          enabled: true
      routes:
        - id: common
          uri: lb://bp-common
          predicates:
            - Path=/my-common/**
          filters:
            - StripPrefix=1


#knife4j的网关聚合配置 文档地址：http://{gateway.host}:{gateway.port}/doc.html
knife4j:
  #  # 聚合swagger文档
  gateway:
    enabled: true
    # 指定手动配置的模式(默认为该模式)
    strategy: manual
    # 服务发现模式的配置
    routes:
      - name: 通用服务
        service-name: bp-common
        url: /my-common/v3/api-docs
        context-path: /my-common/


#加密
my-encrypt:
  aes-key: aes密钥
  rsa-pub-key: 公钥
  rsa-pri-key: 私钥 

````

