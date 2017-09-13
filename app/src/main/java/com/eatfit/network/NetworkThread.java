package com.eatfit.network;

import android.content.Context;

import com.eatfit.constants.Constants;
import com.eatfit.netwrokCallback.NetworkCallback;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.Builders;


public class NetworkThread {
    private String url;
    private NetworkCallback callback;
    public NetworkThread(NetworkCallback callback, String url){
    this.callback=callback;
        this.url=url;
    }

    private Builders.Any.B getIon(Context context, String url, int timeout){
        return Ion.with(context).load(url).setTimeout(timeout);
    }

    public void getNetworkResponse(Context context, JsonObject jsonObject, int timeout){
        Builders.Any.B ion=getIon(context,url,timeout);
        ion.setJsonObjectBody(jsonObject)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if (e != null) {
                            callback.onNetworkTimeOut(Constants.ERR_NETWORK_TIMEOUT, url);
                            return;
                        }
                        callback.onNetworkSuccess(result, url);
                    }
                });
    }
}
