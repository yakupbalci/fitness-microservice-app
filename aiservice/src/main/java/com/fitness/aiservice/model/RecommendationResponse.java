package com.fitness.aiservice.model;

import java.util.List;

public record RecommendationResponse(
        Analysis analysis,
        List<Improvement> improvements,
        List<Suggestion> suggestions,
        List<String> safety
) {
  public record Analysis(
          String overall,
          String pace,
          String heartRate,
          String caloriesBurned
  ) {}

  public record Improvement(
          String area,
          String recommendation
  ) {}

  public record Suggestion(
          String workout,
          String description
  ) {}
}