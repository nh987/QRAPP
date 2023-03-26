package com.example.qrapp;


import java.io.Serializable;

/**
 * The RankPair class is used to store rank information for the "Count" and "Sum" leaderboards
 */
public class RankPair implements Serializable {

    public String PlayerName="";
    public int Number=0;

    /**
     * Constructor for the RankPair object. A player name and a number(sum or count) is required.
     * @param playerName
     * @param number
     */
    public RankPair(String playerName, int number){
        this.PlayerName=playerName;
        this.Number=number;
    }



}
