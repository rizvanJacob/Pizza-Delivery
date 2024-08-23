package com.example.PizzaDelivery.graphSolution;

import com.example.PizzaDelivery.domain.Customer;
import com.example.PizzaDelivery.domain.Factory;
import com.example.PizzaDelivery.domain.PizzaDrone;
import com.example.PizzaDelivery.graphSolution.vertexes.CustomerVertex;
import com.example.PizzaDelivery.graphSolution.vertexes.FactoryVertex;
import com.example.PizzaDelivery.graphSolution.vertexes.LocationVertex;
import com.example.PizzaDelivery.graphSolution.vertexes.PotentialLaunchPointVertex;
import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;
import lombok.Getter;
import org.jgrapht.Graph;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultListenableGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.time.temporal.ChronoUnit;
import java.util.*;

@Getter
public class GraphSolution {
    private final ArrayList<Factory> initialFactories;
    private final List<Customer> initialCustomers;
    private final Graph<LocationVertex, DefaultWeightedEdge> graph;
    private final LocationVertex source;

    public GraphSolution(List<Factory> factories, List<Customer> customers) {
        this.initialFactories = (ArrayList<Factory>) factories;
        this.initialCustomers = customers;
        this.source = new LocationVertex("Source", LatLng.random());
        this.graph = new DefaultListenableGraph<>(
                new SimpleWeightedGraph<>(DefaultWeightedEdge.class)
        );

        graph.addVertex(source);
        factories.stream()
                .map(FactoryVertex::new)
                .peek(graph::addVertex)
                .map(factoryVertex -> graph.addEdge(source, factoryVertex))
                .forEach(edge -> graph.setEdgeWeight(edge, 0.0));
        customers.stream().map(CustomerVertex::new).forEach(graph::addVertex);

        factories.forEach(factory -> customers.forEach(customer -> addEdges(factory, customer)));
    }

    private void addEdges(Factory source, Customer target) {
        source.getCapacities().forEach((pizzaDrone, availability) -> {
            if (availability > 0) {
                addEdges(source, target, pizzaDrone);
            }
        });
    }

    private void addEdges(Factory source, Customer target, PizzaDrone pizzaDrone) {
        if (!target.getAcceptedPizzaDrones().contains(pizzaDrone)) {
            return;
        }

        var factoryVertex = new FactoryVertex(source);
        var customerVertex = new CustomerVertex(target);
        var distanceMeters = LatLngTool.distance(source.getLocation(), target.getLocation(), LengthUnit.METER);
        if (distanceMeters > source.getDeliveryRangeMeters() + pizzaDrone.getDeliveryRangeMeters()) {
            return;
        }

        if (distanceMeters <= pizzaDrone.getDeliveryRangeMeters()) {
            graph.addEdge(factoryVertex, customerVertex, new LabeledWeightedEdge(pizzaDrone.getPizzaName()));
            graph.setEdgeWeight(factoryVertex, customerVertex, pizzaDrone.getDeliveryRangeMeters() / pizzaDrone.getDeliverySpeedMetersPerSecond());
        } else {
            var launchPoint = new PotentialLaunchPointVertex(source, target, pizzaDrone);
            graph.addVertex(launchPoint);

            var distanceToLaunchPointMetres = LatLngTool.distance(source.getLocation(), launchPoint.getLocation(), LengthUnit.METER);
            graph.addEdge(factoryVertex, launchPoint);
            graph.setEdgeWeight(factoryVertex, launchPoint, distanceToLaunchPointMetres / source.getDeliverySpeedMetersPerSecond());

            graph.addEdge(launchPoint, customerVertex, new LabeledWeightedEdge(pizzaDrone.getPizzaName()));
            var distanceToCustomerMetres = LatLngTool.distance(launchPoint.getLocation(), target.getLocation(), LengthUnit.METER);
            graph.setEdgeWeight(launchPoint, customerVertex, distanceToCustomerMetres / pizzaDrone.getDeliverySpeedMetersPerSecond());
        }
    }

    private String solveFastestDelivery(Customer customer) {
        var vertex = new CustomerVertex(customer);
        var shortestPath = new DijkstraShortestPath<>(graph).getPath(source, vertex);
        if (shortestPath == null) {
           return null;
        } else {
            var deliveryTime = shortestPath.getWeight();
            var factory = shortestPath.getVertexList().get(1);
            var pizza = ((LabeledWeightedEdge)shortestPath.getEdgeList().get(shortestPath.getLength() - 1)).getLabel();
            return String.format("Fastest delivery to %s is in %f seconds from %s using %s", customer.getName(), deliveryTime, factory.getUniqueName(), pizza);
        }
    }

    public void solve() {
        initialCustomers.stream()
                .map(this::solveFastestDelivery)
                .filter(Objects::nonNull)
                .forEach(System.out::println);
        System.out.println("******************************************");
    }

    public void startSolving(Integer interval, ChronoUnit unit) {
        var timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                solve();
            }
        }, 0, unit.getDuration().toMillis() * interval);
    }
}
