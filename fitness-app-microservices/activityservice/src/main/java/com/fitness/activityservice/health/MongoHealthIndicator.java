package com.fitness.activityservice.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class MongoHealthIndicator implements HealthIndicator {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public MongoHealthIndicator(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Health health() {
        try {
            mongoTemplate.executeCommand("{ ping: 1 }");
            return Health.up()
                    .withDetail("database", "MongoDB")
                    .withDetail("status", "Connected")
                    .build();
        } catch (Exception e) {
            return Health.down()
                    .withDetail("database", "MongoDB")
                    .withDetail("status", "Disconnected")
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
