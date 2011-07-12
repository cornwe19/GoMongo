package com.gomongo.data;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class DatabaseOpenHelper extends OrmLiteSqliteOpenHelper {

    private static final String TAG = "DatabaseOpenHelper";
    
    public static final String DATABASE_NAME = "gomongo.db";
    private static int DATABASE_VERSION = 1;
    
    public DatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Food.class);
            //TableUtils.createTable(connectionSource, Bowl.class);
        }
        catch (SQLException ex) {
            Log.e(TAG, ex.getMessage(), ex);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        
    }

}
