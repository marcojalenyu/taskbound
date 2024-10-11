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

    public boolean setCurrentUser(String email, String password) {
        for (User user : userList) {
            if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                currentUser = user;
                return true;
            }
        }
        return false;
    }

    public boolean addUser (String email, String userName, String password) {
        for (User user : userList) {
            if (user.getEmail().equals(email)) {
                return false;
            }
        }
        User user = new User (lastUserID, email, userName, password, collectiblesManager.getCollectibles());
        this.lastUserID++;
        this.userList.add(user);
        return true;
    }

    public void clearUserData() {
        currentUser = null;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public CollectiblesManager getCollectiblesManager() {
        return this.collectiblesManager;
    }
}
