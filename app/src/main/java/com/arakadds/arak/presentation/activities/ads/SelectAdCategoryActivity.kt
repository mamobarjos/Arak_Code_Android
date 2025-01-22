package com.arakadds.arak.presentation.activities.ads

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
import com.arakadds.arak.common.helper.LocaleHelper
import com.arakadds.arak.common.helper.connectivity_checker.ConnectionLiveData
import com.arakadds.arak.common.preferaence.IPreferenceHelper
import com.arakadds.arak.common.preferaence.PreferenceManager
import com.arakadds.arak.databinding.ActivitySelectAdsTypeBinding
import com.arakadds.arak.model.other.AdsCategory
import com.arakadds.arak.presentation.activities.other.BaseActivity
import com.arakadds.arak.presentation.activities.other.NoInternetConnectionActivity
import com.arakadds.arak.utils.AppEnums
import com.arakadds.arak.utils.AppProperties
import com.arakadds.arak.utils.GlobalMethodsOldClass
import dagger.android.AndroidInjection
import io.paperdb.Paper

class SelectAdCategoryActivity : BaseActivity(){

    private val preferenceHelper: IPreferenceHelper by lazy { PreferenceManager(applicationContext) }
    lateinit var binding: ActivitySelectAdsTypeBinding
    private lateinit var activityContext: Context

    private lateinit var toolbar: Toolbar
    private var connectionLiveData: ConnectionLiveData? = null

    private lateinit var pageTitle: TextView
    private lateinit var token: String
    private lateinit var language: String
    private lateinit var resources: Resources
    private val adsCategoryArrayList = ArrayList<AdsCategory>()
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connectionLiveData = ConnectionLiveData(this)
        binding = ActivitySelectAdsTypeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AndroidInjection.inject(this)
        activityContext = this

        language = preferenceHelper.getLanguage()

        updateSelectAdCategoryView(language)
        checkNetworkConnection()
        initData()
        initToolbar()
        prepareHomeObject()
        //initUI()

    }

    private fun initToolbar() {
        toolbar = binding.appBarLayout.findViewById(R.id.toolbar)
        toolbar.title=""
        setSupportActionBar(toolbar)
        pageTitle = binding.appBarLayout.findViewById(R.id.page_title)
        val backImageView: ImageView = binding.appBarLayout.findViewById(R.id.back_button)
        //shareImageView = binding.appBarLayout.findViewById(R.id.share_button)
        //favoriteImageView = binding.appBarLayout.findViewById(R.id.favorite_button)
        // = binding.appBarLayout.findViewById(R.id.favorite_button)
        pageTitle.text=resources.getString(R.string.ad_type_activity_title)
        backImageView.setOnClickListener { finish() }
    }


    private fun initData() {
        token = preferenceHelper.getToken()
        if (token == "non" || token.trim().isEmpty()) {
            GlobalMethodsOldClass.askGuestLogin(resources, activityContext)
            return
        }
    }

    private fun updateSelectAdCategoryView(language: String) {
        val context = LocaleHelper.setLocale(this, language)
        resources = context.resources

        this.language = language
    }

    /*private fun initToolbar() {
        toolbar = binding.appBarLayout.findViewById(R.id.toolbar_id)

        setSupportActionBar(toolbar)
        val mTitle: TextView =
            binding.appBarLayout.findViewById(R.id.toolbar_title_TextView_id)
        val backImageView: ImageView =
            binding.appBarLayout.findViewById(R.id.toolbar_category_icon_ImageView_id)

        mTitle.text = resources?.getString(R.string.service_frag_Arak_Service)
        backImageView.setOnClickListener { finish() }
    }*/

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

    private fun prepareHomeObject() {
        val videoAdsCategory = AdsCategory()
        videoAdsCategory.categoryName = "Video Ads"
        videoAdsCategory.categoryIcon = R.drawable.adds_website_icon
        videoAdsCategory.id = 1
        adsCategoryArrayList.add(videoAdsCategory)
        val ImageAdsCategory = AdsCategory()
        ImageAdsCategory.categoryName = "Image Ads"
        ImageAdsCategory.categoryIcon = R.drawable.ads_image_icon
        ImageAdsCategory.id = 2
        adsCategoryArrayList.add(ImageAdsCategory)
        val websiteAdsCategory = AdsCategory()
        websiteAdsCategory.categoryName = "Website Ads"
        websiteAdsCategory.categoryIcon = R.drawable.adds_website_icon
        websiteAdsCategory.id = 3
        adsCategoryArrayList.add(websiteAdsCategory)
    }
}