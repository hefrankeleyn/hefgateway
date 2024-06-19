package io.github.hefrankeleyn.hefgateway.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * @Date 2024/6/11
 * @Author lifei
 */
@Component
public class HelloHandler {

    public Mono<ServerResponse> handle(ServerRequest request) {
        // url
        String url = "http://localhost:8081/hefrpc";
        String requestJson = "{\n" +
                "\"service\": \"cn.hefrankeleyn.hefrpc.demo.api.UserService\",\n" +
                "\"methodSign\": \"findById#int\",\n" +
                "\"args\": [300]\n" +
                "}";
        WebClient webClient = WebClient.create(url);
        Mono<ResponseEntity<String>> entity = webClient.post()
                .header("Content-Type", "application/json")
                .bodyValue(requestJson).retrieve().toEntity(String.class);

        return ServerResponse.ok()
                .header("Content-Type", "application/json")
                .header("hef.gw.version", "0.0.1")
                .body(entity.map(ResponseEntity::getBody), String.class);
    }

}
