package com.mobdeve.s17.taskbound;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

public class UserDBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "taskbound4.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "users";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_COINS = "coins";
    public static final String COLUMN_COLLECTIBLES = "collectibles";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_EMAIL + " TEXT,"
                    + COLUMN_USERNAME + " TEXT,"
                    + COLUMN_PASSWORD + " TEXT,"
                    + COLUMN_COINS + " INTEGER,"
                    + COLUMN_COLLECTIBLES + " TEXT " + ")";

    public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public UserDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }

    public boolean insertUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, user.getEmail());
        values.put(COLUMN_USERNAME, user.getUserName());
        values.put(COLUMN_PASSWORD, user.getPassword());
        values.put(COLUMN_COINS, user.getCoins());
        values.put(COLUMN_COLLECTIBLES, user.getCollectiblesList().toString());
        long result = db.insert(TABLE_NAME, null, values);
        db.close();

        if (result == -1) {
            return false; // Insert failed
        } else {
            return true; // Insert succeeded
        }
    }

    public User getUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[] {COLUMN_ID, COLUMN_EMAIL, COLUMN_USERNAME, COLUMN_PASSWORD, COLUMN_COINS, COLUMN_COLLECTIBLES},
                COLUMN_EMAIL + "=? AND " + COLUMN_PASSWORD + "=?", new String[] {email, password}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int userID = cursor.getInt(0);
            String userEmail = cursor.getString(1);
            String userName = cursor.getString(2);
            String userPassword = cursor.getString(3);
            int coins = cursor.getInt(4);

            // Convert the JSON string to an ArrayList<MyCollectiblesData>
            String collectiblesJson = cursor.getString(5);
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<MyCollectiblesData>>() {}.getType();
            ArrayList<MyCollectiblesData> collectiblesList = gson.fromJson(collectiblesJson, type);

            User user = new User(userID, userEmail, userName, userPassword, coins, collectiblesList);

            cursor.close();
            return user;
        }
        return null;
    }

    public User addUserCoins(String email, int coins) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[] {COLUMN_ID, COLUMN_EMAIL, COLUMN_USERNAME, COLUMN_PASSWORD, COLUMN_COINS, COLUMN_COLLECTIBLES},
                COLUMN_EMAIL + "=?", new String[] {email}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int userID = cursor.getInt(0);
            String userEmail = cursor.getString(1);
            String userName = cursor.getString(2);
            String userPassword = cursor.getString(3);
            int userCoins = cursor.getInt(4) + coins;

            // Convert the JSON string to an ArrayList<MyCollectiblesData>
            String collectiblesJson = cursor.getString(5);
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<MyCollectiblesData>>() {}.getType();
            ArrayList<MyCollectiblesData> collectiblesList = gson.fromJson(collectiblesJson, type);

            User user = new User(userID, userEmail, userName, userPassword, userCoins, collectiblesList);

            cursor.close();
            return user;
        }
        return null;
    }

    public User deductUserCoins(String email, int coins) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[] {COLUMN_ID, COLUMN_EMAIL, COLUMN_USERNAME, COLUMN_PASSWORD, COLUMN_COINS, COLUMN_COLLECTIBLES},
                COLUMN_EMAIL + "=?", new String[] {email}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int userID = cursor.getInt(0);
            String userEmail = cursor.getString(1);
            String userName = cursor.getString(2);
            String userPassword = cursor.getString(3);
            int userCoins = cursor.getInt(4) - coins;

            // Convert the JSON string to an ArrayList<MyCollectiblesData>
            String collectiblesJson = cursor.getString(5);
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<MyCollectiblesData>>() {}.getType();
            ArrayList<MyCollectiblesData> collectiblesList = gson.fromJson(collectiblesJson, type);

            User user = new User(userID, userEmail, userName, userPassword, userCoins, collectiblesList);

            cursor.close();
            return user;
        }
        return null;
    }


}
