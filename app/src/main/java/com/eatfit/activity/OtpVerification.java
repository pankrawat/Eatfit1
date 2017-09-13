package com.eatfit.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.eatfit.R;
import com.eatfit.constants.Constants;
import com.eatfit.helper.Utils;

public class OtpVerification extends AppCompatActivity implements View.OnClickListener {
    TextView continueButton, otpText;
    EditText otp;
    private String otpString = "", checker = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

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

        initViews();

        Intent otpintent = getIntent();
        otpString = otpintent.getStringExtra("OTP");
        checker = otpintent.getStringExtra("activityDecider");

        otpText.setText(Html.fromHtml(getResources().getString(R.string.resetOtpText)));

        continueButton.setOnClickListener(this);
    }

    private void initViews() {
        continueButton = (TextView) findViewById(R.id.continueButton);
        otpText = (TextView) findViewById(R.id.otpInfo);
        otp = (EditText) findViewById(R.id.otp);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.continueButton:
                if (otp.getText().toString().equals("")) {
                    Toast.makeText(this, "Please enter the Otp", Toast.LENGTH_SHORT).show();
                } else if (!otp.getText().toString().equals(otpString)) {
                    Toast.makeText(this, "Please enter correct OTP", Toast.LENGTH_SHORT).show();
                } else {

                    if (checker.equals("0")) {
                        Intent otpVerified = new Intent(OtpVerification.this, BingoScreen.class);
                        startActivity(otpVerified);
                        finish();
                    } else {
                        Intent otpVerified = new Intent(OtpVerification.this, ResetPassword.class);
                        startActivity(otpVerified);
                        finish();
                    }

                }

                break;
        }
    }
}
