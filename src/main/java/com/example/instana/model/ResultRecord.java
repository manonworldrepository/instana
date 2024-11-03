package com.example.instana.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResultRecord {
    private final String id;
    private final Object result;

    @Override
    public String toString() {
        return id + ". " + result;
    }
}