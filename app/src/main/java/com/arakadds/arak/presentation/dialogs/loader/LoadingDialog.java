package com.arakadds.arak.presentation.dialogs.loader;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import com.arakadds.arak.R;


public class LoadingDialog extends ProgressDialog {
    private static String Message;
    private Context context;

    public static ProgressDialog inIt(Context context, String message) {
        LoadingDialog dialog = new LoadingDialog(context);
        Message = message;
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        return dialog;
    }

    private LoadingDialog(Context context) {
        super(context);
        this.context=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progress_dialog);
        //TextView mTextViewMessage = findViewById(R.id.suspended_loading_message_textView_id);
        //TextView  titleTextView= findViewById(R.id.loader_title_TextView_id);
        //titleTextView.setTextColor(context.getResources().getColor(R.color.blue300));
       // mTextViewMessage.setTextColor(context.getResources().getColor(R.color.watermelon));
        //mTextViewMessage.setText(Message);
        //titleTextView.setText(context.getResources().getColor(R.string.processing));
    }

}