package com.mobdeve.s17.taskbound;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A class that represents the manager for the collectibles.
 */
public class CollectiblesManager {

    // List of collectibles
    private final ArrayList<Collectible> collectibles;
    // Cumulative weight of the collectibles
    private int cumWeight;
    // Number of collectibles per rarity
    private final int rNum, srNum, ssrNum, lilyNum;

    /**
     * Default constructor for CollectiblesManager class.
     * Initializes the collectibles.
     */
    public CollectiblesManager() {
        this.collectibles = initializeCollectibles();
        this.cumWeight = 100000;
        // Each rarity has a different probability of being obtained
        int rCount = 0;
        int srCount = 0;
        int ssrCount = 0;
        int lilyCount = 0;
        // Count the number of collectibles per rarity
        for (Collectible collectible : collectibles) {
            switch (collectible.getCollectiblesRarity()) {
                case R:
                    rCount++;
                    break;
                case SR:
                    srCount++;
                    break;
                case SSR:
                    ssrCount++;
                    break;
                case LILY:
                    lilyCount++;
                    break;
            }
        }
        // Calculate the weight of the collectibles
        this.rNum = calculateWeight(rCount, 0.86);
        this.srNum = calculateWeight(srCount, 0.1);
        this.ssrNum = calculateWeight(ssrCount, 0.035);
        this.lilyNum = calculateWeight(lilyCount, 0.005);
        // Calculate the cumulative weight
        this.cumWeight = (rCount * rNum) + (srCount * srNum) + (ssrCount * ssrNum) + (lilyCount * lilyNum);
    }

    /**
     * Initializes the collectibles in the game.
     * @return the list of collectibles
     */
    private ArrayList<Collectible> initializeCollectibles() {
        return new ArrayList<>(Arrays.asList(
                // Common collectibles
                new Collectible(1, "Chicken", Rarity.R, R.drawable.collectible_chicken),
                new Collectible(2, "Capybara", Rarity.R, R.drawable.collectible_capybara),
                new Collectible(3, "Cat", Rarity.R, R.drawable.collectible_cat),
                new Collectible(4, "Coin", Rarity.R, R.drawable.collectible_coin),
                new Collectible(5, "Clover", Rarity.R, R.drawable.collectible_clover),
                // Rare collectibles
                new Collectible(6, "Katana", Rarity.SR, R.drawable.collectible_katana),
                new Collectible(7, "Diamond", Rarity.SR, R.drawable.collectible_diamond),
                new Collectible(8, "Key", Rarity.SR, R.drawable.collectible_key),
                new Collectible(9, "Flower", Rarity.SR, R.drawable.collectible_flower),
                // Super rare collectibles
                new Collectible(12, "Crying Maple", Rarity.SSR, R.drawable.collectible_maplecry),
                new Collectible(13, "Banana", Rarity.SSR, R.drawable.collectible_banana),
                // Lily collectibles
                new Collectible(10, "Lilydayo", Rarity.LILY, R.drawable.collectible_lilydayo),
                new Collectible(11, "Hat Lily", Rarity.LILY, R.drawable.collectible_lilyhat),
                new Collectible(14, "Baby Lily", Rarity.LILY, R.drawable.collectible_babylily)
        ));
    }

    /**
     * Calculates the probability weight of the collectibles.
     * @param count - the count of collectibles
     * @param percentage - the percentage of the collectibles
     * @return the weight of the collectibles in terms of probability
     */
    private int calculateWeight(int count, double percentage) {
        if (count == 0) {
            return 0;
        }
        return (int) ((this.cumWeight * percentage) / count);
    }

    // Getters

    public ArrayList<Collectible> getCollectibles() {
        return collectibles;
    }

    public String getCollectibleName(int index) {
        if (index < 0 || index >= this.collectibles.size()) {
            return "0";
        }
        return this.collectibles.get(index).getCollectibleName().toLowerCase();
    }

    public int getCumWeight() {
        return this.cumWeight;
    }

    public int[] getNumOfEachRarity() {
        return new int[]{this.rNum, this.srNum, this.ssrNum, this.lilyNum};
    }

}
