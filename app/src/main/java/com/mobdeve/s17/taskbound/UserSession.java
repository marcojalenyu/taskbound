package com.mobdeve.s17.taskbound;

public class UserSession {

    private static UserSession instance;
    private User currentUser;

    private UserSession() {}

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

    public void clearUserData() {
        currentUser = null; // Clear only user data, not the instance
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }
}
