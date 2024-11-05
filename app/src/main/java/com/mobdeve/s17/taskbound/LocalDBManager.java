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

public class LocalDBManager extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "taskbound-local.db";
    public static final int DATABASE_VERSION = 1;

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

    // Create table statements
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
                    + USER_COLUMN_DELETED + " INTEGER DEFAULT 0)";

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
                    + "FOREIGN KEY(" + TASK_COLUMN_USER_ID + ") REFERENCES " + USER_TABLE_NAME + "(" + USER_COLUMN_ID + "))";

    public LocalDBManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_TASK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TASK_TABLE_NAME);
        onCreate(db);
    }

    // User table methods
    public void insertUser(User user) {
        if (!(user.getEmail().contains("@") && user.getEmail().contains("."))) {
            return;
        }

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
        long result = db.insert(USER_TABLE_NAME, null, values);
        db.close();

        if (result == -1) {
            return;
        }
    }

    public User getUserWithIdAndPass(String userID, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;
        cursor = db.query(USER_TABLE_NAME, new String[] {USER_COLUMN_ID, USER_COLUMN_EMAIL, USER_COLUMN_USERNAME, USER_COLUMN_PASSWORD, USER_COLUMN_COINS, USER_COLUMN_COLLECTIBLES, USER_COLUMN_SORT_TYPE, USER_COLUMN_LAST_UPDATED, USER_COLUMN_DELETED},
                USER_COLUMN_ID + "=?", new String[] {userID}, null, null, null, null);

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

            // Convert the JSON string to an ArrayList<MyCollectiblesData>
            String collectiblesJson = cursor.getString(5);
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Collectible>>() {}.getType();
            ArrayList<Collectible> collectiblesList = gson.fromJson(collectiblesJson, type);

            User user = new User(userID, userEmail, userName, hashedPassword, coins, collectiblesList, sortType, lastUpdated);

            cursor.close();
            return user;
        }
        return null;
    }

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
        values.put(USER_COLUMN_LAST_UPDATED, user.getLastUpdated());
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

    public void updateUserSortType(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_COLUMN_SORT_TYPE, user.getSortType().toString());
        db.update(USER_TABLE_NAME, values, USER_COLUMN_ID + "=?", new String[] {user.getUserID()});
        db.close();
    }

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
    public void insertTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
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
        db.insert(TASK_TABLE_NAME, null, values);
        db.close();
    }

    public List<Task> getAllTasks(String userId) {
        List<Task> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TASK_TABLE_NAME + " WHERE " + TASK_COLUMN_USER_ID + " = '" + userId + "'";
        Cursor cursor = db.rawQuery(query, null);
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

                    if (idIndex >= 0 && userIdIndex >= 0 && nameIndex >= 0 && contentIndex >= 0 &&
                            deadlineIndex >= 0 && healthIndex >= 0 && coinsIndex >= 0 && monsterIndex >= 0 &&
                            lastUpdatedIndex >= 0 && deletedIndex >= 0) {
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
                                cursor.getInt(deletedIndex) == 1
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
        cursor.close();
        db.close();
        return taskList;
    }

    public List<Task> getAllExistingTasks(String userid) {
        List<Task> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TASK_TABLE_NAME + " WHERE " + TASK_COLUMN_USER_ID + " = '" + userid + "' AND " + TASK_COLUMN_DELETED + " = 0";
        Cursor cursor = db.rawQuery(query, null);
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

                    if (idIndex >= 0 && userIdIndex >= 0 && nameIndex >= 0 && contentIndex >= 0 &&
                            deadlineIndex >= 0 && healthIndex >= 0 && coinsIndex >= 0 && monsterIndex >= 0 &&
                            lastUpdatedIndex >= 0 && deletedIndex >= 0) {
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
                                cursor.getInt(deletedIndex) == 1
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
        cursor.close();
        db.close();
        return taskList;
    }

    public void updateTaskHealth(String taskId, int health) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TASK_COLUMN_HEALTH, health);
        values.put(TASK_COLUMN_LAST_UPDATED, System.currentTimeMillis());
        // UPDATE tasks SET health = ? WHERE id = ? && userid = ?
        String userID = UserSession.getInstance().getCurrentUser().getUserID();
        db.update(TASK_TABLE_NAME, values, TASK_COLUMN_ID + " = ?" + " AND " + TASK_COLUMN_USER_ID + " = ?", new String[] {taskId, userID});
        db.close();
    }

    public void updateTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TASK_COLUMN_NAME, task.getName());
        values.put(TASK_COLUMN_CONTENT, task.getContent());
        values.put(TASK_COLUMN_DEADLINE, task.getDeadlineAsString());
        values.put(TASK_COLUMN_HEALTH, task.getHealth());
        values.put(TASK_COLUMN_COINS, task.getCoins());
        values.put(TASK_COLUMN_MONSTER, task.getMonster());
        values.put(TASK_COLUMN_LAST_UPDATED, task.getLastUpdated());
        // UPDATE tasks SET health = ? WHERE id = ? && userid = ?
        String userID = UserSession.getInstance().getCurrentUser().getUserID();
        db.update(TASK_TABLE_NAME, values, TASK_COLUMN_ID + " = ?" + " AND " + TASK_COLUMN_USER_ID + " = ?", new String[] {task.getId(), userID});
        db.close();
    }

    public void updateTaskInfo(String taskId, String taskName, String taskContent, String taskDeadline) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TASK_COLUMN_NAME, taskName);
        values.put(TASK_COLUMN_CONTENT, taskContent);
        values.put(TASK_COLUMN_DEADLINE, taskDeadline);
        values.put(TASK_COLUMN_LAST_UPDATED, System.currentTimeMillis());
        // UPDATE tasks SET health = ? WHERE id = ? && userid = ?
        String userID = UserSession.getInstance().getCurrentUser().getUserID();
        db.update(TASK_TABLE_NAME, values, TASK_COLUMN_ID + " = ?" + " AND " + TASK_COLUMN_USER_ID + " = ?", new String[] {taskId, userID});
        db.close();
    }

    public void defeatTask(String taskId, int coins) {
        // Soft delete the task
        deleteTask(taskId);
        // UPDATE users SET coins = coins + ? WHERE id = ?
        SQLiteDatabase db = this.getWritableDatabase();
        String userID = UserSession.getInstance().getCurrentUser().getUserID();
        ContentValues values = new ContentValues();
        int currentCoins = UserSession.getInstance().getCurrentUser().getCoins();
        values.put(USER_COLUMN_COINS, currentCoins + coins);
        values.put(USER_COLUMN_LAST_UPDATED, System.currentTimeMillis());
        db.update(USER_TABLE_NAME, values, USER_COLUMN_ID + " = ?", new String[] {String.valueOf(userID)});
        db.close();
    }

    public void deleteTask(String taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TASK_COLUMN_DELETED, 1);
        values.put(TASK_COLUMN_LAST_UPDATED, System.currentTimeMillis());
        // UPDATE tasks SET deleted = 1 WHERE id = ? && userid = ?
        String userID = UserSession.getInstance().getCurrentUser().getUserID();
        db.update(TASK_TABLE_NAME, values, TASK_COLUMN_ID + " = ?" + " AND " + TASK_COLUMN_USER_ID + " = ?", new String[] {taskId, userID});
        db.close();
    }

    public void hardDeleteTask(String taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String userID = UserSession.getInstance().getCurrentUser().getUserID();
        // DELETE FROM tasks WHERE id = ? && userid = ?
        db.delete(TASK_TABLE_NAME, TASK_COLUMN_ID + " = ?" + " AND " + TASK_COLUMN_USER_ID + " = ?", new String[] {taskId, userID});
        db.close();
    }
}
