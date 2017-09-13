package com.eatfit.helper;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Abhinav Suman on 8/3/2016.
 */
public class Utils {

    public static void hideSoftKeyBoard(Activity ctx) {
        View focusedView = ctx.getCurrentFocus();
        if (focusedView != null) {
            InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(focusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    //methods to compress image starts//
    public static Bitmap decodeSampledBitmap(String url, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // BitmapFactory.decodeResource(res, resId, options);
        BitmapFactory.decodeFile(url, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(url, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public static boolean isConnectingToInternet(Context _context) {
        ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }

        }
        return false;
    }

    public static Long getMiliseconds(Long minute) {
        return TimeUnit.MINUTES.toMillis(minute);
    }

    public static boolean isValidEmailCom(String email_validator) {
        // TODO Auto-generated method stub
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email_validator);

        if (matcher.matches() && email_validator.length() > 5) {
            return true;
        }
        return false;
    }

    public static void CopyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for (; ; ) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1)
                    break;
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {
        }
    }

    public static String getYoutubeVideoId(String youtubeUrl) {
        String VIDEO_ID = "";
        if (youtubeUrl != null && youtubeUrl.trim().length() > 0 && youtubeUrl.startsWith("http")) {

            String expression = "^.*((youtu.be" + "\\/)" + "|(v\\/)|(\\/u\\/w\\/)|(embed\\/)|(watch\\?))\\??v?=?([^#\\&\\?]*).*"; // var regExp = /^.*((youtu.be\/)|(v\/)|(\/u\/\w\/)|(embed\/)|(watch\?))\??v?=?([^#\&\?]*).*/;
            CharSequence input = youtubeUrl;
            Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(input);
            if (matcher.matches()) {
                String groupIndex1 = matcher.group(7);
                if (groupIndex1 != null && groupIndex1.length() == 11)
                    VIDEO_ID = groupIndex1;
            }
        }
        return VIDEO_ID;
    }

    /*public static boolean isAfterDate(Date date) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        Calendar today = Calendar.getInstance();
        return today.after(calendar);
    }*/

    /*public static boolean getDifference(Date date) {
        Calendar selectedDate = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        Calendar startcompare1 = Calendar.getInstance();
        Calendar endcompare15 = Calendar.getInstance();
        Calendar startcompare16 = Calendar.getInstance();
        Calendar endcompare31 = Calendar.getInstance();
        selectedDate.setTime(date);
        startcompare1.set(Calendar.DATE, 1);
        endcompare15.set(Calendar.DATE, 15);
        startcompare16.set(Calendar.DATE, 16);
        endcompare31.set(Calendar.DATE, today.getActualMaximum(Calendar.DATE));
        if ((today.after(startcompare1) && today.before(endcompare15)) && (selectedDate.after(startcompare1) && selectedDate.before(endcompare15))) {
            return true;
        } else if ((today.after(startcompare16) && today.before(endcompare31)) && (selectedDate.after(startcompare16) && selectedDate.before(endcompare31))) {
            return true;
        } else {
            return false;
        }
    }*/

    public static boolean getDifference(Date date) {
        int todaydate = Calendar.getInstance().get(Calendar.DATE);
        Calendar seleDate = Calendar.getInstance();
        seleDate.setTime(date);
        int selectedate = seleDate.get(Calendar.DATE);
        if ((todaydate >= 1 && todaydate <= 15) && (selectedate >= 1 && selectedate <= 15)) {
            return true;
        } else if ((todaydate >= 16 && todaydate <= 31) && (selectedate >= 16 && selectedate <= 31)) {
            return true;
        } else {
            return false;
        }
    }

    public static String ChangeDateToString(String str) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        SimpleDateFormat sdfParse = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        try {
            cal.setTime(sdf.parse(str));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return String.valueOf(cal.get(Calendar.DATE));
    }

    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality) {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    private static Bitmap Base64ToBitmap(String myImageData) {
        byte[] imageAsBytes = Base64.decode(myImageData.getBytes(), Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
    }
}
