package com.fitness.activityservice.service;

import com.fitness.activityservice.dto.ActivityRequest;
import com.fitness.activityservice.dto.ActivityResponse;
import com.fitness.activityservice.model.Activity;
import com.fitness.activityservice.repository.ActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityService {
  private final ActivityRepository activityRepository;

  public ActivityResponse trackActivity(@RequestBody ActivityRequest request){
    Activity activity = Activity.builder()
            .userId(request.getUserId())
            .type(request.getType())
            .duration(request.getDuration())
            .caloriesBurned(request.getCaloriesBurned())
            .startTime(request.getStartTime())
            .additionalMetrics(request.getAdditionalMetrics())
            .build();
    Activity savedActivity = activityRepository.save(activity);
    System.out.println("SAVED ACTIVITY: " + savedActivity);
    return convertToActivityResponse(savedActivity);
  }

  public List<ActivityResponse> fetchActivities(){
    List<ActivityResponse> activities = new ArrayList<>();
    for(Activity activity: activityRepository.findAll()){
      System.out.println("FETCH ACTIVITY: " + activity);
      activities.add(convertToActivityResponse(activity));
    }
    return activities;
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
