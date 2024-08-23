package com.example.PizzaDelivery.graphSolution;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jgrapht.graph.DefaultWeightedEdge;

@RequiredArgsConstructor
@Getter
public class LabeledWeightedEdge extends DefaultWeightedEdge {
    private final String label;
    @Override
    public String toString() {
        return "(" + getSource() + " : " + getTarget() + " : " + label + ")";
    }
}
