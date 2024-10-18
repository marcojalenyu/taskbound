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

        // Adding some default users for testing first
        //this.addUser("shaunlim@gmail.com", "shaunlim", "123456");
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
            if (user.getEmail().equals(email) && HashUtil.checkPassword(password, user.getPassword())) {
                currentUser = user;
                return true;
            }
        }
        return false;
    }

    public void addUser(String email, String userName, String password, int coins, ArrayList<MyCollectiblesData> collectiblesList) {
        for (User user : userList) {
            if (user.getEmail().equals(email)) {
                return;
            }
        }
        User user = new User (lastUserID, email, userName, password, coins, collectiblesList);
        this.lastUserID++;
        this.userList.add(user);
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