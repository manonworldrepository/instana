package com.example.instana.model;

import lombok.Getter;
import java.util.*;

@Getter
public class Graph {

    private final Map<String, List<Point>> edges = new HashMap<>();

    public void addEdge(Point point) {
        edges.computeIfAbsent(point.getStart(), key -> new ArrayList<>()).add(point);
    }

    public List<Point> getEdges(String node) {
        return edges.getOrDefault(node, Collections.emptyList());
    }
}
