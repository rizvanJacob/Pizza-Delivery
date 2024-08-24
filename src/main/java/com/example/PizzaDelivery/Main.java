package com.example.PizzaDelivery;

import com.example.PizzaDelivery.domain.Customer;
import com.example.PizzaDelivery.domain.Factory;
import com.example.PizzaDelivery.domain.PizzaDrone;
import com.example.PizzaDelivery.graphSolution.Engine;
import com.example.PizzaDelivery.heuristicSolution.DeliveryPlan;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main {
	public static void main(String[] args) {
		List<PizzaDrone> pizzaDrones = MockDataGenerator.generatePizzaDrones(10);
		List<Factory> factories = MockDataGenerator.generateFactories(300, pizzaDrones);
		List<Customer> customers = MockDataGenerator.generateCustomers(1000, pizzaDrones);

		var graphSolution = new Engine(factories, customers);
		graphSolution.startSolving(5, TimeUnit.SECONDS);
	}

	private static void printSolution(DeliveryPlan solution) {
		var deliveries = solution.getDeliveries();
		System.out.printf("Found %d deliveries for %d customers and %d factories\n", deliveries.size(), solution.getCustomers().size(), solution.getFactories().size());
		for (var delivery : deliveries) {
			var customer = delivery.getCustomer();
			var factory = delivery.getFactory();
			var drone = delivery.getPizzaDrone();
			var deliveryTime = delivery.getDeliveryTimeSeconds();
			System.out.printf("Factory %s delivered to customer %s using drone %s in %d seconds\n",
					factory.toString(), customer.toString(), drone.toString(), deliveryTime);
		}
	}

}

