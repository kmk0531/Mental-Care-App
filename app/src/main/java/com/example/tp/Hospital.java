package com.example.maps;


import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class Hospital {
    public String name;
    public String phone;
    public String address;
    public double rating;
    public LatLng latLng;
    public Marker marker; // ✅ 추가

    public Hospital(String name, String phone, String address, double rating, LatLng latLng, Marker marker) {
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.rating = rating;
        this.latLng = latLng;
        this.marker = marker;
    }
}

