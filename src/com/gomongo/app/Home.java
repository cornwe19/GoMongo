package com.gomongo.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

import com.gomongo.data.DatabaseOpenHelper;
import com.gomongo.data.UpdateIngredientsHelper;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;

public class Home extends OrmLiteBaseActivity<DatabaseOpenHelper> {
    
    Typeface mBurweedFont;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature( Window.FEATURE_NO_TITLE );
        
        setContentView(R.layout.main);
        
        View navigationMenu = findViewById(R.id.nav_menu);
        
        ImageButton homeButton = (ImageButton)navigationMenu.findViewById(R.id.button_home);
        homeButton.setSelected(true);
        NavigationHelper.setupButtonToLaunchActivity(this, navigationMenu, R.id.button_find_us, FindUs.class);
        NavigationHelper.setupButtonToLaunchActivity(this, navigationMenu, R.id.button_create, CreateBowl.class);
        NavigationHelper.setupButtonToLaunchActivity(this, navigationMenu, R.id.button_photo, MongoPhoto.class);
        NavigationHelper.setupButtonToLaunchActivity(this, navigationMenu, R.id.button_about, About.class);
        
        mBurweedFont = Typeface.createFromAsset(getAssets(), "fonts/burweed_icg.ttf" );
        
        View homeMainMenu = (View)findViewById( R.id.home_main_menu );
        
        setupHomeMenuButton(homeMainMenu, R.id.button_find_us, FindUs.class);
        setupHomeMenuButton(homeMainMenu, R.id.button_create, CreateBowl.class);
        setupHomeMenuButton(homeMainMenu, R.id.button_photo, MongoPhoto.class);
        setupHomeMenuButton(homeMainMenu, R.id.button_promotions, Promotions.class);
        
        UpdateIngredientsHelper.AsyncUpdateIngredients(getHelper(), null);
    }
    
    private void setupHomeMenuButton( View parent, int buttonId, final Class<?> clazz ) {
        Button button  = (Button)parent.findViewById(buttonId);
        final Context callingContext = this;
        
        button.setTypeface(mBurweedFont);
        
        button.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View clickedView) {
                Intent intentToLaunch = new Intent(callingContext, clazz);
                startActivity(intentToLaunch);
            }
        });
    }
}