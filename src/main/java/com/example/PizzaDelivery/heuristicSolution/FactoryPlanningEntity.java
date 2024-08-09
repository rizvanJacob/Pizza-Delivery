package com.example.PizzaDelivery.heuristicSolution;


import ai.timefold.solver.core.api.domain.entity.PlanningEntity;
import ai.timefold.solver.core.api.domain.variable.PlanningListVariable;
import com.example.PizzaDelivery.domainObjects.Factory;
import com.javadocmd.simplelatlng.LatLng;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@PlanningEntity
@Getter
public class FactoryPlanningEntity extends Factory {
    @PlanningListVariable(allowsUnassignedValues = true)
    private final List<Delivery> deliveries;
    public FactoryPlanningEntity(Factory factory) {
        super(factory.getName(), factory.getLocation(), factory.getCapacities(), factory.getDeliveryRangeMeters(), factory.getDeliverySpeedMetersPerSecond());
        this.deliveries = new ArrayList<>();
    }
    public FactoryPlanningEntity(String name, LatLng location, Double deliveryRangeMeters, Double deliverySpeedMetersPerSeconds, List<Delivery> deliveries){
        super(name, location, null, deliveryRangeMeters, deliverySpeedMetersPerSeconds);
        this.deliveries = deliveries;
    }
}
