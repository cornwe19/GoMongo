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
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.Toast;

import com.gomongo.app.ui.AnnotationEditorView;
import com.gomongo.app.ui.MongoImage;

public class AnnotateImage extends Activity implements OnClickListener {
	private final String TAG = "AnnotateImage";
	
	public static final String EXTRA_IS_TEMP = "com.gomongo.app.annotateimage.istemp";
	
	private Uri mTempImageUri;
	private AnnotationEditorView mAnnotationEditorView;
	private boolean mIsTempImage;
	
	@Override
	public void onCreate( Bundle savedInstanceState ){
		super.onCreate(savedInstanceState);
		
		setEditorToFullScreen();
		setContentView(R.layout.annotate_image);
		
		mTempImageUri = getIntent().getParcelableExtra(Intent.EXTRA_STREAM);
	    mIsTempImage = getIntent().getBooleanExtra(EXTRA_IS_TEMP, false);
		
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
		
		Button removeButton = (Button)findViewById(R.id.button_delete_sticker);
		removeButton.setOnClickListener(this);
	}

	private void setEditorBackgroundToTempImage() {
		mAnnotationEditorView = (AnnotationEditorView)findViewById(R.id.preview_image_view);
		
		try {
		    InputStream imageStream = getContentResolver().openInputStream(mTempImageUri);
		    
		    BitmapFactory.Options options = getDownsampledBitmapOptions();
		    
            Bitmap backgroundImage = BitmapFactory.decodeStream(imageStream, null, options);
            mAnnotationEditorView.setImageBitmap(backgroundImage);
            
        } catch (FileNotFoundException e) {
            Toast.makeText(this, R.string.error_image_for_editing_missing, Toast.LENGTH_LONG).show();
        }		
	}

    private BitmapFactory.Options getDownsampledBitmapOptions() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPurgeable = true;
        options.inSampleSize = 4;
        return options;
    }

	@Override
	public void onStop() {
		super.onStop();
		
		if( mIsTempImage ) {
		    cleanUpTempImage();
		}
	}

	private void cleanUpTempImage() {
		File tempImageFile = new File( convertAndroidUriToJavaUri( mTempImageUri ) );
		tempImageFile.delete();
	}

	private URI convertAndroidUriToJavaUri( Uri uri ){
		URI javaUri = null;
		
		try {
			String path = uri.getPath();
			if( !path.startsWith("file:/") ) {
			    path = "file://" + path;
			}
		    
		    javaUri = new URI( path );
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
			
			NavigationHelper.shareJpegAtUri(this, Uri.fromFile(imageToShare));
			
			break;
		case R.id.button_delete_sticker:
		    if( !mAnnotationEditorView.isDeleting() ) {
                int redTintColor = 0xFFFF3333;
                view.getBackground().setColorFilter(redTintColor, PorterDuff.Mode.MULTIPLY);
                mAnnotationEditorView.startDeleting();
            }
            else {
                exitDeleteStickerMode(view);
            }
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

    private void exitDeleteStickerMode(View view) {
        int clearTintColor = 0xFFFFFFFF;
        view.getBackground().setColorFilter(clearTintColor, PorterDuff.Mode.MULTIPLY);
        mAnnotationEditorView.stopDeleting();
    }

    private void forceExitDeleteMode() {
        View removeButton = (View)findViewById(R.id.button_delete_sticker);
        exitDeleteStickerMode(removeButton);
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

	Dialog mDialog;
	
	@Override
	public Dialog onCreateDialog( int dialogId ) {
		mDialog = null;
		if ( dialogId == ANNOTATIONS_DIALOG ) {
			mDialog = new Dialog(this);
			mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
			mDialog.setContentView(R.layout.add_annotation_menu);
			
			Map<Integer, List<Integer>> imageIdMap = StaticAnnotationsDefinition.getAnnotations();
			
			Gallery pages = (Gallery)mDialog.findViewById(R.id.gallery_image_annoation_pages);
			pages.setAdapter(new PagingListAdapter(this, imageIdMap, this));
		}
		
		return mDialog;
	}
}
