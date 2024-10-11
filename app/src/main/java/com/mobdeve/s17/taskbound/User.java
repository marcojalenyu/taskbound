package com.mobdeve.s17.taskbound;

import java.util.ArrayList;

public class User {
    private final String email;
    private String userName;
    private String password;
    private int coins;
    private final CollectiblesManager collectiblesManager;
    private final ArrayList<MyCollectiblesData> collectiblesList;

    public User(String email, String userName, String password, int coins, ArrayList<MyCollectiblesData> collectiblesList) {
        this.email = email;
        this.userName = userName;
        this.password = password;
        this.coins = coins;
        this.collectiblesManager = new CollectiblesManager();
        this.collectiblesList = collectiblesList;
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

    public CollectiblesManager getCollectiblesManager() {
        return this.collectiblesManager;
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

    public void obtainCollectible(int collectibleID, boolean isObtained) {
        for (MyCollectiblesData collectible : collectiblesList) {
            if (collectible.getCollectibleID() == collectibleID) {
                collectible.setObtained(true);
                break;
            }
        }
    }
}