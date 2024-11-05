package com.mobdeve.s17.taskbound;

import java.util.ArrayList;
import java.util.Arrays;

public class CollectiblesManager {
    private final ArrayList<Collectible> collectibles;
    private int cumWeight;
    private int rNum, srNum, ssrNum;

    public CollectiblesManager() {
        this.collectibles = initializeCollectibles();

        this.cumWeight = 1000;

        int rCount = 0;
        int srCount = 0;
        int ssrCount = 0;

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
            }
        }

        this.rNum = calculateWeight(cumWeight, rCount, 0.86);
        this.srNum = calculateWeight(cumWeight, srCount, 0.1);
        this.ssrNum = calculateWeight(cumWeight, ssrCount, 0.04);

        this.cumWeight = (rCount * rNum) + (srCount * srNum) + (ssrCount * ssrNum);
    }

    public ArrayList<Collectible> getCollectibles() {
        return collectibles;
    }

    public int getCumWeight() {
        return this.cumWeight;
    }

    public int[] getNums() {
        return new int[]{this.rNum, this.srNum, this.ssrNum};
    }

    private int calculateWeight(int totalWeight, int count, double percentage) {
        if (count == 0) {
            return 0;
        }
        return (int) ((totalWeight * percentage) / count);
    }

    private ArrayList<Collectible> initializeCollectibles() {
        return new ArrayList<>(Arrays.asList(
                new Collectible(1, "Chicken", Rarity.R, R.drawable.collectible_chicken),
                new Collectible(2, "Capybara", Rarity.R, R.drawable.collectible_capybara),
                new Collectible(3, "Cat", Rarity.R, R.drawable.collectible_cat),
                new Collectible(4, "Coin", Rarity.R, R.drawable.collectible_coin),
                new Collectible(5, "Clover", Rarity.R, R.drawable.collectible_clover),

                new Collectible(6, "Katana", Rarity.SR, R.drawable.collectible_katana),
                new Collectible(7, "Diamond", Rarity.SR, R.drawable.collectible_diamond),
                new Collectible(8, "Key", Rarity.SR, R.drawable.collectible_key),
                new Collectible(9, "Flower", Rarity.SR, R.drawable.collectible_flower),

                new Collectible(10, "Lilydayo", Rarity.SSR, R.drawable.collectible_lilydayo),
                new Collectible(11, "Hat Lily", Rarity.SSR, R.drawable.collectible_lilyhat),
                new Collectible(12, "Crying Maple", Rarity.SSR, R.drawable.collectible_maplecry)
        ));
    }
}
