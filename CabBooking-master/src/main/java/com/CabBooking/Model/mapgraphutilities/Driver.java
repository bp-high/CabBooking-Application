package com.CabBooking.Model.mapgraphutilities;

/**
 * Code for {@code Driver} object
 */
public class Driver {
    private String name;
    private String mobile;
    private String vehicleID;
    private double rating;
    private int location;
    private int ratedTrips; // For database conversions

    public Driver(String n, String m, String v, double r) {
        name  = n; mobile = m;
        vehicleID = v;
        rating = r;
    }

    public double getRating() {
        return rating;
    }

    public void setLocation(int l) {
        location = l;
    }

    public String getName() {
        return name;
    }

    public String getMobile() {
        return mobile;
    }

    public String getVehicleID() {
        return vehicleID;
    }

    public int getRatedTrips() {
        return  ratedTrips;
    }

    public int getLocation() {
        return location;
    }
}

