package com.mobdeve.s17.taskbound;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The activity that displays the user's tasks and allows them to add new tasks.
 * (The main activity of the application)
 */
public class HomeActivity extends AppCompatActivity {

    // UI Components
    private Button btnSort, btnFilterComplete;
    private RecyclerView tasksView;
    private TextView tvUsername, tvCoinAmount;
    private SearchView svSearchBar;
    private Spinner spinSortType;
    // Data Components
    private User currentUser;
    private DatabaseReference cloudUserDB, cloudTaskDB;
    private LocalDBManager localDB;
    private SortOrder sortOrder;
    private SortType sortType;
    private boolean isFiltered;

    /**
     * This method is called when the activity is first created.
     * Initializes the data and UI components.
     * Sets up the search bar.
     * Authenticates the user and fetches the user data.
     * Syncs the user and task data with the cloud database.
     * @param savedInstanceState - the saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        UIUtil.hideSystemBars(getWindow().getDecorView());
        initializeData();
        initializeUI();
        setupSearchBar();
        authenticateUser();
        syncCloudTasks();
        tasksView.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * This method initializes the DB and session data.
     */
    private void initializeData() {
        this.currentUser = UserSession.getInstance().getCurrentUser();
        this.localDB = new LocalDBManager(this);
        this.cloudUserDB = FirebaseDatabase.getInstance().getReference("users");
        this.cloudTaskDB = FirebaseDatabase.getInstance().getReference("tasks").child(currentUser.getUserID());
        this.sortOrder = currentUser.getSortOrder();
    }

    /**
     * This method initializes the UI components of the activity.
     */
    private void initializeUI() {
        this.tvUsername = findViewById(R.id.tvUsername);
        this.tvCoinAmount = findViewById(R.id.tvCoinAmount);
        this.svSearchBar = findViewById(R.id.searchView);
        this.btnSort = findViewById(R.id.btnSort);
        this.btnFilterComplete = findViewById(R.id.btnFilter);
        this.tasksView = findViewById(R.id.tasksView);
        this.spinSortType = findViewById(R.id.spinSort);

        ImageView btnProfile = findViewById(R.id.imgProfile);
        int imageID = localDB.getUserPicture(this.currentUser.getUserID());
        Log.d("Lily", "" + imageID);
        for (Collectible collectible : this.currentUser.getCollectiblesList()) {
            Log.d("Lily", "Cloud: " + collectible.getCollectibleID() + "(" + collectible.getCollectibleName() + ") " + collectible.getCollectibleImage());
        }
        btnProfile.setImageResource(imageID);

        ImageButton btnLogout = findViewById(R.id.btnLogout);
        FloatingActionButton btnCollectibles = findViewById(R.id.btnCollectibles);
        FloatingActionButton btnShop = findViewById(R.id.btnShop);
        FloatingActionButton btnAdd = findViewById(R.id.btnAdd);

        btnLogout.setOnClickListener(this::btnClickedLogout);
        btnCollectibles.setOnClickListener(this::btnClickedCollectibles);
        btnShop.setOnClickListener(this::btnClickedShop);
        btnAdd.setOnClickListener(this::btnClickedAddTask);
        btnSort.setOnClickListener(this::btnClickedSort);
        btnFilterComplete.setOnClickListener(this::btnClickedFilterComplete);
        btnProfile.setOnClickListener(this::btnClickedProfile);
        setupSortType();
    }

    private void setupSortType() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sort_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinSortType.setAdapter(adapter);

        spinSortType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinSelectedItem(parent, view, position, id);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                sortType = SortType.ALPHABETICAL;
            }
        });
    }

    /**
     * This method sets up the search bar.
     */
    private void setupSearchBar() {
        // Handle query text change
        svSearchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String q) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String q) {
                filterTasks(q);
                return false;
            }
        });
        // Set the search bar to be expanded by default
        svSearchBar.setIconifiedByDefault(false);
    }

    /**
     * Authenticates the user and fetches the user data.
     * (No need to call DB for user data since it is already fetched in the LoginActivity)
     */
    private void authenticateUser() {
        if (currentUser != null) {
            this.tvCoinAmount.setText(String.valueOf(currentUser.getCoins()));
            this.tvUsername.setText(currentUser.getUserName());
        } else {
            redirectToLogin();
        }
    }

    /**
     * This method is called when the logout button is clicked.
     */
    public void btnClickedLogout(View v) {
        // Clear the user session and SharedPreferences
        UserSession.getInstance().clearUserData();
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        // Redirect to login
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * This method is called when the collectibles button is clicked.
     * Redirects to the CollectiblesActivity.
     */
    public void btnClickedCollectibles(View v) {
        Intent intent = new Intent(this, CollectiblesActivity.class);
        startActivity(intent);
    }

    /**
     * This method is called when the shop button is clicked.
     */
    public void btnClickedShop(View v) {
        Intent intent = new Intent(this, ShopActivity.class);
        startActivity(intent);
    }

    /**
     * This method is called when the add task button is clicked.
     */
    public void btnClickedAddTask(View v) {
        TaskAddFragment taskAddFragment = new TaskAddFragment();
        taskAddFragment.show(getSupportFragmentManager(), "TaskAddFragment");
    }

    private void spinSelectedItem(AdapterView<?> parent, View view, int position, long id) {
        String selectedSortType = parent.getItemAtPosition(position).toString();

        switch (selectedSortType) {
            case "Priority":
                this.sortType = SortType.PRIORITY;
                break;
            case "Alphabetical":
                this.sortType = SortType.ALPHABETICAL;
                break;
            case "Category":
                this.sortType = SortType.CATEGORY;
                break;
            case "Due Date":
            default:
                this.sortType = SortType.DUE_DATE;
                break;
        }
        filterTasks(this.svSearchBar.getQuery().toString());
    }

    /**
     * This method is called when the sort button is clicked.
     */
    public void btnClickedSort(View v) {
        String txtSort = "=";
        String currQuery = this.svSearchBar.getQuery().toString();
        switch (sortOrder) {
            case DUE_DATE_ASCENDING:
                this.sortOrder = SortOrder.DUE_DATE_DESCENDING;
                txtSort = "v";
                break;
            case DUE_DATE_DESCENDING:
                this.sortOrder = SortOrder.DEFAULT;
                txtSort = "=";
                break;
            case DEFAULT:
                this.sortOrder = SortOrder.DUE_DATE_ASCENDING;
                txtSort = "^";
                break;
        }
        btnSort.setText(txtSort);
        filterTasks(currQuery);
        this.currentUser.setSortType(this.sortOrder);
        this.localDB.updateUserSortType(this.currentUser);
    }

    public void btnClickedFilterComplete(View v) {
        String txtFilter = "x";
        String currQuery = this.svSearchBar.getQuery().toString();
        if (isFiltered) {
            txtFilter = "x";
        } else {
            txtFilter = "o";
        }
        this.isFiltered = !this.isFiltered;
        btnFilterComplete.setText(txtFilter);
        filterTasks(currQuery);
    }

    public void btnClickedProfile(View v) {
        ProfileEditFragment profileEditFragment = new ProfileEditFragment();
        profileEditFragment.show(getSupportFragmentManager(), "ProfileEditFragment");
    }

    /**
     * Filters the tasks based on the query.
     * @param query - the query to filter the tasks
     */
    private void filterTasks(String query) {
        List<Task> filteredTasks;
        // Filter the tasks based on the query by name
        if (isFiltered) {
            filteredTasks = localDB.getAllExistingTasks(this.currentUser.getUserID())
                    .stream()
                    .filter(task -> task.getName().toLowerCase().contains(query.toLowerCase())
                            && task.isComplete())
                    .collect(Collectors.toList());
        } else {
            filteredTasks = localDB.getAllExistingTasks(this.currentUser.getUserID())
                    .stream()
                    .filter(task -> task.getName().toLowerCase().contains(query.toLowerCase())
                            && !task.isComplete())
                    .collect(Collectors.toList());
        }
        List<Task> defaultTaskList = new ArrayList<>(filteredTasks);

        // Sort the tasks based on the sort type
        switch (this.sortOrder) {
            case DUE_DATE_ASCENDING:
                filteredTasks.sort(Comparator.comparing(task -> {
                    if (SortType.DUE_DATE.equals(this.sortType)) {
                        return task.getDeadlineAsString();
                    } else if (SortType.PRIORITY.equals(this.sortType)) {
                        String Priority = task.getPriority();
                        switch(Priority) {
                            case "HIGH":
                                Priority = "3" + Priority;
                                break;
                            case "MEDIUM":
                                Priority = "2" + Priority;
                                break;
                            case "LOW":
                                Priority = "1" + Priority;
                                break;
                        }
                        return Priority;
                    } else if (SortType.CATEGORY.equals(this.sortType)) {
                        return task.getCategory();
                    } else {
                        return task.getName();
                    }
                }));
                break;
            case DUE_DATE_DESCENDING:
                filteredTasks.sort(Comparator.comparing(task -> {
                    Task t = (Task) task;
                    if (SortType.DUE_DATE.equals(this.sortType)) {
                        return t.getDeadlineAsString();
                    } else if (SortType.PRIORITY.equals(this.sortType)) {
                        String Priority = t.getPriority();
                        switch(Priority) {
                            case "HIGH":
                                Priority = "3" + Priority;
                                break;
                            case "MEDIUM":
                                Priority = "2" + Priority;
                                break;
                            case "LOW":
                                Priority = "1" + Priority;
                                break;
                        }
                        return Priority;
                    } else if (SortType.CATEGORY.equals(this.sortType)) {
                        return t.getCategory();
                    } else {
                        return t.getName();
                    }
                }).reversed());
                break;
            case DEFAULT:
                filteredTasks = defaultTaskList;
                break;
        }
        // Update the adapter with the filtered tasks
        TaskAdapter taskAdapter = new TaskAdapter(filteredTasks, this.isFiltered, this);
        this.tasksView.setAdapter(taskAdapter);
    }

    /**
     * This method is called when the activity resumes.
     */
    @Override
    protected void onResume() {
        super.onResume();
        syncCloudUser();
        syncCloudTasks();
        fetchLocalData();
        initializeUI();
    }

    /**
     * This method is called when the activity pauses.
     * Syncs the user and task data with the cloud database.
     * (More efficient to sync data when the activity is paused)
     */
    @Override
    protected void onPause() {
        super.onPause();
        syncCloudUser();
        syncCloudTasks();
    }

    /**
     * This method is called when the activity stops.
     * Syncs the user and task data with the cloud database.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        syncCloudUser();
        syncCloudTasks();
    }

    /**
     * Fetch the user's data from the local database.
     */
    private void fetchLocalData() {
        User localUser = localDB.getUserWithIdAndPass(UserSession.getInstance().getCurrentUser().getUserID(), UserSession.getInstance().getCurrentUser().getPassword());
        if (localUser != null) {
            // Update the coin amount when the activity resumes
            this.tvCoinAmount.setText(String.valueOf(localUser.getCoins()));
            this.tvUsername.setText(localUser.getUserName());
            this.currentUser = localUser;
            // Fetch the latest tasks and update the adapter
            filterTasks("");
        } else {
            redirectToLogin();
        }
    }

    /**
     * Fetch the user's data from the cloud database and sync it with the local database.
     */
    private void syncCloudUser() {
        cloudUserDB.child(currentUser.getUserID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User cloudUser = snapshot.getValue(User.class);
                if (cloudUser != null) {
                    syncUserData(cloudUser);
                } else {
                    Toast.makeText(HomeActivity.this, "User data not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, "Error fetching user data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Sync the user's data between the local and cloud databases.
     */
    private void syncUserData(User cloudUser) {
        User localUser = localDB.getUserWithIdAndPass(cloudUser.getUserID(), cloudUser.getPassword());
        if (localUser == null) {
            localDB.insertUser(cloudUser);
        } else {
            if (cloudUser.getLastUpdated() > localUser.getLastUpdated()) {
                localDB.updateUser(cloudUser);
            } else {
                cloudUserDB.child(localUser.getUserID()).setValue(localUser);
            }
        }
    }

    /**
     * Fetch and sync the user's tasks from the cloud database.
     */
    private void syncCloudTasks() {
        cloudTaskDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Task> cloudTasks = new ArrayList<>();
                for (DataSnapshot taskSnapshot : snapshot.getChildren()) {
                    Task task = taskSnapshot.getValue(Task.class);
                    if (task != null) {
                        cloudTasks.add(task);
                    }
                }
                syncTasks(cloudTasks);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, "Error fetching tasks.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Sync the user's tasks between the local and cloud databases.
     */
    private void syncTasks(List<Task> cloudTasks) {
        List<Task> localTasks = localDB.getAllTasks(currentUser.getUserID());
        Map<String, Task> localTaskMap = localTasks.stream().
                collect(Collectors.toMap(Task::getId, task -> task));
        // Sync the tasks from cloud to local
        for (Task cloudTask : cloudTasks) {
            Task localTask = localTaskMap.get(cloudTask.getId());
            // Check if the task is found in the local database
            if (localTask == null) {
                if (cloudTask.isDeleted()) {
                    cloudTaskDB.child(cloudTask.getId()).removeValue();
                } else {
                    localDB.insertTask(cloudTask);
                }
            } else {
                // If the task is found, update whichever is more recent
                if (cloudTask.getLastUpdated() > localTask.getLastUpdated()) {
                    localDB.updateTask(cloudTask);
                } else {
                    cloudTaskDB.child(localTask.getId()).setValue(localTask);
                }
                // If the task is deleted in the cloud, delete it in the local database
                if (cloudTask.isDeleted()) {
                    localDB.hardDeleteTask(localTask.getId());
                    cloudTaskDB.child(localTask.getId()).removeValue();
                }
            }
        }
        // Sync the tasks from local to cloud
        for (Task localTask : localTasks) {
            // If the task is not found in the cloud database
            if (!cloudTasks.contains(localTask)) {
                // If the task is deleted in the local database, delete it in the cloud database
                if (localTask.isDeleted()) {
                    cloudTaskDB.child(localTask.getId()).removeValue();
                    localDB.hardDeleteTask(localTask.getId());
                } else {
                    cloudTaskDB.child(localTask.getId()).setValue(localTask);
                }
            }
        }
    }

    /**
     * Redirect to LoginActivity
     */
    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        Toast.makeText(HomeActivity.this, "Session expired.", Toast.LENGTH_SHORT).show();
        startActivity(intent);
        finish();
    }

    /**
     * Updates the coin amount of the user.
     * (Called when the user completes a task - at TaskAdapter)
     * @param coins - the amount of coins to add
     */
    public void updateCoins(int coins) {
        int newCoins = this.currentUser.getCoins() + coins;
        this.tvCoinAmount.setText(String.valueOf(newCoins));
        this.currentUser.setCoins(newCoins);
    }

    /**
     * Updates the look of the task to red to show that the deadline for the task has passed
     * (Called on create and on update of home activity)
     * @param task - the task to update
     */

    public void updateTaskLook(Task task, View taskItemView) {
        CardView cardOutline = taskItemView.findViewById(R.id.cardOutline);
        TextView tvTaskName = taskItemView.findViewById(R.id.tvTaskName);
        if (cardOutline != null) {
            GradientDrawable border = new GradientDrawable();
            border.setColor(ContextCompat.getColor(this, android.R.color.transparent)); // Transparent background, need this so the border would be even for both cases, see item_task.xml for change
            border.setCornerRadius(24f); //scuffed fix but this is basically around the same curve as the original cardBorder
            if (task.getDeadline().before(UIUtil.getCurrentDate())) {
                border.setStroke(5, ContextCompat.getColor(this, R.color.red));
                if (tvTaskName != null) {
                    tvTaskName.setTextColor(ContextCompat.getColor(this, R.color.red));
                }
            } else {
                border.setStroke(5, ContextCompat.getColor(this, android.R.color.black));
                if (tvTaskName != null) {
                    tvTaskName.setTextColor(ContextCompat.getColor(this, android.R.color.black));
                }
            }
            cardOutline.setBackground(border);
        }
    }
}