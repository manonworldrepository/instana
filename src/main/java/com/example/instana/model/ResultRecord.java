package com.example.instana.model;

import lombok.Getter;

@Getter
public class ResultRecord {
    private final String id;
    private final Object result;

    public ResultRecord(String id, Object result) {
        this.id = id;
        this.result = result;
    }

    @Override
    public String toString() {
        return id + ". " + result;
    }
}