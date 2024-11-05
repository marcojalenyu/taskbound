package com.mobdeve.s17.taskbound;

import java.util.ArrayList;

/**
 * Class for the user session, which stores the current user and the list of users
 */
public class UserSession {

    // Singleton instance of the UserSession
    private static UserSession instance;
    // Attributes
    private User currentUser;
    private final ArrayList<User> userList;
    private final CollectiblesManager collectiblesManager;

    /**
     * Constructor for the UserSession class
     * Initializes the list of users and the collectibles manager
     */
    private UserSession() {
        this.userList = new ArrayList<>();
        this.collectiblesManager = new CollectiblesManager();
    }

    // Methods

    /**
     * Adds a user to the list of users if the user is not already in the list
     * @param newUser - the user to be added
     */
    public void addUser(User newUser) {
        for (User user : userList) {
            if (user.getUserID().equals(newUser.getUserID())) {
                return;
            }
        }
        this.userList.add(newUser);
    }

    /**
     * Clears the current user data
     */
    public void clearUserData() {
        currentUser = null;
    }

    // Getters and setters

    public static UserSession getInstance() {
        // If the instance is null, create a new instance
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public CollectiblesManager getCollectiblesManager() {
        return this.collectiblesManager;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
}