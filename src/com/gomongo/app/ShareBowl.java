package com.gomongo.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.gomongo.data.Bowl;
import com.gomongo.data.DatabaseOpenHelper;
import com.gomongo.data.Food;
import com.gomongo.data.IngredientCount;
import com.gomongo.net.WebHelper;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

public class ShareBowl extends OrmLiteBaseActivity<DatabaseOpenHelper> implements OnClickListener {
    
    private static String TAG = "ShareBowl";
    
    private static String CREATE_BOWL_URL = "http://www.gomongo.com/iphone/iPhonePrintRecipe.php";
    
    private static int NUTRITION_INFO_DIALOG_ID = 0x1;
    private static String ROOT = "root";
    private static String BOWL_NAME = "bowlname";
    
    private static File TEMP_RECIPE_IMAGE = new File( MongoPhoto.PICTURE_TEMP_DIR, "recipe.jpg" );
    
    private Food mTotalNutritionContainer = new Food(); 
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.share_bowl);
        
        View navigationMenu = (View)findViewById(R.id.nav_menu);
        
        NavigationHelper.setupButtonToLaunchActivity(this, navigationMenu, R.id.button_home, Home.class);
        NavigationHelper.setupButtonToLaunchActivity(this, navigationMenu, R.id.button_find_us, FindUs.class);
        ImageButton createButton = (ImageButton)navigationMenu.findViewById(R.id.button_create);
        createButton.setSelected(true);
        NavigationHelper.setupButtonToLaunchActivity(this, navigationMenu, R.id.button_photo, MongoPhoto.class);
        NavigationHelper.setupButtonToLaunchActivity(this, navigationMenu, R.id.button_about, About.class);
        
        int bowlId = getIntent().getExtras().getInt(CreateBowl.EXTRA_BOWL_ID);
        try {
            Dao<IngredientCount,Integer> ingredientDao = getHelper().getDao(IngredientCount.class);
            Dao<Food,Integer> foodDao = getHelper().getDao(Food.class);
            
            Dao<Bowl,Integer> bowlDao = getHelper().getDao(Bowl.class);
            Bowl bowl = bowlDao.queryForId(bowlId);
            
            TextView shareTitle = (TextView)findViewById(R.id.textview_bowl_title);
            shareTitle.setText(bowl.getTitle());
            
            QueryBuilder<IngredientCount,Integer> builder = ingredientDao.queryBuilder();
            builder.where().eq( IngredientCount.COL_BOWL_ID, bowlId).and().gt(IngredientCount.COL_COUNT, 0);
            List<IngredientCount> allIngredients = ingredientDao.query( builder.prepare() );
            
            StringBuilder xmlBuilder = prepareShareBowlXmlPayload(bowl);
            
            for( IngredientCount ingredientCount : allIngredients ) {
                Food ingredient = ingredientCount.getIngredient();
                foodDao.refresh(ingredient);
                
                ingredient.writeFoodXml(xmlBuilder, ingredientCount.getCount());
                
                mTotalNutritionContainer.addCalories(ingredient.getTotalCalories() * ingredientCount.getCount());
                mTotalNutritionContainer.addProtein(ingredient.getProtein() * ingredientCount.getCount());
                mTotalNutritionContainer.addTotalFat(ingredient.getTotalFat() * ingredientCount.getCount());
                mTotalNutritionContainer.addSaturatedFat(ingredient.getSaturatedFat() * ingredientCount.getCount());
                mTotalNutritionContainer.addCarbs(ingredient.getCarbs() * ingredientCount.getCount());
                mTotalNutritionContainer.addDietaryFiber(ingredient.getDietaryFiber() * ingredientCount.getCount());
            }
            
            finishShareBowlXmlPayload(xmlBuilder);
            
            InputStream response = WebHelper.postGetResponse(CREATE_BOWL_URL, buildPostDataForBowl(bowl, xmlBuilder) );
            
            compressResponseToTempImage(response);
            
        } catch (SQLException ex ) {
            Log.w(TAG, "Couldn't open the database for writing", ex );
            
            Toast.makeText(this, R.string.error_problem_connecting_to_database, Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Log.w(TAG, "Problem connecting to internet on prepare share bowl call." );
            
            Toast.makeText(this, R.string.error_preparing_bowl, Toast.LENGTH_LONG).show();
            
            finish();
        }
        
        Button shareBowlButton = (Button)findViewById( R.id.button_share_bowl );
        shareBowlButton.setOnClickListener(this);
        
        Button startOver = (Button)findViewById(R.id.button_create_another_bowl);
        startOver.setOnClickListener(this);
        
        Button nutritionInfo = (Button)findViewById(R.id.button_nutrition_info);
        nutritionInfo.setOnClickListener(this);
    }

    private void finishShareBowlXmlPayload(StringBuilder xmlBuilder) {
        mTotalNutritionContainer.writeSummaryXml(xmlBuilder);
        
        xmlBuilder.append( String.format( "</%s>", ROOT ) );
    }

    private StringBuilder prepareShareBowlXmlPayload(Bowl bowl) {
        StringBuilder xmlBuilder = new StringBuilder();
        xmlBuilder.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        xmlBuilder.append(String.format("<%s>", ROOT));
        xmlBuilder.append(String.format( "<%1$s>%2$s</%1$s>", BOWL_NAME, bowl.getTitle() ) );
        return xmlBuilder;
    }

    private void compressResponseToTempImage(InputStream response) throws FileNotFoundException, IOException {
        Bitmap image = BitmapFactory.decodeStream(response);
        
        OutputStream fileStream = new FileOutputStream(TEMP_RECIPE_IMAGE);
        image.compress(CompressFormat.JPEG, 85, fileStream);
        fileStream.close();
    }

    private String buildPostDataForBowl(Bowl bowl, StringBuilder xmlBuilder) {
        return String.format("printBowlName=%s&printBowl=%s", bowl.getTitle(), xmlBuilder.toString());
    }

    @Override
    public Dialog onCreateDialog( int id ) {
        Dialog dialog = new Dialog(this);
        if( id == NUTRITION_INFO_DIALOG_ID ) {
            dialog.setContentView(R.layout.nutrition_info);
            dialog.setTitle(R.string.nut_info_title);
            View nutritionalTableRoot = dialog.findViewById(R.id.nutrition_info_main);
            
            removeUnnecessaryTableRowsFromDialog( nutritionalTableRoot );
            
            NutritionFactsViewHelper.fillOutCommonFactsLayout(nutritionalTableRoot, mTotalNutritionContainer);
        }
        
        return dialog;
    }

    private void removeUnnecessaryTableRowsFromDialog( View tableRoot ) {
        View inTableTitle = tableRoot.findViewById(R.id.nutrition_in_table_title);
        inTableTitle.setVisibility(View.GONE);
        
        View servingSizeRow = tableRoot.findViewById(R.id.nutrition_serving_size_row);
        servingSizeRow.setVisibility(View.GONE);
    }
    
    @Override
    public void onClick(View clickedView) {
        
        switch( clickedView.getId() ) {
        case R.id.button_nutrition_info:
            showDialog(NUTRITION_INFO_DIALOG_ID);
            break;
        case R.id.button_share_bowl:
            NavigationHelper.shareJpegAtUri(this, Uri.fromFile(TEMP_RECIPE_IMAGE));
            break;
        case R.id.button_create_another_bowl:
            Intent createNewBowl = new Intent( this, CreateBowl.class );
            startActivity(createNewBowl);
            break;
        }
        
    }
}
