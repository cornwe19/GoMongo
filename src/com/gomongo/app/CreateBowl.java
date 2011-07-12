package com.gomongo.app;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;

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
import android.widget.Toast;

import com.gomongo.data.DatabaseOpenHelper;
import com.gomongo.data.Food;
import com.gomongo.net.StaticWebService;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.android.apptools.OpenHelperManager.SqliteOpenHelperFactory;
import com.j256.ormlite.dao.Dao;

public class CreateBowl extends OrmLiteBaseActivity<DatabaseOpenHelper> {

    public static String EXTRA_CATEGORY = "category";
    public static String EXTRA_CATEGORY_TITLE = "category.title";
    
    private static String TAG = "CreateBowl";
    
    private static String FOOD_XPATH = "root/food";
    private static String ALL_INGREDIENTS_REQUEST = "http://gomongo.com/iphone/iPhoneIngredients.php";
    
    // Static initialization of DB open helper factory
    static { 
        OpenHelperManager.setOpenHelperFactory(new SqliteOpenHelperFactory() { 
            public OrmLiteSqliteOpenHelper getHelper( Context context ) {
                return new DatabaseOpenHelper(context);
            }
        } );
    }
    
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
        
        try {
            Dao<Food,Integer> foodDao = getHelper().getDao(Food.class);
            
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
            Log.w(TAG, "Couldn't open the database for writing", ex );
            
            Toast.makeText(this, res.getString( R.string.error_problem_connecting_to_database), Toast.LENGTH_LONG).show();
        }
        
        setupCategoryButton( res.getString(R.string.category_meats_title), "Meats", R.id.button_meat_seafood );
        setupCategoryButton( res.getString(R.string.category_veggies_title), "Vegetables", R.id.button_veggies );
        setupCategoryButton( res.getString(R.string.category_sauces_title), "Sauces", R.id.button_sauces );
        setupCategoryButton( res.getString(R.string.category_spices_title), "Spices", R.id.button_spices );
	}

    private void setupCategoryButton( final String title, final String categoryName, int buttonId) {
        final Context context = this;
        
        Button createButton = (Button)findViewById(buttonId);
        createButton.setOnClickListener( new OnClickListener() {

            @Override
            public void onClick(View clickedView) {
                Intent addFoodIntent = new Intent(context, AddFood.class);
                
                addFoodIntent.putExtra(EXTRA_CATEGORY, categoryName);
                addFoodIntent.putExtra(EXTRA_CATEGORY_TITLE, title);
                context.startActivity(addFoodIntent);
            }
            
        });
    }
}
