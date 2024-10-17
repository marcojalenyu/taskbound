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
        holder.tvTaskDesc.setText(myTaskDataList.getContent());
        holder.tvTaskDeadline.setText(myTaskDataList.getDeadlineAsString());
        holder.imgTaskEnemy.setImageResource(R.drawable.enemy_sample);
    }

    @Override
    public int getItemCount() {
        return myTaskData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgTaskEnemy;
        TextView tvTaskName, tvTaskDesc, tvTaskDeadline;
        FloatingActionButton btnAttack, btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgTaskEnemy = itemView.findViewById(R.id.imgTaskEnemy);
            tvTaskName = itemView.findViewById(R.id.tvTaskName);
            tvTaskDesc = itemView.findViewById(R.id.tvTaskDesc);
            tvTaskDeadline = itemView.findViewById(R.id.tvTaskDeadline);
            btnAttack = itemView.findViewById(R.id.btnAttack);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

}
