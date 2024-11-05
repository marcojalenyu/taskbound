package com.mobdeve.s17.taskbound;

import android.annotation.SuppressLint;

import com.google.firebase.database.IgnoreExtraProperties;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class for the task, which stores the task's information
 */
@IgnoreExtraProperties
public class Task {

    // Attributes
    private String id;
    private String userid;
    private String name;
    private String content;
    private Date deadline;
    private int health;
    private int coins;
    private String monster;
    private long lastUpdated;
    private boolean deleted;
    // Date format for the deadline
    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * Default constructor for Task (for Firebase)
     */
    public Task() {

    }

    /**
     * Constructor for Task (for a new task)
     * @param id - the task's unique ID
     * @param userid - the user's unique ID (who created the task, foreign key)
     * @param name - the task's name
     * @param content - the task's content
     * @param deadline - the task's deadline
     * @param health - the task's health
     * @param coins - the task's coins
     * @param monster - the task's monster
     */
    public Task(String id,
                String userid,
                String name,
                String content,
                String deadline,
                int health,
                int coins,
                String monster) throws ParseException {
        this.id = id;
        this.userid = userid;
        this.name = name;
        this.content = content;
        this.deadline = dateFormat.parse(deadline);
        this.health = health;
        this.coins = coins;
        this.monster = monster;
        this.lastUpdated = System.currentTimeMillis();
        this.deleted = false;
    }

    /**
     * Constructor for Task (for an existing task)
     * @param id - the task's unique ID
     * @param userid - the user's unique ID (who created the task, foreign key)
     * @param name - the task's name
     * @param content - the task's content
     * @param deadline - the task's deadline
     * @param health - the task's health
     * @param coins - the task's coins
     * @param monster - the task's monster
     * @param lastUpdated - the last time the task's data was updated
     */
    public Task(String id,
                String userid,
                String name,
                String content,
                String deadline,
                int health,
                int coins,
                String monster,
                long lastUpdated,
                boolean deleted) throws ParseException {
        this.id = id;
        this.userid = userid;
        this.name = name;
        this.content = content;
        this.deadline = dateFormat.parse(deadline);
        this.health = health;
        this.coins = coins;
        this.monster = monster;
        this.lastUpdated = lastUpdated;
        this.deleted = deleted;
    }

    // Methods

    /**
     * Attacks the task monster, reducing its health by 1
     */
    public void takeDamage() {
        this.health--;
    }

    // Getters and setters

    public String getId() {
        return id;
    }

    public String getUserID() {
        return userid;
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }

    public Date getDeadline() {
        return deadline;
    }

    public String getDeadlineAsString() {
        return dateFormat.format(deadline);
    }

    public int getHealth() {
        return health;
    }

    public int getCoins() {
        return coins;
    }

    public String getMonster() {
        return monster;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public boolean isDeleted() {
        return deleted;
    }
}