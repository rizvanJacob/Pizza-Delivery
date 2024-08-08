package com.example.PizzaDelivery.heuristicSolution;

import ai.timefold.solver.core.api.domain.solution.PlanningEntityCollectionProperty;
import ai.timefold.solver.core.api.domain.solution.PlanningScore;
import ai.timefold.solver.core.api.domain.solution.PlanningSolution;
import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import com.example.PizzaDelivery.domainObjects.Customer;
import com.example.PizzaDelivery.domainObjects.Factory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@PlanningSolution
@RequiredArgsConstructor
@Component
public class DeliveryPlan {
    @Getter
    private final List<Factory> factories;
    @Getter
    private final List<Customer> customers;
    private final List<Delivery> deliveries = new ArrayList<>();
    @Setter
    private HardSoftScore score;
    @PlanningEntityCollectionProperty
    public List<Delivery> getDeliveries() {
        return deliveries;
    }
    @PlanningScore
    public HardSoftScore getScore() {
        return score;
    }
}