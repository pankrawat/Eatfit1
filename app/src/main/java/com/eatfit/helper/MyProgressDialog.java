package com.eatfit.helper;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;

import com.eatfit.R;


/**
 * Created by Surya Mouly on 9/19/2016.
 */
public class MyProgressDialog {
    public static Dialog dialog;

    public static Dialog showProgressDialog(final Activity activity, final String msg) {
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.progressbar);
        return dialog;
    }

    public static void hideProgressDialog() {
        if (dialog.isShowing()) {
            dialog.cancel();
        }
    }
}
