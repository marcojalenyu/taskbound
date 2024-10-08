package com.mobdeve.s17.taskbound;

import java.util.ArrayList;
import java.util.Arrays;

public class CollectiblesManager {
    private static CollectiblesManager instance;
    private final ArrayList<MyCollectiblesData> collectibles;

    private CollectiblesManager() {
        collectibles = initializeCollectibles();
    }

    public static CollectiblesManager getInstance() {
        if (instance == null) {
            instance = new CollectiblesManager();
        }
        return instance;
    }

    public ArrayList<MyCollectiblesData> getCollectibles() {
        return collectibles;
    }

    private ArrayList<MyCollectiblesData> initializeCollectibles() {
        return new ArrayList<>(Arrays.asList(
                new MyCollectiblesData(1, "Chicken", Rarity.R, R.drawable.collectible_chicken),
                new MyCollectiblesData(2, "Capybara", Rarity.R, R.drawable.collectible_capybara),
                new MyCollectiblesData(3, "Cat", Rarity.R, R.drawable.collectible_cat),
                new MyCollectiblesData(4, "Coin", Rarity.R, R.drawable.collectible_coin),
                new MyCollectiblesData(5, "Clover", Rarity.R, R.drawable.collectible_clover),
                new MyCollectiblesData(6, "Katana", Rarity.SR, R.drawable.collectible_katana),
                new MyCollectiblesData(7, "Diamond", Rarity.SR, R.drawable.collectible_diamond),
                new MyCollectiblesData(8, "Key", Rarity.SR, R.drawable.collectible_key),
                new MyCollectiblesData(9, "Flower", Rarity.SR, R.drawable.collectible_flower),
                new MyCollectiblesData(10, "Lilydayo", Rarity.SSR, R.drawable.collectible_lilydayo),
                new MyCollectiblesData(205, "Hat Lily", Rarity.SSR, R.drawable.collectible_lilyhat),
                new MyCollectiblesData(206, "Crying Maple", Rarity.SSR, R.drawable.collectible_maplecry)
        ));
    }
}
