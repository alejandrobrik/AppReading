package com.example.appreading.models;

import com.google.firebase.Timestamp;

import java.io.Serializable;

public class Lecture implements Serializable {
    String id;

    String percentLecture;

    String namePDF;

    Timestamp dateLecture;

    String timeSound;

    String id_student;

    public Lecture() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPercentLecture() {
        return percentLecture;
    }

    public void setPercentLecture(String percentLecture) {
        this.percentLecture = percentLecture;
    }

    public String getNamePDF() {
        return namePDF;
    }

    public void setNamePDF(String namePDF) {
        this.namePDF = namePDF;
    }

    public Timestamp getDateLecture() {
        return dateLecture;
    }

    public void setDateLecture(Timestamp dateLecture) {
        this.dateLecture = dateLecture;
    }

    public String getTimeSound() {
        return timeSound;
    }

    public void setTimeSound(String timeSound) {
        this.timeSound = timeSound;
    }

    public String getId_student() {
        return id_student;
    }

    public void setId_student(String id_student) {
        this.id_student = id_student;
    }
}
