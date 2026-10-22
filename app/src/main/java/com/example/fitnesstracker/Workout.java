package com.example.fitnesstracker;

/** Simple model class representing one row of the Workouts table. */
public class Workout {
    private final long id;
    private final String workoutType;
    private final int duration;        // minutes
    private final int caloriesBurned;  // kcal
    private final String intensity;    // Low / Medium / High
    private final String date;         // yyyy-MM-dd HH:mm

    public Workout(long id, String workoutType, int duration,
                   int caloriesBurned, String intensity, String date) {
        this.id = id;
        this.workoutType = workoutType;
        this.duration = duration;
        this.caloriesBurned = caloriesBurned;
        this.intensity = intensity;
        this.date = date;
    }

    public long getId() { return id; }
    public String getWorkoutType() { return workoutType; }
    public int getDuration() { return duration; }
    public int getCaloriesBurned() { return caloriesBurned; }
    public String getIntensity() { return intensity; }
    public String getDate() { return date; }
}
