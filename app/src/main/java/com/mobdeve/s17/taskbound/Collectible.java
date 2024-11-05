package com.mobdeve.s17.taskbound;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Class for the Collectible object
 */
@IgnoreExtraProperties
public class Collectible implements Parcelable {

    // Attributes
    private int collectibleID;
    private String collectibleName;
    private Rarity collectiblesRarity;
    private boolean isObtained;
    private Integer collectibleImage; // Integer to allow null values

    // Constructors

    /**
     * Default constructor for Collectible (for Firebase)
     */
    public Collectible() {

    }

    /**
     * Constructor for Collectible (for a new collectible)
     * @param collectibleID - the collectible's unique ID (index in the collectibles list)
     * @param collectibleName - the collectible's name
     * @param rarity - the collectible's rarity
     * @param collectibleImage - the collectible's image
     */
    public Collectible(int collectibleID, String collectibleName, Rarity rarity, Integer collectibleImage) {
        this.collectibleID = collectibleID;
        this.collectibleName = collectibleName;
        this.collectiblesRarity = rarity;
        this.isObtained = false;
        this.collectibleImage = collectibleImage;
    }

    // Parcelable methods

    /**
     * Constructor for Collectible (for a new collectible)
     * @param in - the Parcel object containing the Collectible's information
     */
    protected Collectible(Parcel in) {
        collectibleID = in.readInt();
        collectibleName = in.readString();
        collectiblesRarity = Rarity.valueOf(in.readString());
        isObtained = in.readByte() != 0;
        if (in.readByte() == 0) {
            collectibleImage = null;
        } else {
            collectibleImage = in.readInt();
        }
    }

    /**
     * Method to create a new Collectible object from a Parcel
     * (Allows the Collectible object to be passed between activities)
     */
    public static final Creator<Collectible> CREATOR = new Creator<Collectible>() {
        @Override
        public Collectible createFromParcel(Parcel in) {
            return new Collectible(in);
        }
        @Override
        public Collectible[] newArray(int size) {
            return new Collectible[size];
        }
    };

    /**
     * Method to describe the contents of the Collectible object
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Method to write the Collectible object to a Parcel
     * @param dest - the Parcel object to write the Collectible object to
     * @param flags - the flags used for the Parcel
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(collectibleID);
        dest.writeString(collectibleName);
        dest.writeString(collectiblesRarity.name());
        dest.writeByte((byte) (isObtained ? 1 : 0));
        if (collectibleImage == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(collectibleImage);
        }
    }

    // Getters and Setters

    public int getCollectibleID() {
        return collectibleID;
    }

    public String getCollectibleName() {
        return collectibleName;
    }

    public Rarity getCollectiblesRarity() {
        if (this.collectiblesRarity != null) {
            return collectiblesRarity;
        }
        return Rarity.R;
    }

    public boolean isObtained() {
        return isObtained;
    }

    public Integer getCollectibleImage() {
        return collectibleImage;
    }

    public void setObtained(boolean obtained) {
        isObtained = obtained;
    }
}
