package com.AEB13.backend.WeeklyPlan;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface WeeklyPlanRepository extends CrudRepository<WeeklyPlan, Long> {
    
}
