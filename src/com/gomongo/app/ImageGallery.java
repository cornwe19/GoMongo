package com.gomongo.app;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.AdapterView.OnItemSelectedListener;

public class ImageGallery extends Activity implements OnItemSelectedListener {
    
    private static String TAG = "ImageGallery";
    
    Uri mSelectedImageUri;
    ImageAdapter mImageAdapter;
    
    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.image_gallery);
        
        File mongoImageDirectory = MongoPhoto.PICTURE_STORAGE_DIR;
        File[] files = mongoImageDirectory.listFiles();
        
        List<Uri> imageUris = new ArrayList<Uri>();
        for( File file : files ) {
            imageUris.add( Uri.fromFile( file ) );
        }
        
        Gallery imageGallery = (Gallery)findViewById(R.id.gallery_mongo_images);
        imageGallery.setOnItemSelectedListener(this);
        
        mImageAdapter = new ImageAdapter(this, imageUris);
        imageGallery.setAdapter( mImageAdapter );
        
        final Context thisContext = this;
        
        Button reshareButton = (Button)findViewById(R.id.button_reshare);
        reshareButton.setOnClickListener( new OnClickListener() {
            @Override
            public void onClick(View clickedView) {
                AnnotateImage.showUserShareDestinationsForFile(thisContext, mSelectedImageUri );
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View selectedView, int position, long id) {
        Log.d(TAG, String.format("selecting image: %s", mImageAdapter.getItem(position)));
        
        mSelectedImageUri = mImageAdapter.getItem(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // Dont care, keep last selection
    }
    
}
