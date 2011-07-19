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
    private Float mTotalCalories = 0f;
    public Float getTotalCalories() {
        return mTotalCalories;
    }
    public void addCalories( Float amount ) {
        mTotalCalories += amount;
    }
    
    private static String TOTALFAT_XPATH = "tf";
    @DatabaseField
    private Float mTotalFat = 0f;
    public Float getTotalFat() {
        return mTotalFat;
    }
    public void addTotalFat( Float amount ) {
        mTotalFat += amount;
    }
    
    private static String SATFAT_XPATH = "sf";
    @DatabaseField
    private Float mSaturatedFat = 0f;
    public Float getSaturatedFat() {
        return mSaturatedFat;
    }
    public void addSaturatedFat( Float amount ) {
        mSaturatedFat += amount;
    }
    
    private static String CARBS_XPATH = "carb";
    @DatabaseField
    private Float mCarbs = 0f;
    public Float getCarbs() {
        return mCarbs;
    }
    public void addCarbs( Float amount ) {
        mCarbs += amount;
    }
    
    private static String DIETFIBER_XPATH = "df";
    @DatabaseField
    private Float mDietaryFiber = 0f;
    public Float getDietaryFiber() {
        return mDietaryFiber;
    }
    public void addDietaryFiber( Float amount ) {
        mDietaryFiber += amount;
    }
    
    private static String PROTEIN_XPATH = "prot";
    @DatabaseField
    private Float mProtein = 0f;
    public Float getProtein() {
        return mProtein;
    }
    public void addProtein( Float amount ) {
        mProtein += amount;
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
    
    public Food() {}
    
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
