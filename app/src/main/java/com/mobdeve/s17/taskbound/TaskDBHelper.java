package com.mobdeve.s17.taskbound;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.Date;

public class TaskDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "taskbound3.db";
    public static final int DATABASE_VERSION = 2;
    public static final String TABLE_NAME = "tasks";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_DEADLINE = "deadline";
    public static final String COLUMN_HEALTH = "health";
    public static final String COLUMN_COINS = "coins";
    public static final String COLUMN_MONSTER = "monster";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_NAME + " TEXT,"
                    + COLUMN_CONTENT + " TEXT,"
                    + COLUMN_DEADLINE + " TEXT,"
                    + COLUMN_HEALTH + " INTEGER,"
                    + COLUMN_COINS + " INTEGER,"
                    + COLUMN_MONSTER + " TEXT " + ")";

    public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public TaskDBHelper(Context context) {
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

    public void insertTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues(); //class used to store values associated with column names
        values.put(COLUMN_NAME, task.getName());
        values.put(COLUMN_CONTENT, task.getContent());
        values.put(COLUMN_DEADLINE, task.getDeadlineAsString());
        values.put(COLUMN_HEALTH, task.getHealth());
        values.put(COLUMN_COINS, task.getCoins());
        values.put(COLUMN_MONSTER, task.getMonster());
        db.insert(TABLE_NAME, null, values); //null is used when you want to add a row without any column values
        db.close();
    }
}