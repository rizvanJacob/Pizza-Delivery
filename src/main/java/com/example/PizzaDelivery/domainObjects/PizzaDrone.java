package com.example.PizzaDelivery.domainObjects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PizzaDrone {
    private String pizzaName;
    private Double deliveryRangeMeters;
    private Double deliverySpeedMetersPerSecond;
    @Override
    public String toString() {
        return pizzaName;
    }
}
