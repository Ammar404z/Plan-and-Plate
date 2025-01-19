package com.AEB13.backend.WeeklyPlan;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for performing CRUD operations on {@link WeeklyPlan} entities.
 */
@Repository
public interface WeeklyPlanRepository extends CrudRepository<WeeklyPlan, Long> {

    /**
     * Finds a weekly plan by its unique week number.
     *
     * @param week the week number
     * @return an optional containing the matching plan if found, otherwise empty
     */
    Optional<WeeklyPlan> findByWeek(int week);

    /**
     * Retrieves all existing weekly plans.
     *
     * @return a list of all {@link WeeklyPlan} entities
     */
    List<WeeklyPlan> findAll();
}
