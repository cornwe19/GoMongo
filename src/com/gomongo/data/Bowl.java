package com.gomongo.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Bowl {
    
    @DatabaseField
    private String mTitle;
    public String getTitle() { 
        return mTitle;
    }
    public void setTitle( String title ) {
        mTitle = title;
    }
    
    @DatabaseField( generatedId = true )
    private int mId;
    public int getId() {
        return mId;
    }
    
}
