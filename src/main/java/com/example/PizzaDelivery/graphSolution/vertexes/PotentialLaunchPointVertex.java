package com.example.PizzaDelivery.graphSolution.vertexes;

import com.example.PizzaDelivery.domain.Customer;
import com.example.PizzaDelivery.domain.Factory;
import com.example.PizzaDelivery.domain.PizzaDrone;
import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

public class PotentialLaunchPointVertex extends LocationVertex{
    public PotentialLaunchPointVertex(Factory factory, Customer customer, PizzaDrone pizzaDrone){
        super(buildName(factory, customer, pizzaDrone), getPotentialLaunchPoint(factory, customer, pizzaDrone));
    }
    private static String buildName(Factory factory, Customer customer, PizzaDrone pizzaDrone){
        return "Launch point from " + factory.getName() + " to " + customer.getName() + " using " + pizzaDrone.getPizzaName();
    }
    private static LatLng getPotentialLaunchPoint(Factory factory, Customer customer, PizzaDrone pizzaDrone){
        var distanceMeters = LatLngTool.distance(factory.getLocation(), customer.getLocation(), LengthUnit.METER);
        if (distanceMeters > pizzaDrone.getDeliveryRangeMeters() + factory.getDeliveryRangeMeters()){
            throw new IllegalArgumentException("Customer is too far away from factory");
        }

        LatLng optimumLaunchPoint;
        if (factory.getDeliverySpeedMetersPerSecond() > pizzaDrone.getDeliverySpeedMetersPerSecond()){
            //return optimum launch point by maximizing distance travelled by factory
            var bearingToCustomerDegrees = LatLngTool.initialBearing(factory.getLocation(), customer.getLocation());
            optimumLaunchPoint = LatLngTool.travel(factory.getLocation(), bearingToCustomerDegrees, factory.getDeliveryRangeMeters(), LengthUnit.METER);
        } else {
            //return optimum launch point by maximizing distance travelled by drone
            var bearingToFactoryDegrees = LatLngTool.initialBearing(customer.getLocation(), factory.getLocation());
            optimumLaunchPoint = LatLngTool.travel(customer.getLocation(), bearingToFactoryDegrees, pizzaDrone.getDeliveryRangeMeters(), LengthUnit.METER);
        }

        return optimumLaunchPoint;
    }
}
