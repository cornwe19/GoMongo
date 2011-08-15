package com.gomongo.app;

import java.sql.SQLException;
import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gomongo.data.Bowl;
import com.gomongo.data.DatabaseOpenHelper;
import com.gomongo.data.Food;
import com.gomongo.data.IngredientCount;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

public class AddFood extends OrmLiteBaseActivity<DatabaseOpenHelper> {
    
    private static String TAG = "AddFood";
    
    Bowl mBowl;
    AddFoodListAdapter mListAdapter;
    boolean mSaveOnStop = true;
    
    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.add_food);
        
        Bundle intentExtras = getIntent().getExtras();
        Category categoryInfo = intentExtras.getParcelable(CreateBowl.EXTRA_CATEGORY);
        
        TextView categoryTitle = (TextView)findViewById(R.id.add_food_category);
        categoryTitle.setText( categoryInfo.getTitle() );
        
        try {
            Dao<Bowl,Integer> bowlDao = getHelper().getDao(Bowl.class);
            mBowl = bowlDao.queryForId(intentExtras.getInt(CreateBowl.EXTRA_BOWL_ID));
            
            Dao<Food,Integer> foodDao = getHelper().getDao(Food.class);
            
            QueryBuilder<Food,Integer> builder = foodDao.queryBuilder();
            builder.where().eq(Food.CATEGORY, categoryInfo.getId());
            
            List<Food> foodsInCategory = foodDao.query(builder.prepare());
            
            Dao<IngredientCount,Integer> ingredientDao = getHelper().getDao(IngredientCount.class);
            mListAdapter = new AddFoodListAdapter(this, mBowl, ingredientDao, foodsInCategory, categoryInfo );
            ListView foodsList = (ListView)findViewById(R.id.add_food_list);
            foodsList.setAdapter(mListAdapter);
            foodsList.setOnItemClickListener(mListAdapter);
        }
        catch ( SQLException sqlException ) {
            handleSqlException(sqlException);
            
            mSaveOnStop = false;
            finish();
        }
    }

    private void handleSqlException(SQLException sqlException) {
        Log.e(TAG, "Couldnt access database", sqlException);
        
        String dataError = getResources().getString(R.string.error_problem_connecting_to_database);
        Toast.makeText(this, dataError, Toast.LENGTH_LONG).show();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        
        if( mSaveOnStop ) {
            try {
                mListAdapter.save();
            } catch (SQLException ex ) {
                handleSqlException( ex );
            }
        }
    }
}
