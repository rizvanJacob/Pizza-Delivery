package com.example.PizzaDelivery.domainObjects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class Factory {
    private long id;
    private Location location;
    @Setter(lombok.AccessLevel.NONE)
    private Map<PizzaDrone, Integer> capacities = new HashMap<>();
    private Double deliveryRangeMeters;
    private Double deliverySpeedMetersPerSecond;
}
