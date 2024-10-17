package com.mobdeve.s17.taskbound;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MyTasksAdapter extends RecyclerView.Adapter<MyTasksAdapter.ViewHolder> {

    private TaskBoundDBHelper db;
    private List<Task> myTaskData; //used what I made in TaskDBHelper instead of Task[] myTaskData
    Context context;

    public MyTasksAdapter(List<Task> myTaskData, HomeActivity activity) {
        this.myTaskData = myTaskData;
        this.context = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.task_item_list, parent, false);
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
        holder.imgTaskEnemy.setImageResource(imageID);

        // TODO: Make it press hold to attack
        holder.btnAttack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Health of the task decreases by 1
                // Update the health of the task in the database
                // If health is 0, delete the task
                db = new TaskBoundDBHelper(context);
                if (myTaskDataList.getHealth() > 1) {
                    myTaskDataList.damaged();
                    db.updateTaskHealth(myTaskDataList.getId(), myTaskDataList.getHealth());
                } else {
                    db.defeatTask(myTaskDataList.getId(), myTaskDataList.getCoins());
                    myTaskData.remove(position);
                }

                // Refresh the recycler view
                ((HomeActivity) context).onResume();
            }
        });
    }

    @Override
    public int getItemCount() {
        return myTaskData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgTaskEnemy;
        TextView tvTaskName, tvTaskDesc, tvTaskDeadline, tvHealth, tvCoins;
        FloatingActionButton btnAttack, btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgTaskEnemy = itemView.findViewById(R.id.imgTaskEnemy);
            tvTaskName = itemView.findViewById(R.id.tvTaskName);
            tvTaskDesc = itemView.findViewById(R.id.tvTaskDesc);
            tvTaskDeadline = itemView.findViewById(R.id.tvTaskDeadline);
            tvHealth = itemView.findViewById(R.id.tvHealth);
            tvCoins = itemView.findViewById(R.id.tvCoins);
            btnAttack = itemView.findViewById(R.id.btnAttack);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

}
