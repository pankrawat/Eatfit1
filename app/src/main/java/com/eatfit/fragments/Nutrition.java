package com.eatfit.fragments;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eatfit.R;
import com.eatfit.adapter.NutritionDishesAdapter;
import com.eatfit.constants.Constants;
import com.eatfit.helper.MyProgressDialog;
import com.eatfit.helper.SharedPreference;
import com.eatfit.helper.Utils;
import com.eatfit.model.MealDishes;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class Nutrition extends Fragment implements View.OnClickListener {
    TextView veg, nonVeg;
    boolean category = false;
    LinearLayout dishesBackgroundLay;
    ArrayList<MealDishes> vegList, nonVegList;
    GridLayoutManager dishesGridManager;
    private RecyclerView nutrition;
    private int DEFAULT_SPAN_COUNT = 2;
    private Context context;
    private NutritionDishesAdapter nutritionDishesAdapter;
    private SharedPreference spMain;

    public Nutrition() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Utils.isConnectingToInternet(context)) {
            getDishesList();
        } else {
            Toast.makeText(context, Constants.InternetMessage, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_nutrition, container, false);
        context = getActivity();
        vegList = new ArrayList<>();
        nonVegList = new ArrayList<>();
        initViews(view);
        spMain = spMain.getInstance(context);

        dishesGridManager = new GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false);
        nutrition.setLayoutManager(dishesGridManager);

        veg.setOnClickListener(this);
        nonVeg.setOnClickListener(this);
        return view;
    }

    private void initViews(View view) {
        nutrition = (RecyclerView) view.findViewById(R.id.dishesList);
        veg = (TextView) view.findViewById(R.id.veg);
        nonVeg = (TextView) view.findViewById(R.id.nonVeg);
        dishesBackgroundLay = (LinearLayout) view.findViewById(R.id.buttonVegeterian);
    }

    public void getDishesList() {
        final Dialog dialog = MyProgressDialog.showProgressDialog(getActivity(), "");
        dialog.show();

        JsonObject getMeals = new JsonObject();
        getMeals.addProperty("userId", spMain.getString(Constants.userID, ""));

        Log.e("GetLIST", getMeals.toString());

        Ion.with(context)
                .load(Constants.URL + "allMeals")
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
                                            vegDishes.setMealTime(dataResult.optString("mealTime"));

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
                                            nonVegDishes.setMealTime(dataResult.optString("mealTime"));

                                            nonVegList.add(nonVegDishes);
                                        }
                                    }

                                    if (category) {
                                        nutritionDishesAdapter = new NutritionDishesAdapter(Nutrition.this, context, nonVegList);
                                        nutrition.setAdapter(nutritionDishesAdapter);
                                    } else {
                                        nutritionDishesAdapter = new NutritionDishesAdapter(Nutrition.this, context, vegList);
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

    public void updateList() {
        onResume();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.veg:
                category = false;
                dishesBackgroundLay.setBackground(getResources().getDrawable(R.mipmap.veg));
                nutritionDishesAdapter = new NutritionDishesAdapter(Nutrition.this, context, vegList);
                nutrition.setAdapter(nutritionDishesAdapter);
                break;

            case R.id.nonVeg:
                category = true;
                dishesBackgroundLay.setBackground(getResources().getDrawable(R.mipmap.nonveg));
                nutritionDishesAdapter = new NutritionDishesAdapter(Nutrition.this, context, nonVegList);
                nutrition.setAdapter(nutritionDishesAdapter);
                break;
        }

    }
}
