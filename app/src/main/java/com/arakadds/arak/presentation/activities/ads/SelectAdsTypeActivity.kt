package com.arakadds.arak.presentation.activities.ads

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.arakadds.arak.R
import com.arakadds.arak.common.helper.ActivityHelper
import com.arakadds.arak.common.helper.LocaleHelper
import com.arakadds.arak.common.helper.connectivity_checker.ConnectionLiveData
import com.arakadds.arak.common.preferaence.IPreferenceHelper
import com.arakadds.arak.common.preferaence.PreferenceManager
import com.arakadds.arak.databinding.ActivitySelectAdsTypeBinding
import com.arakadds.arak.model.new_mapping_refactore.response.banners.ad_category.AdsCategoriesModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.ad_category.AdsCategoryData
import com.arakadds.arak.presentation.activities.other.BaseActivity
import com.arakadds.arak.presentation.activities.other.NoInternetConnectionActivity
import com.arakadds.arak.presentation.activities.services.ArakServicesActivity
import com.arakadds.arak.presentation.activities.settings.NotificationActivity
import com.arakadds.arak.presentation.activities.settings.SettingsActivity
import com.arakadds.arak.presentation.adapters.NewAdsAdapter
import com.arakadds.arak.presentation.dialogs.ApplicationDialogs
import com.arakadds.arak.presentation.dialogs.ApplicationDialogs.openAlertDialog
import com.arakadds.arak.presentation.viewmodel.AdsTypeViewModel
import com.arakadds.arak.utils.AppEnums.DialogActionTypes.DISMISS
import com.arakadds.arak.utils.AppProperties
import com.arakadds.arak.utils.GlobalMethodsOldClass
import dagger.android.AndroidInjection
import io.paperdb.Paper
import javax.inject.Inject

class SelectAdsTypeActivity : BaseActivity(), ApplicationDialogs.AlertDialogCallbacks {

    private val preferenceHelper: IPreferenceHelper by lazy { PreferenceManager(applicationContext) }
    lateinit var binding: ActivitySelectAdsTypeBinding
    private lateinit var activityContext: Context

    private lateinit var toolbar: Toolbar
    private lateinit var pageTitle: TextView
    private var connectionLiveData: ConnectionLiveData? = null

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: AdsTypeViewModel by viewModels {
        viewModelFactory
    }
    private lateinit var token: String
    private lateinit var language: String
    private lateinit var resources: Resources
    private var adsCategoryDataArrayList = ArrayList<AdsCategoryData>()
    private lateinit var newAdsAdapter: NewAdsAdapter
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connectionLiveData = ConnectionLiveData(this)
        binding = ActivitySelectAdsTypeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AndroidInjection.inject(this)
        activityContext = this

        language = preferenceHelper.getLanguage()

        updateLoginView(language)
        initData()
        initToolbar()
        checkNetworkConnection()
        setupAdsTypeAdapter()
        getAdsType()
        setListeners()
    }

    private fun initData() {
        token = preferenceHelper.getToken()
        if (token == "non" || token.trim().isEmpty()) {
            GlobalMethodsOldClass.askGuestLogin(resources, activityContext)
            return
        }
    }

    private fun updateLoginView(language: String) {
        val context = LocaleHelper.setLocale(this@SelectAdsTypeActivity, language)
        resources = context.resources
        this.language = language
    }
    private fun initToolbar() {
        toolbar = binding.appBarLayout.findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        pageTitle = binding.appBarLayout.findViewById(R.id.page_title)
        val backImageView: ImageView = binding.appBarLayout.findViewById(R.id.back_button)
        //shareImageView = binding.appBarLayout.findViewById(R.id.share_button)
        //favoriteImageView = binding.appBarLayout.findViewById(R.id.favorite_button)

        pageTitle.text = resources.getText(R.string.ad_type_activity_title)
        backImageView.setOnClickListener { finish() }
    }
    fun setListeners() {
       /* binding.newAdsCatSettingsImageViewId1.setOnClickListener {
            ActivityHelper.goToActivity(
                this@SelectAdsTypeActivity,
                SettingsActivity::class.java, false
            )
        }
        binding.newAdsCatNotificationImageViewId.setOnClickListener {
            ActivityHelper.goToActivity(
                this@SelectAdsTypeActivity,
                NotificationActivity::class.java, false
            )
        }
        binding.newAdsCatAppLogoImageViewId.setOnClickListener {
            ActivityHelper.goToActivity(
                this@SelectAdsTypeActivity,
                ArakServicesActivity::class.java, false
            )
        }*/
    }

/*    private fun prepareHomeObject() {
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

    private fun getAdsType() {
        showLoadingDialog(this, "message")
        viewModel.getAdsType("Bearer $token", language)
        viewModel.AdsCategoriesModelModel.observe(
            this,
            Observer(function = fun(adsCategoriesModel: AdsCategoriesModel?) {
                adsCategoriesModel?.let {
                    if (adsCategoriesModel.statusCode == 200) {

                        adsCategoryDataArrayList.addAll(adsCategoriesModel.adsCategoryResponse.adsCategoryDataArrayList)

                        newAdsAdapter.notifyDataSetChanged()
                    } else {
                        openAlertDialog(
                            this,
                            resources.getString(R.string.dialogs_error),
                            adsCategoriesModel.message,
                            false,
                            DISMISS,
                            this
                        )
                        // hideView(binding.loginProgressBarId*//*progressBar*//*)

                    }
                }
                hideLoadingDialog()
            })
        )

        viewModel.throwableResponse.observe(
            this,
            Observer(function = fun(throwable: Throwable?) {
                throwable?.let {
                    openAlertDialog(
                        this,
                        resources?.getString(R.string.dialogs_error),
                        throwable.message,
                        false,
                        DISMISS,
                        this
                    )
                }
                hideLoadingDialog()
            })
        )
    }
    private fun setupAdsTypeAdapter() {
        newAdsAdapter = NewAdsAdapter(
            adsCategoryDataArrayList,
            this@SelectAdsTypeActivity,
            language
        )
        val gridLayoutManager =
            GridLayoutManager(this@SelectAdsTypeActivity, 2)
        binding.newAdsRecyclerViewId.layoutManager = gridLayoutManager
        binding.newAdsRecyclerViewId.isFocusable = false
        binding.newAdsRecyclerViewId.isNestedScrollingEnabled = false
        binding.newAdsRecyclerViewId.adapter = newAdsAdapter
    }

    override fun onClose() {
    }

    override fun onConfirm(actionType: Int) {

    }
}