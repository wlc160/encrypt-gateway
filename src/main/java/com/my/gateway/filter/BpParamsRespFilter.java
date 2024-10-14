package com.my.gateway.filter;

import cn.hutool.core.util.ObjectUtil;
import com.google.common.collect.Lists;
import com.my.gateway.constants.StringConstants;
import com.my.gateway.properties.EncryptProperties;
import com.my.gateway.utils.ParamsAesUtils;
import com.my.gateway.utils.ParamsRsaUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @Description: 加密响应信息
 * @Author: 温陆城
 * @Date: 2024-10-14 17:13:07
 */
@Component
@Slf4j
public class BpParamsRespFilter implements GlobalFilter, Ordered {

    @Resource
    private EncryptProperties encryptProperties;

    @Override
    public int getOrder() {
        return -2;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        Map<String, String> headers = request.getHeaders().toSingleValueMap();
        if (ObjectUtil.isNotEmpty(headers.get(StringConstants.REQUIRED_CHECK_REQ))) {
            log.info("BpParamsRespFilter----------- 返回参数加密开始");
            return respExchange(exchange, chain, response, headers);
        }
        return chain.filter(exchange);
    }

    private Mono<Void> respExchange(ServerWebExchange exchange, GatewayFilterChain chain, ServerHttpResponse response, Map<String, String> headers) {
        DataBufferFactory bufferFactory = response.bufferFactory();
        String aesKeyResp = ParamsAesUtils.getAesKey();
        log.info("AesKeyResp=={}", aesKeyResp);
        response.getHeaders().set(StringConstants.ENCRYPTION_HEADER_KEY, ParamsRsaUtils.encryptParams(aesKeyResp, encryptProperties.getRsaPubKey()));
        response.getHeaders().setAccessControlExposeHeaders(Lists.newArrayList(StringConstants.ENCRYPTION_HEADER_KEY));
        ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(response) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                if (body instanceof Flux<? extends DataBuffer> fluxBody) {
                    return super.writeWith(fluxBody.buffer().map(dataBuffer -> {
                        DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();
                        DataBuffer join = dataBufferFactory.join(dataBuffer);
                        byte[] content = new byte[join.readableByteCount()];
                        join.read(content);
                        //释放掉内存
                        DataBufferUtils.release(join);
                        byte[] bytes = ParamsAesUtils.encrypt(new String(content, StandardCharsets.UTF_8), aesKeyResp, encryptProperties.getAesKey()).getBytes(StandardCharsets.UTF_8);
                        response.getHeaders().setContentLength(bytes.length);
                        return bufferFactory.wrap(bytes);
                    }));
                }
                return super.writeWith(body);
            }
        };
        return chain.filter(exchange.mutate().response(decoratedResponse).build());
    }
}
