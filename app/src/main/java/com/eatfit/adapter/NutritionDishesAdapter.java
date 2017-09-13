package com.eatfit.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eatfit.R;
import com.eatfit.activity.MealDescription;
import com.eatfit.constants.Constants;
import com.eatfit.fragments.Nutrition;
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

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by user on 17/4/17.
 */

public class NutritionDishesAdapter extends RecyclerView.Adapter<NutritionDishesAdapter.NutritionHolder> implements View.OnClickListener {


    CheckedTextView breakfast, lunch, snacks, dinner;
    SharedPreference spMain;
    String mealId;
    private ArrayList<MealDishes> dishesList;
    private Context ctx;
    private Nutrition nutrition;
    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog alertDialog = null;

    public NutritionDishesAdapter(Nutrition context, Context ctx, ArrayList<MealDishes> dishesList) {
        this.nutrition = context;
        this.dishesList = dishesList;
        this.ctx = ctx;
        spMain = spMain.getInstance(ctx);
    }

    @Override
    public NutritionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dishes_item, parent, false);
        return new NutritionHolder(view);
    }

    @Override
    public void onBindViewHolder(final NutritionHolder holder, final int position) {

        holder.dishName.setText(dishesList.get(position).getName());
        holder.calorieCount.setText(dishesList.get(position).getMinCal());

        if (!dishesList.get(position).getMealImage().equals("")) {
            Ion.with(ctx)
                    .load(dishesList.get(position).getMealImage())
                    .asBitmap()
                    .setCallback(new FutureCallback<Bitmap>() {
                        @Override
                        public void onCompleted(Exception e, Bitmap result) {
                            holder.mealImage.setImageBitmap(result);
                        }
                    });
        }

        holder.dishParent.setOnClickListener(this);
        holder.addMeal.setOnClickListener(this);
        holder.addMeal.setTag(position);
        holder.dishParent.setTag(position);
    }

    @Override
    public int getItemCount() {
        return dishesList.size();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.addMeal:
                selectMealOption(v.getTag().toString());
                mealId = dishesList.get(Integer.parseInt(v.getTag().toString())).getId();
                break;

            case R.id.dishParentLayout:
                Intent mealDescription = new Intent(ctx, MealDescription.class);
                mealDescription.putExtra("mealData", (Serializable) dishesList.get(Integer.parseInt(v.getTag().toString())));
                ctx.startActivity(mealDescription);
                break;

            case R.id.breakfast:
                if (breakfast.isChecked()) {
                    Toast.makeText(ctx, "you already added this meal to breakfast", Toast.LENGTH_SHORT).show();
                } else {
                    if (Utils.isConnectingToInternet(ctx)) {
                        addToMeal("0");
                    } else {
                        Toast.makeText(ctx, Constants.InternetMessage, Toast.LENGTH_SHORT).show();
                    }
                }

                break;

            case R.id.lunch:
                if (lunch.isChecked()) {
                    Toast.makeText(ctx, "you already added this meal to lunch", Toast.LENGTH_SHORT).show();
                } else {
                    if (Utils.isConnectingToInternet(ctx)) {
                        addToMeal("1");
                    } else {
                        Toast.makeText(ctx, Constants.InternetMessage, Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case R.id.snacks:
                if (snacks.isChecked()) {
                    Toast.makeText(ctx, "you already added this meal to snacks", Toast.LENGTH_SHORT).show();
                } else {
                    if (Utils.isConnectingToInternet(ctx)) {
                        addToMeal("2");
                    } else {
                        Toast.makeText(ctx, Constants.InternetMessage, Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case R.id.dinner:

                if (dinner.isChecked()) {
                    Toast.makeText(ctx, "you already added this meal to dinner", Toast.LENGTH_SHORT).show();
                } else {
                    if (Utils.isConnectingToInternet(ctx)) {
                        addToMeal("3");
                    } else {
                        Toast.makeText(ctx, Constants.InternetMessage, Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }

    }

    private void addToMeal(final String mealSection) {
        final Dialog dialog = MyProgressDialog.showProgressDialog((Activity) ctx, "");
        dialog.show();

        JsonObject addMeal = new JsonObject();
        addMeal.addProperty("userId", spMain.getString(Constants.userID, ""));
        addMeal.addProperty("mealId", mealId);
        addMeal.addProperty("mealTime", mealSection);

        Log.e("addMeal Request", addMeal.toString());

        Ion.with(ctx)
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
//                                    dishesList.get(tag).setIsAdded("0");
                                    switch (mealSection) {
                                        case "0":
                                            breakfast.setChecked(true);
                                            nutrition.updateList();
                                            break;

                                        case "1":
                                            lunch.setChecked(true);
                                            nutrition.updateList();
                                            break;

                                        case "2":
                                            snacks.setChecked(true);
                                            nutrition.updateList();
                                            break;

                                        case "3":
                                            dinner.setChecked(true);
                                            nutrition.updateList();
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

    private void selectMealOption(String v) {

        LayoutInflater li = LayoutInflater.from(ctx);
        final View promptsView = li.inflate(R.layout.popup_category, null);


        alertDialogBuilder = new AlertDialog.Builder(ctx);
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.setTitle("Select meal option");

        breakfast = (CheckedTextView) promptsView.findViewById(R.id.breakfast);
        lunch = (CheckedTextView) promptsView.findViewById(R.id.lunch);
        snacks = (CheckedTextView) promptsView.findViewById(R.id.snacks);
        dinner = (CheckedTextView) promptsView.findViewById(R.id.dinner);

        if (dishesList.get(Integer.parseInt(v)).getMealTime().contains("0")) {
            breakfast.setChecked(true);
//            breakfast.setEnabled(false);
        }
        if (dishesList.get(Integer.parseInt(v)).getMealTime().contains("1")) {
            lunch.setChecked(true);
//            lunch.setEnabled(false);
        }
        if (dishesList.get(Integer.parseInt(v)).getMealTime().contains("2")) {
            snacks.setChecked(true);
//            snacks.setEnabled(false);
        }
        if (dishesList.get(Integer.parseInt(v)).getMealTime().contains("3")) {
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

    public class NutritionHolder extends RecyclerView.ViewHolder {
        public TextView calorieCount, addMeal, dishName;
        public CircleImageView mealImage;
        public LinearLayout dishParent;

        public NutritionHolder(View itemView) {
            super(itemView);

            calorieCount = (TextView) itemView.findViewById(R.id.dishCalorieCount);
            addMeal = (TextView) itemView.findViewById(R.id.addMeal);
            dishName = (TextView) itemView.findViewById(R.id.dishName);
            mealImage = (CircleImageView) itemView.findViewById(R.id.imageDish);
            dishParent = (LinearLayout) itemView.findViewById(R.id.dishParentLayout);
        }
    }

}
