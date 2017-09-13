package com.eatfit.activity;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eatfit.R;
import com.eatfit.adapter.MyNutritionAdapter;
import com.eatfit.adapter.NutritionDishesAdapter;
import com.eatfit.constants.Constants;
import com.eatfit.fragments.Nutrition;
import com.eatfit.helper.MyProgressDialog;
import com.eatfit.helper.SharedPreference;
import com.eatfit.helper.Utils;
import com.eatfit.model.MealDishes;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MyNutrition extends AppCompatActivity implements View.OnClickListener {
    TextView veg, nonVeg;
    boolean category = false;
    LinearLayout dishesBackgroundLay;
    ArrayList<MealDishes> vegList, nonVegList;
    private RecyclerView nutrition;
    private Context context;
    private MyNutritionAdapter nutritionDishesAdapter;
    private SharedPreference spMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mynutrition);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.mipmap.back);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // back button pressed
                onBackPressed();
            }
        });

        vegList = new ArrayList<>();
        nonVegList = new ArrayList<>();
        initViews();
        spMain = spMain.getInstance(context);

        if (Utils.isConnectingToInternet(this)) {
            getDishesList();
        } else {
            Toast.makeText(context, Constants.InternetMessage, Toast.LENGTH_SHORT).show();
        }

    }

    private void initViews() {

        nutrition = (RecyclerView) findViewById(R.id.dishesList);
        veg = (TextView) findViewById(R.id.veg);
        nonVeg = (TextView) findViewById(R.id.nonVeg);
        dishesBackgroundLay = (LinearLayout) findViewById(R.id.buttonVegeterian);

        nutrition.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        veg.setOnClickListener(this);
        nonVeg.setOnClickListener(this);
    }


    public void getDishesList() {
        final Dialog dialog = MyProgressDialog.showProgressDialog(this, "");
        dialog.show();

        JsonObject getMeals = new JsonObject();
        getMeals.addProperty("userId", spMain.getString(Constants.userID, ""));

        Log.e("GetMealLIST", getMeals.toString());

        Ion.with(this)
                .load(Constants.URL + "userMealListDetail")
                .setJsonObjectBody(getMeals)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        dialog.dismiss();
                        try {
                            if (result != null && e == null) {
                                JSONObject res = new JSONObject(result);

                                if (res.optBoolean("isSuccess")) {
                                    JSONArray data = res.getJSONArray("Result");
                                    Log.e("Result", res.toString());

                                    if (!vegList.isEmpty())
                                        vegList.clear();
                                    if (!nonVegList.isEmpty())
                                        nonVegList.clear();

                                    for (int i = 0; i < data.length(); i++) {
                                        JSONObject dataResult = data.getJSONObject(i);

                                        if (dataResult.getString("category").equals("0")) {
                                            MealDishes vegDishes = new MealDishes();

                                            vegDishes.setId(dataResult.optString("id"));
                                            vegDishes.setName(dataResult.optString("name"));
                                            vegDishes.setCategory(dataResult.optString("category"));
                                            vegDishes.setDescription(dataResult.optString("description"));
                                            vegDishes.setFat(dataResult.optString("fat"));
                                            vegDishes.setProtein(dataResult.optString("protein"));
                                            vegDishes.setCarbohydrate(dataResult.optString("carbohydrate"));
                                            vegDishes.setMinQuantity(dataResult.optString("minQuantity"));
                                            vegDishes.setMinCal(dataResult.optString("minCal"));
                                            vegDishes.setMealImage(dataResult.optString("mealImage"));
//                                            vegDishes.setMealTime(dataResult.optString("mealTime"));

                                            vegList.add(vegDishes);

                                        } else {
                                            MealDishes nonVegDishes = new MealDishes();

                                            nonVegDishes.setId(dataResult.optString("id"));
                                            nonVegDishes.setName(dataResult.optString("name"));
                                            nonVegDishes.setCategory(dataResult.optString("category"));
                                            nonVegDishes.setDescription(dataResult.optString("description"));
                                            nonVegDishes.setFat(dataResult.optString("fat"));
                                            nonVegDishes.setProtein(dataResult.optString("protein"));
                                            nonVegDishes.setCarbohydrate(dataResult.optString("carbohydrate"));
                                            nonVegDishes.setMinQuantity(dataResult.optString("minQuantity"));
                                            nonVegDishes.setMinCal(dataResult.optString("minCal"));
                                            nonVegDishes.setMealImage(dataResult.optString("mealImage"));
//                                            nonVegDishes.setMealTime(dataResult.optString("mealTime"));

                                            nonVegList.add(nonVegDishes);
                                        }
                                    }

                                    if (category) {
                                        nutritionDishesAdapter = new MyNutritionAdapter(MyNutrition.this, nonVegList);
                                        nutrition.setAdapter(nutritionDishesAdapter);
                                    } else {
                                        nutritionDishesAdapter = new MyNutritionAdapter(MyNutrition.this, vegList);
                                        nutrition.setAdapter(nutritionDishesAdapter);
                                    }

                                }
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                    }
                });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.veg:
                category = false;
                dishesBackgroundLay.setBackground(getResources().getDrawable(R.mipmap.veg));
                nutritionDishesAdapter = new MyNutritionAdapter(MyNutrition.this, vegList);
                nutrition.setAdapter(nutritionDishesAdapter);
                break;

            case R.id.nonVeg:
                category = true;
                dishesBackgroundLay.setBackground(getResources().getDrawable(R.mipmap.nonveg));
                nutritionDishesAdapter = new MyNutritionAdapter(MyNutrition.this, nonVegList);
                nutrition.setAdapter(nutritionDishesAdapter);
                break;
        }
    }
}
