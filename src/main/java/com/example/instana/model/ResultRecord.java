package com.example.instana.model;

public record ResultRecord(String id, Object result) {
    @Override
    public String toString() {
        return id + ". " + result;
    }
}