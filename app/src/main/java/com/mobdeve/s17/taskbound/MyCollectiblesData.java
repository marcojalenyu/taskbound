package com.mobdeve.s17.taskbound;

import android.os.Parcel;
import android.os.Parcelable;

public class MyCollectiblesData implements Parcelable {
    private final int collectibleID;
    private final String collectibleName;
    private final Rarity collectiblesRarity;
    private boolean isObtained;
    private final Integer collectibleImage;

    public MyCollectiblesData(int collectibleID, String collectibleName, Rarity rarity, Integer collectibleImage) {
        this.collectibleID = collectibleID;
        this.collectibleName = collectibleName;
        this.collectiblesRarity = rarity;
        this.isObtained = false;
        this.collectibleImage = collectibleImage;
    }

    protected MyCollectiblesData(Parcel in) {
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

    public static final Creator<MyCollectiblesData> CREATOR = new Creator<MyCollectiblesData>() {
        @Override
        public MyCollectiblesData createFromParcel(Parcel in) {
            return new MyCollectiblesData(in);
        }

        @Override
        public MyCollectiblesData[] newArray(int size) {
            return new MyCollectiblesData[size];
        }
    };

    public Integer getCollectibleImage() {
        return collectibleImage;
    }

    public boolean isObtained() {
        return isObtained;
    }

    public void setObtained(boolean obtained) {
        isObtained = obtained;
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

    public int getCollectibleID() {
        return collectibleID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

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
}
