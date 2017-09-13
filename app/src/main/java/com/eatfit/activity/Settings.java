package com.eatfit.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.eatfit.R;
import com.eatfit.constants.Constants;
import com.eatfit.helper.SharedPreference;

public class Settings extends AppCompatActivity implements View.OnClickListener {
    TextView logout, aboutUs, termsCondition, faq, personalInfo, checkUpdates, inviteShare, editProfile, myRewards, changePass;
    SharedPreference spMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setTitle(getResources().getString(R.string.app_name));
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        // TODO get back button on toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // TODO set custom navigation icon on toolbar
        toolbar.setNavigationIcon(R.mipmap.back);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Settings.this, Home.class));
                finishAffinity();
            }
        });

        spMain = spMain.getInstance(this);

        initViews();

        logout.setOnClickListener(this);
        aboutUs.setOnClickListener(this);
        termsCondition.setOnClickListener(this);
        faq.setOnClickListener(this);
        personalInfo.setOnClickListener(this);
        checkUpdates.setOnClickListener(this);
        inviteShare.setOnClickListener(this);
        editProfile.setOnClickListener(this);
        myRewards.setOnClickListener(this);
        changePass.setOnClickListener(this);
    }

    private void initViews() {

        logout = (TextView) findViewById(R.id.logout);
        aboutUs = (TextView) findViewById(R.id.aboutus);
        termsCondition = (TextView) findViewById(R.id.termsCondition);
        faq = (TextView) findViewById(R.id.faq);
        personalInfo = (TextView) findViewById(R.id.personalInfo);
        checkUpdates = (TextView) findViewById(R.id.checkUpdates);
        inviteShare = (TextView) findViewById(R.id.inviteFriends);
        editProfile = (TextView) findViewById(R.id.editprofile);
        myRewards = (TextView) findViewById(R.id.myRewards);
        changePass = (TextView) findViewById(R.id.changePass);

        if (spMain.getString(Constants.LOGINTYPE, "").equals("0")) {
            changePass.setVisibility(View.VISIBLE);
        } else {
            changePass.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.logout:
                spMain.deletePreference();
                startActivity(new Intent(Settings.this, LoginActivity.class));
                finishAffinity();
                break;

            case R.id.aboutus:
                startActivity(new Intent(Settings.this, AboutUs.class));
                break;

            case R.id.termsCondition:
                startActivity(new Intent(Settings.this, TermsConditions.class));
                break;

            case R.id.faq:
                startActivity(new Intent(Settings.this, FAQs.class));
                break;

            case R.id.personalInfo:
                startActivity(new Intent(Settings.this, PersonalInformation.class).putExtra("navigater", true));
                break;

            case R.id.checkUpdates:
                startActivity(new Intent(Settings.this, Updates.class));
                break;

            case R.id.inviteFriends:
                startActivity(new Intent(Settings.this, InviteShare.class));
                break;

            case R.id.changePass:
                Intent forgetPassword = new Intent(Settings.this, ResetInformation.class);
                startActivity(forgetPassword);
                break;

            case R.id.myRewards:
                startActivity(new Intent(Settings.this, Rewards.class));
                break;

            case R.id.editprofile:
                startActivity(new Intent(Settings.this, EditProfile.class));
                break;


        }
    }
}
