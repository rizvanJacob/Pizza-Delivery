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
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.ListenableGraph;
import org.jgrapht.alg.shortestpath.YenKShortestPath;
import org.jgrapht.graph.DefaultListenableGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Getter
public class Engine {
    private final ListenableGraph<LocationVertex, DefaultWeightedEdge> graph;
    private final LocationVertex source;

    public Engine(List<Factory> initialFactories, List<Customer> initialCustomers) {
        this.source = new LocationVertex("Source", LatLng.random());
        this.graph = new DefaultListenableGraph<>(
                new SimpleWeightedGraph<>(DefaultWeightedEdge.class)
        );

        graph.addVertex(source);
        initialFactories.stream()
                .map(FactoryVertex::new)
                .peek(graph::addVertex)
                .map(factoryVertex -> graph.addEdge(source, factoryVertex))
                .forEach(edge -> graph.setEdgeWeight(edge, 0.0));
        initialCustomers.stream()
                .map(CustomerVertex::new)
                .forEach(graph::addVertex);

        initialFactories
                .forEach(factory -> initialCustomers
                        .forEach(customer -> addEdges(factory, customer)));
    }

    public void addFactory(Factory factory) {
        var factoryVertex = new FactoryVertex(factory);
        graph.addVertex(factoryVertex);
        graph.addEdge(source, factoryVertex);
        graph.setEdgeWeight(source, factoryVertex, 0.0);
        addEdgesFromFactory(factoryVertex);
    }

    private void addEdgesFromFactory(FactoryVertex factory) {
        graph.vertexSet().stream()
                .filter(vertex -> vertex instanceof CustomerVertex)
                .map(vertex -> (CustomerVertex) vertex)
                .forEach(customerVertex -> addEdges(factory.getFactory(), customerVertex.getCustomer()));
    }

    public void updateFactory(Factory factory) {
        var factoryVertex = new FactoryVertex(factory);
        if (!graph.containsVertex(factoryVertex)) {
            System.err.println("Factory not found! Adding factory instead");
            addFactory(factory);
            return;
        }

        removeEdgesToSuccessors(factoryVertex);
        addEdgesFromFactory(factoryVertex);
    }

    public void removeFactory(Factory factory){
        var factoryVertex = new FactoryVertex(factory);
        if (!graph.containsVertex(factoryVertex)) {
            System.err.println("Factory not found!");
            return;
        }

        removeEdgesToSuccessors(factoryVertex);
        graph.removeVertex(factoryVertex);
    }

    private void removeEdgesToSuccessors(LocationVertex vertex) {
        Graphs.successorListOf(graph, vertex)
                .forEach(successor -> {
                    graph.removeEdge(vertex, successor);
                    if (successor instanceof PotentialLaunchPointVertex) {
                        graph.removeVertex(successor);
                    }
                    removeEdgesToSuccessors(successor);
                });
    }

    public void addCustomer(Customer customer) {
        var customerVertex = new CustomerVertex(customer);
        graph.addVertex(customerVertex);
        addEdgesToCustomer(customerVertex);
    }

    private void addEdgesToCustomer(CustomerVertex customer) {
        graph.vertexSet().stream()
                .filter(vertex -> vertex instanceof FactoryVertex)
                .map(vertex -> (FactoryVertex) vertex)
                .map(FactoryVertex::getFactory)
                .forEach(factory -> addEdges(factory, customer.getCustomer()));
    }

    public void updateCustomer(Customer customer) {
        var customerVertex = new CustomerVertex(customer);
        if (!graph.containsVertex(customerVertex)) {
            System.err.println("Customer not found! Adding customer instead");
            addCustomer(customer);
            return;
        }

        removeEdgesFromFactories(customerVertex);
        addEdgesToCustomer(customerVertex);
    }

    private void removeEdgesFromFactories(LocationVertex customer) {
        Graphs.predecessorListOf(graph, customer)
                .forEach(predecessor -> {
                    graph.removeEdge(predecessor, customer);
                    if (predecessor instanceof PotentialLaunchPointVertex) {
                        graph.removeVertex(predecessor);
                    }
                    if (!(predecessor instanceof FactoryVertex)) {
                        removeEdgesFromFactories(predecessor);
                    }
                });
    }

    public void removeCustomer(Customer customer) {
        var customerVertex = new CustomerVertex(customer);
        if (!graph.containsVertex(customerVertex)) {
            System.err.println("Customer not found!");
            return;
        }

        removeEdgesFromFactories(customerVertex);
        graph.removeVertex(customerVertex);
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

    private String solveFastestDelivery(CustomerVertex customer) {
        var shortestPaths = new YenKShortestPath<>(graph).getPaths(source, customer, 3);
        if (shortestPaths.size() == 0) {
            return null;
        } else {
            return shortestPaths.stream().map(this::formatPath).collect(Collectors.joining("\n"));
        }
    }

    private String formatPath(GraphPath<LocationVertex, DefaultWeightedEdge> path) {
        var deliveryTime = path.getWeight();
        var factory = path.getVertexList().get(1);
        var customer = path.getEndVertex();
        var pizza = ((LabeledWeightedEdge) path.getEdgeList().get(path.getLength() - 1)).getLabel();
        return String.format("Fastest delivery to %s is in %f seconds from %s using %s", customer.getUniqueName(), deliveryTime, factory.getUniqueName(), pizza);
    }

    public void solve() {
        graph.vertexSet().stream()
                .filter(vertex -> vertex instanceof CustomerVertex)
                .map(vertex -> (CustomerVertex) vertex)
                .map(CustomerVertex::getCustomer)
                .sorted(Comparator.comparing(Customer::getHungerLevel))
                .limit(10)
                .map(CustomerVertex::new)
                .map(this::solveFastestDelivery)
                .filter(Objects::nonNull)
                .forEach(System.out::println);
        System.out.println("******************************************");
    }

    public void startSolving(Integer interval, TimeUnit unit) {
        var scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::solve, 0, interval, unit);
    }
}
