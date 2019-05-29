package com.instant.doctor.models;

public class MedicalNote {
    private String sessionId;
    private String patientId;
    private String doctorName;
    private String diagnosis;
    private String prescription;
    private String medicalNoteUrl;
    private long time;

    public MedicalNote() {
    }

    public MedicalNote(String sessionId, String diagnosis, String prescription, long time,String doctorName) {
        this.sessionId = sessionId;
        this.diagnosis = diagnosis;
        this.prescription = prescription;
        this.time=time;
        this.doctorName=doctorName;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getPrescription() {
        return prescription;
    }

    public void setPrescription(String prescription) {
        this.prescription = prescription;
    }

    public String getMedicalNoteUrl() {
        return medicalNoteUrl;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public void setMedicalNoteUrl(String medicalNoteUrl) {
        this.medicalNoteUrl = medicalNoteUrl;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }
}
