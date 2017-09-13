package com.eatfit.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Base64;
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
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GooglePlus_login.OnClientConnectedListener {

    int token, gender = 0;
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    private String imageURL = "", profileImage = "", email = "", user_name = "", fb_id = "", gl_id = "", pwd = "",
            firstName = "", last_Name = "";
    private SharedPreference spMain;
    private JsonObject json;
    private TextView facebook, google, login, newUser, forgetPassword, loginText;
    private EditText userEmail, password;
    private GooglePlus_login googlePlus_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        // Initialize the SDK before executing any other operations,
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_login);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle(null);
//        // TODO get back button on toolbar
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        // TODO set custom navigation icon on toolbar
//        toolbar.setNavigationIcon(R.mipmap.back_black);
//
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onBackPressed();
//            }
//        });

        initViews();

        spMain = spMain.getInstance(this);
        googlePlus_login = new GooglePlus_login(this);

        loginText.setText(Html.fromHtml(getResources().getString(R.string.socialLoginText)));

        //...............Facebook Integration in app.................//

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        AccessToken accessToken = loginResult.getAccessToken();
                        Profile profile = Profile.getCurrentProfile();

                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {
                                        Log.e("LoginActivity", object.toString());
                                        JSONObject jobj = object;
                                        imageURL = "https://graph.facebook.com/" + jobj.optString("id") + "/picture?type=large";
                                        spMain.putString(Constants.profileUrl, imageURL);

                                        if (!imageURL.isEmpty()) {
                                            MyProgressDialog.showProgressDialog(LoginActivity.this, "");
                                        }
                                        try {
                                            Picasso.with(LoginActivity.this)
                                                    .load(imageURL)
                                                    .into(new Target() {
                                                        @Override
                                                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                                            MyProgressDialog.hideProgressDialog();
                                                            profileImage = encodeToBase64(bitmap, Bitmap.CompressFormat.JPEG, 50);
                                                            spMain.putString(Constants.profileImage, profileImage);
                                                            Log.e("facebook Image", profileImage);
                                                            Log.e("Bitmap", bitmap.toString());
                                                            makeJsonObject();

                                                        }

                                                        @Override
                                                        public void onBitmapFailed(Drawable errorDrawable) {
                                                            MyProgressDialog.hideProgressDialog();
                                                            makeJsonObject();
                                                        }

                                                        @Override
                                                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                                                        }
                                                    });
                                        } catch (Exception ex) {
                                            ex.printStackTrace();
                                            MyProgressDialog.hideProgressDialog();
                                        }

                                        // Application code

                                        if (jobj.has("email")) {
                                            email = jobj.optString("email");
                                        } else {
                                            email = "";
                                        }
                                        token = 1;
                                        user_name = jobj.optString("name");
                                        fb_id = jobj.optString("id");
                                        pwd = "";
                                        firstName = jobj.optString("first_name");
                                        last_Name = jobj.optString("last_name");

                                        if (!jobj.optString("gender").equals("")) {
                                            if (jobj.optString("gender").equals("male")) {
                                                gender = 1;
                                            } else if (jobj.optString("gender").equals("female")) {
                                                gender = 0;
                                            } else {
                                                gender = 1;
                                            }
                                        }
                                        Log.d("FACEBOOK_EMAIL", email);
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email,gender,birthday,picture,first_name,last_name");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {

            }
        };

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                //res.setText(newProfile.getName());
            }
        };

        accessTokenTracker.startTracking();
        profileTracker.startTracking();

        //...............Facebook Integration in app.................Ends.....//


        facebook.setOnClickListener(this);
        google.setOnClickListener(this);
        login.setOnClickListener(this);
        forgetPassword.setOnClickListener(this);
        newUser.setOnClickListener(this);
    }

    private void initViews() {
        facebook = (TextView) findViewById(R.id.fbloginButton);
        google = (TextView) findViewById(R.id.gloginButton);
        login = (TextView) findViewById(R.id.loginButton);
        userEmail = (EditText) findViewById(R.id.emailLogin);
        password = (EditText) findViewById(R.id.passwordLogin);
        newUser = (TextView) findViewById(R.id.newUser);
        forgetPassword = (TextView) findViewById(R.id.forgetPassword);
        loginText = (TextView) findViewById(R.id.socialLoginText);
    }

    public String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality) {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    public void makeJsonObject() {

        final Dialog dialog = MyProgressDialog.showProgressDialog(this, "");
        dialog.show();

        if (token == 1) {                                    // for Facebook Login
            json = new JsonObject();
            json.addProperty("email", email);
            json.addProperty("first_name", firstName);
            json.addProperty("last_name", last_Name);
            json.addProperty("password", pwd);
            json.addProperty("phone", "");
            json.addProperty("gender", gender);
            json.addProperty("device_token", FirebaseInstanceId.getInstance().getToken());
            json.addProperty("device_type", Constants.DEVICETYPE);
            json.addProperty("fb_id", fb_id);
            json.addProperty("gl_id", "");
            json.addProperty("login_type", "1");

            socialLogin(dialog);
        }
        if (token == 2) {                                   // for Google Login
            json = new JsonObject();
            json = new JsonObject();
            json.addProperty("email", email);
            json.addProperty("first_name", spMain.getString(Constants.gfirstName, ""));
            json.addProperty("last_name", spMain.getString(Constants.glastName, ""));
            json.addProperty("password", pwd);
            json.addProperty("phone", "");
            json.addProperty("gender", spMain.getString(Constants.gGender, ""));
            json.addProperty("device_token", FirebaseInstanceId.getInstance().getToken());
            json.addProperty("device_type", Constants.DEVICETYPE);
            json.addProperty("fb_id", "");
            json.addProperty("gl_id", gl_id);
            json.addProperty("login_type", "2");

            socialLogin(dialog);
        }

        if (token == 3) {                                   // for Login
            json = new JsonObject();
            json.addProperty("email", email);
            json.addProperty("password", pwd);
            json.addProperty("device_token", FirebaseInstanceId.getInstance().getToken());
            json.addProperty("device_type", Constants.DEVICETYPE);
            json.addProperty("fb_id", "");
            json.addProperty("gl_id", "");
            json.addProperty("login_type", "0");

            NormalLogin(dialog);
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.fbloginButton:
                token = 1;
                LoginManager.getInstance().logOut();
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile", "email", "user_friends"));

                break;

            case R.id.gloginButton:
                token = 2;
                googlePlus_login.signIn();
                break;

            case R.id.loginButton:
                token = 3;
                email = userEmail.getText().toString();
                pwd = password.getText().toString();

                if (validate()) {
                    if (Utils.isConnectingToInternet(this)) {
                        makeJsonObject();
                    } else {
                        Toast.makeText(this, Constants.InternetMessage, Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case R.id.newUser:

                Intent intent = new Intent(LoginActivity.this, SignUp.class);
                startActivity(intent);

                break;

            case R.id.forgetPassword:

                Intent forgetPassword = new Intent(LoginActivity.this, ResetInformation.class);
                startActivity(forgetPassword);

                break;
        }
    }

    private boolean validate() {
        if (email.equals("") || pwd.equals("")) {
            Toast.makeText(this, "Please enter required fields", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!Utils.isValidEmailCom(email)) {
            Toast.makeText(this, "Please enter valid email", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!(pwd.length() >= 8 || pwd.length() <= 18)) {
            Toast.makeText(this, "Paswword should be in between 8 - 18", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onGoogleProfileFetchComplete() {

        if (Utils.isConnectingToInternet(this)) {
            user_name = spMain.getString(Constants.GName, "");
            email = spMain.getString(Constants.GEmail, "");
            pwd = "";
            gl_id = spMain.getString(Constants.gID, "");
//            try {
//                URL url = new URL(spMain.getString(Constants.profileUrl, ""));
//                Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
//                profileImage = encodeToBase64(image, Bitmap.CompressFormat.JPEG, 100);
//            } catch (IOException e) {
//                System.out.println(e);
//            }
            Picasso.with(this)
                    .load(spMain.getString(Constants.profileUrl, ""))
                    .into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            profileImage = encodeToBase64(bitmap, Bitmap.CompressFormat.JPEG, 100);
                            Log.e("profile Image", profileImage);
                            Log.e("Bitmap", bitmap.toString());
                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                        }
                    });

            makeJsonObject();
        } else {
            Toast.makeText(LoginActivity.this, Constants.InternetMessage, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClientFailed() {
//        Toast.makeText(Login.this, "Login Failed", Toast.LENGTH_SHORT).show();

    }

    // for Social Login in app
    public void socialLogin(final Dialog dialog) {
//        MyProgressDialog.showProgressDialog(LoginActivity.this, "");

        Ion.with(this)
                .load(Constants.URL + "sign_up")
                .setJsonObjectBody(json)
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
                                    spMain.putString(Constants.LOGINTYPE, mainData.optString("login_type"));
                                    spMain.putString(Constants.profileURL, mainData.optString("profile_image"));
                                    spMain.putString(Constants.gender, mainData.optString("gender"));
                                    spMain.putString(Constants.REWARDS, mainData.optString("rewards"));
                                    spMain.putString(Constants.REFERRALCODE, mainData.optString("referralCode"));

                                    if (mainString.optString("is_new").equals("1")) {
                                        spMain.putBoolean(Constants.ISLogin, true);
                                        Intent bingo = new Intent(LoginActivity.this, BingoScreen.class);
                                        startActivity(bingo);
                                        finish();
                                    } else {
                                        spMain.putBoolean(Constants.ISProfile, true);
                                        Intent home = new Intent(LoginActivity.this, Home.class);
                                        home.putExtra("calorie", mainData.optString("calorie"));
                                        spMain.putInteger(Constants.CALORIE, (int) Double.parseDouble(mainData.optString("calorie")));
                                        startActivity(home);
                                        finish();
                                    }
//                                    Intent bingo = new Intent(LoginActivity.this, OtpVerification.class);
//                                    bingo.putExtra("OTP", mainData.optString("otp"));
//                                    bingo.putExtra("activityDecider", "0");
//                                    startActivity(bingo);
//                                    finish();
                                } else {
                                    Toast.makeText(LoginActivity.this, mainString.optString("message"), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            Toast.makeText(LoginActivity.this, Constants.NetworkError, Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }

    // for normal Login in app
    public void NormalLogin(final Dialog dialog) {

        Ion.with(this)
                .load(Constants.URL + "login")
                .setJsonObjectBody(json)
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
                                    spMain.putString(Constants.profileURL, mainData.optString("profile_image"));
                                    spMain.putString(Constants.gender, mainData.optString("gender"));
                                    spMain.putString(Constants.LOGINTYPE, mainData.optString("login_type"));
                                    spMain.putString(Constants.REWARDS, mainData.optString("rewards"));
                                    spMain.putString(Constants.REFERRALCODE, mainData.optString("referralCode"));

                                    if (mainString.optString("is_new").equals("1")) {
                                        spMain.putBoolean(Constants.ISLogin, true);
                                        Intent bingo = new Intent(LoginActivity.this, BingoScreen.class);
                                        startActivity(bingo);
                                        finish();
                                    } else {
                                        spMain.putBoolean(Constants.ISProfile, true);
                                        Intent home = new Intent(LoginActivity.this, Home.class);
                                        home.putExtra("calorie", mainData.optString("calorie"));
                                        spMain.putInteger(Constants.CALORIE, (int) Double.parseDouble(mainData.optString("calorie")));
                                        startActivity(home);
                                        finish();
                                    }
                                }
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (token == 2) {
            googlePlus_login.onActivityResult(requestCode, resultCode, data);
            token = 0;
        }
    }
}
