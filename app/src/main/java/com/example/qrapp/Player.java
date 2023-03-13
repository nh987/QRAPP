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

    /**
     * gets the email associated with the player
     * @return the player's email
     */
    public String getEmail() {
        return email;
    }

    /**
     * sets the email associated with the player
     * @param email the player email to change
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * gets the string location associated with the player
     * @return the string location of the player
     */
    public String getLocation() {
        return location;
    }

    /**
     * sets the string location associated with the player
     * @param location the player location to change
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * gets the phone number associated with the player
     * @return phone number of the player
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * sets the phone number associated with the player
     * @param phoneNumber the player phone number to change
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * gets the username associated with the player
     * @return the player's username
     */
    public String getUsername() {
        return username;
    }

    /**
     * sets the username associated with the player
     * @param username the player username to change
     */
    public void setUsername(String username) {
        this.username = username;
    }

}

