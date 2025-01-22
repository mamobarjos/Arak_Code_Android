package com.arakadds.arak.presentation.activities.services

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import com.arakadds.arak.R
import com.arakadds.arak.common.helper.ActivityHelper
import com.arakadds.arak.common.helper.LocaleHelper
import com.arakadds.arak.common.helper.connectivity_checker.ConnectionLiveData
import com.arakadds.arak.common.preferaence.IPreferenceHelper
import com.arakadds.arak.common.preferaence.PreferenceManager
import com.arakadds.arak.databinding.ActivityServicesBinding
import com.arakadds.arak.presentation.activities.other.BaseActivity
import com.arakadds.arak.presentation.activities.other.NoInternetConnectionActivity
import com.arakadds.arak.utils.AppProperties
import com.arakadds.arak.utils.GlobalMethodsOldClass
import dagger.android.AndroidInjection
import io.paperdb.Paper

class ServicesActivity : BaseActivity(){

    private val preferenceHelper: IPreferenceHelper by lazy { PreferenceManager(applicationContext) }
    lateinit var binding: ActivityServicesBinding
    private lateinit var activityContext: Context

    private lateinit var toolbar: Toolbar
    private lateinit var pageTitle: TextView
    private var connectionLiveData: ConnectionLiveData? = null
    private lateinit var token: String
    private lateinit var language: String
    private lateinit var activityResources: Resources

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connectionLiveData = ConnectionLiveData(this)
        binding = ActivityServicesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AndroidInjection.inject(this)
        activityContext = this

        language = preferenceHelper.getLanguage()

        updateServiceFragView(language)
        checkNetworkConnection()
        initToolbar()
        initData()
        setListeners()
    }
    private fun initToolbar() {
        toolbar = binding.appBarLayout.findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        pageTitle = binding.appBarLayout.findViewById(R.id.page_title)
        val backImageView: ImageView = binding.appBarLayout.findViewById(R.id.back_button)
        //shareImageView = binding.appBarLayout.findViewById(R.id.share_button)
        //favoriteImageView = binding.appBarLayout.findViewById(R.id.favorite_button)
        pageTitle.text = activityResources.getString(R.string.service_frag_Arak_Service)
        backImageView.setOnClickListener { finish() }
    }
    private fun initData() {
        token = preferenceHelper.getToken()
        if (token == "non" || token.trim().isEmpty()) {
            GlobalMethodsOldClass.askGuestLogin(resources, activityContext)
            return
        }
    }
    private fun checkNetworkConnection() {
        connectionLiveData?.observe(this) { isNetworkAvailable ->
            //update ui
            if (!isNetworkAvailable) {
                startActivity(Intent(this, NoInternetConnectionActivity::class.java))
            } else {
                //Toast.makeText(this, "connected", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun updateServiceFragView(language: String) {
        val context = LocaleHelper.setLocale(activityContext, language)
        activityResources = context.resources
        binding.servicesFragSearchTextViewId.text = activityResources.getString(R.string.home_frag_search)
        binding.servicesArakServicesTextViewId.text = activityResources.getString(R.string.service_frag_Arak_Service)
        binding.servicesRankingTextViewId.text = activityResources.getString(R.string.service_frag_Arak_Ranking)
        this.language = language
    }
    private fun setListeners() {

        binding.servicesArakServicesLinearLayoutId.setOnClickListener {
            ActivityHelper.goToActivity(
                activityContext,
                ArakServicesActivity::class.java, false
            )
        }
        binding.servicesRankingLinearLayoutId.setOnClickListener {
            if (token != "non" && token != null) {
                ActivityHelper.goToActivity(
                    activityContext,
                    RankingActivity::class.java, false
                )
            } else {
                GlobalMethodsOldClass.askGuestLogin(activityResources, activityContext)
            }
        }
    }

}