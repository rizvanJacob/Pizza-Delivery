package com.example.PizzaDelivery.graphSolution.vertexes;

import com.example.PizzaDelivery.domain.Customer;
import lombok.Getter;

@Getter
public class CustomerVertex extends LocationVertex{
    private final Customer customer;
    public CustomerVertex(Customer customer) {
        super(customer.getName(), customer.getLocation());
        this.customer = customer;
    }
}
