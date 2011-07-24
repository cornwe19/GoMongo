package com.gomongo.app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class NavigationHelper {

	public static void setupButtonToLaunchActivity( final Context callingContext, View parent, int buttonId, final Class<?> clazz ) {
		Button button  = (Button)parent.findViewById(buttonId);
		button.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View clickedView) {
				Intent intentToLaunch = new Intent(callingContext, clazz);
				callingContext.startActivity(intentToLaunch);
			}
		});
	}
	
	public static void shareJpegAtUri(Context context, Uri imageUriToShare) {
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("image/jpeg");
        shareIntent.putExtra( Intent.EXTRA_STREAM, imageUriToShare );
        Intent shareMethodChooser = Intent.createChooser(shareIntent, context.getResources().getText(R.string.title_share_chooser));
        
        context.startActivity(shareMethodChooser);
    }
}
