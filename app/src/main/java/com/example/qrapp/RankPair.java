package com.example.qrapp;


import java.io.Serializable;

public class RankPair implements Serializable {

    public String PlayerID="";
    public int Number=0;

    public RankPair(String playerID, int number){
        this.PlayerID=playerID;
        this.Number=number;
    }



}
