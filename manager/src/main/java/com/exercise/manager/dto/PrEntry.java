package com.exercise.manager.dto;

public class PrEntry {
    private final String workout;
    private final String part;
    private final double weight;
    private final int reps;
    private final double estimatedOneRm;

    public PrEntry(String workout, String part, double weight, int reps, double estimatedOneRm) {
        this.workout = workout;
        this.part = part;
        this.weight = weight;
        this.reps = reps;
        this.estimatedOneRm = estimatedOneRm;
    }

    public String getWorkout() { return workout; }
    public String getPart() { return part; }
    public double getWeight() { return weight; }
    public int getReps() { return reps; }
    public double getEstimatedOneRm() { return estimatedOneRm; }
}
