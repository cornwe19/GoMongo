package com.gomongo.app;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gomongo.data.Bowl;
import com.gomongo.data.Food;
import com.gomongo.data.IngredientCount;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

public class AddFoodListAdapter extends ArrayAdapter<Food> implements OnClickListener {
    
    private static String TAG = "AddFoodListAdapter";
    
    private static int DONT_CARE_TEXT_RES_ID = 0;
    
    private Bowl mBowl;
    private Dao<IngredientCount,Integer> mIngredientDao;
    
    public AddFoodListAdapter(Context context, Bowl bowl, Dao<IngredientCount,Integer> ingredientDao, List<Food> objects) {
        super(context, DONT_CARE_TEXT_RES_ID, objects);
        
        mBowl = bowl;
        mIngredientDao = ingredientDao;
    }
        
    @Override
    public View getView( int position, View convertView, ViewGroup parent ) {
        LayoutInflater inflater = LayoutInflater.from( getContext() );
        View listItem = inflater.inflate(R.layout.add_food_list_item, null);
        
        Food ingredient = getItem(position);
        
        TextView title = (TextView)listItem.findViewById(R.id.food_item_title);
        title.setText(ingredient.getTitle());
        
        setupPlusMinusButton(position, listItem, R.id.button_food_item_add);
        setupPlusMinusButton(position, listItem, R.id.button_food_item_subtract);
        
        PreparedQuery<IngredientCount> query = prepareIngredientQuery(ingredient);
        
        Integer count = 0;
        
        try {
            IngredientCount ingredientCount = mIngredientDao.queryForFirst(query);
            if( ingredientCount != null ) {
                count = ingredientCount.getCount();
            }
            
        } catch (SQLException ex) {
            handleSQLException(ex);
        }
        
        TextView ingredientCountText = (TextView)listItem.findViewById(R.id.food_item_count);
        ingredientCountText.setText( count.toString() );
        
        return listItem;
    }

    private void handleSQLException(SQLException ex) {
        Log.e(TAG, "There was a problem querying for ingredient counts in the add food adapter", ex);
        
        Toast.makeText(getContext(), R.string.error_problem_connecting_to_database, Toast.LENGTH_LONG).show();
    }

    private PreparedQuery<IngredientCount> prepareIngredientQuery(Food ingredient) {
        QueryBuilder<IngredientCount,Integer> builder = mIngredientDao.queryBuilder();
        PreparedQuery<IngredientCount> query;
        try {
            builder.where().eq( IngredientCount.COL_BOWL_ID, mBowl.getId())
                   .and().eq(IngredientCount.COL_FOOD_ID, ingredient.getId());
            
            query = builder.prepare();
        } catch (SQLException ex) {
            Log.e(TAG, "Query for ingredients couldn't be prepared.", ex);
            
            throw new IllegalStateException( ex );
        }
        
        return query;
    }

    private void setupPlusMinusButton(int position, View viewToSearch, int buttonResource) {
        Button incrementButton = (Button)viewToSearch.findViewById(buttonResource);
        incrementButton.setTag(R.id.tag_food_list_item_id, position);
        incrementButton.setTag(R.id.tag_food_list_item_counter,viewToSearch.findViewById(R.id.food_item_count));
        incrementButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View clickedView) {
       Food ingredient = getItem((Integer)clickedView.getTag(R.id.tag_food_list_item_id));
       
       PreparedQuery<IngredientCount> query = prepareIngredientQuery(ingredient);
       IngredientCount ingredientCount = null;
       
       try {
           ingredientCount = mIngredientDao.queryForFirst(query);
           
           switch( clickedView.getId() ) {
           case R.id.button_food_item_add:
               if( ingredientCount != null ) {
                   ingredientCount.increment();
                   mIngredientDao.update(ingredientCount);
               }
               else {
                   ingredientCount = new IngredientCount( mBowl, ingredient);
                   mIngredientDao.create(ingredientCount);
               }
               break;
           case R.id.button_food_item_subtract:
               if( ingredientCount != null ) {
                   ingredientCount.decrement();
                   mIngredientDao.update(ingredientCount);
               }
               break;
            }
       }
       catch (SQLException ex) {
           handleSQLException(ex);
       }
       
       TextView counter = (TextView)clickedView.getTag(R.id.tag_food_list_item_counter);
       counter.setText(getStringCount(ingredientCount));
    }
    
    private String getStringCount(IngredientCount ingredient) {
        String count = "0";
        
        if( ingredient != null ) {
            count = ingredient.getCount().toString();
        }
        
        return count;
    }
}
