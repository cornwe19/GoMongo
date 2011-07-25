package com.gomongo.app;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.gomongo.net.StaticWebService;

public class About extends Activity {

    private static String TAG = "About";
    
    private static String MONGO_MENU_URL = "http://www.gomongo.com/iphone/promotions.php";
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        
        View navigationMenu = (View)findViewById(R.id.nav_menu);
        
        NavigationHelper.setupButtonToLaunchActivity(this, navigationMenu, R.id.button_home, Home.class);
        NavigationHelper.setupButtonToLaunchActivity(this, navigationMenu, R.id.button_find_us, FindUs.class);
        NavigationHelper.setupButtonToLaunchActivity(this, navigationMenu, R.id.button_create, CreateBowl.class);
        NavigationHelper.setupButtonToLaunchActivity(this, navigationMenu, R.id.button_photo, MongoPhoto.class);
        
        try {
            InputStream response = StaticWebService.getResponseStream(MONGO_MENU_URL);
            
            Bitmap image = BitmapFactory.decodeStream(response);
            
            ImageView promotionView = (ImageView)findViewById( R.id.imageview_mongo_promotion );
            promotionView.setImageBitmap(image);
            
        } catch (MalformedURLException e) {
            Log.e(TAG, String.format("Poorly formatted URL (%s)", MONGO_MENU_URL), e);
            
            throw new RuntimeException( e );
        } catch (IOException e) {
            Log.w(TAG, "Problem connecting to the internet", e );
            
            Toast.makeText(this, R.string.error_connecting_to_internet, Toast.LENGTH_LONG).show();
        }
        
        
	}
}
