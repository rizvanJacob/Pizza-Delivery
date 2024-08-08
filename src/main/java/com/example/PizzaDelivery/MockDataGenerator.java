package com.example.PizzaDelivery;

import com.example.PizzaDelivery.domainObjects.Customer;
import com.example.PizzaDelivery.domainObjects.Factory;
import com.example.PizzaDelivery.domainObjects.Location;
import com.example.PizzaDelivery.domainObjects.PizzaDrone;
import com.javadocmd.simplelatlng.LatLng;

import java.util.*;

public class MockDataGenerator {
    private static final List<String> PIZZAS = List.of(
            "Margherita", "Pepperoni", "Hawaiian", "Veggie", "BBQ Chicken",
            "Buffalo Chicken", "Supreme", "Meat Lover's", "Cheese", "Mushroom",
            "Sausage", "Bacon", "Pineapple", "Spinach", "Garlic"
    );
    private static final Random RANDOM = new Random();
    private static final int MAX_HUNGER = 5;
    private static final int MAX_FACTORY_RANGE = 20000;
    private static final int MIN_FACTORY_RANGE = 15000;
    private static final int MAX_FACTORY_SPEED = 20;
    private static final int MIN_FACTORY_SPEED = 10;
    private static final double MAX_LATITUDE = 90.0;
    private static final double MAX_LONGITUDE = 180.0;
    private static final double MIN_LATITUDE = -90.0;
    private static final double MIN_LONGITUDE = -180.0;
    private static final int MIN_CAPACITY_COUNT = 1;
    private static final int MAX_CAPACITY_COUNT = 5;
    private static final int MAX_CAPACITY = 10;
    private static final double MAX_DRONE_RANGE = 3000.0;
    private static final double MIN_DRONE_RANGE = 500.0;
    private static final double MAX_DRONE_SPEED = 500.0;
    private static final double MIN_DRONE_SPEED = 300.0;
    private static final int MAX_PIZZAS_ACCEPTED = 3;
    private static List<PizzaDrone> pizzaDrones;
    public static List<Customer> generateCustomers(int count) {
        generateRandomPizzaDrones();
        List<Customer> customers = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            Customer customer = new Customer(
                    "Customer " + i,
                    LatLng.random(RANDOM),
                    pizzaDrones,
                    RANDOM.nextInt(MAX_HUNGER) + 1
            );
            customers.add(customer);
        }
        return customers;
    }

    public static List<Factory> generateFactories(int count) {
        List<Factory> factories = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            Factory factory = new Factory(
                    "Factory " + i,
                    LatLng.random(RANDOM),
                    generateRandomCapacities(),
                    RANDOM.nextDouble() * MAX_FACTORY_RANGE + MIN_FACTORY_RANGE,
                    RANDOM.nextDouble() * MAX_FACTORY_SPEED + MIN_FACTORY_SPEED
            );
            factories.add(factory);
        }
        return factories;
    }

    private static Map<PizzaDrone, Integer> generateRandomCapacities() {
        generateRandomPizzaDrones();

        Map<PizzaDrone, Integer> capacities = new HashMap<>();
        var capacityCount = MIN_CAPACITY_COUNT + RANDOM.nextInt(MAX_CAPACITY_COUNT - MIN_CAPACITY_COUNT + 1);
        for (int i = MIN_CAPACITY_COUNT; i <= capacityCount; i++) {
            var drone = pizzaDrones.get(RANDOM.nextInt(pizzaDrones.size()));
            capacities.put(drone, RANDOM.nextInt(MAX_CAPACITY) + 1);
        }
        return capacities;
    }

    private static void generateRandomPizzaDrones() {
        if (pizzaDrones == null) {
            pizzaDrones = new ArrayList<>();
        }
        int numPizzas = RANDOM.nextInt(MAX_PIZZAS_ACCEPTED) + 1; // 1-3 pizza types per customer
        for (int i = 0; i < numPizzas; i++) {
            PizzaDrone drone = new PizzaDrone(
                    PIZZAS.get(RANDOM.nextInt(PIZZAS.size())),
                    RANDOM.nextDouble() * MAX_DRONE_RANGE + MIN_DRONE_RANGE,
                    RANDOM.nextDouble() * MAX_DRONE_SPEED + MIN_DRONE_SPEED
            );
            pizzaDrones.add(drone);
        }
    }
}
