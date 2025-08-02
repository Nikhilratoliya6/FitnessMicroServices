package com.fitness.activityservice.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class LoggingFilter implements WebFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        long startTime = System.currentTimeMillis();
        String path = exchange.getRequest().getPath().value();
        String method = exchange.getRequest().getMethod().name();
        
        logger.info("Incoming Request: {} {}", method, path);
        
        return chain.filter(exchange)
            .doFinally(signalType -> {
                long totalTime = System.currentTimeMillis() - startTime;
                logger.info("Completed Request: {} {} - Time: {}ms", 
                    method, path, totalTime);
            });
    }
}
