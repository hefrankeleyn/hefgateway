package io.github.hefrankeleyn.hefgateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * @Date 2024/6/19
 * @Author lifei
 */
@Component
public class GatewayWebFilter implements WebFilter {

    private static final Logger log = LoggerFactory.getLogger(GatewayWebFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        log.debug("===> hef gateway web filter...");
        if (Objects.isNull(exchange.getRequest().getQueryParams().getFirst("mock"))) {
            return chain.filter(exchange);
        } else {
            // {"result": "mock"}
            String mockRes = "{\"result\": \"mock\"}";
            return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(mockRes.getBytes())));
        }
    }
}
