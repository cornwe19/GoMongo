package com.gomongo.app;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

public class About extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.about_us);

        View navigationMenu = (View) findViewById(R.id.nav_menu);

        NavigationHelper.setupButtonToLaunchActivity(this, navigationMenu, R.id.button_home, Home.class);
        NavigationHelper.setupButtonToLaunchActivity(this, navigationMenu, R.id.button_find_us, FindUs.class);
        NavigationHelper.setupButtonToLaunchActivity(this, navigationMenu, R.id.button_create, CreateBowl.class);
        NavigationHelper.setupButtonToLaunchActivity(this, navigationMenu, R.id.button_photo, MongoPhoto.class);
        ImageButton aboutButton = (ImageButton) navigationMenu.findViewById(R.id.button_about);
        aboutButton.setBackgroundResource(R.drawable.navigation_tab);
        aboutButton.setSelected(true);
        
        Typeface burweedTypeface = Typeface.createFromAsset(getAssets(), "fonts/burweed_icg.ttf");
        TextView huntGatherFeastText = (TextView)findViewById(R.id.hunt_gather_feast_text);
        huntGatherFeastText.setTypeface(burweedTypeface);
    }

}
