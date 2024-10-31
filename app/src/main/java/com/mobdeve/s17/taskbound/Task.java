package com.mobdeve.s17.taskbound;

import com.google.firebase.database.IgnoreExtraProperties;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@IgnoreExtraProperties
public class Task {
    private String id;
    private String userid;
    private String name;
    private String content;
    private Date deadline;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private int health;
    private int coins;
    private String monster;

    public Task() {

    }

    public Task(String id, String userid, String name, String content, String deadline, int health, int coins, String monster) throws ParseException {
        this.id = id;
        this.userid = userid;
        this.name = name;
        this.content = content;
        this.deadline = dateFormat.parse(deadline);
        this.health = health;
        this.coins = coins;
        this.monster = monster;
    }

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

    public void damaged() {
        this.health--;
    }
}