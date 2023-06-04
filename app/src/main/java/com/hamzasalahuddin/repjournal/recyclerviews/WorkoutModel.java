package com.hamzasalahuddin.repjournal.recyclerviews;

public class WorkoutModel {
    String muscleGroupTitle;
    String phaseId;
    String workoutDay;

    public WorkoutModel() {

    }

    public WorkoutModel(String muscleGroupTitle, String phaseId, String workoutDay) {
        this.muscleGroupTitle = muscleGroupTitle;
        this.phaseId = phaseId;
        this.workoutDay = workoutDay;
    }

    public String getWorkoutDay() {
        return workoutDay;
    }

    public void setWorkoutDay(String workoutDay) {
        this.workoutDay = workoutDay;
    }

    public String getPhaseId() {
        return phaseId;
    }

    public void setPhaseId(String phaseId) {
        this.phaseId = phaseId;
    }

    public String getMuscleGroupTitle() {
        return muscleGroupTitle;
    }

    public void setMuscleGroupTitle(String muscleGroupTitle) {
        this.muscleGroupTitle = muscleGroupTitle;
    }
}
