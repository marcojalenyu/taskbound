package com.mobdeve.s17.taskbound;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CollectibleAdapter extends RecyclerView.Adapter<CollectibleAdapter.ViewHolder> {

    Collectible[] myCollectiblesData;
    Context context;
    TextView collectiblesCount;
    int collectedCount = 0;

    public CollectibleAdapter(Collectible[] myCollectiblesData, CollectiblesActivity activity, TextView collectiblesCount) {
        this.myCollectiblesData = myCollectiblesData;
        this.context = activity;
        this.collectiblesCount = collectiblesCount;
        updateCollectiblesCount();
    }

    private void updateCollectiblesCount() {
        for (Collectible collectible : myCollectiblesData) {
            if (collectible.isObtained()) {
                collectedCount++;
            }
        }
        collectiblesCount.setText(collectedCount + " / " + myCollectiblesData.length);
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
        final Collectible collectibleList = myCollectiblesData[position];
        // Set text color depending on rarity
        if (myCollectiblesData[position].getCollectiblesRarity() == Rarity.SR) {
            holder.collectibleName.setTextColor(context.getResources().getColor(R.color.sr));
        } else if (myCollectiblesData[position].getCollectiblesRarity() == Rarity.SSR) {
            holder.collectibleName.setTextColor(context.getResources().getColor(R.color.ssr));
        }
        // If collectible is not obtained, make it a shade of gray
        if (!myCollectiblesData[position].isObtained()) {
            holder.collectibleName.setText("???");
            holder.collectibleImg.setImageResource(R.drawable.ic_unknown);
        } else {
            holder.collectibleName.setText(collectibleList.getCollectibleName());
            holder.collectibleImg.setImageResource(collectibleList.getCollectibleImage());
        }
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