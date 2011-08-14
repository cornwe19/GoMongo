package com.gomongo.app;

import android.graphics.Paint;
import android.view.MotionEvent;

public interface StickerController {

    boolean onTouchEvent( MotionEvent event );
    
    Paint getStickerDrawPaint();
}
