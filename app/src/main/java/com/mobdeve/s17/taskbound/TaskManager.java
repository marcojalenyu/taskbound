package com.mobdeve.s17.taskbound;

import java.text.ParseException;
import java.util.ArrayList;

public class TaskManager {

    private final ArrayList<Task> tasks;

    public TaskManager() throws ParseException {
        this.tasks = initializeTasks();
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    private ArrayList<Task> initializeTasks() throws ParseException {
        ArrayList<Task> tasks = new ArrayList<>();

        tasks.add(new Task(1, 0,"Slime", "Defeat the slime", "2023-12-31", 100, 10, "Slime"));
        tasks.add(new Task(2, 0, "Goblin", "Defeat the goblin", "2023-12-31", 200, 20, "Goblin"));
        tasks.add(new Task(3, 0, "Orc", "Defeat the orc", "2023-12-31", 300, 30, "Orc"));
        tasks.add(new Task(4, 0, "Dragon", "Defeat the dragon", "2023-12-31", 400, 40, "Dragon"));
        tasks.add(new Task(5, 0, "Demon", "Defeat the demon", "2023-12-31", 500, 50, "Demon"));

        return tasks;
    }
}