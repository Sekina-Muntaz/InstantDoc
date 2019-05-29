package com.instant.doctor.models;

import com.google.firebase.firestore.Exclude;

public class MedicalSession {


    @Exclude
    private String id;

    private String patient_id;
    private String symptoms_id;
    private String doctor_id;
    private String patient_name;
    private String doctor_name;
    private String doctor_photoUrl;
    private String doctor_Specialization;
    //    private String medical_note_id;
    private long time;

    public MedicalSession(String patient_id, String symptoms_id, String doctor_id,
                          String doctor_name, String doctorUrl, String doctorSpecialization, String patientName, long time) {
        this.patient_id = patient_id;
        this.symptoms_id = symptoms_id;
        this.doctor_id = doctor_id;
        this.doctor_name = doctor_name;
        this.patient_name = patientName;
        this.doctor_photoUrl = doctorUrl;
        this.doctor_Specialization = doctorSpecialization;
//        this.medical_note_id = medical_note_id;
        this.time = time;

    }


    public MedicalSession() {
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getPatient_name() {
        return patient_name;
    }

    public void setPatient_name(String patient_name) {
        this.patient_name = patient_name;
    }

    public String getDoctor_name() {
        return doctor_name;
    }

    public void setDoctor_name(String doctor_name) {
        this.doctor_name = doctor_name;
    }

    public String getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(String patient_id) {
        this.patient_id = patient_id;
    }

    public String getSymptoms_id() {
        return symptoms_id;
    }

    public void setSymptoms_id(String symptoms_id) {
        this.symptoms_id = symptoms_id;
    }

    public String getDoctor_id() {
        return doctor_id;
    }

    public void setDoctor_id(String doctor_id) {
        this.doctor_id = doctor_id;
    }

    public String getDoctor_photoUrl() {
        return doctor_photoUrl;
    }

    public void setDoctor_photoUrl(String doctor_photoUrl) {
        this.doctor_photoUrl = doctor_photoUrl;
    }

    public String getDoctor_Specialization() {
        return doctor_Specialization;
    }

    public void setDoctor_Specialization(String doctor_Specialization) {
        this.doctor_Specialization = doctor_Specialization;
    }

//    public String getMedical_note_id() {
////        return medical_note_id;
//    }
//
//    public void setMedical_note_id(String medical_note_id) {
////        this.medical_note_id = medical_note_id;
//    }

//    public String getTime() {
//        return time;
//    }

//    public void setTime(String time) {
////        this.time = time;
//    }
}
