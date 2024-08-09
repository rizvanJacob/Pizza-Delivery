package com.example.PizzaDelivery.heuristicSolution;

import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import ai.timefold.solver.core.api.score.stream.*;
import com.example.PizzaDelivery.domain.Customer;

import java.util.function.Function;

public class ConstraintProvider implements ai.timefold.solver.core.api.score.stream.ConstraintProvider {
    @Override
    public Constraint[] defineConstraints(ConstraintFactory factory) {
        return new Constraint[]{
                factoryCapacityConstraint(factory),
                deliveryTimeConstraint(factory),
                pizzaPreferenceConstraint(factory),
                factoryRangeConstraint(factory),
                prioritizeHungriestCustomers(factory),
                maximizeCustomersSatisfied(factory)
        };
    }
//    HARD CONSTRAINTS
    private Constraint factoryCapacityConstraint(ConstraintFactory factory) {
        return factory.forEach(Delivery.class)
                .groupBy(Delivery::getFactory, Delivery::getPizzaDrone, ConstraintCollectors.count())
                .filter((truck, pizza, count) -> count > truck.getCapacities().get(pizza))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Factory capacity exceeded");
    }


    private Constraint pizzaPreferenceConstraint(ConstraintFactory factory) {
        return factory.forEach(Delivery.class)
                .filter(delivery -> !delivery.getCustomer().getAcceptedPizzaDrones().contains(delivery.getPizzaDrone()))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Pizza not accepted");
    }

    private Constraint factoryRangeConstraint(ConstraintFactory factory) {
        return factory.forEach(Delivery.class)
                .filter(Delivery::isFeasible)
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Factory range exceeded");
    }

    //    SOFT CONSTRAINTS
    private Constraint deliveryTimeConstraint(ConstraintFactory factory) {
        return factory.forEach(Delivery.class)
                .groupBy(delivery -> Math.toIntExact(delivery.getCustomer().getHungerLevel() *
                        delivery.getDeliveryTimeSeconds()))
                .penalize(HardSoftScore.ONE_SOFT)
                .asConstraint("Minimize delivery time");
    }
    private Constraint prioritizeHungriestCustomers(ConstraintFactory factory) {
        return factory.forEach(Delivery.class)
                .join(Customer.class,
                        Joiners.equal(Delivery::getCustomer, Function.identity()))
                .groupBy((delivery, customer) -> customer.getHungerLevel())
                .reward(HardSoftScore.ofSoft(2))
                .asConstraint("Prioritize hungriest customers");
    }
    private Constraint maximizeCustomersSatisfied(ConstraintFactory factory) {
        return factory.forEach(Delivery.class)
                .groupBy(Delivery::getCustomer, ConstraintCollectors.count())
                .reward(HardSoftScore.ONE_SOFT)
                .asConstraint("Maximize customers satisfied");
    }
}
