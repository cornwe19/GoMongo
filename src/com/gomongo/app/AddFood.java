package com.gomongo.app;

import java.sql.SQLException;
import java.util.List;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
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

public class AddFood extends OrmLiteBaseActivity<DatabaseOpenHelper> implements OnClickListener {
    
    private static String TAG = "AddFood";
    
    Bowl mBowl;
    AddFoodListAdapter mListAdapter;
    
    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.add_food);
        
        Bundle intentExtras = getIntent().getExtras();
        Category categoryInfo = intentExtras.getParcelable(CreateBowl.EXTRA_CATEGORY);
        
        Typeface burweedTypeface = Typeface.createFromAsset(getAssets(), "fonts/burweed_icg.ttf");
        
        TextView categoryTitle = (TextView)findViewById(R.id.add_food_category);
        categoryTitle.setTypeface(burweedTypeface);
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
            
            Button cancelButton = (Button)findViewById(R.id.button_cancel);
            cancelButton.setOnClickListener(this);
            
            Button doneButton = (Button)findViewById(R.id.button_done);
            doneButton.setOnClickListener(this);
        }
        catch ( SQLException sqlException ) {
            handleSqlException(sqlException);
            
            finish();
        }
    }

    private void handleSqlException(SQLException sqlException) {
        Log.e(TAG, "Couldnt access database", sqlException);
        
        String dataError = getResources().getString(R.string.error_problem_connecting_to_database);
        Toast.makeText(this, dataError, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View clickedView) {
        switch (clickedView.getId()) {
        case R.id.button_cancel:
            finish();
            break;
        case R.id.button_done:
            try {
                mListAdapter.save();
                
                finish();
            }
            catch (SQLException ex ){
                handleSqlException(ex);
            }
            break;
        }
    }
}
