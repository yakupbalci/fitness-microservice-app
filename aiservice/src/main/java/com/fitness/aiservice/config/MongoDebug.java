package com.fitness.aiservice.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MongoDebug {
  private final MongoTemplate mongoTemplate;
  @Value("${spring.data.mongodb.uri:NOT SET}")
  private String uri;

  @Value("${spring.data.mongodb.database:NOT SET}")
  private String database;

  @Value("${spring.profiles.active:NOT SET}")
  private String activeProfiles;

  @PostConstruct
  void printDb() {
    log.info("!!! MONGO DB: {}", mongoTemplate.getDb().getName());
    log.info("!!! MONGO DB: {}", mongoTemplate.getClass().getName());
    log.info("=== Spring Data MongoDB Properties ===");
    log.info("spring.data.mongodb.uri = {}", uri);
    log.info("spring.data.mongodb.database = {}", database);
    log.info("Active profiles = {}", activeProfiles);
    log.info("=====================================");
  }
}
