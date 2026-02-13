package com.fitness.aiservice.service;

import com.fitness.aiservice.model.Activity;
import com.fitness.aiservice.model.Recommendation;
import com.fitness.aiservice.model.RecommendationResponse;
import com.fitness.aiservice.repository.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.mistralai.MistralAiChatOptions;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityAiService {
  private final ChatModel chatModel;
  private final RecommendationRepository recommendationRepository;

  private final BeanOutputConverter<RecommendationResponse> converter = new BeanOutputConverter<>(RecommendationResponse.class);

  public Recommendation generateRecommendation(Activity activity) {
    Prompt prompt = createPromptForActivity(activity);
    ChatResponse response = chatModel.call(prompt);
    log.info("MODEL RESPONDED: {}",response);
    Recommendation recommendation = processResponse(activity, response);
    return recommendationRepository.save(recommendation);
  }

  public Recommendation processResponse(Activity activity, @NonNull ChatResponse response) {
    String rawContent = Objects.requireNonNull(response.getResult()).getOutput().getText();

    if (rawContent == null || rawContent.isBlank()) {
      throw new RuntimeException("AI returned an empty response for activity: " + activity.getId());
    }

    RecommendationResponse responseContent = converter.convert(rawContent);

    return mapToRecommendation(activity, responseContent);
  }

  private Recommendation mapToRecommendation(Activity activity, RecommendationResponse responseContent) {
    List<String> improvements = responseContent.improvements().stream()
            .map(RecommendationResponse.Improvement::recommendation)
            .toList();

    List<String> suggestions = responseContent.suggestions().stream()
            .map(RecommendationResponse.Suggestion::description)
            .toList();

    return Recommendation.builder()
            .recommendation(responseContent.analysis().overall())
            .improvements(improvements)
            .suggestions(suggestions)
            .safety(responseContent.safety())
            .activityId(activity.getId())
            .userId(activity.getUserId())
            .activityType(activity.getType())
            .createdAt(activity.getCreatedAt())
            .build();
  }

  public Prompt createPromptForActivity(@NonNull Activity activity){
    String message = String.format("""
        Analyze this fitness activity and provide detailed recommendations.
        
        Activity Type: %s
        Duration: %d minutes
        Calories Burned: %d
        Additional Metrics: %s
        
        Provide detailed analysis focusing on performance, improvements, next workout suggestions, and safety guidelines.
        
        %s
        """,
            activity.getType(),
            activity.getDuration(),
            activity.getCaloriesBurned(),
            activity.getAdditionalMetrics(),
            converter.getFormat()
    );

    return new Prompt(
            message,
            MistralAiChatOptions.builder()
                    .model("mistral-large-2512")
                    .temperature(0.7)
                    .build()
    );
  }
}
