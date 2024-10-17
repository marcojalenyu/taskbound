package com.mobdeve.s17.taskbound;

import java.util.ArrayList;

public class User {
    private int userID;
    private final String email;
    private String userName;
    private String password;
    private int coins;
    private final ArrayList<MyCollectiblesData> collectiblesList;

    public User(int userID, String email, String userName, String password, int coins, ArrayList<MyCollectiblesData> collectiblesList) {
        this.userID = userID;
        this.email = email;
        this.userName = userName;
        this.password = password;
        this.coins = coins;
        CollectiblesManager collectiblesManager = new CollectiblesManager();
        this.collectiblesList = collectiblesManager.getCollectibles();
    }

    public int getUserID() {
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

    public void setUserID(int userID) {
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
}