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
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

    public static final String EXTRA_CATEGORY = "com.gomongo.app.category";
    public static final String EXTRA_BOWL_ID = "com.gomongo.app.bowl_id";
    
    private static String TAG = "CreateBowl";
    
    private Category mCategoryMeats;
    private Category mCategoryVeggies;
    private Category mCategorySauces;
    private Category mCategorySpices;
    private Category mCategorySides;
    
    private static int LOADING_FOODS_DIALOG = 0x1;
    
    private ProgressDialog mUpdatingFoodsDialog;
    private Bowl mBowl;
    private HashMap<String,Integer> mIngredientCategoryCounts = new HashMap<String,Integer>();
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.create_bowl);
        
        View navigationMenu = (View)findViewById(R.id.nav_menu);
        
        NavigationHelper.setupButtonToLaunchActivity(this, navigationMenu, R.id.button_home, Home.class);
        NavigationHelper.setupButtonToLaunchActivity(this, navigationMenu, R.id.button_find_us, FindUs.class);
        ImageButton createButton = (ImageButton)navigationMenu.findViewById(R.id.button_create);
        createButton.setSelected(true);
        NavigationHelper.setupButtonToLaunchActivity(this, navigationMenu, R.id.button_photo, MongoPhoto.class);
        NavigationHelper.setupButtonToLaunchActivity(this, navigationMenu, R.id.button_about, About.class);
        
        Resources res = getResources();
        
        mCategoryMeats = new Category( "Meats", res.getString(R.string.category_meats_title), 3, 
                res.getString(R.string.category_meats_limit_title), res.getString(R.string.category_meats_limit_message) );
        mCategoryVeggies = new Category( "Vegetables", res.getString(R.string.category_veggies_title), 10, 
                res.getString(R.string.category_veggies_limit_title), res.getString(R.string.category_veggies_limit_message) );
        mCategorySauces = new Category( "Sauces", res.getString(R.string.category_sauces_title), 3, 
                res.getString(R.string.category_sauces_limit_title), res.getString(R.string.category_sauces_limit_message) );
        mCategorySpices = new Category( "Spices", res.getString(R.string.category_spices_title), 50, 
                res.getString(R.string.category_spices_limit_title), res.getString(R.string.category_spices_limit_message) );
        mCategorySides = new Category( "Starches", res.getString(R.string.category_starches_title), 10, 
                res.getString(R.string.category_starches_limit_title), res.getString(R.string.category_starches_limit_message) );
        
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
        
        setupAndRegisterCategoryButton( R.id.button_meat_seafood, mCategoryMeats );
        setupAndRegisterCategoryButton( R.id.button_veggies, mCategoryVeggies );
        setupAndRegisterCategoryButton( R.id.button_sauces, mCategorySauces );
        setupAndRegisterCategoryButton( R.id.button_spices, mCategorySpices );
        setupAndRegisterCategoryButton( R.id.button_starches, mCategorySides );
        
        Button shareBowlButton = (Button)findViewById( R.id.button_share_bowl );
        final Context theContext = this;
        shareBowlButton.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                String title = getTitleFromEditor();
                
                boolean bowlHasIngredients = !bowlIsEmpty();
                
                if( title.length() > 0 && bowlHasIngredients ) {
                    saveAndStartShareBowl(theContext, title);
                }
                else {
                    Toast.makeText(theContext, R.string.error_bowl_needs_name, Toast.LENGTH_LONG).show();
                }
            }

            private boolean bowlIsEmpty() {
                boolean bowlHasIngredients = true;
                for ( Integer count : mIngredientCategoryCounts.values() ) {
                    if( count > 0 ) {
                        bowlHasIngredients = false;
                        break;
                    }
                }
                
                return bowlHasIngredients;
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
            
            refreshButtonText( R.id.button_meat_seafood, mCategoryMeats);
            refreshButtonText( R.id.button_veggies, mCategoryVeggies);
            refreshButtonText( R.id.button_sauces, mCategorySauces);
            refreshButtonText( R.id.button_spices, mCategorySpices);
            refreshButtonText( R.id.button_starches, mCategorySides);
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
        mIngredientCategoryCounts.put(mCategoryMeats.getId(), 0);
	    mIngredientCategoryCounts.put(mCategoryVeggies.getId(), 0);
	    mIngredientCategoryCounts.put(mCategorySauces.getId(), 0);
	    mIngredientCategoryCounts.put(mCategorySpices.getId(), 0);
	    mIngredientCategoryCounts.put(mCategorySides.getId(), 0);
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
	
	private void refreshButtonText( int buttonId, Category category ) {
	    String titleFormat = "%s (%d)";
	    
	    Button button = (Button)findViewById(buttonId);
	    button.setText(String.format(titleFormat, category.getTitle(), mIngredientCategoryCounts.get(category.getId())));
	}
	
    private void setupAndRegisterCategoryButton( int buttonId, final Category category ) {
        final Context context = this;
        
        Button createButton = (Button)findViewById(buttonId);
        createButton.setOnClickListener( new OnClickListener() {

            @Override
            public void onClick(View clickedView) {
                Intent addFoodIntent = new Intent(context, AddFood.class);
                
                addFoodIntent.putExtra( EXTRA_CATEGORY, category );
                addFoodIntent.putExtra( EXTRA_BOWL_ID, mBowl.getId() );
                context.startActivity(addFoodIntent);
            }
            
        });
        
        refreshButtonText( buttonId, category );
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
