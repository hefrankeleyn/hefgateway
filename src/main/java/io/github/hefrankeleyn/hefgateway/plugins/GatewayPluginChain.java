package io.github.hefrankeleyn.hefgateway.plugins;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public interface GatewayPluginChain {

    Mono<Void> handle(ServerWebExchange exchange);

}
