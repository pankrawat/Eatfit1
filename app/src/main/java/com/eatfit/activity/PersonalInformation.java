package com.eatfit.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.eatfit.R;
import com.eatfit.adapter.NothingSelectedSpinnerAdapter;
import com.eatfit.constants.Constants;
import com.eatfit.fragments.FeedFragment;
import com.eatfit.helper.MyProgressDialog;
import com.eatfit.helper.SharedPreference;
import com.eatfit.helper.Utils;
import com.eatfit.model.UserModel;
import com.eatfit.network.NetworkThread;
import com.eatfit.netwrokCallback.NetworkCallback;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONObject;

import static com.eatfit.constants.Constants.gender;
import static com.eatfit.constants.Constants.profileImage;

public class PersonalInformation extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, NetworkCallback {

    TextView caloryCount;
    EditText age, weight, height, bodyFat, exerciseLevel;
    ImageView genderMale, genderFemale;
    String uage = "", uweight = "", uheight = "", ubodyFat = "", uexerciseLevel = "", refCode = "";
    int gender = 1;
    UserModel user;
    AppCompatSpinner exerciseLevelSpinner;
    ArrayAdapter<String> adapterexercise;
    String[] exerciseLevelCat = new String[]
            {"Sedentary",
                    "Mild activity level",
                    "Moderate activity level",
                    "Heavy or (Labor-intensive) activity level",
                    "Extreme level"
            };

    String[] exerciseLevelValue = new String[]{"1.2", "1.375", "1.55", "1.7", "1.9"};
    private SharedPreference spMain;
    private String CATEGORY_SPINER_LABLE = "Select Exercise Level";
    private boolean decider = false;
    private Dialog progressDialog;
    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog alertDialog = null;
    private String profile_Image = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalinformation);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        // TODO get back button on toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // TODO set custom navigation icon on toolbar
        toolbar.setNavigationIcon(R.mipmap.back_black);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        spMain = spMain.getInstance(this);
        getIntentData();
        initViews();
        user = new UserModel();

        adapterexercise = new ArrayAdapter<String>(this, R.layout.textview_layout_spiner, exerciseLevelCat);
        adapterexercise.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        exerciseLevelSpinner.setPrompt("Select a gender");
        exerciseLevelSpinner.setAdapter(new NothingSelectedSpinnerAdapter(adapterexercise, R.layout.spiner_default_text_layout, this, CATEGORY_SPINER_LABLE));


        /*TO handle the spinner item at exercise level Pop up*/
        View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                switch (v.getId()) {
                    case R.id.spinerExercise:
                 /*   case R.id.spinerFuelType:
                    case R.id.spinerVehicleType:*/
                        if (hasFocus) {
                            Utils.hideSoftKeyBoard(PersonalInformation.this);
                        }
                        break;
                }
            }
        };
        exerciseLevelSpinner.setOnFocusChangeListener(focusChangeListener);

        if (decider) {
            getUserDetails();
        }

        /*To check that if already the gender is selected from previous Screen*/

        if (spMain.getString(Constants.gender, "").equals("1")) {
            gender = 1;
            genderMale.setBackground(getResources().getDrawable(R.drawable.background_login));
            genderMale.setImageResource(R.mipmap.female);
            genderFemale.setBackground(getResources().getDrawable(R.drawable.gendermale));
            genderFemale.setImageResource(R.mipmap.male_blue);
        } else {
            gender = 0;
            genderFemale.setBackground(getResources().getDrawable(R.drawable.background_login));
            genderFemale.setImageResource(R.mipmap.male);
            genderMale.setBackground(getResources().getDrawable(R.drawable.gendermale));
            genderMale.setImageResource(R.mipmap.female_blue);
        }

        genderFemale.setOnClickListener(this);
        genderMale.setOnClickListener(this);
        caloryCount.setOnClickListener(this);
        exerciseLevelSpinner.setOnItemSelectedListener(this);
    }

    private void getUserDetails() {
        try {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("userId", spMain.getString(Constants.userID, ""));

            Log.e("getUserDetails", jsonObject.toString());

            if (Utils.isConnectingToInternet(this)) {
                progressDialog = MyProgressDialog.showProgressDialog(this, "");
                progressDialog.show();
                NetworkThread networkThread = new NetworkThread(this, Constants.URL + "getUsersDetails");
                networkThread.getNetworkResponse(this, jsonObject, Constants.SHORT_TIME);
            } else {
                Toast.makeText(this, Constants.InternetMessage, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getIntentData() {
        decider = getIntent().getBooleanExtra("navigater", false);
    }

    private void initViews() {

        age = (EditText) findViewById(R.id.age);
        weight = (EditText) findViewById(R.id.weight);
        height = (EditText) findViewById(R.id.height);
        bodyFat = (EditText) findViewById(R.id.bodyFat);
        exerciseLevel = (EditText) findViewById(R.id.exerciseLevel);
        genderFemale = (ImageView) findViewById(R.id.female);
        genderMale = (ImageView) findViewById(R.id.male);
        caloryCount = (TextView) findViewById(R.id.caloriesButton);
        exerciseLevelSpinner = (AppCompatSpinner) findViewById(R.id.spinerExercise);

    }

    private String getProfileImageUser() {
        if (!spMain.getString(Constants.LOGINTYPE, "").equals("0")) {
            if (spMain.getString(Constants.profileImage, "").equals("")) {
                Ion.with(this).load(spMain.getString(Constants.profileUrl, "")).asBitmap()
                        .setCallback(new FutureCallback<Bitmap>() {
                            @Override
                            public void onCompleted(Exception e, Bitmap result) {
                                profile_Image = Utils.encodeToBase64(result, Bitmap.CompressFormat.JPEG, 50);
                                spMain.putString(Constants.profileImage, profile_Image);
                            }
                        });
                return profile_Image;
            } else {
                return spMain.getString(Constants.profileImage, "");
            }
        } else {
            return profile_Image;
        }
    }

    private void calorieCount() {

        final Dialog dialog = MyProgressDialog.showProgressDialog(this, "");
        dialog.show();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("email", "");
        jsonObject.addProperty("first_name", "");
        jsonObject.addProperty("last_name", "");
        jsonObject.addProperty("password", "");
        jsonObject.addProperty("phone", "");
        jsonObject.addProperty("gender", String.valueOf(gender));
        jsonObject.addProperty("age", uage);
        jsonObject.addProperty("height", uheight);
        jsonObject.addProperty("weight", uweight);
        jsonObject.addProperty("exercise_level", uexerciseLevel);
        jsonObject.addProperty("body_fat", ubodyFat);
        jsonObject.addProperty("user_id", spMain.getString(Constants.userID, ""));
        jsonObject.addProperty("refCode", refCode);

        if (!spMain.getBoolean(Constants.ISProfile, false))
            jsonObject.addProperty("profile_image", getProfileImageUser());
        else
            jsonObject.addProperty("profile_image", "");

        Log.d("CALORY ", jsonObject.toString());

        Ion.with(this)
                .load(Constants.URL + "edit_user")
                .setJsonObjectBody(jsonObject)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        dialog.dismiss();
                        try {
                            if (e == null && result != null) {
                                JSONObject mainData = new JSONObject(result);

                                if (mainData.optBoolean("is_success")) {
                                    JSONObject res = mainData.getJSONObject("Result");

                                    spMain.putInteger(Constants.CALORIE, (int) Double.parseDouble(res.optString("calorie")));
                                    spMain.putString(Constants.profileURL, res.optString("profile_image"));
                                    spMain.putBoolean(Constants.ISProfile, true);
                                    Intent home = new Intent(PersonalInformation.this, Home.class);
                                    home.putExtra("calorie", res.getString("calorie"));
                                    startActivity(home);
                                    finishAffinity();
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

            case R.id.caloriesButton:
                uage = age.getText().toString();
                ubodyFat = bodyFat.getText().toString();
                uheight = height.getText().toString();
                uweight = weight.getText().toString();

                if (validate()) {
                    if (!decider) {
                        cancelPopup();
                    } else {
                        if (Utils.isConnectingToInternet(this)) {
                            calorieCount();
                        } else {
                            Toast.makeText(this, Constants.InternetMessage, Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                break;

            case R.id.male:

                gender = 1;
                genderMale.setBackground(getResources().getDrawable(R.drawable.background_login));
                genderMale.setImageResource(R.mipmap.female);
                genderFemale.setBackground(getResources().getDrawable(R.drawable.gendermale));
                genderFemale.setImageResource(R.mipmap.male_blue);

                break;

            case R.id.female:

                gender = 0;
                genderFemale.setBackground(getResources().getDrawable(R.drawable.background_login));
                genderFemale.setImageResource(R.mipmap.male);
                genderMale.setBackground(getResources().getDrawable(R.drawable.gendermale));
                genderMale.setImageResource(R.mipmap.female_blue);

                break;
        }

    }


    private void cancelPopup() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.popup_referral);

        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);

        TextView submit = (TextView) dialog.findViewById(R.id.submit);
        TextView skip = (TextView) dialog.findViewById(R.id.skip);
        final EditText reason = (EditText) dialog.findViewById(R.id.reason);


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reason.getText().toString().trim().equals("")) {
                    refCode = reason.getText().toString().trim();
                    if (Utils.isConnectingToInternet(PersonalInformation.this)) {
                        calorieCount();
                    } else {
                        Toast.makeText(PersonalInformation.this, Constants.InternetMessage, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(PersonalInformation.this, "Please enter the referral code", Toast.LENGTH_SHORT).show();
                }
            }
        });


        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                refCode = "";
                if (Utils.isConnectingToInternet(PersonalInformation.this)) {
                    calorieCount();
                } else {
                    Toast.makeText(PersonalInformation.this, Constants.InternetMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
    }

    private boolean validate() {

        if (uweight.equals("") || uheight.equals("") || ubodyFat.equals("") || uexerciseLevel.equals("") || uage.equals("")) {
            Toast.makeText(this, "Please enter required fields", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.spinerExercise) {

            switch (position) {
                case 1:
                    uexerciseLevel = exerciseLevelValue[position - 1];
                    break;
                case 2:
                    uexerciseLevel = exerciseLevelValue[position - 1];
                    break;
                case 3:
                    uexerciseLevel = exerciseLevelValue[position - 1];
                    break;
                case 4:
                    uexerciseLevel = exerciseLevelValue[position - 1];
                    break;
                case 5:
                    uexerciseLevel = exerciseLevelValue[position - 1];
                    break;

            }
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onNetworkSuccess(String result, String fromUrl) {
        if (this != null) {
            progressDialog.dismiss();
            try {
                JSONObject jsonObject = new JSONObject(result);
                Log.e("result", jsonObject.toString());
                if (jsonObject.optBoolean("isSuccess")) {
                    if (fromUrl.contains("getUsersDetails")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("Result");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject userObject = jsonArray.getJSONObject(i);

                            user.setId(userObject.getString("id"));
                            user.setName(userObject.getString("name"));
                            user.setEmail(userObject.getString("email"));
                            user.setPhone(userObject.getString("phone"));
                            user.setGender(userObject.getString("gender"));
                            user.setProfileImage(userObject.getString("profile_image"));
                            user.setAge(userObject.getString("age"));
                            user.setHeight(userObject.getString("height"));
                            user.setWeight(userObject.getString("weight"));
                            user.setCalorie(userObject.getString("calorie"));
                            user.setExerciseLevel(userObject.getString("exercise_level"));
                            user.setBodyFat(userObject.getString("body_fat"));

                            setDataUser();
                        }
                    }
                } else {
                    Toast.makeText(this, jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setDataUser() {

        age.setText(user.getAge());
        bodyFat.setText(user.getBodyFat());
        height.setText(user.getHeight());
        weight.setText(user.getWeight());

        for (int i = 0; i < exerciseLevelCat.length; i++) {
            if (exerciseLevelValue[i].contains(user.getExerciseLevel())) {
                exerciseLevelSpinner.setSelection(i + 1);
            }
        }

        if (gender == 1) {
            gender = 1;
            genderMale.setBackground(getResources().getDrawable(R.drawable.background_login));
            genderMale.setImageResource(R.mipmap.female);
            genderFemale.setBackground(getResources().getDrawable(R.drawable.gendermale));
            genderFemale.setImageResource(R.mipmap.male_blue);
        } else {
            gender = 0;
            genderFemale.setBackground(getResources().getDrawable(R.drawable.background_login));
            genderFemale.setImageResource(R.mipmap.male);
            genderMale.setBackground(getResources().getDrawable(R.drawable.gendermale));
            genderMale.setImageResource(R.mipmap.female_blue);
        }
    }

    @Override
    public void onNetworkTimeOut(String message, String fromUrl) {
        Toast.makeText(this, Constants.NetworkError, Toast.LENGTH_SHORT).show();
    }
}
