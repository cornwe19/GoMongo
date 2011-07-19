package com.gomongo.app;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

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
import com.gomongo.data.DatabaseOpenHelper;
import com.gomongo.data.Food;
import com.gomongo.data.IngredientCount;
import com.gomongo.net.StaticWebService;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.android.apptools.OpenHelperManager.SqliteOpenHelperFactory;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

public class CreateBowl extends OrmLiteBaseActivity<DatabaseOpenHelper> {

    public static final String EXTRA_CATEGORY = "category";
    public static final String EXTRA_CATEGORY_TITLE = "category.title";
    public static final String EXTRA_BOWL_ID = "bowl.id";
    
    private static String TAG = "CreateBowl";
    
    private static String FOOD_XPATH = "root/food";
    private static String ALL_INGREDIENTS_REQUEST = "http://gomongo.com/iphone/iPhoneIngredients.php";
    
    private static String CATEGORY_MEAT = "Meats";
    private static String CATEGORY_VEGGIES = "Vegetables";
    private static String CATEGORY_SAUCES = "Sauces";
    private static String CATEGORY_SPICES = "Spices";
    private static String CATEGORY_STARCHES = "Starches";
    
    // Static initialization of DB open helper factory
    static { 
        OpenHelperManager.setOpenHelperFactory(new SqliteOpenHelperFactory() { 
            public OrmLiteSqliteOpenHelper getHelper( Context context ) {
                return new DatabaseOpenHelper(context);
            }
        } );
    }
    
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
            Dao<Food,Integer> foodDao = getHelper().getDao(Food.class);
            Dao<Bowl,Integer> bowlDao = getHelper().getDao(Bowl.class);
            bowlDao.create(mBowl);
            
            
            InputSource response = StaticWebService.getSanitizedResponse(ALL_INGREDIENTS_REQUEST);
            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();
            
            NodeList foods = (NodeList)xpath.evaluate(FOOD_XPATH, response, XPathConstants.NODESET);
            
            for( int i = 0; i < foods.getLength(); i++ ) {
                Node foodNode = foods.item(i);
                
                Food food = Food.getFoodFromXml(foodNode);
                if ( foodDao.queryForId(food.getId() ) == null ) {
                    foodDao.create(food);
                }
                else {
                    foodDao.update(food);
                }
                
                
                Log.d( TAG, String.format("Saving id: %s(%s) to database", food.getId(), food.getTitle() ) );
            }
        
        } 
        catch (MalformedURLException ex) {
            Log.e(TAG, String.format("URL(%s) to reach BD's was malformed.", ALL_INGREDIENTS_REQUEST), ex );
            
            throw new RuntimeException( ex );
        } 
        catch (IOException e) {
            Log.w(TAG, "Couldn't connect to BD's website.", e);
            
            Toast.makeText(this, res.getString( R.string.error_problem_getting_ingredients), Toast.LENGTH_LONG).show();
        } 
        catch (XPathExpressionException ex) {
            Log.e( TAG, String.format( "'%s' was not a valid xpath expression", FOOD_XPATH), ex );
            
            throw new RuntimeException( ex );
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
}
