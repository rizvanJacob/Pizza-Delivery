package com.example.PizzaDelivery.heuristicSolution;

import com.example.PizzaDelivery.domainObjects.Delivery;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintCollectors;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;

public class PlanConstraintProvider implements ConstraintProvider {
    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[]{
                truckCapacityConstraint(constraintFactory),
                deliveryTimeConstraint(constraintFactory),
                pizzaPreferenceConstraint(constraintFactory)
        };
    }

    private Constraint truckCapacityConstraint(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Delivery.class)
                .groupBy(Delivery::getFactory, Delivery::getPizzaDrone, ConstraintCollectors.count())
                .filter((truck, pizza, count) -> count > truck.getCapacities().get(pizza))
                .penalize("Factory capacity exceeded", HardSoftScore.ONE_HARD);
    }

    private Constraint deliveryTimeConstraint(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Delivery.class)
                .penalize("Delivery time", HardSoftScore.ONE_SOFT,
                        delivery -> Math.toIntExact(delivery.getCustomer().getHungerLevel() *
                                delivery.getDeliveryTimeSeconds()));
    }

    private Constraint pizzaPreferenceConstraint(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Delivery.class)
                .filter(delivery -> !delivery.getCustomer().getAcceptedPizzaDrones().contains(delivery.getPizzaDrone()))
                .penalize("Pizza not accepted", HardSoftScore.ONE_HARD);
    }
}
