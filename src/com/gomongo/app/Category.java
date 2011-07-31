package com.gomongo.app;

import android.os.Parcel;
import android.os.Parcelable;

public class Category implements Parcelable {

    private String mId;
    private String mTitle;
    private int mMaxIngredients;
    private String mOverIngredientLimitMessage;
    private String mOverIngredientLimitTitle;
    
    public Category( String id, String title, int maxIngredients, String overIngredientLimitTitle, String overIngredientLimitMessage ) {
        mId = id;
        mTitle = title;
        mMaxIngredients = maxIngredients;
        mOverIngredientLimitTitle = overIngredientLimitTitle;
        mOverIngredientLimitMessage = overIngredientLimitMessage;
    }
    
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString( mId );
        dest.writeString(  mTitle );
        dest.writeInt(  mMaxIngredients );
        dest.writeString(  mOverIngredientLimitTitle );
        dest.writeString(  mOverIngredientLimitMessage );
    }
    
    public static final Parcelable.Creator<Category> CREATOR = new Parcelable.Creator<Category>() {
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }
    
        public Category[] newArray(int size) {
            return new Category[size];
        }
    
    };
    
    private Category(Parcel in) {
        this( in.readString(), in.readString(), in.readInt(), in.readString(), in.readString() );
    }

    public String getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public int getMaxIngredients() {
        return mMaxIngredients;
    }

    public String getOverIngredientLimitMessage() {
        return mOverIngredientLimitMessage;
    }

    public String getOverIngredientLimitTitle() {
        return mOverIngredientLimitTitle;
    }
}
