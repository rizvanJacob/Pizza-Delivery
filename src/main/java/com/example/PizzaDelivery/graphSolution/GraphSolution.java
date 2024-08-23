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

import java.util.ArrayList;

@Getter
public class GraphSolution {
    private final ArrayList<Factory> factories;
    private final ArrayList<Customer> customers;
    private final Graph<LocationVertex, DefaultWeightedEdge> graph;
    private final LocationVertex source;

    public GraphSolution(ArrayList<Factory> factories, ArrayList<Customer> customers) {
        this.factories = factories;
        this.customers = customers;
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

        var distanceMeters = LatLngTool.distance(source.getLocation(), target.getLocation(), LengthUnit.METER);
        if (distanceMeters > source.getDeliveryRangeMeters() + pizzaDrone.getDeliveryRangeMeters()) {
            return;
        }

        if (distanceMeters <= pizzaDrone.getDeliveryRangeMeters()) {
            var edge = graph.addEdge(new FactoryVertex(source), new CustomerVertex(target));
            graph.setEdgeWeight(edge, pizzaDrone.getDeliveryRangeMeters() / pizzaDrone.getDeliverySpeedMetersPerSecond());
        } else {
            var launchPoint = new PotentialLaunchPointVertex(source, target, pizzaDrone);
            graph.addVertex(launchPoint);

            var travelEdge = graph.addEdge(new FactoryVertex(source), launchPoint);
            var distanceToLaunchPointMetres = LatLngTool.distance(source.getLocation(), launchPoint.getLocation(), LengthUnit.METER);
            graph.setEdgeWeight(travelEdge, distanceToLaunchPointMetres / source.getDeliverySpeedMetersPerSecond());

            var launchEdge = graph.addEdge(launchPoint, new CustomerVertex(target));
            var distanceToCustomerMetres = LatLngTool.distance(launchPoint.getLocation(), target.getLocation(), LengthUnit.METER);
            graph.setEdgeWeight(launchEdge, distanceToCustomerMetres / pizzaDrone.getDeliverySpeedMetersPerSecond());
        }
    }

    private String solveFastestDelivery(Customer customer) {
        var vertex = new CustomerVertex(customer);
        var shortestPath = new DijkstraShortestPath<>(graph).getPath(source, vertex);
        if (shortestPath == null) {
            return "No delivery possible to " + customer.getName();
        } else {
            var deliveryTime = shortestPath.getWeight();
            var factory = shortestPath.getVertexList().get(1);
            return String.format("Fastest delivery to %s is in %f seconds from %s", customer.getName(), deliveryTime, factory.getUniqueName());
        }
    }

    public void solve() {
        customers.stream().map(this::solveFastestDelivery)
                .forEach(System.out::println);
    }
}
