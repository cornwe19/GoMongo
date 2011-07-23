package com.gomongo.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Gallery;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.gomongo.app.ui.AnnotationEditorView;
import com.gomongo.app.ui.MongoImage;

public class AnnotateImage extends Activity implements OnClickListener, OnCheckedChangeListener {
	private final String TAG = "AnnotateImage";
	
	private Uri mTempImageUri;
	private AnnotationEditorView mAnnotationEditorView;
	
	@Override
	public void onCreate( Bundle savedInstanceState ){
		super.onCreate(savedInstanceState);
		
		setEditorToFullScreen();
		setContentView(R.layout.annotate_image);
		
		mTempImageUri = getIntent().getParcelableExtra(MediaStore.EXTRA_OUTPUT);
		
		setEditorBackgroundToTempImage();
		
		hookUpEditorOptionButtons();
	}

	private void hookUpEditorOptionButtons() {
		Button cancelButton = (Button)findViewById(R.id.button_cancel);
		cancelButton.setOnClickListener(this);
		
		Button saveAndShareButton = (Button)findViewById(R.id.button_save_and_share);
		saveAndShareButton.setOnClickListener(this);
		
		Button addStickerButton = (Button)findViewById(R.id.button_add_annotation);
		addStickerButton.setOnClickListener(this);
		
		CheckBox deleteStickerToggle = (CheckBox)findViewById(R.id.button_delete_sticker);
		deleteStickerToggle.setOnCheckedChangeListener(this);
	}

	private void setEditorBackgroundToTempImage() {
		BitmapDrawable drawable = decodeDrawableFromUri(mTempImageUri);
		
		mAnnotationEditorView = (AnnotationEditorView)findViewById(R.id.preview_image_view);
		mAnnotationEditorView.setBackgroundDrawable( drawable );
	}

	@Override
	public void onStop() {
		super.onStop();
		
		cleanUpTempImage();
	}

	private void cleanUpTempImage() {
		File tempImageFile = new File( convertAndroidUriToJavaUri( mTempImageUri ) );
		tempImageFile.delete();
	}

	private URI convertAndroidUriToJavaUri( Uri uri ){
		URI javaUri = null;
		
		try {
			javaUri = new URI( uri.toString() );
		}
		catch ( URISyntaxException ex ) {
			Log.e( TAG, String.format( "Caught a poorly formatted URI: %s.", mTempImageUri.toString() ) );
		}
		
		return javaUri;
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
			
			Toast.makeText(this, getResources().getText(R.string.error_image_for_editing_missing), Toast.LENGTH_LONG).show();
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

	public final int ANNOTATIONS_DIALOG = 0x01;
	
	@Override
	public void onClick(View view) {
		switch( view.getId() ) {
		case R.id.button_cancel:
			finish();
			break;
		case R.id.button_add_annotation:
			showDialog(ANNOTATIONS_DIALOG);
			break;
		case R.id.button_save_and_share:
			forceExitDeleteMode();
		    
		    Bitmap compositeBitmap = mAnnotationEditorView.prepareBitmap();
			
			File imageToShare = compressBitmapToImageFile(compositeBitmap);
			
			showUserShareDestinationsForFile(imageToShare);
			break;
		// Handle any dialog image click the same
		case R.id.top_left:
		case R.id.top_right:
		case R.id.bottom_left:
		case R.id.bottom_right:
			MongoImage image = new MongoImage( BitmapFactory.decodeResource(getResources(), (Integer)view.getTag()) );
			image.setCurrentPoint(100, 100);
			mAnnotationEditorView.addAnnotation( image );
			
			mDialog.dismiss();
			
			break;
		}
	}

    private void forceExitDeleteMode() {
        CheckBox deletingCheckbox = (CheckBox)findViewById(R.id.button_delete_sticker);
        deletingCheckbox.setChecked(false);
    }

	private File compressBitmapToImageFile(Bitmap compositeBitmap) {
		File imageToShare = null;
		try {
			imageToShare = File.createTempFile( MongoPhoto.IMAGE_PREFIX, MongoPhoto.IMAGE_FORMAT, MongoPhoto.PICTURE_STORAGE_DIR );
			OutputStream imageStream = new FileOutputStream( imageToShare );
			compositeBitmap.compress(CompressFormat.JPEG, 90, imageStream);
			imageStream.close();
		} 
		catch (FileNotFoundException e) {
			Log.e( TAG, "Failed to create filestream. IOException should have handled the IO error." );
		}
		catch (IOException e) {
			Log.e( TAG, String.format("Could not create image file: %s", imageToShare.getAbsolutePath() ) );
			
			Toast.makeText( this, getResources().getText(R.string.error_couldnt_access_sdcard), Toast.LENGTH_LONG ).show();
		}
		return imageToShare;
	}

	private void showUserShareDestinationsForFile(File imageToShare) {
		Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
		shareIntent.setType("image/jpeg");
		shareIntent.putExtra( Intent.EXTRA_STREAM, Uri.fromFile(imageToShare) );
		Intent shareMethodChooser = Intent.createChooser(shareIntent, getResources().getText(R.string.title_share_chooser));
		
		startActivity(shareMethodChooser);
	}
	
	Dialog mDialog;
	
	@Override
	public Dialog onCreateDialog( int dialogId ) {
		mDialog = null;
		if ( dialogId == ANNOTATIONS_DIALOG ) {
			mDialog = new Dialog(this);
			mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			mDialog.setContentView(R.layout.add_annotation_menu);
			
			Map<Integer, List<Integer>> imageIdMap = StaticAnnotationsDefinition.getAnnotations();
			
			Gallery pages = (Gallery)mDialog.findViewById(R.id.gallery_image_annoation_pages);
			pages.setAdapter(new PagingListAdapter(this, imageIdMap, this));
		}
		
		return mDialog;
	}

    @Override
    public void onCheckedChanged(CompoundButton button, boolean isChecked) {
        if( button.getId() == R.id.button_delete_sticker ) {
            if( isChecked ) {
                int redTintColor = 0xFFFF3333;
                button.getBackground().setColorFilter(redTintColor, PorterDuff.Mode.MULTIPLY);
                mAnnotationEditorView.startDeleting();
            }
            else {
                int clearTintColor = 0xFFFFFFFF;
                button.getBackground().setColorFilter(clearTintColor, PorterDuff.Mode.MULTIPLY);
                mAnnotationEditorView.stopDeleting();
            }
        }
    }
}
