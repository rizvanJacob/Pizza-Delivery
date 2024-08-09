package com.example.PizzaDelivery.domainObjects;

import ai.timefold.solver.core.api.domain.entity.PlanningEntity;
import ai.timefold.solver.core.api.domain.variable.PlanningListVariable;
import ai.timefold.solver.core.api.domain.variable.PlanningVariable;
import com.example.PizzaDelivery.heuristicSolution.Delivery;
import com.javadocmd.simplelatlng.LatLng;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class Factory {
    private String name;
    private LatLng location;
    @Setter(lombok.AccessLevel.NONE)
    private Map<PizzaDrone, Integer> capacities;
    private Double deliveryRangeMeters;
    private Double deliverySpeedMetersPerSecond;
    @Override
    public String toString() {
        return name;
    }
}
