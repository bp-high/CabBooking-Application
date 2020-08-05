package com.CabBooking.Model.mapgraphutilities;

import java.util.ArrayList;
import java.util.List;

/**
 * Code for {@code EdgeWeightedGraph}, used to create. Manipulate and store the Map
 */
public class EdgeWeightedGraph {
    private int vertices;
    private List<ArrayList<WeightedEdge>> adjacencyList;

    EdgeWeightedGraph(int v) {
        this.vertices = v;
        adjacencyList = new ArrayList<>();
        for (int i = 0; i < v; i++) {
            adjacencyList.add(new ArrayList<>());
        }
    }

    void addEdge(WeightedEdge e) {
        int f = e.from();
        adjacencyList.get(f).add(e);
    }

    ArrayList<WeightedEdge> adjacent(int v) {
        return adjacencyList.get(v);
    }

    int numberVertices() {
        return  vertices;
    }
}

