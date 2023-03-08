package com.example.qrapp;

public class Player
{
    private String username;
    private String email;
    private String location;
    private String phoneNumber;

    public Player(String usrN, String em, String loc, String ph) {
        this.username = usrN;
        this.email = em;
        this.location = loc;
        this.phoneNumber = ph;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}

