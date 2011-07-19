package com.gomongo.app;

import java.sql.SQLException;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

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

public class ShareBowl extends OrmLiteBaseActivity<DatabaseOpenHelper> implements OnClickListener {
    
    // Static initialization of DB open helper factory
    static { 
        OpenHelperManager.setOpenHelperFactory(new SqliteOpenHelperFactory() { 
            public OrmLiteSqliteOpenHelper getHelper( Context context ) {
                return new DatabaseOpenHelper(context);
            }
        } );
    }
    
    private static int NUTRITION_INFO_DIALOG_ID = 0x1;
    
    private Food mTotalNutritionContainer = new Food();
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.share_bowl);
        
        int bowlId = getIntent().getExtras().getInt(CreateBowl.EXTRA_BOWL_ID);
        try {
            Dao<IngredientCount,Integer> ingredientDao = getHelper().getDao(IngredientCount.class);
            Dao<Food,Integer> foodDao = getHelper().getDao(Food.class);
            
            Dao<Bowl,Integer> bowlDao = getHelper().getDao(Bowl.class);
            Bowl bowl = bowlDao.queryForId(bowlId);
            
            TextView shareTitle = (TextView)findViewById(R.id.textview_bowl_title);
            shareTitle.setText(bowl.getTitle());
            
            QueryBuilder<IngredientCount,Integer> builder = ingredientDao.queryBuilder();
            builder.where().eq( IngredientCount.COL_BOWL_ID, bowlId);
            List<IngredientCount> allIngredients = ingredientDao.query( builder.prepare() );
            for( IngredientCount ingredientCount : allIngredients ) {
                Food ingredient = ingredientCount.getIngredient();
                foodDao.refresh(ingredient);
                
                
                mTotalNutritionContainer.addCalories(ingredient.getTotalCalories() * ingredientCount.getCount());
                mTotalNutritionContainer.addProtein(ingredient.getProtein() * ingredientCount.getCount());
                mTotalNutritionContainer.addTotalFat(ingredient.getTotalFat() * ingredientCount.getCount());
                mTotalNutritionContainer.addSaturatedFat(ingredient.getSaturatedFat() * ingredientCount.getCount());
                mTotalNutritionContainer.addCarbs(ingredient.getCarbs() * ingredientCount.getCount());
                mTotalNutritionContainer.addDietaryFiber(ingredient.getDietaryFiber() * ingredientCount.getCount());
            }
            
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        Button startOver = (Button)findViewById(R.id.button_create_another_bowl);
        startOver.setOnClickListener(this);
        
        Button nutritionInfo = (Button)findViewById(R.id.button_nutrition_info);
        nutritionInfo.setOnClickListener(this);
    }

    @Override
    public Dialog onCreateDialog( int id ) {
        Dialog dialog = new Dialog(this);
        if( id == NUTRITION_INFO_DIALOG_ID ) {
            dialog.setContentView(R.layout.nutrition_info);
            dialog.setTitle(R.string.nut_info_title);
            View nutritionalTableRoot = dialog.findViewById(R.id.nutrition_info_main);
            
            removeUnnecessaryTableRowsFromDialog( nutritionalTableRoot );
            
            NutritionFactsViewHelper.fillOutCommonFactsLayout(nutritionalTableRoot, mTotalNutritionContainer);
        }
        
        return dialog;
    }

    private void removeUnnecessaryTableRowsFromDialog( View tableRoot ) {
        View inTableTitle = tableRoot.findViewById(R.id.nutrition_in_table_title);
        inTableTitle.setVisibility(View.GONE);
        
        View servingSizeRow = tableRoot.findViewById(R.id.nutrition_serving_size_row);
        servingSizeRow.setVisibility(View.GONE);
    }
    
    @Override
    public void onClick(View clickedView) {
        
        switch( clickedView.getId() ) {
        case R.id.button_nutrition_info:
            showDialog(NUTRITION_INFO_DIALOG_ID);
            break;
        case R.id.button_send_bowl_email:
            break;
        case R.id.button_send_bowl_facebook:
            break;
        case R.id.button_send_bowl_twitter:
            break;
        case R.id.button_create_another_bowl:
            Intent createNewBowl = new Intent( this, CreateBowl.class );
            startActivity(createNewBowl);
            break;
        }
        
    }
}
