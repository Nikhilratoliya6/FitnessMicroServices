package com.fitness.activityservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI fitnessActivityAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Fitness Activity Service API")
                .description("REST API for managing fitness activities")
                .version("1.0.0")
                .contact(new Contact()
                    .name("Fitness App Team")
                    .email("support@fitnessapp.com"))
                .license(new License()
                    .name("Apache 2.0")
                    .url("http://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}
