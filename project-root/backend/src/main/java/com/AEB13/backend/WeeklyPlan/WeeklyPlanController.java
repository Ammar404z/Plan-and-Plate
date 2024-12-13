package com.AEB13.backend.WeeklyPlan;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/weekly-plans")
public class WeeklyPlanController {
    
@Autowired
    private WeeklyPlanService weeklyPlanService;

    @GetMapping("/{id}")
    public ResponseEntity<WeeklyPlan> getWeeklyplan(@PathVariable Long id){
        return ResponseEntity.ok(weeklyPlanService.getWeeklyPlan(id));
    }

    @PostMapping
    public ResponseEntity<WeeklyPlan> createWeeklyPlan(){
        return ResponseEntity.ok(weeklyPlanService.createWeeklyPlan());
    }

    @PutMapping("/{id}")
    public ResponseEntity<WeeklyPlan> updateWeeklyPlan(@PathVariable Long id, @RequestBody Map<String,Long> meals){
        return ResponseEntity.ok(weeklyPlanService.updateWeeklyPlan(id, meals));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWeeklyPlan(@PathVariable Long id){
        weeklyPlanService.deleteWeeklyPlan(id);
        return ResponseEntity.noContent().build();
    }
}
