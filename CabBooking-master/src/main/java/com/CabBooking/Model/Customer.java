package com.CabBooking.Model;

/**
 * Code for the Customer entity
 */
public class Customer {
    private String name;
    private String mobile;
    private final String username;
    private boolean loggedInStatus;
    private int wallet;

    public Customer(String u, String n, String m) {
        username = u;
        name = n;
        mobile = m;
        loggedInStatus = true;
        wallet = 0;
    }

    public void setName(String n) {
        name = n;
    }
    public void setMobile(String m) {
        mobile = m;
    }
    public void changeLoggedInStatus(boolean b) {
        loggedInStatus = b;
    }
    public String getUsername() {
        return username;
    }
    public String getName() {
        return name;
    }
    public String getMobile() {
        return mobile;
    }
    public int getMoney() {
        return wallet;
    }
    public void setMoney(int m) {
        wallet = m;
    }
    public boolean isLoggedIn() {
        return loggedInStatus;
    }
}
