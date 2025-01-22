package com.arakadds.arak.presentation.dialogs.loader;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.arakadds.arak.R;

public class AlertView {
    public static void showError(String message, Context ctx) {
        showAlert(ctx.getResources().getString(R.string.dialogs_error), message, ctx);
    }

    public static void showAlert(String message, Context ctx) {
        showAlert(ctx.getResources().getString(R.string.dialogs_alert), message, ctx);
    }

    public static void showAlert(String title, String message, Context ctx) {
        //Create a builder
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(title);
        builder.setMessage(message);
        //add buttons and listener
        EmptyListener pl = new EmptyListener();
        builder.setPositiveButton(ctx.getResources().getString(R.string.dialogs_confirm), pl);
        //Create the dialog
        AlertDialog ad = builder.create();
        //show
        ad.show();
    }
}

class EmptyListener implements DialogInterface.OnClickListener {
    @Override
    public void onClick(DialogInterface dialog, int which) {
    }
}
