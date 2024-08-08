package com.example.PizzaDelivery.heuristicSolution;

import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintCollectors;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.ConstraintProvider;

public class PlanConstraintProvider implements ConstraintProvider {
    @Override
    public Constraint[] defineConstraints(ConstraintFactory factory) {
        return new Constraint[]{
                factoryCapacityConstraint(factory),
                deliveryTimeConstraint(factory),
                pizzaPreferenceConstraint(factory),
                factoryRangeConstraint(factory)
        };
    }

    private Constraint factoryCapacityConstraint(ConstraintFactory factory) {
        return factory.forEach(Delivery.class)
                .groupBy(Delivery::getFactory, Delivery::getPizzaDrone, ConstraintCollectors.count())
                .filter((truck, pizza, count) -> count > truck.getCapacities().get(pizza))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Factory capacity exceeded");
    }

    private Constraint deliveryTimeConstraint(ConstraintFactory factory) {
        return factory.forEach(Delivery.class)
                .groupBy(delivery -> Math.toIntExact(delivery.getCustomer().getHungerLevel() *
                        delivery.getDeliveryTimeSeconds()))
                .penalize(HardSoftScore.ONE_SOFT)
                .asConstraint("Minimize delivery time");
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
}
