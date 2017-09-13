package com.eatfit.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.eatfit.constants.Constants;
import com.eatfit.helper.SharedPreference;
import com.eatfit.helper.Utils;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;


public class GooglePlus_login implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    //public static final int RESULT_OK = -1;
    public static final int RC_SIGN_IN = 9001;
    Context context;
    //private static final SimpleDateFormat googleFormat=new SimpleDateFormat("yyyy-MM-dd");
    String TAG = "Google Plus";
    SharedPreference spMain;
    private GoogleApiClient mGoogleApiClient;


    public GooglePlus_login(FragmentActivity context) {
        this.context = context;
        spMain = spMain.getInstance(context);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                //.requestScopes(new Scope(Scopes.PROFILE))
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .enableAutoManage(context, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(Plus.API)
                .build();
    }


    public void onStart() {
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN && mGoogleApiClient.isConnected()) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        } else {
            signIn();
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        try {
            Log.d(TAG, "handleSignInResult:" + result.isSuccess());
            if (result.isSuccess() && mGoogleApiClient.hasConnectedApi(Plus.API)) {
                // Signed in successfully, show authenticated UI.
                GoogleSignInAccount acct = result.getSignInAccount();
                Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
                Log.d("Current Person", acct.toString());
                if (currentPerson != null) {
                    if (currentPerson.getBirthday() != null) {
                        //UserBean.getObect().dateOfBirth = currentPerson.getBirthday();
                        Log.d("Birthday", currentPerson.getBirthday());
                    }
                    if (currentPerson.getGender() == 0) {
                        spMain.putString(Constants.gGender, String.valueOf(currentPerson.getGender()));
                        Log.d("Gender", String.valueOf(currentPerson.getGender()));
                    }
                    if (!currentPerson.getImage().getUrl().equals("")) {
                        //UserBean.getObect().gender = "1";
                        String profileImage = "";
                        spMain.putString(Constants.profileUrl, currentPerson.getImage().getUrl());
                        Log.d("ProfileUrl", currentPerson.getImage().getUrl());
                    } else {
                        //UserBean.getObect().gender = "2";
                        Log.d("Gender", String.valueOf(currentPerson.getGender()));
                    }
                }
                String imageUrl = "";
                String email = "";
                if (acct.getPhotoUrl() != null) {
                    imageUrl = acct.getPhotoUrl().toString();
                    Log.e("Image Url", imageUrl);
                }
                if (acct.getEmail() != null) {
                    email = acct.getEmail();
                    Log.e("Email", email);
                    spMain.putString(Constants.GEmail, email);
                }
                if (acct.getFamilyName() != null) {
                    spMain.putString(Constants.glastName, acct.getFamilyName());
                    Log.e("last Name", acct.getFamilyName());
                }
                if (acct.getGivenName() != null) {
                    spMain.putString(Constants.gfirstName, acct.getGivenName());
                    Log.e("last Name", acct.getGivenName());
                }
                if (acct.getId() != null) {
                    spMain.putString(Constants.gID, acct.getId());
                    Log.e("Googel ID", acct.getId());
                }
                //UserBean.getObect().accountType ="4";
                if (acct.getDisplayName() != null) {
                    //UserBean.getObect().name = acct.getDisplayName();
                    Log.e("Display Name", acct.getDisplayName());
                    spMain.putString(Constants.GName, acct.getDisplayName());
                }
                ((OnClientConnectedListener) context).onGoogleProfileFetchComplete();
            } else {
                ((OnClientConnectedListener) context).onClientFailed();
                signOut();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            ((OnClientConnectedListener) context).onClientFailed();
            signOut();
        }
    }

    public void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                    }
                });
    }

    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                        ((Activity) context).startActivityForResult(signInIntent, RC_SIGN_IN);
                    }
                });
    }

    public void signIn() {
        revokeAccess();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(context, connectionResult.toString(), Toast.LENGTH_LONG).show();

        spMain.putString("ERROR_GOOGLE", connectionResult.toString());
        ((OnClientConnectedListener) context).onClientFailed();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    public static interface OnClientConnectedListener {
        void onGoogleProfileFetchComplete();

        void onClientFailed();
    }
}
