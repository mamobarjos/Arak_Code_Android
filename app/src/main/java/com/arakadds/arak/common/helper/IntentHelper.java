package com.arakadds.arak.common.helper;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.CalendarContract;
import android.text.TextUtils;

import com.arakadds.arak.presentation.activities.other.WebViewActivity;

import java.util.Date;

public class IntentHelper {

        public static void startWebActivity(Context context, String url,String title) {
            Intent intent = new Intent(context, WebViewActivity.class);
            intent.putExtra("web_Url", url);
            intent.putExtra("page_title", title);
            context.startActivity(intent);
    }

        public static void mainShare(Activity ctx, String text) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
        sendIntent.setType("text/plain");
        ctx.startActivity(sendIntent);
    }

        public static void startGalleryActivity(Activity context, int requestCode) {
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        context.startActivityForResult(
                Intent.createChooser(intent, "Select File"),
                requestCode);
    }

        public static void startCallActivity(Context context, String mobileNo) throws SecurityException {
//        Intent intent = new Intent(Intent.ACTION_CALL);
//
//        intent.setEntityData(Uri.parse("tel:" + mobileNo));
//        context.startActivity(intent);
    }

        public static void startCallDialerActivity(Context context, String mobileNo) throws SecurityException {
        Uri mobileNumber = Uri.parse("tel:" + mobileNo);
        Intent callMobileIntent = new Intent(Intent.ACTION_DIAL, mobileNumber);
        context.startActivity(callMobileIntent);
    }

        public static void startCallDialerActivity(Context context, Uri mobileNo) throws SecurityException {
        Intent callMobileIntent = new Intent(Intent.ACTION_DIAL, mobileNo);
        context.startActivity(callMobileIntent);
    }

        public static void startEmailActivity(Context context, int toResId, int subjectResId, int bodyResId) {
        startEmailActivity(context, context.getString(toResId), context.getString(subjectResId),
                context.getString(bodyResId));
    }

        public static void startEmailActivity(Context context, String to, String subject, String body) {
        final Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");

        if (!TextUtils.isEmpty(to)) {
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{to});
        }
        if (!TextUtils.isEmpty(subject)) {
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        }
        if (!TextUtils.isEmpty(body)) {
            intent.putExtra(Intent.EXTRA_TEXT, body);
        }

        final PackageManager pm = (PackageManager) context.getPackageManager();
        try {
            if (pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).isEmpty()) {
                intent.setType("text/plain");
            }
        } catch (Exception e) {
//            Log.w("Exception encountered while looking for email intent receiver.", e);
        }

        context.startActivity(intent);
    }


        public static void startGooglePlayActivity(Context ctx) {
        final String appPackageName = ctx.getPackageName();
        try {
            ctx.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            ctx.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }


        public static void openTwitter(Activity ctx, String pageId, String pageName) {
        Intent intent = null;
        try {
            // get the Twitter app if possible
            ctx.getPackageManager().getPackageInfo("com.twitter.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?user_id=" + pageId));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception e) {
            // no Twitter app, revert to browser
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + pageName));
        }
        ctx.startActivity(intent);
    }

        public static void openInstagram(Activity ctx) {

        Uri uri = Uri.parse("http://instagram.com/_u/");
        Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);
        likeIng.setPackage("com.instagram.android");
        try {
            ctx.startActivity(likeIng);
        } catch (ActivityNotFoundException e) {
            ctx.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://instagram.com/")));
        }
    }


        public static void openFacebook(Context ctx, String pageUrl, String pageId) {
        PackageManager packageManager = ctx.getPackageManager();
        String pageLink = "";
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                pageLink = "fb://facewebmodal/f?href=" + pageUrl;
            } else { //older versions of fb app
                pageLink = "fb://page/" + pageId;
            }
        } catch (PackageManager.NameNotFoundException e) {
            pageLink = pageUrl; //normal web url
        }

        Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
        facebookIntent.setData(Uri.parse(pageLink));
        ctx.startActivity(facebookIntent);
    }


        public static void insertToGoogleCalendar(Context ctx, Date time, String title,
            String description, String location) {
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, time.getTime())
//                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
                .putExtra(CalendarContract.Events.TITLE, title)
                .putExtra(CalendarContract.Events.DESCRIPTION, description)
//                .putExtra(CalendarContract.Events.EVENT_LOCATION, location)
                .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
//                .putExtra(Intent.EXTRA_EMAIL, "example@example.com,trevor@example.com");
        ctx.startActivity(intent);
    }

        public static void startMapsIntent(Context activity, String map_cordinates) {
        String[] coordinates;
        if(map_cordinates.contains(",")){
            coordinates = map_cordinates.split(",\\s");
        }else{
            coordinates = map_cordinates.split("\\s");
        }

        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(
                "http://maps.google.com/maps?daddr=" +

                        coordinates[0] + " " + coordinates[1]
                //+
                //                  "&daddr=" +
                //                lat +
                //              "," +
                //            lon
        ));


        activity.startActivity(intent);
    }




}
