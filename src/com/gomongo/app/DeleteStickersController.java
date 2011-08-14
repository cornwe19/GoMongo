package com.gomongo.app;

import java.util.List;

import com.gomongo.app.ui.MongoImage;

import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.view.MotionEvent;

public class DeleteStickersController extends BaseStickerController {

    public DeleteStickersController(List<MongoImage> images) {
        super(images);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        
        performHitTestToSelectImage( event.getX(), event.getY() );
        
        if( mSelectedImage != null && actionWasFingerDown( event.getAction() ) ) {
            mImages.remove(mSelectedImage);
        }
        
        return true;
    }

    private boolean actionWasFingerDown(int actionId) {
        return ( actionId & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN;
    }

    @Override
    public Paint getStickerDrawPaint() {
        
        Paint stickerPaint = new Paint();
        int redTintColor = 0xFF3333;
        int lowGreyToDarkenRed = 0x333;
        ColorFilter filter = new LightingColorFilter( redTintColor, lowGreyToDarkenRed );
        stickerPaint.setColorFilter( filter );
        
        return stickerPaint;
    }
    
}
