package com.example.PizzaDelivery.heuristicSolution;

import ai.timefold.solver.core.api.solver.SolverFactory;
import ai.timefold.solver.core.config.solver.SolverConfig;
import com.example.PizzaDelivery.domainObjects.Customer;
import com.example.PizzaDelivery.domainObjects.Factory;
import com.example.PizzaDelivery.domainObjects.PizzaDrone;
import lombok.Getter;

import java.time.Duration;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Solver {
    private static final Long SOLUTION_LIMIT_SECONDS = 30L;
    private final ai.timefold.solver.core.api.solver.Solver<DeliveryPlan> solver;
    private final Timer timer = new Timer();
    @Getter
    private final DeliveryPlan solution;
    public Solver(List<Factory> factories, List<Customer> customers, List<PizzaDrone> pizzaDrones){
        solution = new DeliveryPlan(factories, customers, pizzaDrones);
        this.solver = initSolver();
    }
    public void solve(){
        solver.solve(solution);
    }
    public void solveEvery(Duration interval){
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                solver.solve(solution);
            }
        }, 0, interval.toMillis());
    }
    private static ai.timefold.solver.core.api.solver.Solver<DeliveryPlan> initSolver(){
        SolverFactory<DeliveryPlan> solverFactory = SolverFactory.create(new SolverConfig()
                .withSolutionClass(DeliveryPlan.class)
                .withEntityClasses(Delivery.class, FactoryPlanningEntity.class)
                .withConstraintProviderClass(ConstraintProvider.class)
                .withTerminationSpentLimit(Duration.ofSeconds(SOLUTION_LIMIT_SECONDS)));

        return solverFactory.buildSolver();
    }
}
