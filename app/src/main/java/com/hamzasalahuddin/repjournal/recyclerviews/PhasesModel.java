package com.hamzasalahuddin.repjournal.recyclerviews;

public class PhasesModel {
    String phaseTitle;
    String dayPhaseCreated;
    String monthPhaseCreated;

    public PhasesModel() {
    }

    public PhasesModel(String phaseTitle, String dayPhaseCreated, String monthPhaseCreated) {
        this.phaseTitle = phaseTitle;
        this.dayPhaseCreated = dayPhaseCreated;
        this.monthPhaseCreated = monthPhaseCreated;
    }

    public String getDayPhaseCreated() {
        return dayPhaseCreated;
    }

    public void setDayPhaseCreated(String dayPhaseCreated) {
        this.dayPhaseCreated = dayPhaseCreated;
    }

    public String getMonthPhaseCreated() {
        return monthPhaseCreated;
    }

    public void setMonthPhaseCreated(String monthPhaseCreated) {
        this.monthPhaseCreated = monthPhaseCreated;
    }

    public String getPhaseTitle() {
        return phaseTitle;
    }

    public void setPhaseTitle(String phaseTitle) {
        this.phaseTitle = phaseTitle;
    }
}

