package com.gomongo.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Home extends Activity implements OnClickListener {
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button homeButton = (Button)findViewById(R.id.button_home);
        homeButton.setPressed(true);
        
        Button findUsButton = (Button)findViewById(R.id.button_find_us);
        findUsButton.setOnClickListener(this);
    }

	@Override
	public void onClick(View view) {
		switch( view.getId() ) {
		case R.id.button_find_us:
			Intent findUsIntent = new Intent(this, FindUs.class);
			startActivity(findUsIntent);
			break;
		default:
			break;
		}
	}
}