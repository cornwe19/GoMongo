package com.gomongo.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class IngredientCount {
    
    private static int MAX_INGEDIENT_COUNT = 5;
    
    public static final String COL_BOWL_ID = "bowl_id";
    public static final String COL_FOOD_ID = "food_id";
    
    @SuppressWarnings("unused") // This ID is only used by ORMLite
    @DatabaseField( generatedId = true )
    private int mId;
    
    @DatabaseField( foreign = true, columnName = COL_BOWL_ID )
    private Bowl mBowl;
    public Bowl getBowl() {
        return mBowl;
    }
    
    @DatabaseField( foreign = true, columnName = COL_FOOD_ID )
    private Food mIngredient;
    public Food getIngredient() {
        return mIngredient;
    }
    
    @DatabaseField
    private Integer mCount = 1;
    public Integer getCount() {
        return mCount;
    }

    // Meaningless default constructor. Required by ORMLite
    public IngredientCount() { }
    
    public IngredientCount( Bowl bowl, Food ingredient ) {
        mBowl = bowl;
        mIngredient = ingredient;
    }
    
    public void increment() {
        mCount = Math.min(mCount + 1, MAX_INGEDIENT_COUNT);
    }
    
    public void decrement() {
        mCount = Math.max(mCount - 1, 0);
    }

    @Override
    public boolean equals( Object other ) {
        boolean areEqual = false;
        
        IngredientCount otherCounter = (IngredientCount)other;
        if( otherCounter != null ) {
            areEqual = otherCounter.getIngredient().getId() == mIngredient.getId();
        }
        
        return areEqual;
    }
    
    @Override
    public int hashCode() {
        return mIngredient.getId() + mCount;
    }
}
