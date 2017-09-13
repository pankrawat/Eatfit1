package com.eatfit.activity;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eatfit.R;
import com.eatfit.constants.Constants;
import com.eatfit.helper.MyProgressDialog;
import com.eatfit.helper.SharedPreference;
import com.eatfit.helper.Utils;
import com.eatfit.model.UserModel;
import com.eatfit.network.NetworkThread;
import com.eatfit.netwrokCallback.NetworkCallback;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONObject;

public class EditProfile extends AppCompatActivity implements View.OnClickListener, NetworkCallback {
    TextView fName, lName, email, password, personalInfo, phone, signUp, loginText;
    ImageView male, female;
    ImageView genderMale, genderFemale;
    int gender;
    private String nFirst = "", nLast = "", uEmail = "", uPassword = "", uPersonalInfo = "", uPhone = "";
    private SharedPreference spMain;
    private Dialog progressDialog;
    private UserModel user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        // TODO get back button on toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // TODO set custom navigation icon on toolbar
        toolbar.setNavigationIcon(R.mipmap.back);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        spMain = spMain.getInstance(this);
        initViews();
        user = new UserModel();

        if (Utils.isConnectingToInternet(this)) {
            getUserDetails();
        } else {
            Toast.makeText(this, Constants.InternetMessage, Toast.LENGTH_SHORT).show();
        }
        signUp.setOnClickListener(this);
        male.setOnClickListener(this);
        female.setOnClickListener(this);
        loginText.setOnClickListener(this);

    }

    private void initViews() {

        fName = (TextView) findViewById(R.id.firstName);
        lName = (TextView) findViewById(R.id.lastName);
        phone = (TextView) findViewById(R.id.phonesignup);
        password = (TextView) findViewById(R.id.passwordsignup);
        email = (TextView) findViewById(R.id.emailsignup);
        personalInfo = (TextView) findViewById(R.id.personalInfoSignup);
        male = (ImageView) findViewById(R.id.male);
        genderFemale = (ImageView) findViewById(R.id.female);
        genderMale = (ImageView) findViewById(R.id.male);
        female = (ImageView) findViewById(R.id.female);
        signUp = (TextView) findViewById(R.id.signupButton);
        loginText = (TextView) findViewById(R.id.signupText);
    }

    public boolean validate() {
        if (nFirst.equals("") || nLast.equals("") || uEmail.equals("") || uPassword.equals("") || uPhone.equals("")) {
            Toast.makeText(this, "Please enter required fields", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!Utils.isValidEmailCom(uEmail)) {
            Toast.makeText(this, "Please enter valid email", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!(password.length() >= 8 || password.length() <= 18)) {
            Toast.makeText(this, "Paswword should be in between 8 - 18", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!(uPhone.length() >= 6 || password.length() <= 15)) {
            Toast.makeText(this, "Phone should be in between 6 - 15", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
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

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.signupButton:
                uEmail = email.getText().toString();
                uPhone = phone.getText().toString();
                uPassword = password.getText().toString();
//                uPersonalInfo = personalInfo.getText().toString();
                nLast = lName.getText().toString();
                nFirst = fName.getText().toString();

                if (validate()) {
                    if (Utils.isConnectingToInternet(this)) {
                        signUp();
                    } else {
                        Toast.makeText(this, Constants.InternetMessage, Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case R.id.male:

                gender = 1;
                male.setBackground(getResources().getDrawable(R.drawable.background_login));
                male.setImageResource(R.mipmap.female);
                female.setBackground(getResources().getDrawable(R.drawable.gendermale));
                female.setImageResource(R.mipmap.male_blue);


                break;

            case R.id.female:

                gender = 0;
                female.setBackground(getResources().getDrawable(R.drawable.background_login));
                female.setImageResource(R.mipmap.male);
                male.setBackground(getResources().getDrawable(R.drawable.gendermale));
                male.setImageResource(R.mipmap.female_blue);

                break;
            case R.id.signupText:
                startActivity(new Intent(EditProfile.this, LoginActivity.class));
                finishAffinity();
                break;

        }

    }

    public void signUp() {
        final Dialog dialog = MyProgressDialog.showProgressDialog(this, "");
        dialog.show();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("email", uEmail);
        jsonObject.addProperty("first_name", nFirst);
        jsonObject.addProperty("last_name", nLast);
        jsonObject.addProperty("password", uPassword);
        jsonObject.addProperty("phone", uPhone);
        jsonObject.addProperty("gender", String.valueOf(gender));
        jsonObject.addProperty("age", "");
        jsonObject.addProperty("height", "");
        jsonObject.addProperty("weight", "");
        jsonObject.addProperty("exercise_level", "");
        jsonObject.addProperty("body_fat", "");
        jsonObject.addProperty("user_id", spMain.getString(Constants.userID, ""));

        Log.e("Request", jsonObject.toString());

        Ion.with(this)
                .load(Constants.URL + "edit_user")
                .setJsonObjectBody(jsonObject)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        dialog.dismiss();
                        try {
                            if (result != null && e == null) {
                                JSONObject mainString = new JSONObject(result);

                                if (mainString.optBoolean("is_success")) {
                                    JSONObject mainData = mainString.getJSONObject("Result");
                                    Log.e("Login Result", mainData.toString());

                                    spMain.putString(Constants.userID, mainData.optString("user_id"));
                                    spMain.putString(Constants.userEmail, mainData.optString("email"));
                                    spMain.putString(Constants.OTP, mainData.optString("otp"));
                                    spMain.putString(Constants.Phone, mainData.optString("phone"));
                                    spMain.putString(Constants.userName, mainData.optString("first_name") + " "
                                            + mainData.optString("last_name"));
                                    spMain.putString(Constants.gender, mainData.optString("gender"));
                                    finish();

                                } else {
                                    Toast.makeText(EditProfile.this, mainString.optString("message"), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });

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
                            user.setFirstName(userObject.getString("first_name"));
                            user.setLastName(userObject.getString("last_name"));
                            user.setEmail(userObject.getString("email"));
                            user.setPhone(userObject.getString("phone"));
                            user.setGender(userObject.getString("gender"));
                            user.setPassword(userObject.getString("password"));
                            user.setProfileImage(userObject.getString("profile_image"));
                            user.setAge(userObject.getString("age"));
                            user.setHeight(userObject.getString("height"));
                            user.setWeight(userObject.getString("weight"));
                            user.setCalorie(userObject.getString("calorie"));
                            user.setExerciseLevel(userObject.getString("exercise_level"));
                            user.setBodyFat(userObject.getString("body_fat"));

//                            spMain.putString(Constants.userID, userObject.optString("user_id"));
                            spMain.putString(Constants.userEmail, userObject.optString("email"));
                            spMain.putString(Constants.OTP, userObject.optString("otp"));
                            spMain.putString(Constants.Phone, userObject.optString("phone"));
                            spMain.putString(Constants.userName, userObject.optString("first_name") + " "
                                    + userObject.optString("last_name"));
                            spMain.putString(Constants.LOGINTYPE, userObject.optString("login_type"));
                            spMain.putString(Constants.profileURL, userObject.optString("profile_image"));
                            spMain.putString(Constants.gender, userObject.optString("gender"));
                            spMain.putString(Constants.REWARDS, userObject.optString("rewards"));
                            spMain.putString(Constants.REFERRALCODE, userObject.optString("referralCode"));

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

        fName.setText(user.getFirstName());
        lName.setText(user.getLastName());
        email.setText(user.getEmail());
        phone.setText(user.getPhone());
        if (spMain.getString(Constants.LOGINTYPE, "").equals("0")) {
            password.setText(user.getPassword());
            password.setEnabled(false);
        } else {
            password.setText("********");
            password.setEnabled(false);
        }


        if (user.getGender().equals("1")) {
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

    }
}
