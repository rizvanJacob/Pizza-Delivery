package com.example.PizzaDelivery;

import com.example.PizzaDelivery.domain.Customer;
import com.example.PizzaDelivery.domain.Factory;
import com.example.PizzaDelivery.domain.PizzaDrone;
import com.example.PizzaDelivery.graphSolution.Engine;
import com.example.PizzaDelivery.heuristicSolution.DeliveryPlan;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        List<PizzaDrone> pizzaDrones = MockDataGenerator.generatePizzaDrones(10);
        List<Factory> factories = MockDataGenerator.generateFactories(3, pizzaDrones);
        List<Customer> customers = MockDataGenerator.generateCustomers(2000, pizzaDrones);

        var currTime = System.currentTimeMillis();
        var graphSolution = new Engine(factories, customers);
        System.out.printf("Built graph in %d ms\n", System.currentTimeMillis() - currTime);
//        graphSolution.printTree();
//        System.out.println(graphSolution.getGraph().toString());

        currTime = System.currentTimeMillis();
        graphSolution.solve();
        System.out.printf("Completed initial solution in %d ms\n", System.currentTimeMillis() - currTime);

        currTime = System.currentTimeMillis();
        MockDataGenerator.updateFactoryLocations(factories)
                .forEach(graphSolution::updateFactory);
        System.out.printf("Updated graph in %d ms\n", System.currentTimeMillis() - currTime);
//        graphSolution.printTree();
//        System.out.println(graphSolution.getGraph().toString());

        currTime = System.currentTimeMillis();
        graphSolution.solve();
        System.out.printf("Completed updated solution in %d ms\n", System.currentTimeMillis() - currTime);

        var scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            var currTime1 = System.currentTimeMillis();
            MockDataGenerator.updateFactoryLocations(factories)
                    .forEach(graphSolution::updateFactory);
            System.out.printf("Updated graph in %d ms\n", System.currentTimeMillis() - currTime1);
            graphSolution.printTree();

            currTime1 = System.currentTimeMillis();
            graphSolution.solve();
            System.out.printf("Completed solution in %d ms\n", System.currentTimeMillis() - currTime1);
        }, 5, 1, TimeUnit.SECONDS);
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

