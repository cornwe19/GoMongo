package com.gomongo.app;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MongoPhoto extends Activity {
	
	private static final String TAG = "MongoPhoto";
	
	public static final File PICTURE_STORAGE_DIR = new File( Environment.getExternalStorageDirectory(), "GoMongo" );
	private static final File PICTURE_TEMP_DIR = new File( PICTURE_STORAGE_DIR, "temp" );
	
	static {
		if ( !PICTURE_STORAGE_DIR.exists() ) {
			PICTURE_STORAGE_DIR.mkdir();
		}
		
		if ( !PICTURE_TEMP_DIR.exists() ) {
			PICTURE_TEMP_DIR.mkdir();
		}
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mongo_photo);
        
        View navigationMenu = (View)findViewById(R.id.nav_menu);
        
        NavigationHelper.setupButtonToLaunchActivity(this, navigationMenu, R.id.button_home, Home.class);
        NavigationHelper.setupButtonToLaunchActivity(this, navigationMenu, R.id.button_find_us, FindUs.class);
        NavigationHelper.setupButtonToLaunchActivity(this, navigationMenu, R.id.button_create, CreateBowl.class);
        NavigationHelper.setupButtonToLaunchActivity(this, navigationMenu, R.id.button_about, About.class);
        
        setUpTakePhotoButtonToLaunchCamera();
        
        setupPhotoLibraryButtonToLaunchGallery();
	}

	private Uri mImageUri;
	private final int TAKE_PHOTO_REQUEST = 0x01;
	
	private void setUpTakePhotoButtonToLaunchCamera() {
		final Context contextForDisplayingErrors = this;
		
		Button takePhotoButton = (Button)findViewById(R.id.button_take_photo);
        takePhotoButton.setOnClickListener(new OnClickListener(){
        	@Override
        	public void onClick(View view) {
        		try {
	        		mImageUri = createTempImage();
	        		
	        		Intent photoIntent = new Intent( MediaStore.ACTION_IMAGE_CAPTURE );
	        		photoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
	        		
	        		startActivityForResult(photoIntent, TAKE_PHOTO_REQUEST);
        		}
        		catch ( IOException ex ) {
        			Log.w(TAG, String.format("Problem creating temp file: %s", ex.getMessage() ));
        			Toast.makeText(contextForDisplayingErrors, R.string.error_couldnt_access_sdcard, Toast.LENGTH_LONG).show();
        		}
        	}
        });
	}

	private static final String TEMP_IMAGE_PREFIX = "goMongoPhotoTmp";
	public static final String IMAGE_PREFIX = "GoMongo";
	public static final String IMAGE_FORMAT = ".jpg";
	
	private Uri createTempImage() throws IOException {
		Uri newImageUri = null;
		
		try {
			
			File tempFile = File.createTempFile( TEMP_IMAGE_PREFIX, IMAGE_FORMAT, PICTURE_TEMP_DIR );
			
			Log.d( TAG, String.format( "Created temp file at %s", tempFile.getAbsolutePath() ) );
			
			newImageUri = Uri.fromFile(tempFile);
		}
		catch ( IllegalArgumentException ex ) {
			Log.e( TAG, "Temp image prefix needs to be more than 3 characters long" );
		}
		
		return newImageUri;
	}
	
	private void setupPhotoLibraryButtonToLaunchGallery() {
		Button photoLibraryButton = (Button)findViewById(R.id.button_view_gallery);
        photoLibraryButton.setOnClickListener(new OnClickListener(){
        	@Override
        	public void onClick( View view ) {
        		// Looks like I'm going to have to roll my own Gallery for this
        		// Android doesnt let you query the built in gallery
        	}
        });
	}
	
	@Override
	public void onActivityResult( int requestCode, int resultCode, Intent data ){
		if( requestCode == TAKE_PHOTO_REQUEST && resultCode != RESULT_CANCELED) {
			Intent annotateImageIntent = new Intent( this, AnnotateImage.class );
			annotateImageIntent.putExtra( MediaStore.EXTRA_OUTPUT, mImageUri );
			
			startActivity( annotateImageIntent );
		}
	}
}
