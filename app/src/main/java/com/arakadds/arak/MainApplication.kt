package com.arakadds.arak

import android.content.res.Configuration
import com.arakadds.arak.common.MyApplication
import com.arakadds.arak.di.components.DaggerAppComponent
import com.arakadds.arak.utils.managers.RunTimeLocaleManager
import com.google.android.material.color.DynamicColors

import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import io.paperdb.Paper

class MainApplication : DaggerApplication() {
    private var singleInstance: MainApplication? = null
    private val applicationInjector = DaggerAppComponent.builder().application(this).build()
    override fun applicationInjector(): AndroidInjector<out DaggerApplication> = applicationInjector

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        RunTimeLocaleManager.overrideLocale(this)
    }


    override fun onCreate() {
        super.onCreate()
        singleInstance = this
        //For dynamic theming on Android 12 and above
        DynamicColors.applyToActivitiesIfAvailable(this)
        Paper.init(this);
    }

    public fun getAppInstance(): MainApplication? {
        return singleInstance
    }
}