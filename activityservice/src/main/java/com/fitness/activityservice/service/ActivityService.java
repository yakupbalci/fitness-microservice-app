package com.fitness.activityservice.service;

import com.fitness.activityservice.dto.ActivityRequest;
import com.fitness.activityservice.dto.ActivityResponse;
import com.fitness.activityservice.model.Activity;
import com.fitness.activityservice.repository.ActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivityService {
  private final ActivityRepository activityRepository;
  private final UserValidationService userValidationService;
  private final RabbitTemplate rabbitTemplate;

  @Value("${rabbitmq.exchange.name}")
  private String exchangeName;
  @Value("${rabbitmq.routing.key}")
  private String routingKey;

  public ActivityResponse trackActivity(@RequestBody ActivityRequest request){
    boolean isValidUser = userValidationService.validateUser(request.getUserId());
    if(!isValidUser){
      throw new RuntimeException("Invalid user: " + request.getUserId());
    }

    Activity activity = Activity.builder()
            .userId(request.getUserId())
            .type(request.getType())
            .duration(request.getDuration())
            .caloriesBurned(request.getCaloriesBurned())
            .startTime(request.getStartTime())
            .additionalMetrics(request.getAdditionalMetrics())
            .build();
    Activity savedActivity = activityRepository.save(activity);
    // RabbitMQ Ai processing
    try {
      rabbitTemplate.convertAndSend(exchangeName, routingKey, savedActivity);
    }catch (Exception e){
      throw new RuntimeException("Failed to send activity to RabbitMQ: " + e.getMessage());
    }
    System.out.println("SAVED ACTIVITY: " + savedActivity);
    return convertToActivityResponse(savedActivity);
  }

  public List<ActivityResponse> getActivities(){
    List<Activity> activities = this.activityRepository.findAll();
    return activities
            .stream()
            .map(this::convertToActivityResponse)
            .collect(Collectors.toList());
  }

  public List<ActivityResponse> getUserActivities(String userId){
    List<Activity> activities = this.activityRepository.findByUserId(userId);
    return activities
            .stream()
            .map(this::convertToActivityResponse)
            .collect(Collectors.toList());
  }

  public ActivityResponse getActivityById(String id){
    return activityRepository.findById(id)
            .map(this::convertToActivityResponse)
            .orElseThrow(() -> new RuntimeException("Activity not found with id: " + id));
  }

  public ActivityResponse convertToActivityResponse(Activity activity){
    ActivityResponse activityResponse = new ActivityResponse();
    activityResponse.setId(activity.getId());
    activityResponse.setUserId(activity.getUserId());
    activityResponse.setType(activity.getType());
    activityResponse.setDuration(activity.getDuration());
    activityResponse.setStartTime(activity.getStartTime());
    activityResponse.setAdditionalMetrics(activity.getAdditionalMetrics());
    activityResponse.setCaloriesBurned(activity.getCaloriesBurned());
    activityResponse.setCreatedAt(activity.getCreatedAt());
    activityResponse.setUpdatedAt(activity.getUpdatedAt());
    return activityResponse;
  }
}
