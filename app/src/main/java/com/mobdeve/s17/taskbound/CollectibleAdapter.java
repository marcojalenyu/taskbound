package com.mobdeve.s17.taskbound;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * A class that represents the adapter for the collectibles recycler view.
 */
public class CollectibleAdapter extends RecyclerView.Adapter<CollectibleAdapter.ViewHolder> {

    // UI components
    private final TextView collectiblesCount;
    // Data components
    private int collectedCount = 0;
    private final Collectible[] collectibles;
    private final Context context;

    /**
     * Constructor for the CollectibleAdapter class.
     * @param collectibles - the collectibles data
     * @param activity - the CollectiblesActivity
     * @param collectiblesCount - the TextView for the collectibles count
     */
    public CollectibleAdapter(Collectible[] collectibles,
                              CollectiblesActivity activity,
                              TextView collectiblesCount) {
        this.collectibles = collectibles;
        this.context = activity;
        this.collectiblesCount = collectiblesCount;
        updateCollectiblesCount();
    }

    /**
     * Updates the collectibles count.
     */
    @SuppressLint("DefaultLocale")
    private void updateCollectiblesCount() {
        // Count the number of collectibles obtained by the user
        for (Collectible collectible : collectibles) {
            if (collectible.isObtained()) {
                collectedCount++;
            }
        }
        collectiblesCount.setText(String.format("%d / %d", collectedCount, collectibles.length));
    }

    /**
     * This method is called when the view holder is created.
     * @param parent - the ViewGroup
     * @param viewType - the int
     * @return the ViewHolder
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_collectible,parent,false);
        return new ViewHolder(view);
    }

    /**
     * This method is called when the view holder is bound.
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Collectible collectibleList = collectibles[position];
        // Set text color depending on rarity
        if (collectibles[position].getCollectiblesRarity() == Rarity.SR) {
            holder.collectibleName.setTextColor(context.getResources().getColor(R.color.sr));
        } else if (collectibles[position].getCollectiblesRarity() == Rarity.SSR) {
            holder.collectibleName.setTextColor(context.getResources().getColor(R.color.ssr));
        }
        // If collectible is not obtained, make it a shade of gray
        if (!collectibles[position].isObtained()) {
            holder.collectibleName.setText("???");
            holder.collectibleImg.setImageResource(R.drawable.ic_unknown);
        } else {
            holder.collectibleName.setText(collectibleList.getCollectibleName());
            holder.collectibleImg.setImageResource(collectibleList.getCollectibleImage());
            holder.collectibleImg.setOnClickListener(v -> {
                CollectibleViewFragment collectibleFragment = new CollectibleViewFragment(collectibles[position]);
                collectibleFragment.show(((CollectiblesActivity) context).getSupportFragmentManager(), "Collectible Details");
            });
        }
    }

    /**
     * This method returns the number of items in the data set held by the adapter.
     * @return the number of collectibles
     */
    @Override
    public int getItemCount() {
        return collectibles.length;
    }

    /**
     * A class that represents the view holder for the collectibles recycler view.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView collectibleImg;
        TextView collectibleName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            collectibleImg = itemView.findViewById(R.id.collectibleImg);
            collectibleName = itemView.findViewById(R.id.collectibleName);
        }
    }

}