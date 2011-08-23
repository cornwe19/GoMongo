package com.gomongo.app;

import android.content.Context;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class DismissKeyboardEditorActionInterpreter implements OnEditorActionListener {

    private Context mContext;
    
    public DismissKeyboardEditorActionInterpreter( Context context ) {
        mContext = context;
    }
    
    @Override
    public boolean onEditorAction(TextView editor, int actionId, KeyEvent event) {
        
        if( enterKeyWasPressed(event) ) {
            dismissKeyboard(editor);
        }
        
        return onEnterKeyPressed( editor );
    }

    protected boolean onEnterKeyPressed( TextView editor ) {
        return true;
    }
    
    private boolean enterKeyWasPressed(KeyEvent event) {
        return event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER;
    }

    protected void dismissKeyboard(TextView editor) {
        InputMethodManager inputMethodManager = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(editor.getWindowToken(), 0);
    }

}
