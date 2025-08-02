package com.fitness.gateway.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Service
@Validated
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private static final String VALIDATE_USER_ENDPOINT = "/api/users/{userId}/validate";
    private static final String REGISTER_USER_ENDPOINT = "/api/users/register";
    
    private static final String ERROR_USER_NOT_FOUND = "USER_NOT_FOUND";
    private static final String ERROR_INVALID_REQUEST = "INVALID_REQUEST";
    private static final String ERROR_UNAUTHORIZED = "UNAUTHORIZED";
    private static final String ERROR_UNKNOWN = "UNKNOWN_ERROR";
    
    private final WebClient userServiceWebClient;

    public UserService(WebClient userServiceWebClient) {
        this.userServiceWebClient = userServiceWebClient;
    }

    public Mono<Boolean> validateUser(@NotNull String userId) {
        if (userId.trim().isEmpty()) {
            return Mono.error(new UserServiceException("User ID cannot be empty", ERROR_INVALID_REQUEST, HttpStatus.BAD_REQUEST));
        }
        
        log.info("Validating user with ID: {}", userId);
        return userServiceWebClient.get()
                .uri(VALIDATE_USER_ENDPOINT, userId)
                .retrieve()
                .bodyToMono(Boolean.class)
                .doOnSuccess(valid -> log.info("User validation completed for ID: {}, Result: {}", userId, valid))
                .onErrorResume(WebClientResponseException.class, e -> {
                    log.error("Error validating user {}: {} - {}", userId, e.getStatusCode(), e.getMessage());
                    if (e.getStatusCode() == HttpStatus.NOT_FOUND)
                        return Mono.error(new UserServiceException("User not found: " + userId, ERROR_USER_NOT_FOUND, HttpStatus.NOT_FOUND));
                    else if (e.getStatusCode() == HttpStatus.BAD_REQUEST)
                        return Mono.error(new UserServiceException("Invalid request: " + userId, ERROR_INVALID_REQUEST, HttpStatus.BAD_REQUEST));
                    else if (e.getStatusCode() == HttpStatus.UNAUTHORIZED)
                        return Mono.error(new UserServiceException("Unauthorized access", ERROR_UNAUTHORIZED, HttpStatus.UNAUTHORIZED));
                    return Mono.error(new UserServiceException("Unexpected error: " + e.getMessage(), ERROR_UNKNOWN, HttpStatus.INTERNAL_SERVER_ERROR));
                });
    }

    public Mono<UserResponse> registerUser(@Valid @NotNull RegisterRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());
        return userServiceWebClient.post()
                .uri(REGISTER_USER_ENDPOINT)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(UserResponse.class)
                .doOnSuccess(response -> log.info("User registered successfully: {}", response.getEmail()))
                .onErrorResume(WebClientResponseException.class, e -> {
                    log.error("Error registering user: {} - {}", e.getStatusCode(), e.getMessage());
                    if (e.getStatusCode() == HttpStatus.BAD_REQUEST)
                        return Mono.error(new UserServiceException("Invalid registration request", ERROR_INVALID_REQUEST, HttpStatus.BAD_REQUEST));
                    return Mono.error(new UserServiceException("Unexpected error during registration: " + e.getMessage(), ERROR_UNKNOWN, HttpStatus.INTERNAL_SERVER_ERROR));
                });
    }
}
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
