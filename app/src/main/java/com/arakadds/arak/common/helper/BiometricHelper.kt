package com.arakadds.arak.common.helper

import android.app.Activity
import android.os.Build
import android.widget.ImageView
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.arakadds.arak.R
import com.arakadds.arak.presentation.dialogs.ApplicationDialogs
import com.arakadds.arak.presentation.dialogs.ApplicationDialogs.openAlertDialog
import com.arakadds.arak.utils.AppEnums.DialogActionTypes.DISMISS
import com.arakadds.arak.utils.Constants
import com.arakadds.arak.utils.Utilities
import java.util.concurrent.Executor


private lateinit var executor: Executor
private lateinit var biometricPrompt: BiometricPrompt
private lateinit var promptInfo: BiometricPrompt.PromptInfo

private fun FragmentActivity.setPrompt(act: Activity/*, textViewAuthResult: TextView*/) {
    biometricPrompt = BiometricPrompt(this, executor,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(
                errorCode: Int,
                errString: CharSequence
            ) {
                super.onAuthenticationError(errorCode, errString)
                Utilities.showSnackBar(
                    Constants.AUTHENTICATION_ERROR + " " + errString,
                    act
                )
            }

            override fun onAuthenticationSucceeded(
                result: BiometricPrompt.AuthenticationResult
            ) {
                super.onAuthenticationSucceeded(result)
                Utilities.showSnackBar(
                    Constants.AUTHENTICATION_SUCCEEDED,
                    act
                )
                //textViewAuthResult.visibility = View.VISIBLE
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Utilities.showSnackBar(
                    Constants.AUTHENTICATION_FAILED,
                    act
                )
            }
        })
}

fun FragmentActivity.checkBiometric(
    act: Activity,
    /*textViewAuthResult: TextView,*/
    authenticateFingerPrintImageView: ImageView,
    alertDialogCallbacks: ApplicationDialogs.AlertDialogCallbacks
) {

    if (Utilities.deviceHasPasswordPinLock(this)) {
        //binding.DeviceHasPINPasswordLock.text = Contestants.TRUE
    } else {
        //binding.DeviceHasPINPasswordLock.text = Contestants.FALSE
    }

    executor = ContextCompat.getMainExecutor(this)

    setPrompt(act/*, textViewAuthResult*/)

    if (Utilities.isBiometricHardWareAvailable(this)) {
        // binding.DeviceHasBiometricFeatures.text = Contestants.AVAILABLE
        //binding.DeviceHasFingerPrint.text = Contestants.TRUE

        //Enable the button if the device has biometric hardware available
        //binding.authenticatefingerprintbutton.isEnabled = true

        initBiometricPrompt(
            Constants.BIOMETRIC_AUTHENTICATION,
            Constants.BIOMETRIC_AUTHENTICATION_SUBTITLE,
            Constants.BIOMETRIC_AUTHENTICATION_DESCRIPTION,
            false,
            authenticateFingerPrintImageView
        )
    } else {

        //Fallback, use device password/pin
        if (Utilities.deviceHasPasswordPinLock(this)) {
            initBiometricPrompt(
                Constants.PASSWORD_PIN_AUTHENTICATION,
                Constants.PASSWORD_PIN_AUTHENTICATION_SUBTITLE,
                Constants.PASSWORD_PIN_AUTHENTICATION_DESCRIPTION,
                true,
                authenticateFingerPrintImageView
            )
        } else {
            openAlertDialog(
                this,
                resources.getString(R.string.dialogs_error),
                "لا يمتلك بصمة اصبع",
                false,
                DISMISS,
                alertDialogCallbacks
            )
        }
    }
}

private fun initBiometricPrompt(
    title: String,
    subtitle: String,
    description: String,
    setDeviceCred: Boolean,
    authenticateFingerPrintImageView: ImageView
) {
    if (setDeviceCred) {
        /*For API level > 30
          Newer API setAllowedAuthenticators is used*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val authFlag =
                BiometricManager.Authenticators.DEVICE_CREDENTIAL or BiometricManager.Authenticators.BIOMETRIC_STRONG
            promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle(title)
                .setSubtitle(subtitle)
                .setDescription(description)
                .setAllowedAuthenticators(authFlag)
                .build()
        } else {
            /*SetDeviceCredentials method deprecation is ignored here
              as this block is for API level<30*/
            @Suppress("DEPRECATION")
            promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle(title)
                .setSubtitle(subtitle)
                .setDescription(description)
                .setDeviceCredentialAllowed(true)
                .build()
        }
    } else {
        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setDescription(description)
            .setNegativeButtonText(Constants.CANCEL)
            .build()
    }

    authenticateFingerPrintImageView.setOnClickListener {
        biometricPrompt.authenticate(promptInfo)
    }
}

