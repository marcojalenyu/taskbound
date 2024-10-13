package com.mobdeve.s17.taskbound;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Task {
    private final int id;
    private final String name;
    private final String content;
    private final Date deadline;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public Task(int id, String name, String content, String deadline) throws ParseException {
        this.id = id;
        this.name = name;
        this.content = content;
        this.deadline = dateFormat.parse(deadline);
    }

    public int getId() {
        return id;
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
}