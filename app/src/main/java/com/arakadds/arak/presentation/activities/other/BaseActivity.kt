package com.arakadds.arak.presentation.activities.other

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import com.arakadds.arak.presentation.dialogs.loader.ProgressDialogUtil
import com.arakadds.arak.utils.managers.RunTimeLocaleManager
import dagger.android.support.DaggerAppCompatActivity

open class BaseActivity : DaggerAppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.R)
    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        RunTimeLocaleManager.overrideLocale(this)
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        window.setDecorFitsSystemWindows(true)
        //window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
    }

    fun showLoadingDialog(context: Context?, message: String?) {
        try {
            ProgressDialogUtil.showLoadingDialog(message, context)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun hideLoadingDialog() {
        try {
            ProgressDialogUtil.hideLoadingDialog()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(RunTimeLocaleManager.wrapContext(newBase))
    }

}
