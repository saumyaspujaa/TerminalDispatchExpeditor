package com.digipodium.tde.models;

import android.os.Parcel;
import android.os.Parcelable;

public class UserModel implements Parcelable {
    public String email;
    public String aadhar;
    public String phone;
    public String city;
    public String fullName;
    public String address;
    public String id;

    public UserModel() {
    }

    public UserModel(String fullName, String email, String aadhar, String phone, String city, String address, String id) {
        this.email = email;
        this.aadhar = aadhar;
        this.phone = phone;
        this.city = city;
        this.fullName = fullName;
        this.address = address;
        this.id = id;
    }


    protected UserModel(Parcel in) {
        email = in.readString();
        aadhar = in.readString();
        phone = in.readString();
        city = in.readString();
        fullName = in.readString();
        address = in.readString();
        id = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(email);
        dest.writeString(aadhar);
        dest.writeString(phone);
        dest.writeString(city);
        dest.writeString(fullName);
        dest.writeString(address);
        dest.writeString(id);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<UserModel> CREATOR = new Parcelable.Creator<UserModel>() {
        @Override
        public UserModel createFromParcel(Parcel in) {
            return new UserModel(in);
        }

        @Override
        public UserModel[] newArray(int size) {
            return new UserModel[size];
        }
    };
}