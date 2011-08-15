package com.gomongo.app;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.gomongo.data.Bowl;
import com.gomongo.data.Food;
import com.gomongo.data.IngredientCount;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

public class AddFoodListAdapter extends ArrayAdapter<Food> implements OnClickListener, OnItemClickListener {
    
    private static String TAG = "AddFoodListAdapter";
    
    private static int DONT_CARE_TEXT_RES_ID = 0;
    
    private Bowl mBowl;
    private Dao<IngredientCount,Integer> mIngredientDao;
    
    private Category mCategoryInfo;
    
    private List<Food> mItems;
    
    private int mSelectedPosition = -1;
    
    private HashMap<Integer,Integer> mCounts = new HashMap<Integer,Integer>();
    private int mCountTotalIngredients = 0;
    
    public AddFoodListAdapter(Context context, Bowl bowl, Dao<IngredientCount,Integer> ingredientDao, List<Food> items, Category categoryInfo ) {
        super(context, DONT_CARE_TEXT_RES_ID, items);
        
        mBowl = bowl;
        mIngredientDao = ingredientDao;
        mCategoryInfo = categoryInfo;
        
        mItems = items;
        
        try {
            Iterator<IngredientCount> ingredientCountIter = ingredientDao.iterator( prepareIngredientCountQuery() );
            
            while( ingredientCountIter.hasNext() ) {
                IngredientCount ingredientCount = ingredientCountIter.next();
                
                mCounts.put(ingredientCount.getIngredient().getId(),ingredientCount.getCount());
                mCountTotalIngredients += ingredientCount.getCount();
            }
        } catch (SQLException ex) {
            handleSQLException(ex);
        }
    }

    @Override
    public View getView( int position, View listItem, ViewGroup parent ) {
        
        LayoutInflater inflater = LayoutInflater.from( getContext() );
        listItem = inflater.inflate(R.layout.add_food_list_item, null);
        
        Food ingredient = getItem(position);
        
        setTextOnView(listItem, R.id.food_item_title, ingredient.getTitle());
        
        if( position == mSelectedPosition ) {
            
            View mainItemUI = listItem.findViewById(R.id.add_food_minimized_ui);
            mainItemUI.setBackgroundResource(R.drawable.active_ingredient_background);
            
            setTextOnView(listItem, R.id.serving_nut_info, ingredient.getServingSize());
            NutritionFactsViewHelper.fillOutCommonFactsLayout(listItem, ingredient);
            
            Button moreInfoButton = (Button)listItem.findViewById(R.id.button_food_item_more_info);
            moreInfoButton.setVisibility(View.VISIBLE);
            
            moreInfoButton.setTag(listItem.findViewById(R.id.more_details_pane));
            moreInfoButton.setOnClickListener( this );
          
            LinearLayout counterControls = (LinearLayout)listItem.findViewById(R.id.food_item_count_controls);
            counterControls.setVisibility(View.VISIBLE);
            
            setupPlusMinusButton(ingredient.getId(), listItem, R.id.button_food_item_add);
            setupPlusMinusButton(ingredient.getId(), listItem, R.id.button_food_item_subtract);
            
            Integer count = getCountOrDefault0(ingredient.getId());
            
            TextView ingredientCountText = (TextView)listItem.findViewById(R.id.food_item_count);
            ingredientCountText.setText( count.toString() );
        }
        
        return listItem;
    }

    private Integer getCountOrDefault0(Integer ingredientId) {
        Integer count = mCounts.get(ingredientId);
        if( count == null ) {
            count = 0;
        }
        return count;
    }

    private void setTextOnView(View listItem, int viewId, Object text) {
        TextView title = (TextView)listItem.findViewById(viewId);
        title.setText(text.toString());
    }

    private void handleSQLException(SQLException ex) {
        Log.e(TAG, "There was a problem querying for ingredient counts in the add food adapter", ex);
        
        Toast.makeText(getContext(), R.string.error_problem_connecting_to_database, Toast.LENGTH_LONG).show();
    }

    private PreparedQuery<IngredientCount> prepareIngredientCountQuery() {
        QueryBuilder<IngredientCount,Integer> builder = mIngredientDao.queryBuilder();
        PreparedQuery<IngredientCount> query;
        
        List<Integer> ingredientIds = getIdsForFoodsInThisCategory();
        
        try {
            builder.where().eq( IngredientCount.COL_BOWL_ID, mBowl.getId())
                .and().in(IngredientCount.COL_FOOD_ID, ingredientIds);
            
            query = builder.prepare();
        } catch (SQLException ex) {
            Log.e(TAG, "Query for ingredients couldn't be prepared.", ex);
            
            throw new IllegalStateException( ex );
        }
        
        return query;
    }

    private List<Integer> getIdsForFoodsInThisCategory() {
        List<Integer> ingredientIds = new ArrayList<Integer>();
        for( Food ingredient : mItems ) {
            ingredientIds.add(ingredient.getId());
        }
        return ingredientIds;
    }

    private void setupPlusMinusButton(int id, View viewToSearch, int buttonResource) {
        Button incrementButton = (Button)viewToSearch.findViewById(buttonResource);
        incrementButton.setTag(R.id.tag_food_list_food_id, id);
        incrementButton.setTag(R.id.tag_food_list_item_counter,viewToSearch.findViewById(R.id.food_item_count));
        incrementButton.setOnClickListener(this);
    }

    
    
    @Override
    public void onClick(View clickedView) {
       if( clickedView.getId() == R.id.button_food_item_more_info ) {
           toggleNutritionInfo(clickedView);
       }
       else {
           incrementOrDecrementFoodCount(clickedView);
       }
    }

    private void toggleNutritionInfo(View clickedView) {
        View moreDetailsPane = (View)clickedView.getTag();
        Button moreInfoButton = (Button)clickedView;
       
        if( moreDetailsPane.getVisibility() != View.VISIBLE ) {
            moreDetailsPane.setVisibility(View.VISIBLE);
            moreInfoButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.expander_ic_maximized, 0);
        }
        else {
            moreDetailsPane.setVisibility(View.GONE);
            moreInfoButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.expander_ic_minimized, 0);
        }
    }

    private void incrementOrDecrementFoodCount(View clickedView) {
       Integer ingredientId = (Integer)clickedView.getTag(R.id.tag_food_list_food_id);
       
       Integer ingredientCount = null;
       ingredientCount = getCountOrDefault0(ingredientId);
       
       switch( clickedView.getId() ) {
       
       case R.id.button_food_item_add:
           
           if( mCountTotalIngredients < mCategoryInfo.getMaxIngredients() ) {
               mCounts.put(ingredientId, ++ingredientCount);
               mCountTotalIngredients++;
           }
           else {
               final AlertDialog.Builder cantAddFoodAlert = new AlertDialog.Builder(getContext());
               cantAddFoodAlert.setTitle(mCategoryInfo.getOverIngredientLimitTitle());
               cantAddFoodAlert.setMessage(mCategoryInfo.getOverIngredientLimitMessage());
               cantAddFoodAlert.setPositiveButton(R.string.button_cant_add_food_dismiss, null);
               
               cantAddFoodAlert.show();
           }
           
           break;
       case R.id.button_food_item_subtract:
           if( ingredientCount > 0 ) {
               mCounts.put(ingredientId, --ingredientCount);
               mCountTotalIngredients--;
           }
           
           break;
       }
       
       TextView counter = (TextView)clickedView.getTag(R.id.tag_food_list_item_counter);
       counter.setText(ingredientCount.toString());
    }
    
    public void save() throws SQLException {
        for ( Map.Entry<Integer, Integer> entry : mCounts.entrySet() ) {
            QueryBuilder<IngredientCount,Integer> builder = mIngredientDao.queryBuilder();
            builder.where().eq(IngredientCount.COL_BOWL_ID, mBowl.getId())
                .and().eq(IngredientCount.COL_FOOD_ID, entry.getKey());
            
            IngredientCount ingredientCount = mIngredientDao.queryForFirst(builder.prepare());
            if( ingredientCount != null ) {
                ingredientCount.setCount(entry.getValue());
                mIngredientDao.update(ingredientCount);
            }
            else {
                ingredientCount = new IngredientCount( mBowl, Food.getEmptyWithId(entry.getKey()) );
                ingredientCount.setCount(entry.getValue());
                mIngredientDao.create(ingredientCount);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View selectedView, int position, long id) {
//        for( View visibleControl : mLastSelectedItemControls ) {
//            visibleControl.setVisibility(View.INVISIBLE);
//        }
//        mLastSelectedItemControls.clear();
//        
//        if( mLastSelectedItem != null ) {
//            mLastSelectedItem.setSelected(false);
//        }
        
        mSelectedPosition = position;
        
//        mLastSelectedItem = selectedView;
//        mLastSelectedItem.setSelected(true);
//        
//        Button moreInfoButton = (Button)mLastSelectedItem.findViewById(R.id.button_food_item_more_info);
//        moreInfoButton.setVisibility(View.VISIBLE);
//        
//        LinearLayout counterControls = (LinearLayout)mLastSelectedItem.findViewById(R.id.food_item_count_controls);
//        counterControls.setVisibility(View.VISIBLE);
//        
//        mLastSelectedItemControls.add( moreInfoButton );
//        mLastSelectedItemControls.add( counterControls );
        
        parent.invalidate();
    }
}
