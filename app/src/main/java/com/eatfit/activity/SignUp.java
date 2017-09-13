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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONObject;

public class SignUp extends AppCompatActivity implements View.OnClickListener {

    TextView fName, lName, email, password, personalInfo, phone, signUp, loginText;
    ImageView male, female;
    int gender = 1;
    private String nFirst = "", nLast = "", uEmail = "", uPassword = "", uPersonalInfo = "", uPhone = "";
    private SharedPreference spMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

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
        jsonObject.addProperty("device_token", FirebaseInstanceId.getInstance().getToken());
        jsonObject.addProperty("device_type", Constants.DEVICETYPE);
        jsonObject.addProperty("fb_id", "");
        jsonObject.addProperty("gl_id", "");
        jsonObject.addProperty("login_type", "0");

        Log.e("Request", jsonObject.toString());

        Ion.with(this)
                .load(Constants.URL + "sign_up")
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
                                    spMain.putString(Constants.REWARDS, mainData.optString("rewards"));
                                    spMain.putString(Constants.REFERRALCODE, mainData.optString("referralCode"));
                                    spMain.putBoolean(Constants.ISLogin, true);
                                    spMain.putString(Constants.LOGINTYPE, mainData.optString("login_type"));

                                    Intent bingo = new Intent(SignUp.this, OtpVerification.class);
                                    bingo.putExtra("OTP", mainData.optString("otp"));
                                    bingo.putExtra("activityDecider", "0");
                                    startActivity(bingo);
                                    finish();

                                } else {
                                    Toast.makeText(SignUp.this, mainString.optString("message"), Toast.LENGTH_SHORT).show();
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
                startActivity(new Intent(SignUp.this, LoginActivity.class));
                finishAffinity();
                break;

        }
    }
}
