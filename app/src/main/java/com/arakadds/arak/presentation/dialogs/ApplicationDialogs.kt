package com.arakadds.arak.presentation.dialogs

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.arakadds.arak.R

object ApplicationDialogs {

    interface OnSendRequest {
        fun onConfirm()
    }

    interface AlertDialogCallbacks {
        fun onClose()
        fun onConfirm(actionType: Int)

    }


    interface DialogCallBack {
        fun openCameraCallback()
        fun openGalleryCallback()
        fun continuePressedCallback()
    }

    fun successDialog(
        context: Context,
        activity: Activity?,
        resources: Resources,
        targetedActivity: Class<*>?,
        contentText: String?,
        buttonText: String?,
        dialogCallBack: DialogCallBack
    ) {
        val alertDialog = androidx.appcompat.app.AlertDialog.Builder(context).create()
        alertDialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val inflater: LayoutInflater = alertDialog.getLayoutInflater()
        val dialogView = inflater.inflate(R.layout.dialog_sucess, null)
        val goToMyAdsTextView = dialogView.findViewById<TextView>(R.id.go_to_my_ads_textView_id)
        val successDescTextView = dialogView.findViewById<TextView>(R.id.success_desc_textView_id)
        val successTitleTextView = dialogView.findViewById<TextView>(R.id.seccess_title_textView_id)
        successTitleTextView.text = resources.getString(R.string.Success_activity_Success)
        successDescTextView.text = contentText
        goToMyAdsTextView.text = buttonText
        goToMyAdsTextView.setOnClickListener { v: View? ->
            alertDialog.dismiss()
            dialogCallBack.continuePressedCallback()
            val intent = Intent(context, targetedActivity)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
            try {
                activity?.finish()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        alertDialog.setCancelable(false)
        alertDialog.setView(dialogView)
        alertDialog.show()
    }


    fun addImageDialog(context: Context?, dialogCallBack: DialogCallBack) {
        val dialog = Dialog(context!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // before
        dialog.setContentView(R.layout.dialog_select_media_type)
        dialog.setCancelable(true)

        /* WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;*/
        val titleTextView = dialog.findViewById<TextView>(R.id.dialog_media_title_textView_id)
        val cameraImageView = dialog.findViewById<ImageView>(R.id.dialog_media_camera_imageView_id)
        val GalleryImageView =
            dialog.findViewById<ImageView>(R.id.dialog_media_Gallery_imageView_id)
        val cameraTitleTextView =
            dialog.findViewById<TextView>(R.id.dialog_media_camera_title_TextView_id)
        val GalleryTitleTextView =
            dialog.findViewById<TextView>(R.id.dialog_media_Gallery_title_TextView_id)
        cameraImageView.setOnClickListener { v: View? ->
            dialogCallBack.openCameraCallback()
            dialog.dismiss()
        }
        GalleryImageView.setOnClickListener { v: View? ->
            dialogCallBack.openGalleryCallback()
            dialog.dismiss()
        }
        dialog.show()
        //dialog.getWindow().setAttributes(lp);
    }

    fun openAlertDialog(
        activity: Activity,
        title: String?,
        descMessage: String?,
        isHasCancelButton: Boolean,
        actionType: Int,
        alertDialogCallbacks: AlertDialogCallbacks
    ) {
        val dialogBuilder = AlertDialog.Builder(activity)
        val inflater = activity.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.dialog_alert, null)
        dialogBuilder.setView(dialogView)

        val confirm1Button = dialogView.findViewById<Button>(R.id.dialog_alert_ok1_button)
        val confirm2Button = dialogView.findViewById<Button>(R.id.dialog_alert_ok2_button)
        val linearLayout = dialogView.findViewById<LinearLayout>(R.id.dialog_alert_buttons_linearLayout_id)
        val cancelButton = dialogView.findViewById<Button>(R.id.dialog_alert_cancel_button)
        val titleTextView = dialogView.findViewById<TextView>(R.id.dialog_alert_title_TextView_id)
        val descTextView = dialogView.findViewById<TextView>(R.id.dialog_alert_desc_TextView_id)

        val alertDialog = dialogBuilder.create()

        titleTextView.text = title
        descTextView.text = descMessage

        if (isHasCancelButton) {
            linearLayout.visibility = View.VISIBLE
            confirm2Button.visibility = View.GONE
        } else {
            linearLayout.visibility = View.GONE
            confirm2Button.visibility = View.VISIBLE
        }

        confirm1Button.setOnClickListener { v: View? ->
            alertDialogCallbacks.onConfirm(actionType)
            alertDialog.dismiss()
        }

        confirm2Button.setOnClickListener { v: View? ->
            alertDialogCallbacks.onConfirm(actionType)
            alertDialog.dismiss()
        }

        cancelButton.setOnClickListener { v: View? ->
            alertDialogCallbacks.onClose()
            alertDialog.dismiss()
        }

        alertDialog.setCancelable(false)
        alertDialog.setCanceledOnTouchOutside(false)
        if (!activity.isFinishing) {
            alertDialog.show()
        }
    }

}