package com.gomongo.data;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Food {
    
    private static String ID_XPATH = "id";
    @DatabaseField( id = true )
    private int mId;
    public int getId() {
        return mId;
    }
    
    private static String TITLE_XPATH = "title";
    @DatabaseField
    private String mTitle;
    public String getTitle() {
        return mTitle;
    }
    
    private static String CATEGORY_XPATH = "cat";
    public final static String CATEGORY = "category";
    @DatabaseField( index = true, columnName = CATEGORY )
    private String mCategory;
    public String getCategory() {
        return mCategory;
    }
    
    private static String SERVSIZE_XPATH = "ss";
    @DatabaseField
    private String mServingSize;
    public String getServingSize() {
        return mServingSize;
    }
    
    private static String TOTALCAL_XPATH = "tc";
    @DatabaseField
    private float mTotalCalories;
    public float getTotalCalories() {
        return mTotalCalories;
    }
    
    private static String TOTALFAT_XPATH = "tf";
    @DatabaseField
    private float mTotalFat;
    public float getTotalFat() {
        return mTotalFat;
    }
    
    private static String SATFAT_XPATH = "sf";
    @DatabaseField
    private float mSaturatedFat;
    public float getSaturatedFat() {
        return mSaturatedFat;
    }
    
    private static String CARBS_XPATH = "carb";
    @DatabaseField
    private float mCarbs;
    public float getCarbs() {
        return mCarbs;
    }
    
    private static String DIETFIBER_XPATH = "df";
    @DatabaseField
    private float mDietaryFiber;
    public float getDietaryFiber() {
        return mDietaryFiber;
    }
    
    private static String PROTEIN_XPATH = "prot";
    @DatabaseField
    private float mProtein;
    public float getProtein() {
        return mProtein;
    }
    
    private static String DESCRIPTION_XPATH = "description";
    @DatabaseField
    private String mDescription;
    public String getDescription() {
        return mDescription;
    }
    
    @Override
    public boolean equals( Object other ) {
        Food otherFood = (Food)other;
        boolean areEqual = otherFood != null && otherFood.getId() == mId;
        
        return areEqual;
    }
    
    private Food() {}
    
    public static Food getFoodFromXml( Node xmlNode ) {
        Food loadedFood = new Food();
        NamedNodeMap attributes = xmlNode.getAttributes();
        
        loadFoodFieldsFromAttributes(loadedFood, attributes);
        
        return loadedFood;
    }

    private static void loadFoodFieldsFromAttributes(Food loadedFood, NamedNodeMap attributes) {
        loadedFood.mId = Integer.parseInt(attributes.getNamedItem(ID_XPATH).getNodeValue());
        
        loadedFood.mCategory = attributes.getNamedItem(CATEGORY_XPATH).getNodeValue();
        
        loadedFood.mTitle = attributes.getNamedItem(TITLE_XPATH).getNodeValue();
        loadedFood.mDescription = attributes.getNamedItem(DESCRIPTION_XPATH).getNodeValue();
        loadedFood.mServingSize = attributes.getNamedItem(SERVSIZE_XPATH).getNodeValue();
        
        loadedFood.mTotalCalories = Float.parseFloat(attributes.getNamedItem(TOTALCAL_XPATH).getNodeValue());
        loadedFood.mTotalFat = Float.parseFloat(attributes.getNamedItem(TOTALFAT_XPATH).getNodeValue());
        loadedFood.mSaturatedFat = Float.parseFloat(attributes.getNamedItem(SATFAT_XPATH).getNodeValue());
        loadedFood.mCarbs = Float.parseFloat(attributes.getNamedItem(CARBS_XPATH).getNodeValue());
        loadedFood.mDietaryFiber = Float.parseFloat(attributes.getNamedItem(DIETFIBER_XPATH).getNodeValue());
        loadedFood.mProtein = Float.parseFloat(attributes.getNamedItem(PROTEIN_XPATH).getNodeValue());
    }
}
