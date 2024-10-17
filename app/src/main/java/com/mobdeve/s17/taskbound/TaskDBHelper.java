package com.mobdeve.s17.taskbound;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class TaskDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "taskbound4.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "tasks";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USER_ID = "userid";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_DEADLINE = "deadline";
    public static final String COLUMN_HEALTH = "health";
    public static final String COLUMN_COINS = "coins";
    public static final String COLUMN_MONSTER = "monster";
    //create variable for mutable list of tasks
    private List<Task> taskList;


    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_USER_ID + " INTEGER,"
                    + COLUMN_NAME + " TEXT,"
                    + COLUMN_CONTENT + " TEXT,"
                    + COLUMN_DEADLINE + " TEXT,"
                    + COLUMN_HEALTH + " INTEGER,"
                    + COLUMN_COINS + " INTEGER,"
                    + COLUMN_MONSTER + " TEXT " + ")";

    public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public TaskDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        taskList = new ArrayList<>(); //initialize taskList, needed for getAllTask()
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
        values.put(COLUMN_USER_ID, task.getUserID());
        values.put(COLUMN_NAME, task.getName());
        values.put(COLUMN_CONTENT, task.getContent());
        values.put(COLUMN_DEADLINE, task.getDeadlineAsString());
        values.put(COLUMN_HEALTH, task.getHealth());
        values.put(COLUMN_COINS, task.getCoins());
        values.put(COLUMN_MONSTER, task.getMonster());
        db.insert(TABLE_NAME, null, values); //null is used when you want to add a row without any column values
        db.close();
    }

    public List<Task> getAllTask() {
        SQLiteDatabase db = this.getReadableDatabase();
        taskList.clear();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_USER_ID + " = " + UserSession.getInstance().getCurrentUser().getUserID(); //get all tasks of the current user
        Cursor cursor = db.rawQuery(query, null);
         //need to import cursor to get data from database
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                int userID = cursor.getInt(cursor.getColumnIndex(COLUMN_USER_ID));
                String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                String content = cursor.getString(cursor.getColumnIndex(COLUMN_CONTENT));
                String deadline = cursor.getString(cursor.getColumnIndex(COLUMN_DEADLINE));
                int health = cursor.getInt(cursor.getColumnIndex(COLUMN_HEALTH));
                int coins = cursor.getInt(cursor.getColumnIndex(COLUMN_COINS));
                String monster = cursor.getString(cursor.getColumnIndex(COLUMN_MONSTER));
                try {
                    Task task = new Task(id, userID, name, content, deadline, health, coins, monster);
                    taskList.add(task);  //add task to arraylist
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return taskList;
    }
}