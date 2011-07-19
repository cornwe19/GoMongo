package com.gomongo.app;

import android.view.View;
import android.widget.TextView;

import com.gomongo.data.Food;

public class NutritionFactsViewHelper {
    
    public static void fillOutCommonFactsLayout( View parent, Food food ) {
        setTextOnView(parent, R.id.calories_nut_info, food.getTotalCalories());
        setTextOnView(parent, R.id.fat_nut_info, food.getTotalFat());
        setTextOnView(parent, R.id.satfat_nut_info, food.getSaturatedFat());
        setTextOnView(parent, R.id.carbs_nut_info, food.getCarbs());
        setTextOnView(parent, R.id.fiber_nut_info, food.getDietaryFiber());
        setTextOnView(parent, R.id.protein_nut_info, food.getProtein());
    }
    
    
    private static void setTextOnView(View parent, int viewId, Object text) {
        TextView title = (TextView)parent.findViewById(viewId);
        title.setText(text.toString());
    }
}
