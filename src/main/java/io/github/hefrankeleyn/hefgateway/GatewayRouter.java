package io.github.hefrankeleyn.hefgateway;

import io.github.hefrankeleyn.hefgateway.handler.GatewayHandler;
import io.github.hefrankeleyn.hefgateway.handler.HelloHandler;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

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
//    @Bean
//    public RouterFunction<?> helloRouterFunction() {
//        return route(GET("/hello"), helloHandler::handle);
//    }
//
//    @Bean
//    public RouterFunction<?> gatewayRouterFunction() {
//        return route(GET("/gw").or(POST("/gw/**")), gatewayHandler::handle);
//    }


}
