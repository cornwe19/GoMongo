package com.gomongo.app;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.gomongo.net.WebHelper;

public class Promotions extends Activity implements OnClickListener {

    private static String TAG = "About";
    
    private static String MONGO_MENU_URL = "http://www.gomongo.com/iphone/promotions.php";
    private static String MONGO_PROMO_LANDING_PAGE = "http://www.gomongo.com/?redirect=android";
    
    private static int LOADING_PROMOTIONS_ID = 0x1;
    
    private Dialog mLoadingDialog;
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.promotions);
        
        View navigationMenu = (View)findViewById(R.id.nav_menu);
        
        NavigationHelper.setupButtonToLaunchActivity(this, navigationMenu, R.id.button_home, Home.class);
        NavigationHelper.setupButtonToLaunchActivity(this, navigationMenu, R.id.button_find_us, FindUs.class);
        NavigationHelper.setupButtonToLaunchActivity(this, navigationMenu, R.id.button_create, CreateBowl.class);
        NavigationHelper.setupButtonToLaunchActivity(this, navigationMenu, R.id.button_photo, MongoPhoto.class);
        NavigationHelper.setupButtonToLaunchActivity(this, navigationMenu, R.id.button_about, About.class);
        
        ImageView promotionImage = (ImageView)findViewById(R.id.imageview_mongo_promotion);
        promotionImage.setOnClickListener(this);
        
        Button findUsButton = (Button)findViewById(R.id.button_promotion_find_us);
        findUsButton.setOnClickListener(this);
        
        final Context thisContext = this;
        
        Thread promotionsThread = new Thread( new Runnable() {
        
            @Override
            public void run() {
                try {
                    InputStream response = WebHelper.getResponseStream(MONGO_MENU_URL);
                    
                    Bitmap image = BitmapFactory.decodeStream(response);
                    
                    runOnUiThread( getSetPromotionImageRunnable(image) );
                    
                } catch (MalformedURLException e) {
                    Log.e(TAG, String.format("Poorly formatted URL (%s)", MONGO_MENU_URL), e);
                    
                    throw new RuntimeException( e );
                } catch (IOException e) {
                    Log.w(TAG, "Problem connecting to the internet", e );
                    runOnUiThread( getIOErrorRunnable(thisContext));
                }
                
                mLoadingDialog.dismiss();
            }
        });
        
        showDialog(LOADING_PROMOTIONS_ID);
        
        promotionsThread.start();
	}
	
	private Runnable getIOErrorRunnable(final Context thisContext) {
        return new Runnable() {
            @Override
            public void run() {
                Toast.makeText(thisContext, R.string.error_connecting_to_internet, Toast.LENGTH_LONG).show(); 
            }
        };
    }

    private Runnable getSetPromotionImageRunnable(final Bitmap image) {
        return new Runnable() {
            @Override
            public void run() {
                ImageView promotionView = (ImageView)findViewById( R.id.imageview_mongo_promotion );
                promotionView.setImageBitmap(image);                
            }
        };
    }
    
    @Override
    protected Dialog onCreateDialog(int dialogId) {
        if( dialogId == LOADING_PROMOTIONS_ID ) {
            Resources res = getResources();
            
            mLoadingDialog = ProgressDialog.show(this, 
                    res.getString(R.string.loading_promotions_title), 
                    res.getString(R.string.loading_promotions_message));
        }
        
        return mLoadingDialog;
    }

    @Override
    public void onClick(View clickedView) {
        switch( clickedView.getId() ) {
        case R.id.imageview_mongo_promotion:
            Intent promotionsPage = new Intent( Intent.ACTION_VIEW, Uri.parse(MONGO_PROMO_LANDING_PAGE) );
            startActivity(promotionsPage);
            break;
        case R.id.button_promotion_find_us:
            Intent findUs = new Intent( this, FindUs.class );
            startActivity(findUs);
            break;
        }
    }
}
