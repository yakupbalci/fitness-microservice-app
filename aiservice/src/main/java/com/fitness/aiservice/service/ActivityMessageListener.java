package com.fitness.aiservice.service;

import com.fitness.aiservice.model.Activity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityMessageListener {
  private final ActivityAiService activityAiService;

  @RabbitListener(queues = "activity.queue")
  public void processActivityMessage(Activity activity) {
    log.info("Message received with Activity: {} {}", activity.getId(),activity.getType());
    log.info("Generated Recommendation: {}", activityAiService.generateRecommendation(activity));
  }
}
