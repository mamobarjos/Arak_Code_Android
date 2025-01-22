package com.arakadds.arak.utils;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.provider.Settings;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDex;

import com.google.firebase.messaging.FirebaseMessaging;

import java.util.List;

public class MyApplication extends Application {
    private static MyApplication singleInstance;
    /*private static DatabaseManager manager;*/
    //private static BusinessManagers businessManagers;

    @Override
    public void onCreate() {
        super.onCreate();

//        try {
//            if()
//            ProviderInstaller.installIfNeeded(getApplicationContext());
//            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
//            sslContext.init(null, null, null);
//            sslContext.createSSLEngine();
//        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException | NoSuchAlgorithmException | KeyManagementException e) {
//            e.printStackTrace();
//        }

        // Create global configuration and initialize ImageLoader with this config
      /*  ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);
        Contacts.initialize(this);
*/
        AddSubscribe();
        singleInstance = this;

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

//        // Enable verbose OneSignal logging to debug issues if needed.
//        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
//
//        // OneSignal Initialization
//        OneSignal.initWithContext(this);
//        OneSignal.setAppId(ONESIGNAL_APP_ID);

        try {
            //to solve mirroring issue
            new WebView(this).destroy();
        } catch (Exception ignored) {

        }
    }

    public static MyApplication getAppInstance() {
        return singleInstance;
    }


    private void AddSubscribe() {
        FirebaseMessaging.getInstance().subscribeToTopic("general").addOnCompleteListener(task -> {
            if (task.isCanceled()) {
                task.getException().printStackTrace();
            }
        });
    }

    private void RemoveSubscribe() {
        FirebaseMessaging.getInstance().unsubscribeFromTopic("general").addOnCompleteListener(task -> {
            if (task.isCanceled()) {
                task.getException().printStackTrace();
            }
        });
    }

  /*  public DatabaseManager getDatabaseManager() throws java.io.IOException {
        DatabaseManager.init(getApplicationContext());
        if (manager == null) {
            manager = DatabaseManager.getInstance();
        }
        return manager;
    }

    public void createDatabase(Handler handler) throws java.io.IOException {
        if (manager != null) {
            manager.createDatabase(getApplicationContext(), handler);
        } else {
            DatabaseManager.init(getApplicationContext());
            manager = DatabaseManager.getInstance();
            manager.createDatabase(getApplicationContext(), handler);
        }
    }*/

   /* public static BusinessManagers getManagers() {
        if (businessManagers == null) {
            businessManagers = BusinessManagers.getInstance();
        }
        return businessManagers;
    }*/


    public String getDeviceID() {
        @SuppressLint("HardwareIds")
        String android_id = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return android_id;
    }


//    public String getDeviceID() {
//        @SuppressLint("HardwareIds")
//        String android_id = Settings.Secure.getString(getContentResolver(),
//                Settings.Secure.ANDROID_ID);
//        return "a6d1a93b7b89cd6c";
//    }

    //    to solve MultiDex issue
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


    public static String getErrorsString(List<Object> errors) {

        StringBuilder sb = new StringBuilder();
        ;
        for (Object error : errors) {
            sb.append(error).append(" \n");
        }
        return sb.toString();
    }

  /*  public static boolean isHMSAvailable(Context context) {
        HuaweiApiAvailability hms = HuaweiApiAvailability.getInstance();
        int isHMS = hms.isHuaweiMobileServicesAvailable(context);
        return isHMS == com.huawei.hms.api.ConnectionResult.SUCCESS;
    }

    public static boolean checkGooglePlayServices(Context context) {
        GoogleApiAvailability gms = GoogleApiAvailability.getInstance();
        int isGMS = gms.isGooglePlayServicesAvailable(context);
        return isGMS == ConnectionResult.SUCCESS;
    }*/
}

