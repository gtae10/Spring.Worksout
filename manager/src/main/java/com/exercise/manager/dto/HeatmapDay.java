package com.exercise.manager.dto;

public class HeatmapDay {
    private final int day;
    private final boolean active;
    private final boolean isToday;

    public HeatmapDay(int day, boolean active, boolean isToday) {
        this.day = day;
        this.active = active;
        this.isToday = isToday;
    }

    public int getDay() { return day; }
    public boolean isActive() { return active; }
    public boolean isToday() { return isToday; }
}
