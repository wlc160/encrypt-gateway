package com.my.gateway.filter;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.my.gateway.constants.StringConstants;
import com.my.gateway.properties.EncryptProperties;
import com.my.gateway.utils.ParamsAesUtils;
import com.my.gateway.utils.ParamsRsaUtils;
import com.my.gateway.utils.WebfluxResponseUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.factory.rewrite.CachedBodyOutputMessage;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.*;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * @Description: 请求参数解密
 * @Author: 温陆城
 * @Date: 2024-10-14 17:06:24
 */
@Component
@Slf4j
public class BpParamsReqFilter implements GlobalFilter, Ordered {

    @Resource
    private EncryptProperties encryptProperties;

    private final List<HttpMessageReader<?>> messageReaders = HandlerStrategies.withDefaults().messageReaders();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        Map<String, String> headers = request.getHeaders().toSingleValueMap();
        if (HttpMethod.POST == request.getMethod() && MediaType.APPLICATION_JSON.equals(request.getHeaders().getContentType())) {
            log.info("BpParamsReqFilter----------- 请求解密参数逻辑开始");
            return reqExchange(exchange, chain, request, headers);
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -3;
    }

    private Mono<Void> reqExchange(ServerWebExchange exchange, GatewayFilterChain chain, ServerHttpRequest request, Map<String, String> headers) {
        String aesKey = headers.get(StringConstants.ENCRYPTION_HEADER_KEY);
        if (StrUtil.isEmpty(aesKey)) {
            return WebfluxResponseUtil.responseFailed(exchange, HttpStatus.OK.value(), "非法请求.参数缺失 " + StringConstants.ENCRYPTION_HEADER_KEY);
        }
        ServerRequest serverRequest = ServerRequest.create(exchange, messageReaders);
        Mono<String> modifiedBody = serverRequest.bodyToMono(String.class)
                .flatMap(body -> {
                    if (MediaType.APPLICATION_JSON.isCompatibleWith(request.getHeaders().getContentType())) {
                        String rsaKey = ParamsRsaUtils.decryptParams(aesKey, encryptProperties.getRsaPriKey());
                        String data = JSONUtil.parseObj(body).getStr(StringConstants.ENCRYPT_DATA);
                        return Mono.just(ParamsAesUtils.decrypt(data, rsaKey, encryptProperties.getAesKey()));
                    }
                    return Mono.empty();
                });
        BodyInserter<Mono<String>, ReactiveHttpOutputMessage> bodyInserter = BodyInserters.fromPublisher(modifiedBody, String.class);
        HttpHeaders tempHeaders = new HttpHeaders();
        tempHeaders.putAll(request.getHeaders());
        tempHeaders.remove(HttpHeaders.CONTENT_LENGTH);
        CachedBodyOutputMessage outputMessage = new CachedBodyOutputMessage(exchange, tempHeaders);
        return bodyInserter.insert(outputMessage, new BodyInserterContext())
                .then(Mono.defer(() -> {
                    ServerHttpRequestDecorator decorator = new ServerHttpRequestDecorator(request) {
                        @Override
                        public HttpHeaders getHeaders() {
                            tempHeaders.set(StringConstants.REQUIRED_CHECK_REQ ,"1");
                            tempHeaders.set(HttpHeaders.TRANSFER_ENCODING, "chunked");
                            return tempHeaders;
                        }
                        @Override
                        public Flux<DataBuffer> getBody() {
                            return outputMessage.getBody();
                        }
                    };
                    return chain.filter(exchange.mutate().request(decorator).build());
                }));
    }
}
