package com.mobdeve.s17.taskbound;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;

@IgnoreExtraProperties
public class User {
    private String userID;
    private String email;
    private String userName;
    private String password;
    private int coins;
    private ArrayList<MyCollectiblesData> collectiblesList;
    private long lastUpdated;

    /**
     * Default constructor for User (required by Firebase)
     */
    public User(){
        this.collectiblesList = new ArrayList<>(); // Initialize collectiblesList
    }

    public User(String userID, String email, String userName, String password, int coins, ArrayList<MyCollectiblesData> collectiblesList) {
        this.userID = userID;
        this.email = email;
        this.userName = userName;
        this.password = password;
        this.coins = coins;
        CollectiblesManager collectiblesManager = new CollectiblesManager();
        this.collectiblesList = collectiblesManager.getCollectibles();
    }

    public String getUserID() {
        return this.userID;
    }
    public String getEmail() {
        return this.email;
    }
    public String getUserName() {
        return this.userName;
    }
    public String getPassword() {
        return this.password;
    }
    public int getCoins() {
        return this.coins;
    }
    public ArrayList<MyCollectiblesData> getCollectiblesList() {
        return this.collectiblesList;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setCoins(int coins) {
        this.coins = coins;
    }
    public void setCollectiblesList(ArrayList<MyCollectiblesData> collectiblesList) {
        if (collectiblesList == null) {
            this.collectiblesList = new ArrayList<>();
        }
        this.collectiblesList.clear();
        this.collectiblesList.addAll(collectiblesList);
    }

    public void obtainCollectible(int collectibleID, boolean isObtained) {
        for (MyCollectiblesData collectible : collectiblesList) {
            if (collectible.getCollectibleID() == collectibleID) {
                collectible.setObtained(isObtained);
                break;
            }
        }
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}