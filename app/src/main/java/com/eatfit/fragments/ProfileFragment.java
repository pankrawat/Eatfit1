package com.eatfit.fragments;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eatfit.R;
import com.eatfit.activity.FollowersFollowing;
import com.eatfit.activity.Home;
import com.eatfit.activity.MyNutrition;
import com.eatfit.activity.MyRegimen;
import com.eatfit.activity.MyWorkouts;
import com.eatfit.activity.PersonalInformation;
import com.eatfit.activity.Rewards;
import com.eatfit.activity.Settings;
import com.eatfit.constants.Constants;
import com.eatfit.helper.CircleImageView;
import com.eatfit.helper.GetFilePath;
import com.eatfit.helper.ImageSave;
import com.eatfit.helper.MyProgressDialog;
import com.eatfit.helper.SharedPreference;
import com.eatfit.helper.Utils;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {

    RelativeLayout settingsLay, nutritionLay, workoutsLay, followersLay, regimenLay, rewardsLay;
    Context ctx;
    TextView userName, userDescription, calorieValue;
    CircleImageView userImage;
    ImageView headerImage, headerImages;
    private SharedPreference spMain;
    int SELECT_IMAGE = 1, REQUEST_CAMERA_EDIT = 2;
    String selectedPath = "", profileBase64 = "";
    private RelativeLayout headerImageLay;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ctx = getActivity();

        spMain = spMain.getInstance(ctx);

        initViews(view);

        userName.setText(spMain.getString(Constants.userName, ""));
        calorieValue.setText(String.valueOf(spMain.getInteger(Constants.CALORIE, 0)));

        if (!spMain.getString(Constants.profileURL, "").equals("")) {
            Ion.with(ctx).load(spMain.getString(Constants.profileURL, ""))
                    .asBitmap().setCallback(new FutureCallback<Bitmap>() {
                @Override
                public void onCompleted(Exception e, Bitmap result) {
                    if (e == null) {
                        userImage.setImageBitmap(result);
//                        headerImages.setImageBitmap(result);
                    } else {
                        userImage.setImageResource(R.mipmap.default_images);
//                        headerImages.setImageResource(R.mipmap.header_bg_02);
                    }
                }
            });
        }

        settingsLay.setOnClickListener(this);
        followersLay.setOnClickListener(this);
        rewardsLay.setOnClickListener(this);
        regimenLay.setOnClickListener(this);
        workoutsLay.setOnClickListener(this);
        nutritionLay.setOnClickListener(this);
        userImage.setOnClickListener(this);
        return view;
    }

    private void initViews(View view) {
        settingsLay = (RelativeLayout) view.findViewById(R.id.settingsLay);
        userDescription = (TextView) view.findViewById(R.id.descriptionUser);
        userName = (TextView) view.findViewById(R.id.nameUser);
        userImage = (CircleImageView) view.findViewById(R.id.profilePic);
        calorieValue = (TextView) view.findViewById(R.id.calorieText);
        nutritionLay = (RelativeLayout) view.findViewById(R.id.nutritionLay);
        workoutsLay = (RelativeLayout) view.findViewById(R.id.workoutsLay);
        regimenLay = (RelativeLayout) view.findViewById(R.id.regimenLay);
        rewardsLay = (RelativeLayout) view.findViewById(R.id.rewardsLay);
        followersLay = (RelativeLayout) view.findViewById(R.id.followersLay);
        headerImage = (ImageView) view.findViewById(R.id.headerImage);
        headerImages = (ImageView) view.findViewById(R.id.headerImages);
        headerImageLay = (RelativeLayout) view.findViewById(R.id.headerImageLay);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.settingsLay:
                Intent settings = new Intent(getActivity(), Settings.class);
                startActivity(settings);
                break;

            case R.id.followersLay:
                Intent followersBtn = new Intent(getActivity(), FollowersFollowing.class);
                startActivity(followersBtn);
                break;

            case R.id.workoutsLay:
                Intent myWorkouts = new Intent(getActivity(), MyWorkouts.class);
                startActivity(myWorkouts);
                break;

            case R.id.nutritionLay:
                Intent myNutrition = new Intent(getActivity(), MyNutrition.class);
                startActivity(myNutrition);
                break;

            case R.id.rewardsLay:
                Intent myRewards = new Intent(getActivity(), Rewards.class);
                startActivity(myRewards);
                break;

            case R.id.regimenLay:
                Intent myRegimen = new Intent(getActivity(), MyRegimen.class);
                startActivity(myRegimen);
                break;

            case R.id.profilePic:
                selectImage();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_IMAGE)
                onSelectImageGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA_EDIT)
                //updateProfileImage((Bitmap) data.getExtras().get("data"));
                onSelectImageCameraResult(data);
        }
    }

    private void onSelectImageGalleryResult(final Intent data) {

        Uri selectedUri = data.getData();
        //MEDIA GALLERY IMAGE
        selectedPath = GetFilePath.getPath(ctx, selectedUri);
        Log.i("Image File Path", "" + selectedPath);

        Bitmap ThumbImage = Utils.decodeSampledBitmap(selectedPath, 150, 150);

        selectedPath = ImageSave.saveImageToSdcard(ThumbImage, (Activity) ctx, "profile_img");

        //Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(selectedPath), 250, 250);
        updateProfileImage(ThumbImage);
        System.out.println(profileBase64);

    }

    private void onSelectImageCameraResult(final Intent data) {

        Bitmap ThumbImage = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ThumbImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        selectedPath = ImageSave.saveImageToSdcard(ThumbImage, (Activity) ctx, "profile_img");
        Bitmap ThumbImages = Utils.decodeSampledBitmap(selectedPath, 150, 150);
        selectedPath = ImageSave.saveImageToSdcard(ThumbImages, (Activity) ctx, "profile_img");
        Log.i("Image File Path", "" + selectedPath);

        updateProfileImage(ThumbImage);

    }

    private void updateProfileImage(Bitmap bitmapimage) {
        profileBase64 = Utils.encodeToBase64(bitmapimage, Bitmap.CompressFormat.JPEG, 50);
        spMain.putString(Constants.profileImage, profileBase64);

        if (Utils.isConnectingToInternet(ctx)) {
            updateImage();
        } else {
            Toast.makeText(ctx, Constants.InternetMessage, Toast.LENGTH_SHORT).show();
        }
    }

    private void selectImage() {
        final CharSequence[] items = {ctx.getResources().getString(R.string.chooseImage), ctx.getResources().getString(R.string.click),
                ctx.getResources().getString(R.string.cancel)};
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(ctx.getResources().getString(R.string.chooseImage))) {
                    if (Utils.isConnectingToInternet(ctx)) {
                        Intent intent = new Intent(Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/*");
                        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_IMAGE);
                    } else {
                        Toast.makeText(ctx, Constants.InternetMessage, Toast.LENGTH_SHORT).show();
                    }

                } else if (items[item].equals(ctx.getResources().getString(R.string.click))) {
                    if (Utils.isConnectingToInternet(ctx)) {
                        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, REQUEST_CAMERA_EDIT);
                    } else {
                        Toast.makeText(ctx, Constants.InternetMessage, Toast.LENGTH_SHORT).show();
                    }
                } else if (items[item].equals(ctx.getResources().getString(R.string.cancel))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void updateImage() {

        final Dialog dialog = MyProgressDialog.showProgressDialog((Activity) ctx, "");
        dialog.show();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("email", "");
        jsonObject.addProperty("first_name", "");
        jsonObject.addProperty("last_name", "");
        jsonObject.addProperty("password", "");
        jsonObject.addProperty("phone", "");
        jsonObject.addProperty("gender", "");
        jsonObject.addProperty("profile_image", profileBase64);
        jsonObject.addProperty("age", "");
        jsonObject.addProperty("height", "");
        jsonObject.addProperty("weight", "");
        jsonObject.addProperty("exercise_level", "");
        jsonObject.addProperty("body_fat", "");
        jsonObject.addProperty("user_id", spMain.getString(Constants.userID, ""));
        jsonObject.addProperty("refCode", "");

        Log.d("profile Image Updation ", jsonObject.toString());

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
                                    spMain.putString(Constants.profileURL, res.getString("profile_image"));
                                    if (!spMain.getString(Constants.profileURL, "").equals("")) {
                                        Ion.with(ctx).load(spMain.getString(Constants.profileURL, ""))
                                                .asBitmap().setCallback(new FutureCallback<Bitmap>() {
                                            @Override
                                            public void onCompleted(Exception e, Bitmap result) {
                                                if (e == null) {
                                                    userImage.setImageBitmap(result);
                                                } else {
                                                    userImage.setImageResource(R.mipmap.default_images);
                                                }
                                            }
                                        });
                                    }
                                }
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });

    }
}
