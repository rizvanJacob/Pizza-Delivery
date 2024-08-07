package com.example.PizzaDelivery;

import com.example.PizzaDelivery.domainObjects.Customer;
import com.example.PizzaDelivery.domainObjects.Factory;
import com.example.PizzaDelivery.heuristicSolution.DeliverySolution;

import java.util.List;

public class Main {
	public static void main(String[] args) {
		List<Factory> factories = MockDataGenerator.generateFactories(10);
		List<Customer> customers = MockDataGenerator.generateCustomers(20);

		var heuristicSolution = new DeliverySolution(factories, customers);
		heuristicSolution.solve();

		var solution = heuristicSolution.getSolution().getDeliveries();
		for (var delivery : solution) {
			var customer = delivery.getCustomer();
			var factory = delivery.getFactory();
			var drone = delivery.getPizzaDrone();
			var deliveryTime = delivery.getDeliveryTimeSeconds();

			System.out.printf("Customer %s ordered a pizza from Factory %s using PizzaDrone %s. The delivery took %d seconds.\n",
					customer.getName(), factory.getId(), drone.getId(), deliveryTime);
		}
	}

}

