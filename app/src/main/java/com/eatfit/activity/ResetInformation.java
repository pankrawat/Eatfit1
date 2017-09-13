package com.eatfit.activity;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.eatfit.R;
import com.eatfit.constants.Constants;
import com.eatfit.helper.MyProgressDialog;
import com.eatfit.helper.SharedPreference;
import com.eatfit.helper.Utils;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONObject;

public class ResetInformation extends AppCompatActivity implements View.OnClickListener {

    TextView submit, resetInfo;
    EditText uEmail;
    String phone;
    SharedPreference spMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetinformation);

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
        resetInfo.setText(Html.fromHtml(getResources().getString(R.string.resetInfoText)));

        submit.setOnClickListener(this);
    }

    private void initViews() {

        uEmail = (EditText) findViewById(R.id.resetInfoText);
        resetInfo = (TextView) findViewById(R.id.resetInfo);
        submit = (TextView) findViewById(R.id.submitButton);
    }

    @Override
    public void onClick(View v) {


        switch (v.getId()) {

            case R.id.submitButton:
                phone = uEmail.getText().toString();
                if (uEmail.getText().toString().equals("")) {
                    Toast.makeText(this, "Please enter the correct Number", Toast.LENGTH_SHORT).show();
                } else if ((phone.length() <= 6 || phone.length() >= 15)) {
                    Toast.makeText(this, "Please enter phone number in between 6-15", Toast.LENGTH_SHORT).show();
                } else {
                    if (Utils.isConnectingToInternet(this)) {
                        sendOtp();
                    } else {
                        Toast.makeText(this, Constants.InternetMessage, Toast.LENGTH_SHORT).show();
                    }
                }
                break;

        }
    }

    private void sendOtp() {

        final Dialog dialog = MyProgressDialog.showProgressDialog(this, "");
        dialog.show();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("phone", uEmail.getText().toString());
        jsonObject.addProperty("password", "");
        jsonObject.addProperty("user_id", spMain.getString(Constants.userID, ""));

        Log.d("resetInfo ", jsonObject.toString());

        Ion.with(this)
                .load(Constants.URL + "send_otp")
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

                                    Intent home = new Intent(ResetInformation.this, OtpVerification.class);
                                    home.putExtra("OTP", res.getString("otp"));
                                    home.putExtra("activityDecider", "1");
                                    startActivity(home);
                                } else {
                                    Toast.makeText(ResetInformation.this, mainData.optString("message"), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });

    }
}
