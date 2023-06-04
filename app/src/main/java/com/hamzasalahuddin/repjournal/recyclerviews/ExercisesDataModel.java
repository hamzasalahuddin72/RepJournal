package com.hamzasalahuddin.repjournal.recyclerviews;

public class ExercisesDataModel {
    String exerciseDayCreated;
    String exerciseMonthCreated;
    String exerciseDataCreated;

    public ExercisesDataModel() {

    }

    public ExercisesDataModel(String exerciseDayCreated, String exerciseMonthCreated, String exerciseDataCreated) {
        this.exerciseDayCreated = exerciseDayCreated;
        this.exerciseMonthCreated = exerciseMonthCreated;
        this.exerciseDataCreated = exerciseDataCreated;
    }

    public String getExerciseDataCreated() {
        return exerciseDataCreated;
    }

    public void setExerciseDataCreated(String exerciseDataCreated) {
        this.exerciseDataCreated = exerciseDataCreated;
    }

    public String getExerciseDayCreated() {
        return exerciseDayCreated;
    }

    public void setExerciseDayCreated(String exerciseDayCreated) {
        this.exerciseDayCreated = exerciseDayCreated;
    }

    public String getExerciseMonthCreated() {
        return exerciseMonthCreated;
    }

    public void setExerciseMonthCreated(String exerciseMonthCreated) {
        this.exerciseMonthCreated = exerciseMonthCreated;
    }
}
