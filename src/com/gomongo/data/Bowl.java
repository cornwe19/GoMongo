package com.gomongo.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Bowl {
    
    @DatabaseField
    public String title;
    
    @DatabaseField( generatedId = true )
    private int mId;
    public int getId() {
        return mId;
    }
}
