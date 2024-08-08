package com.example.PizzaDelivery.heuristicSolution;

import ai.timefold.solver.core.api.solver.Solver;
import ai.timefold.solver.core.api.solver.SolverFactory;
import ai.timefold.solver.core.config.solver.SolverConfig;
import com.example.PizzaDelivery.domainObjects.Customer;
import com.example.PizzaDelivery.domainObjects.Factory;
import lombok.Getter;

import java.time.Duration;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class DeliverySolution {
    private static final Long SOLUTION_LIMIT_SECONDS = 30L;
    private final Solver<DeliveryPlan> solver;
    private final Timer timer = new Timer();
    @Getter
    private final DeliveryPlan solution;
    public DeliverySolution(List<Factory> factories, List<Customer> customers){
        solution = new DeliveryPlan(factories, customers);
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
    private static Solver<DeliveryPlan> initSolver(){
        SolverFactory<DeliveryPlan> solverFactory = SolverFactory.create(new SolverConfig()
                .withSolutionClass(DeliveryPlan.class)
                .withEntityClasses(Delivery.class)
                .withConstraintProviderClass(PlanConstraintProvider.class)
                .withTerminationSpentLimit(Duration.ofSeconds(30)));

        return solverFactory.buildSolver();
    }
}
