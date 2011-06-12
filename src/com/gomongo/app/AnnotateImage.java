package com.gomongo.app;

import java.io.FileNotFoundException;
import java.io.InputStream;

import com.gomongo.app.ui.AnnotationEditorView;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class AnnotateImage extends Activity {
	private final String TAG = "AnnotateImage";
	
	@Override
	public void onCreate( Bundle savedInstanceState ){
		super.onCreate(savedInstanceState);
		
		setEditorToFullScreen();
		setContentView(R.layout.annotate_image);
		
		Uri imageUri = getIntent().getParcelableExtra(MediaStore.EXTRA_OUTPUT);
		
		BitmapDrawable drawable = decodeDrawableFromUri(imageUri);
		
		AnnotationEditorView imageView = (AnnotationEditorView)findViewById(R.id.preview_image_view);
		imageView.setBackgroundDrawable( drawable );
	}

	private void setEditorToFullScreen() {
		getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );
		requestWindowFeature( Window.FEATURE_NO_TITLE );
	}

	private BitmapDrawable decodeDrawableFromUri(Uri imageUri) {
		InputStream stream = null;
		try {
			stream = getContentResolver().openInputStream(imageUri);
		} catch (FileNotFoundException e) {
			Log.w(TAG, String.format( "Failed to find captured image file %s", imageUri.toString() ) );
			
			Toast.makeText(this, R.string.error_image_for_editing_missing, Toast.LENGTH_LONG).show();
		}
		
		Bitmap image = decodeBitmapAtHalfResolution(stream);
		BitmapDrawable drawable = new BitmapDrawable(image);
		return drawable;
	}

	// NOTE: This method is intended to help support phones with
	// NOTE: high res cameras and low image memory.
	private Bitmap decodeBitmapAtHalfResolution(InputStream stream) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		int samplePixelDecreaseFactor = 2;
		options.inSampleSize = samplePixelDecreaseFactor;
		
		Bitmap image = BitmapFactory.decodeStream(stream,null,options);
		return image;
	}
}
