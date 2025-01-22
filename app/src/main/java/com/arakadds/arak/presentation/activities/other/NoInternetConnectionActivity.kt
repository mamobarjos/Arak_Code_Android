package com.arakadds.arak.presentation.activities.other

import android.content.pm.ActivityInfo
import android.content.res.Resources
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.arakadds.arak.R
import com.arakadds.arak.common.helper.LocaleHelper
import com.arakadds.arak.common.helper.connectivity_checker.ConnectionLiveData
import com.arakadds.arak.common.preferaence.IPreferenceHelper
import com.arakadds.arak.common.preferaence.PreferenceManager
import com.arakadds.arak.databinding.ActivityNoInternetConnectionBinding
import com.arakadds.arak.utils.AppProperties


import io.paperdb.Paper

class NoInternetConnectionActivity : AppCompatActivity() {

    private val preferenceHelper: IPreferenceHelper by lazy { PreferenceManager(applicationContext) }
    lateinit var binding: ActivityNoInternetConnectionBinding

    var language: String = "ar"
    var connectionLiveData: ConnectionLiveData? = null
    private lateinit var activityResources: Resources

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connectionLiveData = ConnectionLiveData(this)

        binding = ActivityNoInternetConnectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        language = preferenceHelper.getLanguage()

        checkNetworkConnection()
        updateNoInternetView(language)
        setListeners()
    }

    private fun updateNoInternetView(language: String) {
        val context = LocaleHelper.setLocale(this@NoInternetConnectionActivity, language)
        activityResources = context.resources
        binding.textView7.text =
            activityResources.getString(R.string.no_internet_connection_activity_title_label)
        binding.textView8.text =
            activityResources.getString(R.string.no_internet_connection_activity_desc_label)
        binding.noInternetConnectionTryAgainButtonId.text =
            activityResources.getString(R.string.no_internet_connection_activity_try_again_label)
        this.language = language
    }

    private fun setListeners() {
        binding.noInternetConnectionTryAgainButtonId.setOnClickListener {
            checkNetworkConnection()
        }
    }

    private fun checkNetworkConnection() {
        connectionLiveData?.observe(this) { isNetworkAvailable ->
            //update ui
            if (!isNetworkAvailable) {
                Toast.makeText(this, "Offline", Toast.LENGTH_SHORT).show()
            } else {
                finish()
                Toast.makeText(this, "Back Online", Toast.LENGTH_SHORT).show()
            }
        }
    }
}