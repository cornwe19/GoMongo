package com.gomongo.app;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Filter;

public class FilterLocationHelper implements TextWatcher {

    private Filter mlocationsAdapterFilter;
    
    public FilterLocationHelper( Filter locationsAdapterFilter ) {
        mlocationsAdapterFilter = locationsAdapterFilter;
    }
    
    @Override
    public void afterTextChanged(Editable s) { }

    @Override
    public void beforeTextChanged(CharSequence text, int start, int count, int after) { }

    @Override
    public void onTextChanged(CharSequence text, int start, int before, int count) {
        mlocationsAdapterFilter.filter(text);
    }

}
