package com.example.PizzaDelivery.heuristicSolution;

import ai.timefold.solver.core.api.domain.entity.PlanningEntity;
import ai.timefold.solver.core.api.domain.variable.InverseRelationShadowVariable;
import ai.timefold.solver.core.api.domain.variable.PlanningVariable;
import com.example.PizzaDelivery.domain.Customer;
import com.example.PizzaDelivery.domain.Factory;
import com.example.PizzaDelivery.domain.PizzaDrone;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@PlanningEntity
public class Delivery {
    @InverseRelationShadowVariable(sourceVariableName = "deliveries")
    private Factory factory;
    @PlanningVariable
    private Customer customer;
    @PlanningVariable
    private PizzaDrone pizzaDrone;
    public Double getDistanceMeters() {
        return LatLngTool.distance(factory.getLocation(), customer.getLocation(), LengthUnit.METER);
    }
    public boolean isFeasible() {
        var factoryRange = factory.getDeliveryRangeMeters();
        var pizzaRange = pizzaDrone.getDeliveryRangeMeters();
        return factoryRange + pizzaRange >= getDistanceMeters();
    }

    public Long getDeliveryTimeSeconds() {
        var factoryRange = factory.getDeliveryRangeMeters();
        var factorySpeed = factory.getDeliverySpeedMetersPerSecond();

        var pizzaRange = pizzaDrone.getDeliveryRangeMeters();
        var pizzaSpeed = pizzaDrone.getDeliverySpeedMetersPerSecond();

        var distance = getDistanceMeters();
        var totalRange = factoryRange + pizzaRange;
        if (totalRange > distance) {
            throw new IllegalArgumentException("Delivery distance exceeds the combined range of factory and pizza.");
        }

        var factoryTime = distance / factorySpeed;
        var pizzaTime = distance / pizzaSpeed;

        double factoryDistance = Math.min(factoryRange, (factorySpeed / (factorySpeed + pizzaSpeed)) * distance);
        double pizzaDistance = distance - factoryDistance;

        if (pizzaDistance > pizzaRange) {
            pizzaDistance = pizzaRange;
            factoryDistance = distance - pizzaDistance;
        }

        factoryTime = factoryDistance / factorySpeed;
        pizzaTime = pizzaDistance / pizzaSpeed;

        return Math.round(factoryTime + pizzaTime);
    }
}
