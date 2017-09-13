package com.eatfit.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.eatfit.R;
import com.eatfit.adapter.MyNutritionAdapter;
import com.eatfit.constants.Constants;
import com.eatfit.helper.MyProgressDialog;
import com.eatfit.helper.SharedPreference;
import com.eatfit.helper.Utils;
import com.eatfit.model.MealHistory;
import com.eatfit.network.NetworkThread;
import com.eatfit.netwrokCallback.NetworkCallback;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MyRegimenNutrition extends AppCompatActivity implements NetworkCallback {

    RecyclerView mealList;
    MyNutritionAdapter myNutritionAdapter;
    ArrayList<MealHistory> mealHistoryArrayList, newMealArraylist;
    Calendar calendar;
    SharedPreference spMain;
    SimpleDateFormat df;
    MealHistory mealHistory;
    String date;
    private Dialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myregimen_nutrition);

        Intent in = getIntent();
        mealHistoryArrayList = (ArrayList<MealHistory>) in.getSerializableExtra("list");
        newMealArraylist = new ArrayList<MealHistory>();
        date = in.getStringExtra("Date");
        spMain = spMain.getInstance(this);

        df = new SimpleDateFormat("yyyy-MM-dd");

        mealList = (RecyclerView) findViewById(R.id.dishesList);

        getMealHistory();

        mealList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

    }

    private void getMealHistory() {
        try {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("userId", spMain.getString(Constants.userID, ""));
            jsonObject.addProperty("date", date);
            Log.e("getMeal History", jsonObject.toString());
            if (Utils.isConnectingToInternet(this)) {
                progressDialog = MyProgressDialog.showProgressDialog(this, "");
                progressDialog.show();
                NetworkThread networkThread = new NetworkThread(this, Constants.URL + "userMealHistory");
                networkThread.getNetworkResponse(this, jsonObject, Constants.SHORT_TIME);
            } else {
                Toast.makeText(this, Constants.InternetMessage, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNetworkSuccess(String result, String fromUrl) {
        if (this != null) {
            try {
                progressDialog.dismiss();
                JSONObject jsonObject = new JSONObject(result);
                Log.e("result", jsonObject.toString());
                if (jsonObject.optBoolean("isSuccess")) {
                    if (fromUrl.contains("userMealHistory")) {
                        for (int i = 0; i < jsonObject.getJSONArray("Result").length(); i++) {
                            JSONObject res = jsonObject.getJSONArray("Result").getJSONObject(i);
                            mealHistory = new Gson().fromJson(res.toString(), MealHistory.class);
                            mealHistoryArrayList.add(mealHistory);
                        }
                        myNutritionAdapter = new MyNutritionAdapter(this, mealHistoryArrayList, date);
                        mealList.setAdapter(myNutritionAdapter);
                    }
                } else {
                    Toast.makeText(this, jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onNetworkTimeOut(String message, String fromUrl) {
        progressDialog.dismiss();
    }
}
