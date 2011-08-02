package com.gomongo.data;

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

import android.util.Log;

import com.gomongo.net.WebHelper;
import com.j256.ormlite.dao.Dao;

public class UpdateIngredientsHelper {

    private static String TAG  = "UpdateIngredientsHelper";
    
    private static String ALL_INGREDIENTS_REQUEST = "http://gomongo.com/iphone/iPhoneIngredients.php";
    private static String FOOD_XPATH = "root/food";
    
    public static void AsyncUpdateIngredients( final DatabaseOpenHelper helper, final DataUpdateCompleteHandler handler ) {
        Thread updateThread = new Thread( new Runnable() {
            
            @Override
            public void run() {
                Throwable thrownException = null;
                
                try {
                    UpdateIngredients(helper);
                } catch (SQLException e) {
                    thrownException = e;
                } catch (IOException e) {
                    thrownException = e;
                }
                
                if( handler != null ) {
                    handler.dataUpdateComplete(thrownException);
                }
            }
            
        });
        
        updateThread.start();
    }
    
    private static void UpdateIngredients( DatabaseOpenHelper helper ) throws SQLException, IOException {
        try {
            Dao<Food,Integer> foodDao = helper.getDao(Food.class);
            
            InputSource response = WebHelper.getSanitizedResponse(ALL_INGREDIENTS_REQUEST);
            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();
            
            NodeList foods = (NodeList)xpath.evaluate(FOOD_XPATH, response, XPathConstants.NODESET);
            
            for( int i = 0; i < foods.getLength(); i++ ) {
                Node foodNode = foods.item(i);
                
                Food food = Food.getFoodFromXml(foodNode);
                foodDao.createOrUpdate( food );
                
                Log.d( TAG, String.format("Saving id: %s(%s) to database", food.getId(), food.getTitle() ) );
            }
        
        } 
        catch (MalformedURLException ex) {
            Log.e(TAG, String.format("URL(%s) to reach BD's was malformed.", ALL_INGREDIENTS_REQUEST), ex );
            
            throw new RuntimeException( ex );
        } 
        catch (XPathExpressionException ex) {
            Log.e( TAG, String.format( "'%s' was not a valid xpath expression", FOOD_XPATH), ex );
            
            throw new RuntimeException( ex );
        }
    }
    
}
