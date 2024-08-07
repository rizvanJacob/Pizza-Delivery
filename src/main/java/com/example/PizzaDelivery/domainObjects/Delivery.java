package com.example.PizzaDelivery.domainObjects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Delivery {
    private Factory factory;
    private Customer customer;
    private PizzaDrone pizzaDrone;

    public Double getDistanceMeters() {
        return Math.sqrt(Math.pow(factory.getLocation().getLatitude() - customer.getLocation().getLatitude(), 2) + Math.pow(factory.getLocation().getLongitude() - customer.getLocation().getLongitude(), 2));
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
