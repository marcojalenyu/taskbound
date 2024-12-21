package com.mobdeve.s17.taskbound;

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
     * Note: For the ID, the first digit represents the rarity of the collectible.
     *      1 - Rare, 2 - Super Rare, 3 - Super Super Rare, 4 - Lily
     * @return the list of collectibles
     */
    private ArrayList<Collectible> initializeCollectibles() {
        return new ArrayList<>(Arrays.asList(
                // Common collectibles
                new Collectible(1000, "Chicken", Rarity.R, R.drawable.collectible_chicken),
                new Collectible(1001, "Capybara", Rarity.R, R.drawable.collectible_capybara),
                new Collectible(1002, "Cat", Rarity.R, R.drawable.collectible_cat),
                new Collectible(1003, "Clover", Rarity.R, R.drawable.collectible_clover),
                new Collectible(1004, "Flower", Rarity.R, R.drawable.collectible_flower),
                // Rare collectibles
                new Collectible(2000, "Ring", Rarity.SR, R.drawable.collectible_ring),
                new Collectible(2001, "Coin", Rarity.SR, R.drawable.collectible_coin),
                new Collectible(2002, "Key", Rarity.SR, R.drawable.collectible_key),
                new Collectible(2003, "Diamond", Rarity.SR, R.drawable.collectible_diamond),
                new Collectible(2004, "Necklace", Rarity.SR, R.drawable.collectible_necklace),
                new Collectible(2005, "Charm", Rarity.SR, R.drawable.collectible_charm),

                // Super rare collectibles
                new Collectible(3000, "Angel", Rarity.SSR, R.drawable.collectible_angel),
                new Collectible(3001, "Crying Maple", Rarity.SSR, R.drawable.collectible_maplecry),
                new Collectible(3002, "Alien", Rarity.SSR, R.drawable.collectible_alien),

                // Lily collectibles
                new Collectible(4000, "Lily", Rarity.LILY, R.drawable.collectible_lily),
                new Collectible(4001, "Baby Lily", Rarity.LILY, R.drawable.collectible_babylily),
                new Collectible(4002, "Hat Lily", Rarity.LILY, R.drawable.collectible_lilyhat)
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
