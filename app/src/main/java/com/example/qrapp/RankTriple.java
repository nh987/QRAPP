package com.example.qrapp;

public class RankTriple {

    public String PlayerID="";
    public String QRcFace = "";
    public Long QRcPoints = 0L;

    public RankTriple(String playerID, String qrcFace, Long qrcPoints){
        this.PlayerID=playerID;
        this.QRcFace = qrcFace;
        this.QRcPoints = qrcPoints;
    }

}
