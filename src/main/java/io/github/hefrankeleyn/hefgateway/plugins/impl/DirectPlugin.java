package io.github.hefrankeleyn.hefgateway.plugins.impl;

import io.github.hefrankeleyn.hefgateway.plugins.AbstractGatewayPlugin;
import io.github.hefrankeleyn.hefgateway.plugins.GatewayPluginChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * @Date 2024/6/20
 * @Author lifei
 */
@Component("directPlugin")
public class DirectPlugin extends AbstractGatewayPlugin {

    private static final Logger log = LoggerFactory.getLogger(DirectPlugin.class);

    private static final String NAME = "direct";

    @Override
    public Mono<Void> doHandle(ServerWebExchange exchange, GatewayPluginChain chain) {
        log.debug("====> [DirectPlugin] doHandle....");
        // 获取直接请求的url
        String url = exchange.getRequest().getQueryParams().getFirst("backend");
        exchange.getResponse().getHeaders().add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        exchange.getResponse().getHeaders().add("hef.gw.plugin",  getName());
        if (Objects.isNull(url) || url.isEmpty()) {
            // 直接返回
            return exchange.getRequest().getBody().flatMap(x->exchange.getResponse()
                    .writeWith(Mono.just(x))).then(chain.handle(exchange));
        }
        // 4. 获取请求报文
        Flux<DataBuffer> requestBody = exchange.getRequest().getBody();
        // 5. 通过WebClient 发送post请求
        WebClient webClient = WebClient.create(url);
        Mono<ResponseEntity<String>> entity = webClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody, DataBuffer.class).retrieve().toEntity(String.class);
        // 6. 获取响应报文
        Mono<String> responseBody = entity.map(ResponseEntity::getBody);
        return responseBody.flatMap(res -> exchange.getResponse()
                .writeWith(Mono.just(exchange.getResponse().bufferFactory()
                        .wrap(res.getBytes())))).then(chain.handle(exchange));
    }

    @Override
    public String getName() {
        return NAME;
    }
}
