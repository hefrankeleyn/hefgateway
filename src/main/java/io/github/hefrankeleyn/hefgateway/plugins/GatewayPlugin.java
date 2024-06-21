package io.github.hefrankeleyn.hefgateway.plugins;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public interface GatewayPlugin {

    String GATEWAY_PREFIX = "/gw";

    String getName();

    String gatewayPrefix();

    boolean support(ServerWebExchange exchange);

    Mono<Void> handle(ServerWebExchange exchange, GatewayPluginChain chain);


}
