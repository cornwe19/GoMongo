package com.gomongo.app;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

public class PagingListAdapter extends BaseAdapter {
	
	private Context mContext;
	private Map<Integer,List<Integer>> mPagesWithContentIds;	
	private OnClickListener mClickListener;
	
	public PagingListAdapter( Context context, Map<Integer,List<Integer>> pagesWithContentIds, OnClickListener touchListener ) {
		mContext = context;
		mPagesWithContentIds = pagesWithContentIds;
		mClickListener = touchListener;
	}
	
	@Override
	public int getCount() {
		return mPagesWithContentIds.size();
	}

	@Override
	public Object getItem(int position) {
		return mPagesWithContentIds.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if( convertView == null ) {
			View stickerGalleryItem = inflateItemView(R.layout.listitem_sticker_gallery);
			
			fillInGalleryPageWithAnnotations(position, stickerGalleryItem);
			
			convertView = stickerGalleryItem;
		}
		
		return convertView;
	}

	private void fillInGalleryPageWithAnnotations(int position, View stickerGalleryItem) {
		int imageResourceId;
		ImageView imageView;
		
		imageResourceId = mPagesWithContentIds.get(position).get(0);
		imageView = setImageResourceInGallerySlot(stickerGalleryItem, imageResourceId, R.id.top_left);
		setupImageForSelection(imageResourceId, imageView);
		
		imageResourceId = mPagesWithContentIds.get(position).get(1);
		imageView = setImageResourceInGallerySlot(stickerGalleryItem, imageResourceId, R.id.top_right);
		setupImageForSelection(imageResourceId, imageView);
		
		imageResourceId = mPagesWithContentIds.get(position).get(2);
		imageView = setImageResourceInGallerySlot(stickerGalleryItem, imageResourceId, R.id.bottom_left);
		setupImageForSelection(imageResourceId, imageView);
		
		imageResourceId = mPagesWithContentIds.get(position).get(3);
		imageView = setImageResourceInGallerySlot(stickerGalleryItem, imageResourceId, R.id.bottom_right);
		setupImageForSelection(imageResourceId, imageView);
	}

	private void setupImageForSelection(int imageResourceId, ImageView imageView) {
		imageView.setTag(imageResourceId);
		imageView.setOnClickListener(mClickListener);
	}

	private ImageView setImageResourceInGallerySlot(View stickerGalleryItem, int resourceId, int slotId) {
		ImageView imageView = (ImageView)stickerGalleryItem.findViewById(slotId);
		
		imageView.setImageResource(resourceId);
		return imageView;
	}
	
	private View inflateItemView(int resourceIdToInflate) {
		LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
		View viewToInflate = null;
		
		try {
			viewToInflate = inflater.inflate(resourceIdToInflate, null);
		}
		catch( InflateException ex ) {
			Toast.makeText(mContext, ex.getMessage(), Toast.LENGTH_LONG).show();
		}
		
		return viewToInflate;
	}
}
