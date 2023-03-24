package com.example.qrapp;

import java.io.Serializable;

public class RankTriple implements Serializable {

    public String PlayerID="";
    public String QRcFace = "";
    public int QRcPoints = 0;

    public RankTriple(String playerID, String qrcFace, int qrcPoints){
        this.PlayerID=playerID;
        this.QRcFace = qrcFace;
        this.QRcPoints = qrcPoints;
    }

}
