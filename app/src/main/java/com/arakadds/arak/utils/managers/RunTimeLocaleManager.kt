package com.arakadds.arak.utils.managers

import android.content.Context
import android.content.res.Configuration
import android.preference.PreferenceManager
import com.arakadds.arak.common.preferaence.IPreferenceHelper
import java.util.*

object RunTimeLocaleManager {

//    private val preferenceHelper: IPreferenceHelper by lazy { PreferenceManager(applicationContext) }
    fun wrapContext(context: Context?): Context? {

        // Override default locale
        val savedLocale =
            getUserPrefsLocale(context)
        Locale.setDefault(savedLocale)

        // create new configuration with the saved locale
        val newConfig = context?.resources?.configuration ?: Configuration()
        newConfig.setLocale(savedLocale)

        return context?.createConfigurationContext(newConfig)
    }

    fun overrideLocale(context: Context?): Context? {

        // Override default locale
        val savedLocale =
            getUserPrefsLocale(context)
        Locale.setDefault(savedLocale)

        // create new configuration with the saved locale
        val newConfig = context?.resources?.configuration ?: Configuration()
        newConfig.setLocale(savedLocale)

        // over passed context locale
        context?.createConfigurationContext(newConfig)
        if (context != context?.applicationContext) {
            return context?.applicationContext?.createConfigurationContext(newConfig)
        }
        return context
    }

    private fun getUserPrefsLocale(context: Context?): Locale {
        return if (context != null) {
            val preferenceHelper: IPreferenceHelper by lazy {
                com.arakadds.arak.common.preferaence.PreferenceManager(
                    context
                )
            }
//            val prefs = UserPreferences(
//                context
//            )
            preferenceHelper.getLanguage()?.let {
                Locale(it)
            } ?: run {
                preferenceHelper.setLanguage(Locale.getDefault().language)
                Locale(Locale.getDefault().language)
            }
        } else {
            //default language
            Locale(Locale.getDefault().language)
        }

    }
}
