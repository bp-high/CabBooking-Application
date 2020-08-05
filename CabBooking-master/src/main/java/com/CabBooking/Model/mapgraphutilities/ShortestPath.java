package com.CabBooking.Model.mapgraphutilities;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Stack;

/**
 * Class to find and store shortest path and distance
 */
public class ShortestPath {
    private double[] distTo;
    private WeightedEdge[] edgeFrom;
    private PriorityQueue<Integer> nodes;
    private int from;

    /**
     * Implementation of dijkstra's algorithm for shortest path
     */
    public ShortestPath(EdgeWeightedGraph g, int initialNode) {
        from = initialNode;
        distTo = new double[g.numberVertices()];
        edgeFrom = new WeightedEdge[g.numberVertices()];
        nodes = new PriorityQueue<>(g.numberVertices(), new WeightedEdgeComparator(distTo));
        for (int i = 0, v = g.numberVertices(); i < v; i++) {
            distTo[i] = Double.POSITIVE_INFINITY;
        }
        distTo[initialNode] = 0;
        nodes.add(initialNode);

        while (!nodes.isEmpty()) {
            int minWeightNode = nodes.poll();
            for (WeightedEdge e : g.adjacent(minWeightNode)) {
                relaxEdge(e);
            }
        }
    }


    private void relaxEdge(WeightedEdge e) {
        int e1 = e.from(), e2 = e.to();
        double d2e1 = distTo[e1], d2e2 = distTo[e2];
        if (d2e1 + e.getWeight() < d2e2) {
            distTo[e2] = d2e1 + e.getWeight();
            edgeFrom[e2] = e;
            if (!nodes.contains(e2)) {
                nodes.add(e2);
            }
        }
    }

    public Stack<Integer> getPathTo(int to) {
        Stack<Integer> path = new Stack<>();
        for (int i = to; i != from; i = edgeFrom[i].from()) {
            path.add(i);
        }
        path.add(from);
        Stack<Integer> revPath = new Stack<>();
        for (int w = 0, l = path.size(); w < l; w++) {
            int element = path.pop();
            revPath.add(element);
        }
        return revPath;
    }

    public double getDistanceTo(int to) {
        return distTo[to];
    }

    public double[] getDistances() {
        return distTo;
    }
}

/**
 * Comparator for WeightedEdges
 */
class WeightedEdgeComparator implements Comparator<Integer> {
    private double[] distTo;

    WeightedEdgeComparator(double[] distances) {
        distTo = distances;
    }

    @Override
    public int compare(Integer e1, Integer e2) {
        return Double.compare(distTo[e2], distTo[e1]);
    }
}
