package com.mobdeve.s17.taskbound;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    private LocalDBManager locaLDB;
    private List<Task> myTaskData;
    private Handler handler;
    Context context;

    public TaskAdapter(List<Task> myTaskData, HomeActivity activity) {
        this.myTaskData = myTaskData;
        this.context = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_task, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Task myTaskDataList = myTaskData.get(position);
        holder.tvTaskName.setText(myTaskDataList.getName());
        if (myTaskDataList.getContent().isEmpty()) {
            holder.tvTaskDesc.setVisibility(View.GONE);
        } else {
            holder.tvTaskDesc.setText(myTaskDataList.getContent());
        }
        holder.tvTaskDeadline.setText(myTaskDataList.getDeadlineAsString());
        holder.tvHealth.setText(String.valueOf(myTaskDataList.getHealth()));
        holder.tvCoins.setText(String.valueOf(myTaskDataList.getCoins()));

        // Get the image of the enemy based on the monster name
        // Format of the image is "enemy_" + monster name
        String monsterName = myTaskDataList.getMonster().toLowerCase();
        int imageID = context.getResources().getIdentifier("enemy_" + monsterName, "drawable", context.getPackageName());
        // holder.imgTaskEnemy.setImageResource(imageID);

        // Load the spritesheet
        Bitmap spriteSheet = BitmapFactory.decodeResource(context.getResources(), imageID);
        int frameWidth = spriteSheet.getWidth() / 2;
        int frameHeight = spriteSheet.getHeight();
        Bitmap frame1 = Bitmap.createBitmap(spriteSheet, 0, 0, frameWidth, frameHeight);
        Bitmap frame2 = Bitmap.createBitmap(spriteSheet, frameWidth, 0, frameWidth, frameHeight);

        AnimationDrawable animation = new AnimationDrawable();
        animation.addFrame(new BitmapDrawable(context.getResources(), frame1), 400);
        animation.addFrame(new BitmapDrawable(context.getResources(), frame2), 400);
        animation.setOneShot(false);

        holder.imgTaskEnemy.setBackground(animation);
        
        // Start the animation
        holder.imgTaskEnemy.post(new Runnable() {
            @Override
            public void run() {
                animation.start();
            }
        });

        Runnable attackRunnable = new Runnable() {
            @Override
            public void run() {
                // Health of the task decreases by 1
                // Update the health of the task in the database
                // If health is 0, delete the task
                locaLDB = new LocalDBManager(context);
                if (myTaskDataList.getHealth() > 1) {
                    myTaskDataList.takeDamage();
                    locaLDB.updateTaskHealth(myTaskDataList.getId(), myTaskDataList.getHealth());
                } else {
                    locaLDB.defeatTask(myTaskDataList.getId(), myTaskDataList.getCoins());
                    // Update the coins of the user
                    ((HomeActivity) context).updateCoins(myTaskDataList.getCoins());
                    // Remove the task from the list
                    myTaskData.remove(position);
                }
                // Refresh only the health of the task
                holder.tvHealth.setText(String.valueOf(myTaskDataList.getHealth()));
            }
        };

        // Attack the task when the attack button is pressed and held
        holder.btnAttack.setOnTouchListener(new View.OnTouchListener() {
            private boolean isPressed = false;
            private int progress = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isPressed = true;
                        holder.btnAttack.setVisibility(View.GONE);
                        holder.pbarAttack.setVisibility(View.VISIBLE);
                        handler = new Handler();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (isPressed) {
                                    progress += 10;
                                    holder.pbarAttack.setProgress(progress);
                                    if (progress >= 100) {
                                        handler.post(attackRunnable);
                                        progress = 0;
                                    }
                                    handler.postDelayed(this, 100);
                                }
                            }
                        });
                        return true;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_OUTSIDE:
                    case MotionEvent.ACTION_CANCEL:
                        isPressed = false;
                        handler.removeCallbacksAndMessages(null);
                        holder.btnAttack.setVisibility(View.VISIBLE);
                        holder.pbarAttack.setVisibility(View.GONE);
                        holder.pbarAttack.setProgress(0);
                        return true;
                }
                return false;
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskDeleteFragment taskDeleteFragment = new TaskDeleteFragment(myTaskDataList);
                taskDeleteFragment.show(((HomeActivity) context).getSupportFragmentManager(), "Delete Task");
            }
        });

        holder.llTaskDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskFragment taskFragment = new TaskFragment(myTaskDataList);
                taskFragment.show(((HomeActivity) context).getSupportFragmentManager(), "Task Details");
            }
        });
    }

    @Override
    public int getItemCount() {
        return myTaskData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout llTaskDetails;
        ImageView imgTaskEnemy;
        TextView tvTaskName, tvTaskDesc, tvTaskDeadline, tvHealth, tvCoins;
        Button btnAttack;
        FloatingActionButton btnDelete;
        ProgressBar pbarAttack;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            llTaskDetails = itemView.findViewById(R.id.llTaskDetails);
            imgTaskEnemy = itemView.findViewById(R.id.imgTvIcon);
            tvTaskName = itemView.findViewById(R.id.tvTaskName);
            tvTaskDesc = itemView.findViewById(R.id.tvTaskDesc);
            tvTaskDeadline = itemView.findViewById(R.id.tvDeadline);
            tvHealth = itemView.findViewById(R.id.tvHealth);
            tvCoins = itemView.findViewById(R.id.tvCoins);
            btnAttack = itemView.findViewById(R.id.btnAttack);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            pbarAttack = itemView.findViewById(R.id.pbarAttack);
        }
    }

}
