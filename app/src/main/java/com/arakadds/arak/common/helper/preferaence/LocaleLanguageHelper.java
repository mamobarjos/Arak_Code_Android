package com.arakadds.arak.common.helper.preferaence;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;

import java.util.Locale;

/*
*
* created by hussam zuriqat at 8/2018
*
* */
public class LocaleLanguageHelper {

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
}
