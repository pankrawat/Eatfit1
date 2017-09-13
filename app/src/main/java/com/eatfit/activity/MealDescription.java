package com.eatfit.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eatfit.R;
import com.eatfit.constants.Constants;
import com.eatfit.helper.CircleImageView;
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

public class MealDescription extends AppCompatActivity implements View.OnClickListener {
    TextView calorieCount, protein, fat, carbohydrate, description, dishName, addMealButton;
    CircleImageView dishImages;
    SharedPreference spMain;
    MealDishes dishesList;
    CheckedTextView breakfast, lunch, snacks, dinner;
    String mealId;
    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog alertDialog = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mealdescription);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.mipmap.back);

        spMain = spMain.getInstance(this);
        getIntentData();
        initViews();

        setDesiptionData();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // back button pressed
                onBackPressed();
            }
        });

        addMealButton.setOnClickListener(this);
    }

    private void setDesiptionData() {
        try {
            dishName.setText(dishesList.getName());
            protein.setText(dishesList.getProtein() + "%");
            fat.setText(dishesList.getFat() + "%");
            carbohydrate.setText(dishesList.getCarbohydrate() + "%");
            description.setText(dishesList.getDescription());
            calorieCount.setText(dishesList.getMinCal());
            if (!dishesList.getMealImage().equals("")) {
                Ion.with(MealDescription.this)
                        .load(dishesList.getMealImage())
                        .asBitmap()
                        .setCallback(new FutureCallback<Bitmap>() {
                            @Override
                            public void onCompleted(Exception e, Bitmap result) {
                                dishImages.setImageBitmap(result);
                            }
                        });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getIntentData() {
        Intent intent = getIntent();
        dishesList = (MealDishes) intent.getSerializableExtra("mealData");
    }

    private void initViews() {
        calorieCount = (TextView) findViewById(R.id.dishCalorieCount);
        protein = (TextView) findViewById(R.id.proteinCount);
        fat = (TextView) findViewById(R.id.fatCount);
        carbohydrate = (TextView) findViewById(R.id.carbohydrateCount);
        description = (TextView) findViewById(R.id.description);
        dishName = (TextView) findViewById(R.id.mealTitle);
        dishImages = (CircleImageView) findViewById(R.id.imageMeal);
        addMealButton = (TextView) findViewById(R.id.addMealButton);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.addMealButton:
                selectMealOption();
                break;

            case R.id.breakfast:
                if (breakfast.isChecked()) {
                    Toast.makeText(this, "you already added this meal to breakfast", Toast.LENGTH_SHORT).show();
                } else {
                    if (Utils.isConnectingToInternet(this)) {
                        addToMeal("0");
                    } else {
                        Toast.makeText(this, Constants.InternetMessage, Toast.LENGTH_SHORT).show();
                    }
                }

                break;

            case R.id.lunch:
                if (lunch.isChecked()) {
                    Toast.makeText(this, "you already added this meal to lunch", Toast.LENGTH_SHORT).show();
                } else {
                    if (Utils.isConnectingToInternet(this)) {
                        addToMeal("1");
                    } else {
                        Toast.makeText(this, Constants.InternetMessage, Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case R.id.snacks:
                if (snacks.isChecked()) {
                    Toast.makeText(this, "you already added this meal to snacks", Toast.LENGTH_SHORT).show();
                } else {
                    if (Utils.isConnectingToInternet(this)) {
                        addToMeal("2");
                    } else {
                        Toast.makeText(this, Constants.InternetMessage, Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case R.id.dinner:

                if (dinner.isChecked()) {
                    Toast.makeText(this, "you already added this meal to dinner", Toast.LENGTH_SHORT).show();
                } else {
                    if (Utils.isConnectingToInternet(this)) {
                        addToMeal("3");
                    } else {
                        Toast.makeText(this, Constants.InternetMessage, Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    private void selectMealOption() {

        LayoutInflater li = LayoutInflater.from(this);
        final View promptsView = li.inflate(R.layout.popup_category, null);


        alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.setTitle("Select meal option");

        breakfast = (CheckedTextView) promptsView.findViewById(R.id.breakfast);
        lunch = (CheckedTextView) promptsView.findViewById(R.id.lunch);
        snacks = (CheckedTextView) promptsView.findViewById(R.id.snacks);
        dinner = (CheckedTextView) promptsView.findViewById(R.id.dinner);

        if (dishesList.getMealTime().contains("0")) {
            breakfast.setChecked(true);
//            breakfast.setEnabled(false);
        }
        if (dishesList.getMealTime().contains("1")) {
            lunch.setChecked(true);
//            lunch.setEnabled(false);
        }
        if (dishesList.getMealTime().contains("2")) {
            snacks.setChecked(true);
//            snacks.setEnabled(false);
        }
        if (dishesList.getMealTime().contains("3")) {
            dinner.setChecked(true);
//            dinner.setEnabled(false);
        }

        breakfast.setOnClickListener(this);
        lunch.setOnClickListener(this);
        snacks.setOnClickListener(this);
        dinner.setOnClickListener(this);

        alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.setCancelable(true);
        // show it
        alertDialog.show();
    }

    private void addToMeal(final String mealSection) {
        final Dialog dialog = MyProgressDialog.showProgressDialog(this, "");
        dialog.show();

        JsonObject addMeal = new JsonObject();
        addMeal.addProperty("userId", spMain.getString(Constants.userID, ""));
        addMeal.addProperty("mealId", dishesList.getId());
        addMeal.addProperty("mealTime", mealSection);

        Log.e("addMeal Request", addMeal.toString());

        Ion.with(this)
                .load(Constants.URL + "addToMeal")
                .setJsonObjectBody(addMeal)
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

                                    switch (mealSection) {
                                        case "0":
                                            breakfast.setChecked(true);
                                            dishesList.setIsAdded("0");
                                            break;

                                        case "1":
                                            lunch.setChecked(true);
                                            dishesList.setIsAdded("1");
                                            break;

                                        case "2":
                                            snacks.setChecked(true);
                                            dishesList.setIsAdded("2");
                                            break;

                                        case "3":
                                            dinner.setChecked(true);
                                            dishesList.setIsAdded("3");
                                            break;

                                    }
                                }
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });

    }
}
