package com.exercise.manager.dto;

import java.time.LocalDate;

public class WeightPoint {
    private final LocalDate date;
    private final double weight;

    public WeightPoint(LocalDate date, double weight) {
        this.date = date;
        this.weight = weight;
    }

    public LocalDate getDate() { return date; }
    public double getWeight() { return weight; }
}
