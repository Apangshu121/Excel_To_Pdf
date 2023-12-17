package com.example.demo.models;

import java.util.Date;

public class Interview {
    private String dateOfInterview;
    private String team;
    private String panelName;
    private String round;
    private String skill;
    private String time;
    private String candidateCurrentLoc;
    private String preferredLocation;
    private String candidateName;

    public String getDate() {
        return dateOfInterview;
    }

    public void setDate(Date date) {
        this.dateOfInterview = date.toString().substring(4,10);
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getPanelName() {
        return panelName;
    }

    public void setPanelName(String panelName) {
        this.panelName = panelName;
    }

    public String getRound() {
        return round;
    }

    public void setRound(String round) {
        this.round = round;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public String getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time.toString().substring(11,19);
    }

    public String getCandidateCurrentLoc() {
        return candidateCurrentLoc;
    }

    public void setCandidateCurrentLoc(String candidateCurrentLoc) {
        this.candidateCurrentLoc = candidateCurrentLoc;
    }

    public String getPreferredLocation() {
        return preferredLocation;
    }

    public void setPreferredLocation(String preferredLocation) {
        this.preferredLocation = preferredLocation;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }
}
