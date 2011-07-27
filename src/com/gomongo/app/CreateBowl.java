package com.gomongo.app;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gomongo.data.Bowl;
import com.gomongo.data.DataUpdateCompleteHandler;
import com.gomongo.data.DatabaseOpenHelper;
import com.gomongo.data.Food;
import com.gomongo.data.IngredientCount;
import com.gomongo.data.UpdateIngredientsHelper;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

public class CreateBowl extends OrmLiteBaseActivity<DatabaseOpenHelper> implements DataUpdateCompleteHandler {

    public static final String EXTRA_CATEGORY = "category";
    public static final String EXTRA_CATEGORY_TITLE = "category.title";
    public static final String EXTRA_BOWL_ID = "bowl.id";
    
    private static String TAG = "CreateBowl";
    
    private static String CATEGORY_MEAT = "Meats";
    private static String CATEGORY_VEGGIES = "Vegetables";
    private static String CATEGORY_SAUCES = "Sauces";
    private static String CATEGORY_SPICES = "Spices";
    private static String CATEGORY_STARCHES = "Starches";
    
    private static int LOADING_FOODS_DIALOG = 0x1;
    
    private ProgressDialog mUpdatingFoodsDialog;
    private Bowl mBowl;
    private HashMap<String,Integer> mIngredientCategoryCounts = new HashMap<String,Integer>();
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_bowl);
        
        View navigationMenu = (View)findViewById(R.id.nav_menu);
        
        NavigationHelper.setupButtonToLaunchActivity(this, navigationMenu, R.id.button_home, Home.class);
        NavigationHelper.setupButtonToLaunchActivity(this, navigationMenu, R.id.button_find_us, FindUs.class);
        NavigationHelper.setupButtonToLaunchActivity(this, navigationMenu, R.id.button_photo, MongoPhoto.class);
        NavigationHelper.setupButtonToLaunchActivity(this, navigationMenu, R.id.button_about, About.class);
        
        Resources res = getResources();
        
        mBowl = new Bowl();
        mBowl.setTitle(res.getString(R.string.default_bowl_title));
        
        try {
            Dao<Bowl,Integer> bowlDao = getHelper().getDao(Bowl.class);
            bowlDao.create(mBowl);
            
            Dao<Food,Integer> foodDao = getHelper().getDao(Food.class);
            
            
            if( foodDao.countOf() < 1 ) {
                showDialog(LOADING_FOODS_DIALOG);
                UpdateIngredientsHelper.AsyncUpdateIngredients(getHelper(), this);
            }
        }
        catch (SQLException ex) {
            handleSQLException(ex);
        }
        
        Button shareBowlButton = (Button)findViewById( R.id.button_share_bowl );
        final Context theContext = this;
        shareBowlButton.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                String title = getTitleFromEditor();
                
                if( title.length() > 0 ) {
                    saveAndStartShareBowl(theContext, title);
                }
                else {
                    Toast.makeText(theContext, R.string.error_bowl_needs_name, Toast.LENGTH_LONG).show();
                }
            }

            private void saveAndStartShareBowl(final Context theContext, String title) {
                try {
                        
                    Dao<Bowl,Integer> bowlDao = getHelper().getDao(Bowl.class);
                    
                    mBowl.setTitle(title);
                    bowlDao.update(mBowl);
                    
                    Intent shareBowlIntent = new Intent( theContext, ShareBowl.class );
                    shareBowlIntent.putExtra(EXTRA_BOWL_ID, mBowl.getId());
                    theContext.startActivity(shareBowlIntent);
                } catch (SQLException ex) {
                    handleSQLException(ex);
                }
            }

            private String getTitleFromEditor() {
                EditText bowlTitle = (EditText)findViewById(R.id.edittext_bowl_title);
                String title = bowlTitle.getText().toString();
                return title;
            } 
            
        });
        
        setupAndRegisterCategoryButton( res.getString(R.string.category_meats_title), CATEGORY_MEAT, R.id.button_meat_seafood );
        setupAndRegisterCategoryButton( res.getString(R.string.category_veggies_title), CATEGORY_VEGGIES, R.id.button_veggies );
        setupAndRegisterCategoryButton( res.getString(R.string.category_sauces_title), CATEGORY_SAUCES, R.id.button_sauces );
        setupAndRegisterCategoryButton( res.getString(R.string.category_spices_title), CATEGORY_SPICES, R.id.button_spices );
        setupAndRegisterCategoryButton( res.getString(R.string.category_spices_title), CATEGORY_STARCHES, R.id.button_starches );
	}

    private void handleSQLException(SQLException ex) {
        Log.w(TAG, "Couldn't open the database for writing", ex );
        
        Toast.makeText(this, R.string.error_problem_connecting_to_database, Toast.LENGTH_LONG).show();
    }

	@Override
	public void onResume() {
	    super.onResume();
	    
	    resetCategoryCounts();
	    
	    try {
            Dao<IngredientCount,Integer> ingredientDao = getHelper().getDao(IngredientCount.class);
            QueryBuilder<IngredientCount,Integer> builder = ingredientDao.queryBuilder();
            builder.where().eq(IngredientCount.COL_BOWL_ID, mBowl.getId());

            List<IngredientCount> ingredientCounts = ingredientDao.query(builder.prepare());
            for( IngredientCount ingredientCount : ingredientCounts ) {                
                updateAggregateCountForCategory( ingredientCount );
            }
            
            updateBackgroundBasedOnBowlComplete();
            
            Resources res = getResources();
            refreshButtonText( R.id.button_meat_seafood, res.getString(R.string.category_meats_title), CATEGORY_MEAT);
            refreshButtonText( R.id.button_veggies, res.getString(R.string.category_veggies_title), CATEGORY_VEGGIES);
            refreshButtonText( R.id.button_sauces, res.getString(R.string.category_sauces_title), CATEGORY_SAUCES);
            refreshButtonText( R.id.button_spices, res.getString(R.string.category_spices_title), CATEGORY_SPICES);
            refreshButtonText( R.id.button_starches, res.getString(R.string.category_starches_title), CATEGORY_STARCHES);
        }
	    catch (SQLException ex) {
            handleSQLException(ex);
        }
	}

    private void updateBackgroundBasedOnBowlComplete() {
        View mainLayout = (View)findViewById(R.id.create_bowl_root);
        
        if( sum( mIngredientCategoryCounts.values() ) > 0 ) {
            mainLayout.setBackgroundResource(R.drawable.create_bowl_background_end);
        }
        else {
            mainLayout.setBackgroundResource(R.drawable.create_bowl_background_begin);
        }
    }

    private int sum(Collection<Integer> values) {
        int sum = 0;
        for( int value : values ) {
            sum += value;
        }
        return sum;
    }

    private void resetCategoryCounts() {
        mIngredientCategoryCounts.put(CATEGORY_MEAT, 0);
	    mIngredientCategoryCounts.put(CATEGORY_VEGGIES, 0);
	    mIngredientCategoryCounts.put(CATEGORY_SAUCES, 0);
	    mIngredientCategoryCounts.put(CATEGORY_SPICES, 0);
	    mIngredientCategoryCounts.put(CATEGORY_STARCHES, 0);
    }
	
	private void updateAggregateCountForCategory( IngredientCount ingredientCount ) {
	    try {
            Dao<Food,Integer> foodDao = getHelper().getDao(Food.class);
            foodDao.refresh(ingredientCount.getIngredient());
            
            String category = ingredientCount.getIngredient().getCategory();
            Integer count = mIngredientCategoryCounts.get(category);
            
            count += ingredientCount.getCount();
            
            mIngredientCategoryCounts.put( category, count );
            
	    } catch (SQLException ex) {
            handleSQLException(ex);
        }
	}
	
	private void refreshButtonText( int buttonId, String title, String categoryName ) {
	    String titleFormat = "%s (%d)";
	    
	    Button button = (Button)findViewById(buttonId);
	    button.setText(String.format(titleFormat, title, mIngredientCategoryCounts.get(categoryName)));
	}
	
    private void setupAndRegisterCategoryButton( final String title, final String categoryName, int buttonId) {
        final Context context = this;
        
        Button createButton = (Button)findViewById(buttonId);
        createButton.setOnClickListener( new OnClickListener() {

            @Override
            public void onClick(View clickedView) {
                Intent addFoodIntent = new Intent(context, AddFood.class);
                
                addFoodIntent.putExtra(EXTRA_CATEGORY, categoryName);
                addFoodIntent.putExtra(EXTRA_CATEGORY_TITLE, title);
                addFoodIntent.putExtra(EXTRA_BOWL_ID, mBowl.getId());
                context.startActivity(addFoodIntent);
            }
            
        });
        
        refreshButtonText( buttonId, title, categoryName );
    }

    @Override
    protected Dialog onCreateDialog( int dialogId ) {
        Resources res = getResources();
        mUpdatingFoodsDialog = ProgressDialog.show(this, 
                res.getString( R.string.loading_foods_title ), 
                res.getString( R.string.loading_foods_message ), true);
        return mUpdatingFoodsDialog;
    }
    
    @Override
    public void dataUpdateComplete(Throwable error) {
        mUpdatingFoodsDialog.dismiss();
        if( error != null ) {
            final int toastTextResId = interpretBackgroundException(error);
            final Context thisContext = this;
            
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(thisContext, toastTextResId, Toast.LENGTH_LONG).show();
                    
                    disableButton(R.id.button_meat_seafood);
                    disableButton(R.id.button_veggies);
                    disableButton(R.id.button_spices);
                    disableButton(R.id.button_sauces);
                    disableButton(R.id.button_starches);
                }
                
            });
        }
    }

    private int interpretBackgroundException(Throwable error) {
        int toastTextResId = 0;
        
        
        if( error instanceof IOException ) {
            toastTextResId = R.string.error_connecting_to_internet;
        }
        else if ( error instanceof SQLException ) {
            toastTextResId = R.string.error_problem_connecting_to_database;
        }
        return toastTextResId;
    }

    private void disableButton(int buttonId) {
        Button categoryButton = (Button)findViewById(buttonId);
        categoryButton.setEnabled(false);
    }
}
