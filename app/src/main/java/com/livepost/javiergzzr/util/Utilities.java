package com.livepost.javiergzzr.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.math.BigInteger;

public class Utilities {

    public static boolean isOnline(Context ctx){
        ConnectivityManager connManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mMobile = connManager .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return mWifi.isConnected() || mMobile.isConnected();
    }

    public static boolean isNullOrEmpty(String str) {
        return (str == null || str.trim().length() == 0);
    }
}
