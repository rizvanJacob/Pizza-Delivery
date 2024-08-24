package com.example.PizzaDelivery.graphSolution.vertexes;

import com.javadocmd.simplelatlng.LatLng;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@Getter
@RequiredArgsConstructor
public class LocationVertex {
    private final String uniqueName;
    private final LatLng location;
    @Override
    public String toString() {
        return uniqueName;
    }
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof LocationVertex)) {
            return false;
        }
        return Objects.equals(uniqueName, ((LocationVertex) obj).uniqueName);
    }
    @Override
    public int hashCode() {
        return uniqueName.hashCode();
    }
}
