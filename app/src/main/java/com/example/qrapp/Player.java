package com.example.qrapp;


/**
 * Class for representing a 'Player', i.e a user of the app
 */
public class Player
{
    private String username;
    private String email;
    private String location;
    private String phoneNumber;

    /**
     * Constructor for init of new player objects
     * @param usrN  Player "username"
     * @param em Player "email"
     * @param loc Player "location" (specifically in string format for some use cases)
     * @param ph Player "phone number"
     */
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

