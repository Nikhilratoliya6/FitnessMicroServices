package com.fitness.gateway.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private static final String VALIDATE_USER_ENDPOINT = "/api/users/{userId}/validate";
    private static final String REGISTER_USER_ENDPOINT = "/api/users/register";
    private final WebClient userServiceWebClient;

    public UserService(WebClient userServiceWebClient) {
        this.userServiceWebClient = userServiceWebClient;
    }

    public Mono<Boolean> validateUser(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException("User ID cannot be null or empty"));
        }
        log.info("Calling User Validation API for userId: {}", userId);
            return userServiceWebClient.get()
                    .uri(VALIDATE_USER_ENDPOINT, userId)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .onErrorResume(WebClientResponseException.class, e -> {
                        log.error("Error validating user {}: {} - {}", userId, e.getStatusCode(), e.getMessage());
                        if (e.getStatusCode() == HttpStatus.NOT_FOUND)
                            return Mono.error(new RuntimeException("User Not Found: " + userId));
                        else if (e.getStatusCode() == HttpStatus.BAD_REQUEST)
                            return Mono.error(new RuntimeException("Invalid Request: " + userId));
                        else if (e.getStatusCode() == HttpStatus.UNAUTHORIZED)
                            return Mono.error(new RuntimeException("Unauthorized access"));
                        return Mono.error(new RuntimeException("Unexpected error: " + e.getMessage()));
                    });
        }

    public Mono<UserResponse> registerUser(RegisterRequest request) {
        if (request == null) {
            return Mono.error(new IllegalArgumentException("Register request cannot be null"));
        }
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException("Email cannot be null or empty"));
        }
        log.info("Calling User Registration API for email: {}", request.getEmail());
        return userServiceWebClient.post()
                .uri(REGISTER_USER_ENDPOINT)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(UserResponse.class)
                .onErrorResume(WebClientResponseException.class, e -> {
                    if (e.getStatusCode() == HttpStatus.BAD_REQUEST)
                        return Mono.error(new RuntimeException("Invalid Request: " + request.getEmail()));
                    return Mono.error(new RuntimeException("Unexpected error: " + e.getMessage()));
                });
    }
}
