package io.github.hefrankeleyn.hefgateway;

import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import org.springframework.web.reactive.function.server.RouterFunction;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * @Date 2024/6/11
 * @Author lifei
 */
@Component
public class GatewayRouter {

    @Resource
    private HelloHandler helloHandler;

    @Resource
    private GatewayHandler gatewayHandler;

    /**
     * 使用动态编程的方式，创建接口路径和响应
     * @return
     */
    @Bean
    public RouterFunction<?> helloRouterFunction() {
        return route(GET("/hello"), helloHandler::handle);
    }

    @Bean
    public RouterFunction<?> gatewayRouterFunction() {
        return route(GET("/gw").or(POST("/gw/**")), gatewayHandler::handle);
    }


}
