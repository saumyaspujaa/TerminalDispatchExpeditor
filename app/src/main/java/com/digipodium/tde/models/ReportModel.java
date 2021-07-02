package com.digipodium.tde.models;

public class ReportModel {

    public String subject;
    public String detail;
    public String uid;

    public ReportModel(String subject, String detail, String uid) {
        this.uid = uid;
        this.subject = subject;
        this.detail = detail;
    }

    public ReportModel() {
    }
}
