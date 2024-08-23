package com.example.PizzaDelivery.graphSolution.vertexes;

import com.example.PizzaDelivery.domain.Customer;

public class CustomerVertex extends LocationVertex{
    public CustomerVertex(Customer customer) {
        super(customer.getName(), customer.getLocation());
    }
}
