package com.arakadds.arak.common.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;

import androidx.fragment.app.Fragment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActivityHelper {

    public static void goToActivity(Context ctx, Class<?> to, boolean isFinished) {

        android.content.Intent i = new android.content.Intent(ctx, to);
//        i.addFlags(android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION);
        i.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
        ctx.startActivity(i);
        if (isFinished)
            ((android.app.Activity) ctx).finish();

    }

    public static void goToActivity(Context ctx, Class<?> to, boolean isFinished,String tag,String extra) {

        android.content.Intent i = new android.content.Intent(ctx, to);
//        i.addFlags(android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION);
        i.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
        i.putExtra(tag,extra);
        ctx.startActivity(i);
        if (isFinished)
            ((android.app.Activity) ctx).finish();

    }

    public static void startActivity(android.content.Context ctx, Class<?> to, boolean isFinished, Bundle bundle) {

        android.content.Intent i = new android.content.Intent(ctx, to);
//        i.addFlags(android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION);
        i.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtras(bundle);
        ctx.startActivity(i);
        if (isFinished)
            ((android.app.Activity) ctx).finish();

    }
    public static void goToActivity(android.content.Context ctx, Class<?> to, boolean isFinished, Bundle bundle) {

        android.content.Intent i = new android.content.Intent(ctx, to);
//        i.addFlags(android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION);
//        i.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtras(bundle);
        ctx.startActivity(i);
        if (isFinished)
            ((android.app.Activity) ctx).finish();

    }

    public static void goToActivityFresh(Context ctx, Class<?> to, boolean isFinished, Bundle bundle) {

        Intent i = new Intent(ctx, to);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.putExtras(bundle);
        ctx.startActivity(i);
        if (isFinished)
            ((Activity) ctx).finish();

    }

    public static void goToActivityForResult(Activity ctx, Class<?> to, int REQUEST_CODE) {

        android.content.Intent i = new android.content.Intent(ctx, to);
//        i.addFlags(android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION);
        i.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        ctx.startActivityForResult(i, REQUEST_CODE);
    }

    public static void goToActivityForResult(Activity ctx, Class<?> to, int REQUEST_CODE,
                                             Bundle bundle) {

        android.content.Intent i = new android.content.Intent(ctx, to);
//        i.addFlags(android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION);
        i.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.putExtras(bundle);
        ctx.startActivityForResult(i, REQUEST_CODE);
    }

    public static boolean isValid(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    public static boolean isValidWebUrl(String url) {
        Pattern p = Patterns.WEB_URL;
        Matcher m = p.matcher(url.toLowerCase());
        return m.matches();
    }

    public static String getColoredSpanned(String text, String color) {
        String input = "<font color=" + color + ">" + text + "</font>";
        return input;
    }
}
