package com.gomongo.app;

import android.os.Bundle;
import android.view.View;

import com.gomongo.data.DatabaseOpenHelper;
import com.gomongo.data.UpdateIngredientsHelper;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;

public class Home extends OrmLiteBaseActivity<DatabaseOpenHelper> {
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        View navigationMenu = findViewById(R.id.nav_menu);
        
        NavigationHelper.setupButtonToLaunchActivity(this, navigationMenu, R.id.button_find_us, FindUs.class);
        NavigationHelper.setupButtonToLaunchActivity(this, navigationMenu, R.id.button_create, CreateBowl.class);
        NavigationHelper.setupButtonToLaunchActivity(this, navigationMenu, R.id.button_photo, MongoPhoto.class);
        NavigationHelper.setupButtonToLaunchActivity(this, navigationMenu, R.id.button_about, About.class);
        
        View homeMainMenu = (View)findViewById( R.id.home_main_menu );
        
        NavigationHelper.setupButtonToLaunchActivity(this, homeMainMenu, R.id.button_find_us, FindUs.class);
        NavigationHelper.setupButtonToLaunchActivity(this, homeMainMenu, R.id.button_create, CreateBowl.class);
        NavigationHelper.setupButtonToLaunchActivity(this, homeMainMenu, R.id.button_photo, MongoPhoto.class);
        NavigationHelper.setupButtonToLaunchActivity(this, homeMainMenu, R.id.button_promotions, Promotions.class);
        
        UpdateIngredientsHelper.AsyncUpdateIngredients(getHelper(), null);
    }
}