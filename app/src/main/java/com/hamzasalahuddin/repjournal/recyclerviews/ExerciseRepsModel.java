package com.hamzasalahuddin.repjournal.recyclerviews;

public class ExerciseRepsModel {
    String repCount;
    String setCount;
    String weight;

    public ExerciseRepsModel() {

    }

    public ExerciseRepsModel(String repCount, String setCount, String weight) {
        this.repCount = repCount;
        this.setCount = setCount;
        this.weight = weight;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getSetCount() {
        return setCount;
    }

    public void setSetCount(String setCount) {
        this.setCount = setCount;
    }

    public String getRepCount() {
        return repCount;
    }

    public void setRepCount(String repCount) {
        this.repCount = repCount;
    }
}
