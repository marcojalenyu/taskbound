package com.mobdeve.s17.taskbound;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Task {
    private final int id;
    private final int userid;
    private final String name;
    private final String content;
    private final Date deadline;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final int health;
    private final int coins;
    private final String monster;

    public Task(int id, int userid, String name, String content, String deadline, int health, int coins, String monster) throws ParseException {
        this.id = id;
        this.userid = userid;
        this.name = name;
        this.content = content;
        this.deadline = dateFormat.parse(deadline);
        this.health = health;
        this.coins = coins;
        this.monster = monster;
    }

    public int getId() {
        return id;
    }

    public int getUserID() {
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
}