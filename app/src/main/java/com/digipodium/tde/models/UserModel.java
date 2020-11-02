package com.digipodium.tde.models;

public class UserModel {
    public String email, aadhar, phone, city, fullName, address, id;

    public UserModel(String fullName, String email, String aadhar, String phone, String city, String address, String id) {
        this.email = email;
        this.aadhar = aadhar;
        this.phone = phone;
        this.city = city;
        this.fullName = fullName;
        this.address = address;
        this.id = id;
    }

}
