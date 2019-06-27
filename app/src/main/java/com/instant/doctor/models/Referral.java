package com.instant.doctor.models;

public class Referral {

    private String sessionId;
    private String patientId;
    private String doctorName;



    private boolean isLab;
    private String notes;
    private String notesUrl;
    private long time;


    public Referral(String sessionId, String patientId, String doctorName, String notes, long time) {
        this.sessionId = sessionId;
        this.patientId = patientId;
        this.doctorName = doctorName;
        this.notes = notes;
        this.time = time;
    }

    public Referral() {
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getNotesUrl() {
        return notesUrl;
    }

    public void setNotesUrl(String notesUrl) {
        this.notesUrl = notesUrl;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isLab() {
        return isLab;
    }

    public void setLab(boolean lab) {
        isLab = lab;
    }
}
