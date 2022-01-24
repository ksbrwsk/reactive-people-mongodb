package de.ksbrwsk.people;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class PersonRouter {
    @Bean
    RouterFunction<ServerResponse> http(PersonHandler personHandler) {
        return nest(path(Constants.API),
                route(GET(""), personHandler::handleFindAll)
                        .andRoute(GET("/{id}"), personHandler::handleFindById)
                        .andRoute(DELETE("/{id}"), personHandler::handleDeleteById)
                        .andRoute(POST(""), personHandler::handleCreate)
                        .andRoute(PUT("/{id}"), personHandler::handleUpdate)
        );
    }
}
