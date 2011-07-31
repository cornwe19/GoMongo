package com.gomongo.data;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Food {
    
    private static String ROOT = "food";
    private static String TOTAL_ROOT = "totals";
    private static String NUM = "num";
    
    private static String ID_XPATH = "id";
    @DatabaseField( id = true )
    private int mId;
    public int getId() {
        return mId;
    }
    
    public static Food getEmptyWithId( int id ) {
        Food food = new Food();
        food.mId = id;
        return food;
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

    public void writeFoodXml( StringBuilder builder, Integer count ) {
        builder.append( "<" + ROOT );
        
        addAttribute( builder, ID_XPATH, mId );
        addAttribute( builder, TITLE_XPATH, mTitle );
        addAttribute( builder, SERVSIZE_XPATH, mServingSize );
        writeCommonXmlAttributes(builder);
        addAttribute( builder, CATEGORY_XPATH, mCategory );
        addAttribute( builder, NUM, count );
        
        builder.append( " />" );
    }
    
    public void writeSummaryXml(StringBuilder builder) {
        builder.append("<" + TOTAL_ROOT );
        writeCommonXmlAttributes(builder);
        builder.append(" />");
    }
    private void writeCommonXmlAttributes(StringBuilder builder) {
        addAttribute( builder, TOTALCAL_XPATH, mTotalCalories );
        addAttribute( builder, TOTALFAT_XPATH, mTotalFat );
        addAttribute( builder, SATFAT_XPATH, mSaturatedFat );
        addAttribute( builder, CARBS_XPATH, mCarbs );
        addAttribute( builder, DIETFIBER_XPATH, mDietaryFiber );
        addAttribute( builder, PROTEIN_XPATH, mProtein );
    }
    
    private void addAttribute( StringBuilder builder, String attribute, Object value ) {
        builder.append( String.format( " %s=\"%s\"", attribute, value.toString() ) );
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
