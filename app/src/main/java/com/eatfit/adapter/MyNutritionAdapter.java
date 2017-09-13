package com.eatfit.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eatfit.R;
import com.eatfit.activity.MyNutrition;
import com.eatfit.activity.MyRegimenNutrition;
import com.eatfit.helper.CircleImageView;
import com.eatfit.helper.Utils;
import com.eatfit.model.MealDishes;
import com.eatfit.model.MealHistory;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by appsquadz on 3/8/17.
 */

public class MyNutritionAdapter extends RecyclerView.Adapter<MyNutritionAdapter.NutritionHolder> {
    Context myNutrition;
    ArrayList<MealDishes> list;
    ArrayList<MealHistory> mealHistoryArrayList;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    String date_ = "", newDateString = "";
    String date;


    public MyNutritionAdapter(Context myNutrition, ArrayList<MealDishes> List) {
        this.myNutrition = myNutrition;
        this.list = List;
    }

    public MyNutritionAdapter(Context myNutrition, ArrayList<MealHistory> List, String t) {
        this.myNutrition = myNutrition;
        this.mealHistoryArrayList = List;
        this.date = t;
    }

    @Override
    public NutritionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nutrition_dishes, parent, false);
        return new NutritionHolder(view);
    }

    @Override
    public void onBindViewHolder(final NutritionHolder holder, int position) {

        if (myNutrition instanceof MyRegimenNutrition) {

            newDateString = Utils.ChangeDateToString(mealHistoryArrayList.get(position).getCreated());
            if (mealHistoryArrayList.size() > 0) {
                holder.message.setVisibility(View.GONE);
                holder.dishParent.setVisibility(View.VISIBLE);
                holder.dishName.setText(mealHistoryArrayList.get(position).getName());
                holder.calorieCount.setText(mealHistoryArrayList.get(position).getMinCal());

                Ion.with(myNutrition)
                        .load(mealHistoryArrayList.get(position).getMealImage())
                        .asBitmap()
                        .setCallback(new FutureCallback<Bitmap>() {
                            @Override
                            public void onCompleted(Exception e, Bitmap result) {
                                if (e == null) {
                                    holder.mealImage.setImageBitmap(result);
                                } else {
                                    holder.mealImage.setImageResource(R.mipmap.food_01);
                                }
                            }
                        });
            } else {
                holder.dishParent.setVisibility(View.GONE);
                holder.message.setVisibility(View.VISIBLE);
            }

        } else if (myNutrition instanceof MyNutrition) {
            holder.message.setVisibility(View.GONE);
            holder.dishParent.setVisibility(View.VISIBLE);
            holder.dishName.setText(list.get(position).getName());
            holder.calorieCount.setText(list.get(position).getMinCal());

            Ion.with(myNutrition)
                    .load(list.get(position).getMealImage())
                    .asBitmap()
                    .setCallback(new FutureCallback<Bitmap>() {
                        @Override
                        public void onCompleted(Exception e, Bitmap result) {
                            if (e == null) {
                                holder.mealImage.setImageBitmap(result);
                            } else {
                                holder.mealImage.setImageResource(R.mipmap.food_01);
                            }
                        }
                    });
        }

    }

    @Override
    public int getItemCount() {
        if (myNutrition instanceof MyNutrition) {
            return list.size();
        } else {
            return mealHistoryArrayList.size();
        }
    }

    public class NutritionHolder extends RecyclerView.ViewHolder {
        public TextView calorieCount, addMeal, dishName, message;
        public CircleImageView mealImage;
        public LinearLayout dishParent;

        public NutritionHolder(View itemView) {
            super(itemView);

            calorieCount = (TextView) itemView.findViewById(R.id.dishCalorieCount);
            addMeal = (TextView) itemView.findViewById(R.id.addMeal);
            dishName = (TextView) itemView.findViewById(R.id.dishName);
            mealImage = (CircleImageView) itemView.findViewById(R.id.imageDish);
            dishParent = (LinearLayout) itemView.findViewById(R.id.dishParentLayout);
            message = (TextView) itemView.findViewById(R.id.message);

            message.setVisibility(View.GONE);
        }
    }
}
