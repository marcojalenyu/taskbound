package com.mobdeve.s17.taskbound;

import java.util.ArrayList;

public class UserSession {

    private static UserSession instance;
    private User currentUser;
    private ArrayList<User> userList;
    private CollectiblesManager collectiblesManager;
    private int lastUserID;

    private UserSession() {
        this.userList = new ArrayList<>();
        this.collectiblesManager = new CollectiblesManager();
        this.lastUserID = 0;
    }

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public void addUser(User newUser) {
        for (User user : userList) {
            if (user.getUserID().equals(newUser.getUserID())) {
                return;
            }
        }
        this.userList.add(newUser);
    }

    public void clearUserData() {
        currentUser = null;
    }

    public CollectiblesManager getCollectiblesManager() {
        return this.collectiblesManager;
    }
}