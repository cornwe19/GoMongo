package com.gomongo.app;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.gomongo.data.Food;

public class AddFoodListAdapter extends ArrayAdapter<Food> {

    public AddFoodListAdapter(Context context, int textViewResourceId, List<Food> objects) {
        super(context, textViewResourceId, objects);
    }
    
    @Override
    public View getView( int position, View convertView, ViewGroup parent ) {
        LayoutInflater inflater = LayoutInflater.from( getContext() );
        View listItem = inflater.inflate(R.layout.add_food_list_item, null);
        
        TextView title = (TextView)listItem.findViewById(R.id.food_item_title);
        title.setText(getItem(position).getTitle());
        
        return listItem;
    }
}
