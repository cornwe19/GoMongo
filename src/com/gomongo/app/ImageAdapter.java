package com.gomongo.app;

import java.util.List;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

public class ImageAdapter extends ArrayAdapter<Uri> {

    public ImageAdapter( Context context, List<Uri> imageUris ) {
        super(context, 0, imageUris);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = new ImageView( getContext() );
        
        imageView.setImageURI(getItem(position));
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER); 
        imageView.setLayoutParams(new Gallery.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));        
        
        
        return imageView;
    }
    
}
