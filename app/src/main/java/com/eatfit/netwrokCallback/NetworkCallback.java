package com.eatfit.netwrokCallback;


/**
 * Created by fourthscreen on 3/11/2016.
 */
public interface NetworkCallback {
    void onNetworkSuccess(String result, String fromUrl);
    void onNetworkTimeOut(String message, String fromUrl);
}
