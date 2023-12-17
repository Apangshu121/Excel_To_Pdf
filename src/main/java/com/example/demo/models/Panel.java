package com.example.demo.models;

public class Panel {
    String panelName;
    int noOfInterviews;

    public Panel(String panelName, int noOfInterviews) {
        this.panelName = panelName;
        this.noOfInterviews = noOfInterviews;
    }

    public String getPanelName() {
        return panelName;
    }

    public int getNoOfInterviews() {
        return noOfInterviews;
    }
}
