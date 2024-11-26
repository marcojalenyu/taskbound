package com.mobdeve.s17.taskbound;

import java.text.ParseException;
import java.util.ArrayList;

/**
 * Manages the tasks in the game.
 */
public class TaskManager {

    // List of tasks
    private final ArrayList<Task> tasks;

    /**
     * Default constructor for TaskManager class.
     * Initializes the task monsters.
     * @throws ParseException - exception for parsing
     */
    public TaskManager() throws ParseException {
        this.tasks = initializeTasks();
    }

    /**
     * Initializes the tasks in the game.
     * Each task is a monster that the player must defeat (e.g., slime, bat, ghost, etc.).
     * Only the health, coins, and name of the monster should be copied to the task.
     * (As the rest are defined by the user)
     * @return the list of tasks
     * @throws ParseException - exception for parsing
     */
    private ArrayList<Task> initializeTasks() throws ParseException {
        ArrayList<Task> tasks = new ArrayList<>();

        // Common enemies
        tasks.add(new Task("1", "0","Slime", "", "2023-12-31", 1, 10, "Slime", Priority.LOW, ""));
        tasks.add(new Task("2", "0", "Bat", "", "2023-12-31", 2, 15, "Bat", Priority.LOW, ""));
        tasks.add(new Task("3", "0", "Ghost", "", "2023-12-31", 2, 20, "Ghost", Priority.LOW, ""));

        // Uncommon enemies
        tasks.add(new Task("4", "0", "Skeleton", "", "2023-12-31", 5, 30, "Skeleton", Priority.MEDIUM, ""));
        tasks.add(new Task("5", "0", "Shroom", "", "2023-12-31", 7, 35, "Shroom", Priority.MEDIUM, ""));

        // Rare enemies
        tasks.add(new Task("6", "0", "Demon", "", "2023-12-31", 12, 50, "Demon", Priority.HIGH, ""));

        // Boss enemies
        tasks.add(new Task("7", "0", "Dragon", "", "2023-12-31", 20, 75, "Dragon", Priority.HIGH, ""));

        return tasks;
    }

    // Getter

    public ArrayList<Task> getTasks() {
        return tasks;
    }
}