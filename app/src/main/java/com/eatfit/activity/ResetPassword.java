package com.eatfit.activity;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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

public class ResetPassword extends AppCompatActivity implements View.OnClickListener {

    EditText password, resetPassword;
    TextView reset;
    String uPassword, uConfirmPassword;
    private SharedPreference spMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetpassword);

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

        spMain = spMain.getInstance(this);
        initViews();

        reset.setOnClickListener(this);
    }

    private void initViews() {

        password = (EditText) findViewById(R.id.resetpassword);
        resetPassword = (EditText) findViewById(R.id.resetpasswordConfirm);
        reset = (TextView) findViewById(R.id.resetButton);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.resetButton:
                uPassword = password.getText().toString();
                uConfirmPassword = resetPassword.getText().toString();

                if (uConfirmPassword.equals("") || uPassword.equals("")) {
                    Toast.makeText(this, "Please enter all the fields", Toast.LENGTH_SHORT).show();
                } else if (!uConfirmPassword.equals(uPassword)) {
                    Toast.makeText(this, "Password and Confirm Password do not match", Toast.LENGTH_SHORT).show();
                } else {
                    if (Utils.isConnectingToInternet(this)) {
                        resetPassword();
                    } else {
                        Toast.makeText(this, Constants.InternetMessage, Toast.LENGTH_SHORT).show();
                    }
                }

                break;
        }
    }

    private void resetPassword() {

        final Dialog dialog = MyProgressDialog.showProgressDialog(this, "");
        dialog.show();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("email", "");
        jsonObject.addProperty("first_name", "");
        jsonObject.addProperty("last_name", "");
        jsonObject.addProperty("password", uPassword);
        jsonObject.addProperty("phone", "");
        jsonObject.addProperty("gender", "");
        jsonObject.addProperty("user_id", spMain.getString(Constants.userID, ""));

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

                                    Intent home = new Intent(ResetPassword.this, PasswordDone.class);
                                    startActivity(home);
                                    finish();
                                }
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });

    }
}
