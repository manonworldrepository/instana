package com.example.instana.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Point {
    private String start;
    private String end;
    private int latency;

    public Point(String start, int latency) {
        this.start = start;
        this.latency = latency;
    }
}
