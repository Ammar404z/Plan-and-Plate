package com.AEB13.backend.WeeklyPlan;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WeeklyPlanService {

    @Autowired
    private WeeklyPlanRepository weeklyPlanRepository;

    public WeeklyPlan getWeeklyPlan(Long id) {
        return weeklyPlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plan with ID " + id + " not found"));
    }

    public WeeklyPlan createWeeklyPlan(WeeklyPlan weeklyPlan) {
        Optional<WeeklyPlan> existingPlan = weeklyPlanRepository.findByWeek(weeklyPlan.getWeek());
        if (existingPlan.isPresent()) {
            throw new IllegalArgumentException("A plan for this week already exists.");
        }
        return weeklyPlanRepository.save(weeklyPlan);
    }


    public WeeklyPlan updateWeeklyPlan(Long id, Map<String, Long> meals) {
        WeeklyPlan plan = getWeeklyPlan(id);
        plan.setMeals(meals);
        return weeklyPlanRepository.save(plan);
    }

    public void deleteWeeklyPlan(Long id) {
        if (!weeklyPlanRepository.existsById(id)) {
            throw new RuntimeException("Plan with ID " + id + " does not exist");
        }
        weeklyPlanRepository.deleteById(id);
    }

    public List<WeeklyPlan> getAllWeeklyPlans() {
        return weeklyPlanRepository.findAll();
    }
}