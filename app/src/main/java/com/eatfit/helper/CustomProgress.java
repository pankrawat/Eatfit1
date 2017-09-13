package com.eatfit.helper;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by admin1 on 4/7/16.
 */
public class CustomProgress {

    ProgressDialog progress;
    Context ctx;

    public CustomProgress(Context ctx) {
        this.ctx = ctx;
        progress = new ProgressDialog(ctx);
        progress.setMessage("Please Wait");
        progress.setCancelable(false);
    }

    public void showProgress() {

        progress.show();
    }

    public void showProgress(String msg) {
        progress.setMessage("" + msg);
        progress.show();
    }


    public void hideProgress() {
        if (progress != null && progress.isShowing()) {
            progress.hide();
            progress.dismiss();
        }
    }

    public boolean isShowing() {
        return progress.isShowing();
    }

}
