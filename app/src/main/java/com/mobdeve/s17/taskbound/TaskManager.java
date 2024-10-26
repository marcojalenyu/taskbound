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

        // Common enemies
        tasks.add(new Task(1, 0,"Slime", "Defeat the slime", "2023-12-31", 1, 10, "Slime"));
        tasks.add(new Task(2, 0, "Bat", "Defeat the bat", "2023-12-31", 2, 15, "Bat"));
        tasks.add(new Task(3, 0, "Ghost", "Defeat the ghost", "2023-12-31", 2, 20, "Ghost"));

        // Uncommon enemies
        tasks.add(new Task(4, 0, "Skeleton", "Defeat the skeleton", "2023-12-31", 5, 30, "Skeleton"));
        tasks.add(new Task(5, 0, "Shroom", "Defeat the shroom", "2023-12-31", 7, 35, "Shroom"));

        // Rare enemies
        tasks.add(new Task(7, 0, "Demon", "Defeat the demon", "2023-12-31", 12, 50, "Demon"));

        // Boss enemies
        tasks.add(new Task(6, 0, "Dragon", "Defeat the dragon", "2023-12-31", 20, 40, "Dragon"));

        return tasks;
    }
}