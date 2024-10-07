package com.mobdeve.s17.taskbound;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyCollectiblesAdapter extends RecyclerView.Adapter<MyCollectiblesAdapter.ViewHolder> {

    MyCollectiblesData[] myCollectiblesData;
    Context context;

    public MyCollectiblesAdapter(MyCollectiblesData[] myMovieData,CollectiblesActivity activity) {
        this.myCollectiblesData = myMovieData;
        this.context = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.activity_collectibles_list,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final MyCollectiblesData myMovieDataList = myCollectiblesData[position];
        holder.collectibleName.setText(myMovieDataList.getCollectibleName());
        holder.collectibleImg.setImageResource(myMovieDataList.getCollectibleImage());

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(context, myMovieDataList.getCollectibleName(), Toast.LENGTH_SHORT).show();
//                Intent i = new Intent(context, MovieActivity.class);
//                i.putExtra("image",myMovieDataList.getMovieImage());
//                i.putExtra("name",myMovieDataList.getMovieName());
//                i.putExtra("date",myMovieDataList.getMovieDate());
//                i.putExtra("summary",myMovieDataList.getMovieSummary());
//
//                context.startActivity(i);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return myCollectiblesData.length;
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView collectibleImg;
        TextView collectibleName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            collectibleImg = itemView.findViewById(R.id.collectibleImg);
            collectibleName = itemView.findViewById(R.id.collectibleName);
        }
    }

}