package com.arakadds.arak.common.helper;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;

import java.util.Locale;

import static com.arakadds.arak.common.helper.ActivityHelper.goToActivity;

/*
*
* created by hussam zuriqat at 8/2018
*
* */
public class LocaleHelper {

    private static final String SELECTE_LANGUAGE="Locale.Helper.Selected.Language";

    public static Context onAttach(Context context){

        String lang=getPersistedData(context, Locale.getDefault().getLanguage());

        return setLocale(context,lang);
    }

    public static Context onAttach(Context context, String defaultLanguage){

        String lang=getPersistedData(context, defaultLanguage);

        return setLocale(context,lang);
    }

    public static Context setLocale(Context context, String lang) {
        persist(context,lang);
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.N)
            return updateResources(context,lang);

        return updateResourcesLegacy(context,lang);
    }



    @TargetApi(Build.VERSION_CODES.N)
    private static Context updateResources(Context context, String lang) {

        Locale locale=new Locale(lang);
        Locale.setDefault(locale);

        Configuration config=context.getResources().getConfiguration();
        config.setLocale(locale);
        config.setLayoutDirection(locale);

        return context.createConfigurationContext(config);
    }

    @SuppressWarnings("deprecation")
    private static Context updateResourcesLegacy(Context context, String lang) {

        Locale locale=new Locale(lang);
        Locale.setDefault(locale);

        Resources resources=context.getResources();

        Configuration config=resources.getConfiguration();
        config.locale=locale;
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.JELLY_BEAN_MR1);
        config.setLayoutDirection(locale);
        resources.updateConfiguration(config,resources.getDisplayMetrics());
        return context;
    }

    private static void persist(Context context, String lang) {
        try {
            SharedPreferences pref= PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor=pref.edit();

            editor.putString(SELECTE_LANGUAGE,lang);
            editor.apply();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static String getPersistedData(Context context, String language) {

        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(context);

        return preferences.getString(SELECTE_LANGUAGE,language);
    }

   /* public static void createNewUser(HashMap<String, Object> hashMap, String language, Button button, Context context, ProgressBar progressBar, Resources resources, boolean isSocialLogin) {
        progressBar.setVisibility(View.VISIBLE);
        Controller.registration(language, hashMap, new Reception((data, tag) -> {
            if (data != null && tag == TAGS.REGISTRATION_ID) {
                RegistrationResponseData registrationResponseData = (RegistrationResponseData) data;
                if (registrationResponseData != null) {
                    if (registrationResponseData.getStatusCode() == 200) {
                        SharedPreferencesHelper.putSharedPreferencesString(
                                SharedPreferencesKeys.KEY_TOKEN, registrationResponseData.getToken());

                        SharedPreferencesHelper.putSharedPreferencesString(
                                SharedPreferencesKeys.SOCIAL_TOKEN_ID, registrationResponseData.getData().getSocialToken());

                        SharedPreferencesHelper.putSharedPreferencesInt(
                                SharedPreferencesKeys.KEY_USER_STATUS, registrationResponseData.getData().getIs_active());

                        SharedPreferencesHelper.putSharedPreferencesInt(
                                SharedPreferencesKeys.KEY_USER_ROLE, registrationResponseData.getData().getRole());

                        SharedPreferencesHelper.putSharedPreferencesFloat(
                                SharedPreferencesKeys.KEY_USER_BALANCE, registrationResponseData.getData().getBalance());

                        SharedPreferencesHelper.putSharedPreferencesString(
                                SharedPreferencesKeys.KEY_USER_PHONE_NUMBER, registrationResponseData.getData().getPhone_no());

                        SharedPreferencesHelper.putSharedPreferencesString(
                                SharedPreferencesKeys.KEY_USER_FULL_NAME, registrationResponseData.getData().getFullname());

                        SharedPreferencesHelper.putSharedPreferencesString(
                                SharedPreferencesKeys.KEY_USER_EMAIL, registrationResponseData.getData().getEmail());

                        SharedPreferencesHelper.putSharedPreferencesString(
                                SharedPreferencesKeys.KEY_USER_COUNTRY, registrationResponseData.getData().getCountry());

                        SharedPreferencesHelper.putSharedPreferencesString(
                                SharedPreferencesKeys.KEY_USER_CITY, registrationResponseData.getData().getCity());

                        SharedPreferencesHelper.putSharedPreferencesString(
                                SharedPreferencesKeys.KEY_USER_GENDER, registrationResponseData.getData().getGender());

                        SharedPreferencesHelper.putSharedPreferencesString(
                                SharedPreferencesKeys.KEY_USER_IMAGE, registrationResponseData.getData().getImg_avatar());

                        SharedPreferencesHelper.putSharedPreferencesString(
                                SharedPreferencesKeys.COMPANY_NAME, registrationResponseData.getData().getCompanyName());

                        SharedPreferencesHelper.putSharedPreferencesInt(
                                SharedPreferencesKeys.KEY_USER_ID, registrationResponseData.getData().getId());

                        goToActivity(context, HomeActivity.class, false);
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                    button.setEnabled(true);
                }
            } else {
                if (isSocialLogin){
                    Toaster.show(resources.getString(R.string.toast_phone_number_registered));
                }else {
                    Toaster.show(resources.getString(R.string.toast_please_try_again_later));
                }
                progressBar.setVisibility(View.GONE);
                button.setEnabled(true);
            }
        }));
    }*/
}
