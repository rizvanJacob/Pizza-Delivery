package com.example.PizzaDelivery.domainObjects;

import com.javadocmd.simplelatlng.LatLng;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class Customer {
    private String name;
    private LatLng location;
    @Setter(lombok.AccessLevel.NONE)
    private List<PizzaDrone> acceptedPizzaDrones;
    private Integer hungerLevel;
    @Override
    public String toString() {
        return name;
    }
}
