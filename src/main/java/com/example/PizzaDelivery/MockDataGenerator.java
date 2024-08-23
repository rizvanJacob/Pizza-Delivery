package com.example.PizzaDelivery;

import com.example.PizzaDelivery.domain.Customer;
import com.example.PizzaDelivery.domain.Factory;
import com.example.PizzaDelivery.domain.PizzaDrone;
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
    private static final double MAX_LATITUDE = 2.0;
    private static final double MIN_LATITUDE = -2.0;
    private static final double MAX_LONGITUDE = 5.0;
    private static final double MIN_LONGITUDE = 0.0;
    private static final int MIN_CAPACITY_COUNT = 1;
    private static final int MAX_CAPACITY_COUNT = 5;
    private static final int MAX_CAPACITY = 10;
    private static final double MAX_DRONE_RANGE = 3000.0;
    private static final double MIN_DRONE_RANGE = 500.0;
    private static final double MAX_DRONE_SPEED = 500.0;
    private static final double MIN_DRONE_SPEED = 300.0;
    private static final int MAX_PIZZAS_ACCEPTED = 3;
    public static List<Customer> generateCustomers(int count, List<PizzaDrone> pizzaDrones) {
        List<Customer> customers = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            Customer customer = new Customer(
                    "Customer " + i,
                    generateRandomLocation(),
                    generatePizzasAccepted(pizzaDrones),
                    RANDOM.nextInt(MAX_HUNGER) + 1
            );
            customers.add(customer);
        }
        return customers;
    }
    private static List<PizzaDrone> generatePizzasAccepted(List<PizzaDrone> availablePizzas){
        var pizzasAcceptedSet = new HashSet<PizzaDrone>();
        var pizzasAcceptedCount = RANDOM.nextInt(MAX_PIZZAS_ACCEPTED) + 1;
        if (pizzasAcceptedCount > availablePizzas.size()) {
            throw new IllegalArgumentException("Cannot generate more pizzas than available");
        }
        while (pizzasAcceptedSet.size() < pizzasAcceptedCount) {
            pizzasAcceptedSet.add(availablePizzas.get(RANDOM.nextInt(availablePizzas.size())));
        }
        return new ArrayList<>(pizzasAcceptedSet);
    }

    public static List<Factory> generateFactories(int count, List<PizzaDrone> pizzaDrones) {
        List<Factory> factories = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            Factory factory = new Factory(
                    "Factory " + i,
                    generateRandomLocation(),
                    generatePizzaDroneCapacities(pizzaDrones),
                    RANDOM.nextDouble() * MAX_FACTORY_RANGE + MIN_FACTORY_RANGE,
                    RANDOM.nextDouble() * MAX_FACTORY_SPEED + MIN_FACTORY_SPEED
            );
            factories.add(factory);
        }
        return factories;
    }

    private static Map<PizzaDrone, Integer> generatePizzaDroneCapacities(List<PizzaDrone> availablePizzaDrones) {
        Map<PizzaDrone, Integer> capacities = new HashMap<>();
        var capacityCount = MIN_CAPACITY_COUNT + RANDOM.nextInt(MAX_CAPACITY_COUNT - MIN_CAPACITY_COUNT + 1);
        if (capacityCount > availablePizzaDrones.size()) {
            throw new IllegalArgumentException("Cannot generate more capacities than pizza drones");
        }
        while (capacities.size() < capacityCount) {
            var drone = availablePizzaDrones.get(RANDOM.nextInt(availablePizzaDrones.size()));
            if (!capacities.containsKey(drone)) {
                capacities.put(drone, RANDOM.nextInt(MAX_CAPACITY) + 1);
            }
        }
        return capacities;
    }

    public static List<PizzaDrone> generatePizzaDrones(int count) {
        if (count > PIZZAS.size()) {
            throw new IllegalArgumentException("Cannot generate more pizza drones than pizzas");
        }
        var shuffledPizzas = new ArrayList<>(PIZZAS);
        Collections.shuffle(shuffledPizzas, RANDOM);
        var pizzaDrones = new ArrayList<PizzaDrone>();
        for (int i = 0; i < count; i++) {
            var drone = new PizzaDrone(
                    shuffledPizzas.get(i),
                    RANDOM.nextDouble() * MAX_DRONE_RANGE + MIN_DRONE_RANGE,
                    RANDOM.nextDouble() * MAX_DRONE_SPEED + MIN_DRONE_SPEED
            );
            pizzaDrones.add(drone);
        }
        return pizzaDrones;
    }
    private static LatLng generateRandomLocation(){
        var latitude = RANDOM.nextDouble() * (MAX_LATITUDE - MIN_LATITUDE) + MIN_LATITUDE;
        var longitude = RANDOM.nextDouble() * (MAX_LONGITUDE - MIN_LONGITUDE) + MIN_LONGITUDE;
        return new LatLng(latitude, longitude);
    }
}
