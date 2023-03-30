package com.example.qrapp;

import java.io.Serializable;

/**
 * The RankTriple class is used to store rank information for the "Score" and "Local" leaderboards
 */
public class RankTriple implements Serializable {

    public String PlayerName="";
    public String QRcFace = "";
    public int QRcPoints = 0;

    /**
     * Constructor for the RankTriple object. a player name, QR Code icon and points of the QR Code
     * is required
     * @param playerName
     * @param qrcFace
     * @param qrcPoints
     */
    public RankTriple(String playerName, String qrcFace, int qrcPoints){
        this.PlayerName=playerName;
        this.QRcFace = qrcFace;
        this.QRcPoints = qrcPoints;
    }

}
