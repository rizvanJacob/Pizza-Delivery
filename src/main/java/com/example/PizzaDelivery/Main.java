package com.example.PizzaDelivery;

import com.example.PizzaDelivery.domainObjects.Customer;
import com.example.PizzaDelivery.domainObjects.Factory;
import com.example.PizzaDelivery.domainObjects.PizzaDrone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
	private static List<Customer> customers;
	private static List<Factory> factories;
	public static void main(String[] args) {
		customers = MockDataGenerator.generateCustomers(20);
		factories = MockDataGenerator.generateFactories(10);
	}

}

