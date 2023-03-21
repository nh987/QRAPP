package com.example.qrapp;

import com.google.android.material.color.utilities.Score;

public class RankTriple {

    public String PlayerID="";
    public Long Score = 0L;
    public String Name = "";

    public RankTriple(String playerID, Long score, String name){
        this.PlayerID=playerID;
        this.Score=score;
        this.Name=name;
    }

}
