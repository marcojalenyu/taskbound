package com.mobdeve.s17.taskbound;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;

/**
 * Class for the user, which stores the user's information
 */
@IgnoreExtraProperties
public class User {

    // Attributes
    private String userID;
    private String email;
    private String userName;
    private String password;
    private int coins;
    private int picture;
    private ArrayList<Collectible> collectiblesList;
    private SortOrder sortOrder;
    private long lastUpdated;
    private boolean deleted;

    // Constructors

    /**
     * Default constructor for User (for Firebase)
     */
    public User(){
        this.collectiblesList = new ArrayList<>();
    }

    /**
     * Constructor for User (for a new user)
     * @param userID - the user's unique ID
     * @param email - the user's email
     * @param userName - the user's username
     * @param password - the user's password (hashed)
     */
    public User(String userID,
                String email,
                String userName,
                String password) {
        this.userID = userID;
        this.email = email;
        this.userName = userName;
        this.password = password;
        // Default values (150 coins, empty collectiblesList, default sortOrder, current time)
        this.coins = 150;
        CollectiblesManager collectiblesManager = new CollectiblesManager();
        this.collectiblesList = collectiblesManager.getCollectibles();
        this.sortOrder = SortOrder.DEFAULT;
        this.lastUpdated = System.currentTimeMillis();
        this.deleted = false;
        this.picture = -1;
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
     * @param picture - the id of the collectible set as the profile picture
     */
    public User(String userID,
                String email,
                String userName,
                String password,
                int coins,
                ArrayList<Collectible> collectiblesList,
                String sortType,
                long lastUpdated,
                int picture) {
        this.userID = userID;
        this.email = email;
        this.userName = userName;
        this.password = password;
        this.coins = coins;
        this.collectiblesList = collectiblesList;
        this.sortOrder = SortOrder.valueOf(sortType);
        this.lastUpdated = lastUpdated;
        this.deleted = false;
        this.picture = picture;
    }

    // Getters and setters

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

    public int getPicture() { return this.picture; }

    public ArrayList<Collectible> getCollectiblesList() {
        return this.collectiblesList;
    }

    public SortOrder getSortOrder() {
        return this.sortOrder;
    }

    public long getLastUpdated() {
        return this.lastUpdated;
    }

    public boolean isDeleted() {
        return this.deleted;
    }

    public void setUserName(String userName) { this.userName = userName; }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public void setSortType(SortOrder sortOrder) {
        this.sortOrder = sortOrder;
    }

    public void setPicture(int pic) { this.picture = pic; }
}