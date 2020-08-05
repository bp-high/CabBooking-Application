package com.CabBooking.Model.mapgraphutilities;

/**
 * Implementation of {@code WeightedEdge} for map
 */
class WeightedEdge {
    private int e1, e2;
    private double weight;

    WeightedEdge(int from, int to, double wt) {
        this.e2 = to;
        this.e1 = from;
        this.weight = wt;
    }

    int from() {
        return e1;
    }

    int other(int x) {
        if (x == e1) {
            return e2;
        } else {
            return e1;
        }
    }

    int to() {
        return e2;
    }

    double getWeight() {
        return weight;
    }

}
