package com.mobdeve.s17.taskbound;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

/**
 * The LocalDBManager class handles the local database of the application.
 */
public class LocalDBManager extends SQLiteOpenHelper {

    // Database information
    public static final String DATABASE_NAME = "taskbound-local.db";
    public static final int DATABASE_VERSION = 4;

    // User table
    public static final String USER_TABLE_NAME = "users";
    public static final String USER_COLUMN_ID = "id";
    public static final String USER_COLUMN_EMAIL = "email";
    public static final String USER_COLUMN_USERNAME = "username";
    public static final String USER_COLUMN_PASSWORD = "password";
    public static final String USER_COLUMN_COINS = "coins";
    public static final String USER_COLUMN_COLLECTIBLES = "collectibles";
    public static final String USER_COLUMN_SORT_TYPE = "sort_type";
    public static final String USER_COLUMN_LAST_UPDATED = "last_updated";
    public static final String USER_COLUMN_DELETED = "deleted";
    public static final String USER_COLUMN_PICTURE = "profile_picture";

    // Task table
    public static final String TASK_TABLE_NAME = "tasks";
    public static final String TASK_COLUMN_ID = "id";
    public static final String TASK_COLUMN_USER_ID = "userid";
    public static final String TASK_COLUMN_NAME = "name";
    public static final String TASK_COLUMN_CONTENT = "content";
    public static final String TASK_COLUMN_DEADLINE = "deadline";
    public static final String TASK_COLUMN_HEALTH = "health";
    public static final String TASK_COLUMN_COINS = "coins";
    public static final String TASK_COLUMN_MONSTER = "monster";
    public static final String TASK_COLUMN_LAST_UPDATED = "last_updated";
    public static final String TASK_COLUMN_DELETED = "deleted";
    public static final String TASK_COLUMN_PRIORITY = "priority";
    public static final String TASK_COLUMN_CATEGORY = "category";

    // Create table query for user table
    private static final String CREATE_USER_TABLE =
            "CREATE TABLE " + USER_TABLE_NAME + "("
                    + USER_COLUMN_ID + " TEXT,"
                    + USER_COLUMN_EMAIL + " TEXT,"
                    + USER_COLUMN_USERNAME + " TEXT,"
                    + USER_COLUMN_PASSWORD + " TEXT,"
                    + USER_COLUMN_COINS + " INTEGER,"
                    + USER_COLUMN_COLLECTIBLES + " TEXT,"
                    + USER_COLUMN_SORT_TYPE + " TEXT,"
                    + USER_COLUMN_LAST_UPDATED + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
                    + USER_COLUMN_DELETED + " INTEGER DEFAULT 0,"
                    + USER_COLUMN_PICTURE + " INTEGER DEFAULT -1)";

    // Create table query for task table
    private static final String CREATE_TASK_TABLE =
            "CREATE TABLE " + TASK_TABLE_NAME + "("
                    + TASK_COLUMN_ID + " TEXT PRIMARY KEY,"
                    + TASK_COLUMN_USER_ID + " TEXT,"
                    + TASK_COLUMN_NAME + " TEXT,"
                    + TASK_COLUMN_CONTENT + " TEXT,"
                    + TASK_COLUMN_DEADLINE + " TEXT,"
                    + TASK_COLUMN_HEALTH + " INTEGER,"
                    + TASK_COLUMN_COINS + " INTEGER,"
                    + TASK_COLUMN_MONSTER + " TEXT,"
                    + TASK_COLUMN_LAST_UPDATED + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
                    + TASK_COLUMN_DELETED + " INTEGER DEFAULT 0,"
                    + TASK_COLUMN_PRIORITY + " TEXT DEFAULT 'LOW',"
                    + TASK_COLUMN_CATEGORY + " TEXT DEFAULT '',"
                    + "FOREIGN KEY(" + TASK_COLUMN_USER_ID + ") REFERENCES " + USER_TABLE_NAME + "(" + USER_COLUMN_ID + "))";

    // Constructors and lifecycle methods

    /**
     * Constructor for the LocalDBManager class.
     * Initializes the local database.
     * @param context - the context of the activity
     */
    public LocalDBManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Creates the user and task tables in the local database.
     * @param db - the SQLiteDatabase
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_TASK_TABLE);
    }

    /**
     * Upgrades the user and task tables in the local database.
     * @param db - the SQLiteDatabase
     * @param oldVersion - the old version of the database
     * @param newVersion - the new version of the database
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TASK_TABLE_NAME);
        onCreate(db);
    }

    // User table methods

    /**
     * Inserts a user into the local database.
     * @param user - the user to be inserted
     */
    public void insertUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(USER_TABLE_NAME,
                new String[] { USER_COLUMN_EMAIL },
                USER_COLUMN_EMAIL + "=?",
                new String[] { user.getEmail() },
                null, null, null);

        if (cursor != null) {
            if (cursor.getCount() > 0) {
                // Email already exists, so return false
                cursor.close();
                db.close();
                return;
            }
            cursor.close();
        }

        ContentValues values = new ContentValues();
        values.put(USER_COLUMN_ID, user.getUserID());
        values.put(USER_COLUMN_EMAIL, user.getEmail());
        values.put(USER_COLUMN_USERNAME, user.getUserName());
        values.put(USER_COLUMN_PASSWORD, user.getPassword());
        values.put(USER_COLUMN_COINS, user.getCoins());
        Gson gson = new Gson();
        String collectiblesJson = gson.toJson(user.getCollectiblesList());
        values.put(USER_COLUMN_COLLECTIBLES, collectiblesJson);
        values.put(USER_COLUMN_LAST_UPDATED, user.getLastUpdated());
        values.put(USER_COLUMN_SORT_TYPE, user.getSortType().toString());
        values.put(USER_COLUMN_DELETED, user.isDeleted() ? 1 : 0);
        values.put(USER_COLUMN_PICTURE, user.getPicture());
        long result = db.insert(USER_TABLE_NAME, null, values);
        db.close();

        if (result == -1) {
            return;
        }
    }


    // For debugging
    public void logAllUserIds() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(USER_TABLE_NAME, new String[]{USER_COLUMN_ID}, null, null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String userId = cursor.getString(0); // Column 0 is USER_COLUMN_ID
                    Log.d("LocalDBManager", "User ID: " + userId);
                } while (cursor.moveToNext());
            } else {
                Log.d("LocalDBManager", "No users found in the table.");
            }
        } catch (Exception e) {
            Log.e("LocalDBManager", "Error retrieving user IDs: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


    /**
     * Gets a user from the local database using the user ID and password.
     * @param userID - the ID of the user
     * @param password - the password of the user
     * @return the user with the given ID and password
     */
    public User getUserWithIdAndPass(String userID, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;
        cursor = db.query(USER_TABLE_NAME, new String[] {USER_COLUMN_ID, USER_COLUMN_EMAIL, USER_COLUMN_USERNAME, USER_COLUMN_PASSWORD, USER_COLUMN_COINS, USER_COLUMN_COLLECTIBLES, USER_COLUMN_SORT_TYPE, USER_COLUMN_LAST_UPDATED, USER_COLUMN_DELETED, USER_COLUMN_PICTURE},
                USER_COLUMN_ID + "=?", new String[] {userID}, null, null, null, null);
        // logAllUserIds();

        if (cursor != null && cursor.moveToFirst()) {
            String hashedPassword = cursor.getString(3);

            try {
                if (!password.equals(hashedPassword)) {
                    cursor.close();
                    return null;
                }
            } catch (Exception e) {
                Log.e("LoginReal", e + "");
                return null;
            }

            boolean isDeleted = cursor.getInt(8) == 1;
            // Check if the user is deleted
            if (isDeleted) {
                cursor.close();
                return null;
            }

            String userEmail = cursor.getString(1);
            String userName = cursor.getString(2);
            int coins = cursor.getInt(4);
            String sortType = cursor.getString(6);
            long lastUpdated = cursor.getLong(7);
            int picture = cursor.getInt(9);

            // Convert the JSON string to an ArrayList<MyCollectiblesData>
            String collectiblesJson = cursor.getString(5);
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Collectible>>() {}.getType();
            ArrayList<Collectible> collectiblesList = gson.fromJson(collectiblesJson, type);

            User user = new User(userID, userEmail, userName, hashedPassword, coins, collectiblesList, sortType, lastUpdated, picture);

            cursor.close();
            return user;
        }
        return null;
    }

    /**
     * Gets a user from the local database using the user ID.
     * @param user - the user to be inserted
     */
    public void updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_COLUMN_EMAIL, user.getEmail());
        values.put(USER_COLUMN_USERNAME, user.getUserName());
        values.put(USER_COLUMN_PASSWORD, user.getPassword());
        values.put(USER_COLUMN_COINS, user.getCoins());
        Gson gson = new Gson();
        String collectiblesJson = gson.toJson(user.getCollectiblesList());
        values.put(USER_COLUMN_COLLECTIBLES, collectiblesJson);
        values.put(USER_COLUMN_LAST_UPDATED, System.currentTimeMillis());
        values.put(USER_COLUMN_PICTURE, user.getPicture());
        db.update(USER_TABLE_NAME, values, USER_COLUMN_ID + "=?", new String[] {user.getUserID()});
        db.close();
    }

    /**
     * Deletes a user from the local database (hard delete).
     * @param userID
     */
    public void hardDeleteUser(String userID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        // DELETE FROM users WHERE id = ?
        db.delete(USER_TABLE_NAME, USER_COLUMN_ID + "=?", new String[] {userID});
        db.close();
    }

    /**
     * Soft deletes a user from the local database.
     * @param user - the user to be deleted
     */
    public void updateUserSortType(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_COLUMN_SORT_TYPE, user.getSortType().toString());
        db.update(USER_TABLE_NAME, values, USER_COLUMN_ID + "=?", new String[] {user.getUserID()});
        db.close();
    }

    /**
     * Soft deletes a user from the local database.
     * @param userID - the ID of the user to be deleted
     * @param coins - the coins to be deducted from the user
     */
    public void deductUserCoins(String userID, int coins) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(USER_TABLE_NAME, new String[] {USER_COLUMN_COINS},
                USER_COLUMN_ID + "=?", new String[] {userID}, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int coinsColumnIndex = cursor.getColumnIndex(USER_COLUMN_COINS);
            if (coinsColumnIndex >= 0) {
                int currentCoins = cursor.getInt(coinsColumnIndex);
                ContentValues values = new ContentValues();
                values.put(USER_COLUMN_COINS, currentCoins - coins);
                values.put(USER_COLUMN_LAST_UPDATED, System.currentTimeMillis());
                db.update(USER_TABLE_NAME, values, USER_COLUMN_ID + "=?", new String[] {userID});
            }
            cursor.close();
        }
        db.close();
    }

    /**
     * Adds a collectible to the user in the local database.
     * @param userID - the ID of the user
     * @param collectibleID - the ID of the collectible to be added
     */
    public void addCollectibleToUser(String userID, int collectibleID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(USER_TABLE_NAME, new String[] {USER_COLUMN_COLLECTIBLES},
                USER_COLUMN_ID + "=?", new String[] {userID}, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(USER_COLUMN_COLLECTIBLES);
            if (columnIndex != -1) {
                String collectiblesJson = cursor.getString(columnIndex);
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<Collectible>>() {}.getType();
                ArrayList<Collectible> collectiblesList = gson.fromJson(collectiblesJson, type);

                if (collectiblesList != null) {
                    for (Collectible collectible : collectiblesList) {
                        if (collectible.getCollectibleID() == collectibleID) {
                            collectible.setObtained(true);
                            break;
                        }
                    }
                    String updatedCollectiblesJson = gson.toJson(collectiblesList);

                    ContentValues values = new ContentValues();
                    values.put(USER_COLUMN_COLLECTIBLES, updatedCollectiblesJson);

                    db.update(USER_TABLE_NAME, values, USER_COLUMN_ID + "=?", new String[] {userID});
                }
            }
            cursor.close();
        }
        db.close();
    }

    /**
     * Gets the coins of the user from the local database.
     * @param userID - the ID of the user
     * @return the coins of the user
     */
    public int getUserCoins(String userID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(USER_TABLE_NAME, new String[] {USER_COLUMN_COINS},
                USER_COLUMN_ID + "=?", new String[] {userID}, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int coinsColumnIndex = cursor.getColumnIndex(USER_COLUMN_COINS);
            if (coinsColumnIndex >= 0) {
                int coins = cursor.getInt(coinsColumnIndex);
                cursor.close();
                db.close();
                return coins;
            }
        }
        return -1;
    }

    private int checkValidCollectible(String userID, int collectibleID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(USER_TABLE_NAME,
                new String[] {USER_COLUMN_COLLECTIBLES},
                USER_COLUMN_ID + "=?",
                new String[] {userID},
                null, null, null, null);

        int imageID = R.drawable.collectible_0;
        ArrayList<Collectible> collectiblesList = null;

        if (cursor != null && cursor.moveToFirst()) {
            int collectiblesColumnIndex = cursor.getColumnIndex(USER_COLUMN_COLLECTIBLES);
            if (collectiblesColumnIndex == -1) {
                return imageID;
            }
            String collectiblesJson = cursor.getString(collectiblesColumnIndex);
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Collectible>>() {}.getType();
            collectiblesList = gson.fromJson(collectiblesJson, type);
            cursor.close();
            db.close();
        }

        if (collectiblesList == null) {
            return imageID;
        }

        for (int i = 0; i < collectiblesList.size(); i++) {
            if (collectiblesList.get(i).getCollectibleID() == collectibleID) {
                return collectiblesList.get(i).getCollectibleImage();
            }
        }

        return imageID;
    }

    /**
     * Gets the collectible id of the user set as the profile pic from the local database.
     * @param userID - the ID of the user
     * @return the id of the collectible set as the profile picture
     */
    public Integer getUserPicture(String userID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(USER_TABLE_NAME,
                new String[] {USER_COLUMN_PICTURE},
                USER_COLUMN_ID + "=?",
                new String[] {userID},
                null, null, null, null);

        int imageID = R.drawable.collectible_0;

        if (cursor != null && cursor.moveToFirst()) {
            int pictureColumnIndex = cursor.getColumnIndex(USER_COLUMN_PICTURE);
            if (pictureColumnIndex < 0) {
                return imageID;
            }
            int picture = cursor.getInt(pictureColumnIndex);
            imageID = checkValidCollectible(userID, picture);
        }

        if (cursor != null) {
            cursor.close();
        }
        db.close();

        return imageID;
    }

    /**
     * Soft deletes a user from the local database.
     * @param userID - the ID of the user to be deleted
     * @param collectibleID - the ID of the new profile picture
     */
    public void updateUserPicture(String userID, int collectibleID) {
        if (checkValidCollectible(userID, collectibleID) == R.drawable.collectible_0) {
            return;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_COLUMN_PICTURE, collectibleID);
        values.put(USER_COLUMN_LAST_UPDATED, System.currentTimeMillis());
        db.update(USER_TABLE_NAME, values, USER_COLUMN_ID + "=?", new String[] {userID});
        db.close();
    }


    /**
     * Gets the collectibles of the user from the local database.
     * @param userID - the ID of the user
     * @return a list of collectibles of the user
     */
    public ArrayList<Collectible> getUserCollectibles(String userID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(USER_TABLE_NAME, new String[] {USER_COLUMN_COLLECTIBLES},
                USER_COLUMN_ID + "=?", new String[] {userID}, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(USER_COLUMN_COLLECTIBLES);
            if (columnIndex != -1) {
                String collectiblesJson = cursor.getString(columnIndex);
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<Collectible>>() {}.getType();
                ArrayList<Collectible> collectiblesList = gson.fromJson(collectiblesJson, type);
                cursor.close();
                db.close();
                return collectiblesList;
            }
        }
        return null;
    }

    // Task table methods

    /**
     * Inserts a task into the local database.
     * @param task - the task to be inserted
     */
    public void insertTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Set values of id, userid, name, content, deadline, health, coins, monster, last_updated, and deleted
        ContentValues values = new ContentValues();
        values.put(TASK_COLUMN_ID, task.getId());
        values.put(TASK_COLUMN_USER_ID, task.getUserID());
        values.put(TASK_COLUMN_NAME, task.getName());
        values.put(TASK_COLUMN_CONTENT, task.getContent());
        values.put(TASK_COLUMN_DEADLINE, task.getDeadlineAsString());
        values.put(TASK_COLUMN_HEALTH, task.getHealth());
        values.put(TASK_COLUMN_COINS, task.getCoins());
        values.put(TASK_COLUMN_MONSTER, task.getMonster());
        values.put(TASK_COLUMN_LAST_UPDATED, task.getLastUpdated());
        values.put(TASK_COLUMN_DELETED, task.isDeleted() ? 1 : 0);
        values.put(TASK_COLUMN_PRIORITY, task.getPriority());
        values.put(TASK_COLUMN_CATEGORY, task.getCategory());
        // INSERT INTO tasks (id, userid, name, content, deadline, health, coins, monster, last_updated, deleted) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        db.insert(TASK_TABLE_NAME, null, values);
        db.close();
    }

    private List<Task> getTasksFromCursor(Cursor cursor) {
        List<Task> taskList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                try {
                    int idIndex = cursor.getColumnIndex(TASK_COLUMN_ID);
                    int userIdIndex = cursor.getColumnIndex(TASK_COLUMN_USER_ID);
                    int nameIndex = cursor.getColumnIndex(TASK_COLUMN_NAME);
                    int contentIndex = cursor.getColumnIndex(TASK_COLUMN_CONTENT);
                    int deadlineIndex = cursor.getColumnIndex(TASK_COLUMN_DEADLINE);
                    int healthIndex = cursor.getColumnIndex(TASK_COLUMN_HEALTH);
                    int coinsIndex = cursor.getColumnIndex(TASK_COLUMN_COINS);
                    int monsterIndex = cursor.getColumnIndex(TASK_COLUMN_MONSTER);
                    int lastUpdatedIndex = cursor.getColumnIndex(TASK_COLUMN_LAST_UPDATED);
                    int deletedIndex = cursor.getColumnIndex(TASK_COLUMN_DELETED);
                    int priorityIndex = cursor.getColumnIndex(TASK_COLUMN_PRIORITY);
                    int categoryIndex = cursor.getColumnIndex(TASK_COLUMN_CATEGORY);

                    if (idIndex >= 0 && userIdIndex >= 0 && nameIndex >= 0 && contentIndex >= 0 &&
                            deadlineIndex >= 0 && healthIndex >= 0 && coinsIndex >= 0 && monsterIndex >= 0 &&
                            lastUpdatedIndex >= 0 && deletedIndex >= 0 && priorityIndex >= 0 && categoryIndex >= 0) {
                        Task task = new Task(
                                cursor.getString(idIndex),
                                cursor.getString(userIdIndex),
                                cursor.getString(nameIndex),
                                cursor.getString(contentIndex),
                                cursor.getString(deadlineIndex),
                                cursor.getInt(healthIndex),
                                cursor.getInt(coinsIndex),
                                cursor.getString(monsterIndex),
                                cursor.getLong(lastUpdatedIndex),
                                cursor.getInt(deletedIndex) == 1,
                                Priority.valueOf(cursor.getString(priorityIndex)),
                                cursor.getString(categoryIndex)
                        );
                        taskList.add(task);
                    } else {
                        throw new IllegalArgumentException("Invalid column index");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }
        return taskList;
    }

    /**
     * Gets all tasks of the user from the local database.
     * @param userId - the ID of the user
     * @return a list of tasks of the user (including deleted tasks)
     */
    public List<Task> getAllTasks(String userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TASK_TABLE_NAME + " WHERE " + TASK_COLUMN_USER_ID + " = '" + userId + "'";
        Cursor cursor = db.rawQuery(query, null);
        List<Task> taskList = getTasksFromCursor(cursor);
        cursor.close();
        db.close();
        return taskList;
    }

    /**
     * Gets all existing tasks of the user from the local database.
     * @param userid - the ID of the user
     * @return a list of tasks of the user which are not deleted
     */
    public List<Task> getAllExistingTasks(String userid) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TASK_TABLE_NAME + " WHERE " + TASK_COLUMN_USER_ID + " = '" + userid + "' AND " + TASK_COLUMN_DELETED + " = 0";
        Cursor cursor = db.rawQuery(query, null);
        List<Task> taskList = getTasksFromCursor(cursor);
        cursor.close();
        db.close();
        return taskList;
    }

    /**
     * Updates the information of a task in the local database.
     * @param task - the task to be updated
     */
    public void updateTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        String userID = UserSession.getInstance().getCurrentUser().getUserID();
        // Set values of name, content, deadline, health, coins, monster, and last_updated
        ContentValues values = new ContentValues();
        values.put(TASK_COLUMN_NAME, task.getName());
        values.put(TASK_COLUMN_CONTENT, task.getContent());
        values.put(TASK_COLUMN_DEADLINE, task.getDeadlineAsString());
        values.put(TASK_COLUMN_HEALTH, task.getHealth());
        values.put(TASK_COLUMN_COINS, task.getCoins());
        values.put(TASK_COLUMN_MONSTER, task.getMonster());
        values.put(TASK_COLUMN_LAST_UPDATED, task.getLastUpdated());
        values.put(TASK_COLUMN_PRIORITY, task.getPriority().toString());
        values.put(TASK_COLUMN_CATEGORY, task.getCategory());
        // UPDATE tasks SET name = ?, content = ?, deadline = ?, health = ?, coins = ?, monster = ? WHERE id = ? && userid = ?
        db.update(TASK_TABLE_NAME, values, TASK_COLUMN_ID + " = ?" + " AND " + TASK_COLUMN_USER_ID + " = ?", new String[] {task.getId(), userID});
        db.close();
    }

    /**
     * Updates the information of a task in the local database.
     * @param taskId - the ID of the task to be updated
     * @param taskName - the new name of the task
     * @param taskContent - the new content of the task
     * @param taskDeadline - the new deadline of the task
     */
    public void updateTaskInfo(String taskId, String taskName, String taskContent, String taskDeadline) {
        SQLiteDatabase db = this.getWritableDatabase();
        String userID = UserSession.getInstance().getCurrentUser().getUserID();
        // Set values of name, content, deadline, and last_updated
        ContentValues values = new ContentValues();
        values.put(TASK_COLUMN_NAME, taskName);
        values.put(TASK_COLUMN_CONTENT, taskContent);
        values.put(TASK_COLUMN_DEADLINE, taskDeadline);
        values.put(TASK_COLUMN_LAST_UPDATED, System.currentTimeMillis());
        // UPDATE tasks SET name = ?, content = ?, deadline = ? WHERE id = ? && userid = ?
        db.update(TASK_TABLE_NAME, values, TASK_COLUMN_ID + " = ?" + " AND " + TASK_COLUMN_USER_ID + " = ?", new String[] {taskId, userID});
        db.close();
    }

    /**
     * Updates the health of a task in the local database.
     * @param taskId - the ID of the task to be updated
     * @param health - the new health of the task
     */
    public void updateTaskHealth(String taskId, int health) {
        SQLiteDatabase db = this.getWritableDatabase();
        String userID = UserSession.getInstance().getCurrentUser().getUserID();
        // Set values of health and last_updated
        ContentValues values = new ContentValues();
        values.put(TASK_COLUMN_HEALTH, health);
        values.put(TASK_COLUMN_LAST_UPDATED, System.currentTimeMillis());
        // UPDATE tasks SET health = ? WHERE id = ? && userid = ?
        db.update(TASK_TABLE_NAME, values, TASK_COLUMN_ID + " = ?" + " AND " + TASK_COLUMN_USER_ID + " = ?", new String[] {taskId, userID});
        db.close();
    }

    /**
     * Defeats a task by soft deleting it and updating the user's coins.
     * @param taskId - the ID of the task to be defeated
     * @param coins - the coins to be added to the user
     */
    public void defeatTask(String taskId, int coins) {
        // Soft delete the task
        deleteTask(taskId);
        // Get the user's current coins
        String userID = UserSession.getInstance().getCurrentUser().getUserID();
        int currentCoins = getUserCoins(userID);
        // Set values of coins and last_updated
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_COLUMN_COINS, currentCoins + coins);
        values.put(USER_COLUMN_LAST_UPDATED, System.currentTimeMillis());
        // UPDATE users SET coins = coins + ? WHERE id = ?
        db.update(USER_TABLE_NAME, values, USER_COLUMN_ID + " = ?", new String[] {String.valueOf(userID)});
        db.close();
    }

    /**
     * Soft deletes a task from the local database.
     * @param taskId - the ID of the task to be deleted
     */
    public void deleteTask(String taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String userID = UserSession.getInstance().getCurrentUser().getUserID();
        // Set values of deleted and last_updated
        ContentValues values = new ContentValues();
        values.put(TASK_COLUMN_DELETED, 1);
        values.put(TASK_COLUMN_LAST_UPDATED, System.currentTimeMillis());
        // UPDATE tasks SET deleted = 1 WHERE id = ? && userid = ?
        db.update(TASK_TABLE_NAME, values, TASK_COLUMN_ID + " = ?" + " AND " + TASK_COLUMN_USER_ID + " = ?", new String[] {taskId, userID});
        db.close();
    }

    /**
     * Hard deletes a task from the local database.
     * @param taskId - the ID of the task to be deleted
     */
    public void hardDeleteTask(String taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String userID = UserSession.getInstance().getCurrentUser().getUserID();
        // DELETE FROM tasks WHERE id = ? && userid = ?
        db.delete(TASK_TABLE_NAME, TASK_COLUMN_ID + " = ?" + " AND " + TASK_COLUMN_USER_ID + " = ?", new String[] {taskId, userID});
        db.close();
    }
}
