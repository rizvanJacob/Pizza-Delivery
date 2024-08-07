package com.example.PizzaDelivery.domainObjects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class Customer {
    private long id;
    private Location location;
    private String name;
    @Setter(lombok.AccessLevel.NONE)
    private List<PizzaDrone> acceptedPizzaDrones;
    private Integer hungerLevel;
}
