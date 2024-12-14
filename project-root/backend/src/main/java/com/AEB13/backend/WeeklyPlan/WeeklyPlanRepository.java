package com.AEB13.backend.WeeklyPlan;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface WeeklyPlanRepository extends CrudRepository<WeeklyPlan, Long> {
    Optional<WeeklyPlan> findByWeek(int week);
    List<WeeklyPlan> findAll();
    
    
}
