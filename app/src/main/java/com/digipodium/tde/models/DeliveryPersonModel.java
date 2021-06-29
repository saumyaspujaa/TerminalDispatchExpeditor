package com.digipodium.tde.models;

public class DeliveryPersonModel {

    public String fullName;
    public String password;
    public String phone;
    public String email;
    public String city;
    public String aadhar;
    public String address;
    public String age;
    public String transport;
    public String uid;
    public boolean approved;
    public boolean ondelivery;

    public DeliveryPersonModel(String fullName, String phone, String email, String city, String aadhar, String address, String age, String transport, String uid, String password, boolean approved, boolean ondelivery) {
        this.fullName = fullName;
        this.password = password;
        this.phone = phone;
        this.email = email;
        this.city = city;
        this.aadhar = aadhar;
        this.address = address;
        this.age = age;
        this.transport = transport;
        this.uid = uid;
        this.approved = approved;
        this.ondelivery = ondelivery;
    }

    public DeliveryPersonModel() {
    }
}
