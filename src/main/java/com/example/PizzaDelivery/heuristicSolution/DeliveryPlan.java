package com.example.PizzaDelivery.heuristicSolution;

import ai.timefold.solver.core.api.domain.solution.PlanningEntityCollectionProperty;
import ai.timefold.solver.core.api.domain.solution.PlanningScore;
import ai.timefold.solver.core.api.domain.solution.PlanningSolution;
import ai.timefold.solver.core.api.domain.solution.ProblemFactCollectionProperty;
import ai.timefold.solver.core.api.domain.valuerange.ValueRangeProvider;
import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import com.example.PizzaDelivery.domain.Customer;
import com.example.PizzaDelivery.domain.Factory;
import com.example.PizzaDelivery.domain.PizzaDrone;
import lombok.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@PlanningSolution
@Component
@Getter
@Setter
@NoArgsConstructor
public class DeliveryPlan {
    @PlanningEntityCollectionProperty
    private List<FactoryPlanningEntity> factories;
    @ValueRangeProvider
    @ProblemFactCollectionProperty
    private List<Customer> customers;
    @ValueRangeProvider
    @ProblemFactCollectionProperty
    private List<PizzaDrone> pizzaDrones;
    @PlanningEntityCollectionProperty
    @ValueRangeProvider
    private List<Delivery> deliveries;
    @PlanningScore
    private HardSoftScore score;

    public DeliveryPlan(List<Factory> factories, List<Customer> customers, List<PizzaDrone> pizzaDrones) {
        this.factories = factories.stream()
                .map(FactoryPlanningEntity::new)
                .toList();
        this.customers = customers;
        this.pizzaDrones = pizzaDrones;
        this.deliveries = new ArrayList<>();
        this.score = HardSoftScore.ZERO;
    }
}