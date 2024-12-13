package com.AEB13.backend.WeeklyPlan;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WeeklyPlanService {

    @Autowired  // injecting the repository into this service class
    private WeeklyPlanRepository weeklyPlanRepository;

    public WeeklyPlan getWeeklyPlan(Long id){
        return weeklyPlanRepository.findById(id).orElseThrow(() -> new RuntimeException("Plan not found"));
    }
    
    public WeeklyPlan createWeeklyPlan(){
        return weeklyPlanRepository.save(new WeeklyPlan());
    }
    
    public WeeklyPlan updateWeeklyPlan(Long id, Map<String, Long> meals) {
        WeeklyPlan plan = getWeeklyPlan(id);
        plan.setMeals(meals);
        return weeklyPlanRepository.save(plan);
    }

    public void deleteWeeklyPlan(Long id){
        weeklyPlanRepository.deleteById(id);
    }
    
}

