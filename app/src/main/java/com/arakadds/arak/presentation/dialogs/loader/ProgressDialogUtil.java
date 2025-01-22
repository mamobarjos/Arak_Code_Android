package com.arakadds.arak.presentation.dialogs.loader;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;

public class ProgressDialogUtil {

    private static ProgressDialog mProgressDialog = null;

    public static void showLoadingDialog(String message, Context context) {

        mProgressDialog = LoadingDialog.inIt(context, message);
        mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    public static void hideLoadingDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}