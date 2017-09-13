package com.eatfit.activity;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.eatfit.R;
import com.eatfit.constants.Constants;
import com.eatfit.helper.SharedPreference;

import java.util.List;

public class InviteShare extends AppCompatActivity implements View.OnClickListener {
    private SharedPreference spMain;
    private RelativeLayout fbLay, gLay, msgLay, whatsappLay;
    private String referCode = "";
    private String msg = "";
    private Uri urlToShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_share);

        spMain = spMain.getInstance(this);
        referCode = spMain.getString(Constants.REFERRALCODE, "");
        msg = "Welcome to the Eat Fit App. Use this referral code " + referCode + " to Download the app from Play Store\n Play Store Url :  ";
        initViews();
        urlToShare = Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName());

    }

    private void initViews() {
        fbLay = (RelativeLayout) findViewById(R.id.facebookLay);
        gLay = (RelativeLayout) findViewById(R.id.googleLay);
        msgLay = (RelativeLayout) findViewById(R.id.messagesLay);
        whatsappLay = (RelativeLayout) findViewById(R.id.whatsappLay);


        fbLay.setOnClickListener(this);
        gLay.setOnClickListener(this);
        msgLay.setOnClickListener(this);
        whatsappLay.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.facebookLay:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, msg);
                // See if official Facebook app is found
                boolean facebookAppFound = false;
                List<ResolveInfo> matches = getPackageManager().queryIntentActivities(intent, 0);
                for (ResolveInfo info : matches) {
                    if (info.activityInfo.packageName.toLowerCase().startsWith("com.facebook.katana")) {
                        intent.setPackage(info.activityInfo.packageName);
                        facebookAppFound = true;
                        break;
                    }
                }
                // As fallback, launch sharer.php in a browser
                if (!facebookAppFound) {
                    String sharerUrl = "https://www.facebook.com/sharer/sharer.php?u=" + urlToShare;
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
                }
                startActivity(intent);

                break;

            case R.id.googleLay:
                Intent mailintent = new Intent(Intent.ACTION_SEND);
                mailintent.setType("text/plain");
                mailintent.putExtra(Intent.EXTRA_SUBJECT, "Eat Fit App Url");
                mailintent.putExtra(Intent.EXTRA_TEXT, msg + urlToShare);
                List<ResolveInfo> mailmatches = getPackageManager().queryIntentActivities(mailintent, 0);
                for (ResolveInfo info : mailmatches) {
                    if (info.activityInfo.packageName.toLowerCase().startsWith("com.google.android.gm")) {
                        mailintent.setPackage(info.activityInfo.packageName);
                        break;
                    }
                }
                startActivity(mailintent);
                break;

            case R.id.whatsappLay:
                // See if official Whatsapp app is found
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, msg + urlToShare);
                sendIntent.setType("text/plain");
                boolean WhatsappFound = false;
                List<ResolveInfo> matche = getPackageManager().queryIntentActivities(sendIntent, 0);
                for (ResolveInfo info : matche) {
                    if (info.activityInfo.packageName.toLowerCase().startsWith("com.whatsapp")) {
                        sendIntent.setPackage("com.whatsapp");
                        startActivity(sendIntent);
                        WhatsappFound = true;
                        break;
                    }
                }
                // As fallback, launch sharer.php in a browser
                if (!WhatsappFound) {
                    try {
                        Intent viewIntent = new Intent("android.intent.action.VIEW",
                                Uri.parse("https://play.google.com/store/apps/details?id=com.whatsapp"));
                        startActivity(viewIntent);
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Unable to Connect Try Again...",
                                Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }

                break;

            case R.id.messagesLay:
                Intent sendIntents = new Intent(Intent.ACTION_VIEW);
                sendIntents.setData(Uri.parse("sms:"));
                sendIntents.putExtra("sms_body", msg + urlToShare);
                startActivity(sendIntents);
                break;
        }
    }
}
