package com.gomongo.app;

import java.util.ArrayList;
import java.util.List;

import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class MongoItemizedOveraly extends ItemizedOverlay<OverlayItem> {

	List<OverlayItem> mOverlayItems = new ArrayList<OverlayItem>();
	
	public MongoItemizedOveraly(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
	}

	@Override
	protected OverlayItem createItem(int position) {
		return mOverlayItems.get(position);
	}

	public void addOverlay(OverlayItem overlay) {
        mOverlayItems.add(overlay);
        populate();
    }
	
	@Override
	public int size() {
		return mOverlayItems.size();
	}

}
