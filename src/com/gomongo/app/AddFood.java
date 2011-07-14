package com.gomongo.app;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gomongo.data.Bowl;
import com.gomongo.data.DatabaseOpenHelper;
import com.gomongo.data.Food;
import com.gomongo.data.IngredientCount;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.android.apptools.OpenHelperManager.SqliteOpenHelperFactory;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

public class AddFood extends OrmLiteBaseActivity<DatabaseOpenHelper> {
    
    private static String TAG = "AddFood";
    
    // Static initialization of DB open helper factory
    static { 
        OpenHelperManager.setOpenHelperFactory(new SqliteOpenHelperFactory() { 
            public OrmLiteSqliteOpenHelper getHelper( Context context ) {
                return new DatabaseOpenHelper(context);
            }
        } );
    }
    
    Bowl mBowl;
    
    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.add_food);
        
        Bundle intentExtras = getIntent().getExtras();
        
        TextView categoryTitle = (TextView)findViewById(R.id.add_food_category);
        categoryTitle.setText(intentExtras.getString(CreateBowl.EXTRA_CATEGORY_TITLE));
        
        String category = intentExtras.getString(CreateBowl.EXTRA_CATEGORY);
        
        try {
            Dao<Bowl,Integer> bowlDao = getHelper().getDao(Bowl.class);
            mBowl = bowlDao.queryForId(intentExtras.getInt(CreateBowl.EXTRA_BOWL_ID));
            
            Dao<Food,Integer> foodDao = getHelper().getDao(Food.class);
            
            QueryBuilder<Food,Integer> builder = foodDao.queryBuilder();
            builder.where().eq(Food.CATEGORY, category);
            
            List<Food> foodsInCategory = foodDao.query(builder.prepare());
            
            Dao<IngredientCount,Integer> ingredientDao = getHelper().getDao(IngredientCount.class);
            AddFoodListAdapter listAdapter = new AddFoodListAdapter(this, mBowl, ingredientDao, foodsInCategory);
            ListView foodsList = (ListView)findViewById(R.id.add_food_list);
            foodsList.setAdapter(listAdapter);
        }
        catch ( SQLException sqlException ) {
            Log.e(TAG, "Couldnt access database", sqlException);
            
            String dataError = getResources().getString(R.string.error_problem_connecting_to_database);
            Toast.makeText(this, dataError, Toast.LENGTH_LONG).show();
            
            finish();
        }
    }
}
