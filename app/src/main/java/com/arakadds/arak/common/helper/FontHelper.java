package com.arakadds.arak.common.helper;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.arakadds.arak.R;
import com.arakadds.arak.presentation.customView.CustomTypefaceSpan;

import java.util.Hashtable;

public class FontHelper  {
//   //Arabic
//    private static final String BOLD_AR =          "NotoNaskhArabic-Bold.ttf";
//
//    private static final String REGULAR_AR =       "NotoNaskhArabic-Regular.ttf";
//
//
//    //English
//
//    //Light
//    private static final String LIGHT_EN =              "Open Sans Light.ttf";
//
//    private static final String LIGHT_ITALIC_EN =       "Open Sans Light Italic.ttf";
//
//
//   //Regular
//    private static final String REGULAR_EN =              "Open Sans.ttf";
//
//    private static final String REGULAR_ITALIC_EN =       "Open Sans Italic.ttf";
//
//    //Semi-Bold
//    private static final String SEMI_BOLD_EN =              "Open Sans Semibold.ttf";
//
//    private static final String SEMI_BOLD_ITALIC_EN =       "Open Sans Semibold Italic.ttf";
//
//
//    //Bold
//    private static final String BOLD_EN =                 "Open Sans Bold.ttf";
//
//    private static final String BOLD_ITALIC_EN =          "Open Sans Bold Italic.ttf";
//
//    //Extra Bold
//    private static final String EXTRA_BOLD_EN =                 "Open Sans Extrabold.ttf";
//
//    private static final String EXTRA_BOLD_ITALIC_EN =          "Open Sans Extrabold Italic.ttf";
//
//
//
//     private static  String FONT_NAME_REGULAR ;
//    private static  String FONT_NAME_BOLD ;
//
//
//
//
//
//   public static void setFontLocale(String loc) {
//        if(loc.equals(LocaleHelper.ARABIC)){
//            FONT_NAME_BOLD=BOLD_AR;
//            FONT_NAME_REGULAR=REGULAR_AR;
//        }else{
//            FONT_NAME_BOLD=BOLD_EN;
//            FONT_NAME_REGULAR=REGULAR_EN;
//        }
//
//    }

    private static final Hashtable<String, Typeface> typeFaceCache = new Hashtable<>();

    public static String getTypeFaceByPosition(Context ctx, int pos){
        String[] fonts = ctx.getResources().getStringArray(R.array.fonts);
        return fonts[pos];
    }
    public static Typeface getTypeFaceByName(Context ctx,String typefaceName){
        return getFont(ctx,typefaceName);

    }

    public static String getFontNameBold(Context context) {
        return context.getString(R.string.bold);
    }

    public static String getFontNameRegular(Context context) {
        return context.getString(R.string.regular);
    }

    public static void setFont(Context currentActivity , TextView view){
        Typeface font = getFont(currentActivity,getFontNameRegular(currentActivity));

        view.setTypeface(font);
    }


    public static void setFont(Activity currentActivity , Button view){
        Typeface font = getFont(currentActivity,getFontNameRegular(currentActivity));
        view.setTypeface(font);
    }

    public static void setFont(Activity currentActivity , EditText view){
        Typeface font = getFont(currentActivity,getFontNameRegular(currentActivity));
        view.setTypeface(font);
    }
    public static void setFont(Activity currentActivity , AutoCompleteTextView view){
        Typeface font = getFont(currentActivity,getFontNameRegular(currentActivity));
        view.setTypeface(font);
    }public static Typeface getTypeFace(Context currentActivity){
        return getFont(currentActivity,getFontNameRegular(currentActivity));
    }

    public static SpannableString setSpannableString(Context context, String text){
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new CustomTypefaceSpan("",getTypeFace(context)),0
                ,spannableString.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return spannableString;
    }
    public static SpannableString setSpannableString(Context context, String text,int firstChar,int lastChar,int colorRes,Typeface typeface){
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new ForegroundColorSpan(context.getResources().getColor(colorRes)),firstChar
                ,lastChar, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        spannableString.setSpan(new CustomTypefaceSpan("",typeface),0
                ,spannableString.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return spannableString;
    }
    public static SpannableString setSpannableString(Context context, String text, int colorRes){
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new CustomTypefaceSpan("",getTypeFace(context)),0
                ,spannableString.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(colorRes),0
                ,spannableString.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        return spannableString;
    }


    public static SpannableString setMultiColorsToText(Context context, String createAccountText, int firstIndex, int midIndex, int lastIndex) {
        SpannableString spannableString = new SpannableString(createAccountText);
        try{
            ForegroundColorSpan fcsBlue = new ForegroundColorSpan(context.getResources().getColor(R.color.dark_blue));
            ForegroundColorSpan fcsOrange = new ForegroundColorSpan(context.getResources().getColor(R.color.orange_dark));
            spannableString.setSpan(fcsBlue, firstIndex, midIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(fcsOrange, midIndex, lastIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }catch (Exception e){
            e.printStackTrace();
        }
        return spannableString;
    }

    public static Typeface getFont(Context c, String assetPath) {
        synchronized (typeFaceCache) {
            if (!typeFaceCache.containsKey(assetPath)) {
                try {
                    Typeface t = Typeface.createFromAsset(c.getAssets(),
                            assetPath);
                    typeFaceCache.put(assetPath, t);
                } catch (Exception e) {
                    /*LoggerHelper.error("Could not get typeface '" + assetPath
                            + "' because " + e.getMessage());*/
                    return null;
                }
            }
            return typeFaceCache.get(assetPath);
        }
    }

}
