package com.mobdeve.s17.taskbound;

public class MyCollectiblesData {
    private final int collectibleID;
    private final String collectibleName;
    private Rarity collectiblesRarity;
    private boolean isObtained;
    private final Integer collectibleImage;

    public MyCollectiblesData(int collectibleID, String collectibleName, String rarity, Integer collectibleImage) {
        this.collectibleID = collectibleID;
        this.collectibleName = collectibleName;
        this.collectiblesRarity = Rarity.valueOf(rarity);
        this.isObtained = false;
        this.collectibleImage = collectibleImage;
    }

    public Integer getCollectibleImage() {
        return collectibleImage;
    }

    public boolean isObtained() {
        return isObtained;
    }

    public void setObtained(boolean obtained) {
        this.isObtained = obtained;
    }

    public String getCollectibleName() {
        return collectibleName;
    }

    public Rarity getCollectiblesRarity() {
        return collectiblesRarity;
    }

    public int getCollectibleID() {
        return collectibleID;
    }
}
