package com.arakadds.arak.common.helper

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.MediaStore
import android.view.View
import androidx.annotation.RequiresApi
import com.arakadds.arak.presentation.activities.home.HomeActivity
import com.arakadds.arak.utils.Constants.CAMERA_REQUEST_CODE
import com.arakadds.arak.utils.Constants.GALLERY_REQUEST_CODE
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale


object MethodHelper {
    fun showHide(view: View) {
        view.visibility = if (view.visibility == View.VISIBLE) {
            View.INVISIBLE
        } else {
            View.VISIBLE
        }
    }

    fun hideView(view: View) {
        view.visibility = if (view.visibility == View.GONE) {
            View.GONE
        } else {
            View.GONE
        }
    }

    fun disableView(view: View) {
        view.isEnabled = false
        view.isClickable = false
    }

    fun enableView(view: View) {
        view.isEnabled = true
        view.isClickable = true
    }

    fun backToHome(currentActivity: Activity) {
        //clear activities stack
        val intent = Intent(currentActivity, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        currentActivity.startActivity(intent)
        currentActivity.finish()
    }

    fun isValidPassword(password: String): Boolean {
        val passwordRegex = Regex("^(?=.*[\\W_])(?=.*[0-9a-zA-Z]).{6,}$")
        return password.matches(passwordRegex)
    }

    fun gallery(activity: Activity) {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        activity.startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    fun camera(activity: Activity) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        activity.startActivityForResult(intent, CAMERA_REQUEST_CODE)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun formatDate(inputDate: String): String {
        // Define the formatter for parsing the input date
        val inputFormatter = DateTimeFormatter.ISO_DATE_TIME

        // Define the formatter for the output date
        val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        // Parse the input date string
        val dateTime = ZonedDateTime.parse(inputDate, inputFormatter)

        // Get the current time in the same time zone as the input date
        val now = ZonedDateTime.now(ZoneId.of(dateTime.zone.id))

        // Calculate the duration between now and the input date
        val duration = Duration.between(dateTime, now)

        // Check if the duration is less than 24 hours
        return if (duration.toHours() < 24) {
            "Time is less than 24 hours ago"
        } else {
            // Format the date to "yyyy-MM-dd"
            dateTime.format(outputFormatter)
        }
    }
    fun shareAd(adTitle: String, adDescription: String, adUrl: String, context: Context) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Check out this ad: $adTitle")
            putExtra(Intent.EXTRA_TEXT, "$adTitle\n\n$adDescription\n\nCheck it out here: $adUrl")
        }

        // Starting the share Intent
        context.startActivity(Intent.createChooser(shareIntent, "Share ad via"))
    }

    fun stringToDate(dateString: String, format: String): Date? {
        val formatter = SimpleDateFormat(format, Locale.getDefault())
        return try {
            formatter.parse(dateString)
        } catch (e: Exception) {
            e.printStackTrace() // Handle exception as needed
            null
        }
    }
}