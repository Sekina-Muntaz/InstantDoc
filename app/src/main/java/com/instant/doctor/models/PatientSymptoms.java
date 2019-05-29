package com.instant.doctor.models;

import java.util.ArrayList;

public class PatientSymptoms {
    private String patientId;
    private String specialConditions;
    private ArrayList<String> symptoms;


    public PatientSymptoms(String specialConditions, ArrayList<String> symptoms,String patientId) {
        this.specialConditions = specialConditions;
        this.symptoms = symptoms;
        this.patientId=patientId;
    }

    public PatientSymptoms() {
    }

    public String getSpecialConditions() {
        return specialConditions;
    }

    public void setSpecialConditions(String date) {
        this.specialConditions = specialConditions;
    }

    public ArrayList<String> getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(ArrayList<String> time) {
        this.symptoms = time;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }
}
