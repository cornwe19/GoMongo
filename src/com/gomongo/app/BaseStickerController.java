package com.gomongo.app;

import java.util.List;

import com.gomongo.app.ui.MongoImage;

public abstract class BaseStickerController implements StickerController {
    
    List<MongoImage> mImages;
    MongoImage mSelectedImage;
    
    public BaseStickerController( List<MongoImage> images ) {
        mImages = images;
    }
    
    protected void performHitTestToSelectImage(float x, float y) {
        // Read images in reverse so that topmost images will be selected
        for( int imageIndex = mImages.size()-1; imageIndex >= 0; imageIndex-- ) {
            MongoImage image = mImages.get(imageIndex);
            
            if( image.containsPoint(x, y) ) {
                
                mSelectedImage = image;
                break;
            }
        }
    }
    
}
