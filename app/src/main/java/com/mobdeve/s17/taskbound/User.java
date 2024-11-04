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
    private SortType sortType;
    private long lastUpdated;
    private boolean deleted;

    /**
     * Default constructor for User (required by Firebase)
     */
    public User(){
        this.collectiblesList = new ArrayList<>(); // Initialize collectiblesList
    }

    /**
     * Constructor for User (for a new user)
     * @param userID - the user's unique ID
     * @param email - the user's email
     * @param userName - the user's username
     * @param password - the user's password (hashed)
     */
    public User(String userID, String email, String userName, String password) {
        this.userID = userID;
        this.email = email;
        this.userName = userName;
        this.password = password;
        // Default values (150 coins, empty collectiblesList, default sortType, current time)
        this.coins = 150;
        CollectiblesManager collectiblesManager = new CollectiblesManager();
        this.collectiblesList = collectiblesManager.getCollectibles();
        this.sortType = SortType.DEFAULT;
        this.lastUpdated = System.currentTimeMillis();
        this.deleted = false;
    }

    /**
     * Constructor for User (for an existing user)
     * @param userID - the user's unique ID
     * @param email - the user's email
     * @param userName - the user's username
     * @param password - the user's password (hashed)
     * @param coins - the user's coins
     * @param collectiblesList - the user's collectibles list
     * @param sortType - the user's sort type
     * @param lastUpdated - the last time the user's data was updated
     */
    public User(String userID, String email, String userName, String password, int coins, ArrayList<MyCollectiblesData> collectiblesList, String sortType, long lastUpdated) {
        this.userID = userID;
        this.email = email;
        this.userName = userName;
        this.password = password;
        this.coins = coins;
        this.collectiblesList = collectiblesList;
        this.sortType = SortType.valueOf(sortType);
        this.lastUpdated = lastUpdated;
        this.deleted = false;
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
    public SortType getSortType() {
        return this.sortType;
    }
    public long getLastUpdated() {
        return this.lastUpdated;
    }
    public boolean isDeleted() {
        return this.deleted;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
    public void setCoins(int coins) {
        this.coins = coins;
    }
    public void setCollectiblesList(ArrayList<MyCollectiblesData> collectiblesList) {
        if (collectiblesList == null) {
            this.collectiblesList = new ArrayList<>();
        }
        this.collectiblesList.clear();
        assert collectiblesList != null;
        this.collectiblesList.addAll(collectiblesList);
    }
    public void setSortType(SortType sortType) {
        this.sortType = sortType;
    }
    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
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