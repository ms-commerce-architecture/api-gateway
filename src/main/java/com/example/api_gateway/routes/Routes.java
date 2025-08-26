package com.example.api_gateway.routes;


import io.github.resilience4j.springboot3.circuitbreaker.monitoring.endpoint.CircuitBreakerEndpoint;
import org.springframework.cloud.gateway.server.mvc.filter.CircuitBreakerFilterFunctions;

import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;


import java.net.URI;

import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
@Configuration
public class Routes {

    @Bean
    public RouterFunction<ServerResponse> productServiceRoutes() {
        return RouterFunctions.route()
                .add(GatewayRouterFunctions.route("product_service")
                .route(RequestPredicates.path("/api/v1/product"), HandlerFunctions.http("http://localhost:8080"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("product_service_circuitBreaker", URI.create("forward:/fallbackRoute")))
                .build())

                .add(GatewayRouterFunctions.route("product_service")
                .route(RequestPredicates.path("/api/v1/categories"), HandlerFunctions.http("http://localhost:8080"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("product_service_circuitBreaker", URI.create("forward:/fallbackRoute")))
                .build())
                .add(GatewayRouterFunctions.route("product_service")
                        .route(RequestPredicates.path("/api/v1/attributes"), HandlerFunctions.http("http://localhost:8080"))
                        .filter(CircuitBreakerFilterFunctions.circuitBreaker("product_service_circuitBreaker", URI.create("forward:/fallbackRoute")))
                        .build())
                .build() ;
    }


    @Bean
    public RouterFunction<ServerResponse> orderServiceRoutes() {
        return GatewayRouterFunctions.route("order-service")
                .route(RequestPredicates.path("/api/order"),HandlerFunctions.http("http://localhost:8081"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("order_service_circuitBreaker", URI.create("forward:/fallbackRoute")))
                .build();

    }

    @Bean
    public RouterFunction<ServerResponse> inventoryServiceRoutes(CircuitBreakerEndpoint circuitBreakerEndpoint) {
        return GatewayRouterFunctions.route("inventory-service")
                .route(RequestPredicates.path("api/ineventory"), HandlerFunctions.http("http://localhost:8082"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("inventory_service_circuitBreaker", URI.create("forward:/fallbackRoute")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> fallbackRoute() {
        return route("fallbackRoute").GET("/fallbackRoute",request -> ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE).body("SERVICE UNAVAILABLE")).build();
    }
}
