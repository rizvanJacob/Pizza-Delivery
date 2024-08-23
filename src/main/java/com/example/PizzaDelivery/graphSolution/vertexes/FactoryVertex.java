package com.example.PizzaDelivery.graphSolution.vertexes;

import com.example.PizzaDelivery.domain.Factory;

public class FactoryVertex extends LocationVertex{
    public FactoryVertex(Factory factory) {
        super(factory.getName(), factory.getLocation());
    }
}
