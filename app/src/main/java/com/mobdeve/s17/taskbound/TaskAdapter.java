package com.mobdeve.s17.taskbound;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    private LocalDBManager localDB;
    private final List<Task> myTaskData;
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
        return new ViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        final Task myTaskDataList = myTaskData.get(position);
        initializeUI(holder, myTaskDataList);
        ((HomeActivity) context).updateTaskLook(myTaskDataList, holder.itemView);
        holder.btnAttack.setOnTouchListener(createAttackListener(holder, myTaskDataList, position));
    }

    /**
     * This method returns the number of items in the list
     * @return the number of items in the list
     */
    @Override
    public int getItemCount() {
        return myTaskData.size();
    }

    /**
     * This method initializes the UI
     */
    public void initializeUI(ViewHolder holder, Task myTaskDataList) {
        holder.tvTaskName.setText(myTaskDataList.getName());

        if (myTaskDataList.getCategory().isEmpty()) {
            holder.tvCategory.setVisibility(View.GONE);
        } else {
            holder.tvCategory.setText(myTaskDataList.getCategory());
        }

        if (myTaskDataList.getContent().isEmpty()) {
            holder.tvTaskDesc.setVisibility(View.GONE);
        } else {
            holder.tvTaskDesc.setText(myTaskDataList.getContent());
        }

        if (myTaskDataList.getPriority().equals(Priority.LOW.toString())) {
            holder.tvPriority.setVisibility(View.GONE);
        } else if (myTaskDataList.getPriority().equals(Priority.MEDIUM.toString())) {
            holder.tvPriority.setText("!");
        } else {
            holder.tvPriority.setText("!!!");
        }

        holder.tvTaskDeadline.setText(myTaskDataList.getDeadlineAsString());
        holder.tvHealth.setText(String.valueOf(myTaskDataList.getHealth()));
        holder.tvCoins.setText(String.valueOf(myTaskDataList.getCoins()));
        setEnemySpriteAnimation(holder, myTaskDataList.getMonster().toLowerCase());

        holder.btnDelete.setOnClickListener(v -> {
            TaskDeleteFragment taskDeleteFragment = new TaskDeleteFragment(myTaskDataList);
            taskDeleteFragment.show(((HomeActivity) context).getSupportFragmentManager(), "Delete Task");
        });

        holder.llTaskDetails.setOnClickListener(v -> {
            TaskFragment taskFragment = new TaskFragment(myTaskDataList);
            taskFragment.show(((HomeActivity) context).getSupportFragmentManager(), "Task Details");
        });
    }

    /**
     * This method runs the animation
     */
    @SuppressLint("DiscouragedApi")
    public void setEnemySpriteAnimation(ViewHolder holder, String monsterName) {
        int imageID = context.getResources().getIdentifier("enemy_" + monsterName, "drawable", context.getPackageName());
        AnimationDrawable animation = UIUtil.getAnimatedSprite(context, imageID, 2, 400);
        holder.imgTaskEnemy.setBackground(animation);
        holder.imgTaskEnemy.post(animation::start);
    }

    /**
     * This method creates the onTouchListener for attacking the task
     */
    private View.OnTouchListener createAttackListener(ViewHolder holder, Task myTaskDataList, int position) {
        return new View.OnTouchListener() {
            private boolean isPressed = false;
            private int progress = 0;
            private final Handler handler = new Handler();
            private final MediaPlayer attackSound = MediaPlayer.create(context, R.raw.sfx_attack);

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        handleActionDown(holder);
                        return true;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_OUTSIDE:
                    case MotionEvent.ACTION_CANCEL:
                        handleActionUp(holder);
                        return true;
                }
                return false;
            }

            /**
             * This method handles the action down event
             * It starts the attack animation and updates the task health
             * @param holder - the ViewHolder of the task
             */
            private void handleActionDown(ViewHolder holder) {
                isPressed = true;
                handler.post(new Runnable() {
                    @SuppressLint("UseCompatLoadingForDrawables")
                    @Override
                    public void run() {
                        if (isPressed) {
                            progress += 5;
                            holder.btnAttack.setForeground(context.getResources().getDrawable(R.drawable.ic_task_finish_1));
                            if (progress >= 100) {
                                handler.post(createDataUpdater(myTaskDataList, holder, position));
                                holder.btnAttack.setForeground(context.getResources().getDrawable(R.drawable.ic_task_finish_2));
                                attackSound.start();
                                progress = 0;
                            }
                            handler.postDelayed(this, 30);
                        }
                    }
                });
            }

            /**
             * This method handles the action up event
             * It stops the attack animation
             * @param holder - the ViewHolder of the task
             */
            @SuppressLint("UseCompatLoadingForDrawables")
            private void handleActionUp(ViewHolder holder) {
                isPressed = false;
                handler.removeCallbacksAndMessages(null);
                holder.btnAttack.setForeground(context.getResources().getDrawable(R.drawable.ic_task_finish_1));
            }
        };
    }

    /**
     * This method creates the backend for attacking the task
     */
    @SuppressLint("NotifyDataSetChanged")
    private Runnable createDataUpdater(Task myTaskDataList, ViewHolder holder, int position) {
        return () -> {
            localDB = new LocalDBManager(context);
            if (myTaskDataList.getHealth() > 1) {
                myTaskDataList.takeDamage();
                localDB.updateTaskHealth(myTaskDataList.getId(), myTaskDataList.getHealth());
            } else {
                localDB.defeatTask(myTaskDataList.getId(), myTaskDataList.getCoins());
                ((HomeActivity) context).updateCoins(myTaskDataList.getCoins());
                myTaskData.remove(position);
                notifyDataSetChanged();
                MediaPlayer.create(context, R.raw.sfx_complete).start();
            }
            holder.tvHealth.setText(String.valueOf(myTaskDataList.getHealth()));
        };
    }

    /**
     * This class holds the views of the task
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout llTaskDetails;
        ImageView imgTaskEnemy;
        TextView tvTaskName, tvCategory, tvTaskDesc, tvPriority, tvTaskDeadline, tvHealth, tvCoins;
        Button btnAttack;
        FloatingActionButton btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            llTaskDetails = itemView.findViewById(R.id.llTaskDetails);
            imgTaskEnemy = itemView.findViewById(R.id.imgTvIcon);
            tvTaskName = itemView.findViewById(R.id.tvTaskName);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvTaskDesc = itemView.findViewById(R.id.tvTaskDesc);
            tvPriority = itemView.findViewById(R.id.tvPriority);
            tvTaskDeadline = itemView.findViewById(R.id.tvDeadline);
            tvHealth = itemView.findViewById(R.id.tvHealth);
            tvCoins = itemView.findViewById(R.id.tvCoins);
            btnAttack = itemView.findViewById(R.id.btnAttack);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        public Task getTask() {
            return myTaskData.get(getAdapterPosition());
        }
    }
}
