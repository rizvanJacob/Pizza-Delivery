package com.example.PizzaDelivery.graphSolution.vertexes;

import com.example.PizzaDelivery.domain.Factory;
import lombok.Getter;

@Getter
public class FactoryVertex extends LocationVertex{
    private final Factory factory;
    public FactoryVertex(Factory factory) {
        super(factory.getName(), factory.getLocation());
        this.factory = factory;
    }
}
