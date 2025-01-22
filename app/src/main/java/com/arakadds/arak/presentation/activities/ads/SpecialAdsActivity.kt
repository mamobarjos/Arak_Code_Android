package com.arakadds.arak.presentation.activities.ads

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.arakadds.arak.R
import com.arakadds.arak.common.helper.ActivityHelper
import com.arakadds.arak.common.helper.LocaleHelper
import com.arakadds.arak.common.helper.connectivity_checker.ConnectionLiveData
import com.arakadds.arak.common.preferaence.IPreferenceHelper
import com.arakadds.arak.common.preferaence.PreferenceManager
import com.arakadds.arak.databinding.ActivitySpecialAdsBinding
import com.arakadds.arak.model.new_mapping_refactore.home.AdsData
import com.arakadds.arak.model.new_mapping_refactore.home.HomeAdsModel
import com.arakadds.arak.presentation.activities.other.BaseActivity
import com.arakadds.arak.presentation.activities.other.NoInternetConnectionActivity
import com.arakadds.arak.presentation.activities.services.ArakServicesActivity
import com.arakadds.arak.presentation.activities.settings.NotificationActivity
import com.arakadds.arak.presentation.activities.settings.SettingsActivity
import com.arakadds.arak.presentation.adapters.HomeSpecialAddsAdapter
import com.arakadds.arak.presentation.dialogs.ApplicationDialogs
import com.arakadds.arak.presentation.dialogs.ApplicationDialogs.openAlertDialog
import com.arakadds.arak.presentation.viewmodel.HomeAdsViewModel
import com.arakadds.arak.utils.AppEnums.DialogActionTypes.DISMISS
import com.arakadds.arak.utils.AppProperties
import com.arakadds.arak.utils.Constants.BASE_URL
import com.arakadds.arak.utils.GlobalMethodsOldClass
import dagger.android.AndroidInjection
import io.paperdb.Paper
import javax.inject.Inject

class SpecialAdsActivity : BaseActivity(), ApplicationDialogs.AlertDialogCallbacks,
    HomeSpecialAddsAdapter.HomeSpecialAdsCallBacks {

    private val preferenceHelper: IPreferenceHelper by lazy { PreferenceManager(applicationContext) }
    lateinit var binding: ActivitySpecialAdsBinding
    private lateinit var activityContext: Context

    private lateinit var toolbar: Toolbar
    private var connectionLiveData: ConnectionLiveData? = null

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: HomeAdsViewModel by viewModels {
        viewModelFactory
    }
    private lateinit var token: String
    private lateinit var language: String
    private lateinit var resources: Resources
    private lateinit var homeSpecialAddsAdapter: HomeSpecialAddsAdapter
    private val currentPageSpecialAdsDataArrayList = ArrayList<AdsData>()
    private val url = BASE_URL + "ads/get-featured-ads"
    private var currentPage = 1
    private var lastPage: Int = 1
    private var searchName: String = ""

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connectionLiveData = ConnectionLiveData(this)
        binding = ActivitySpecialAdsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AndroidInjection.inject(this)
        activityContext = this

        language = preferenceHelper.getLanguage()

        updateSearchView(language)
        initData()
        setupAdapter()
        setListeners()
        getHomeFeaturedAds(isFeatured = true, page = currentPage)

    }

    private fun initData() {
        token = preferenceHelper.getToken()
    }

    fun setListeners() {
        binding.specialAdsSettingsImageViewId.setOnClickListener {
            if (token != "non" && token != null) {
                ActivityHelper.goToActivity(
                    this@SpecialAdsActivity,
                    SettingsActivity::class.java, false
                )
            } else {
                GlobalMethodsOldClass.askGuestLogin(resources, this@SpecialAdsActivity)
            }
        }
        binding.specialAdsNotificationImageViewId.setOnClickListener {
            if (token != "non" && token != null) {
                ActivityHelper.goToActivity(
                    this@SpecialAdsActivity,
                    NotificationActivity::class.java, false
                )
            } else {
                GlobalMethodsOldClass.askGuestLogin(resources, this@SpecialAdsActivity)
            }
        }
        binding.specialAdsLogoImageViewId.setOnClickListener {
            ActivityHelper.goToActivity(
                this@SpecialAdsActivity,
                ArakServicesActivity::class.java, false
            )
        }
        binding.specialAdsSearchTextViewId.setOnClickListener {
            ActivityHelper.goToActivity(
                this@SpecialAdsActivity,
                SearchActivity::class.java, false
            )
        }
    }

    private fun updateSearchView(language: String) {
        val context = LocaleHelper.setLocale(this@SpecialAdsActivity, language)
        resources = context.resources
        binding.specialAdsSearchTextViewId.text = resources.getString(R.string.home_frag_search)
        binding.specialAdsEmptyViewTextViewId.text =
            resources.getString(R.string.error_messages_no_data_found)
        this.language = language
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

    private fun getHomeFeaturedAds(
        isFeatured: Boolean? = null,
        filterPackageId: String? = null,
        textSearch: String? = null,
        page: Int
    ) {
        showLoadingDialog(this, "message")
        viewModel.getSpecialAds(
            language,
            "Bearer $token",
            page,
            filterPackageId,
            textSearch,
            )
        viewModel.specialAdsModelModel.observe(
            this,
            Observer(function = fun(homeAdsModel: HomeAdsModel?) {
                homeAdsModel?.let {
                    if (homeAdsModel.statusCode == 200) {
                        currentPageSpecialAdsDataArrayList.addAll(
                            homeAdsModel.homeAdsData.adsDataArrayList
                        )
                        homeSpecialAddsAdapter.notifyDataSetChanged()
                        if (currentPageSpecialAdsDataArrayList.size == 0) {
                            binding.specialAdsEmptyViewImageViewId.visibility = View.VISIBLE
                            binding.specialAdsEmptyViewTextViewId.visibility = View.VISIBLE
                        } else {
                            binding.specialAdsEmptyViewImageViewId.visibility = View.GONE
                            binding.specialAdsEmptyViewTextViewId.visibility = View.GONE
                        }
                    } else {
                        openAlertDialog(
                            this,
                            resources?.getString(R.string.dialogs_error),
                            homeAdsModel.message,
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

    private fun setupAdapter() {
        homeSpecialAddsAdapter = HomeSpecialAddsAdapter(
            currentPageSpecialAdsDataArrayList,
            null,
            this@SpecialAdsActivity,
            language,
            true,
            true,
            resources,
            this
        )
        val mLayoutManager = LinearLayoutManager(
            this@SpecialAdsActivity,
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.specialAdsRecyclerViewId.layoutManager = mLayoutManager
        binding.specialAdsRecyclerViewId.isFocusable = false
        binding.specialAdsRecyclerViewId.isNestedScrollingEnabled = false
        binding.specialAdsRecyclerViewId.adapter = homeSpecialAddsAdapter
        homeSpecialAddsAdapter.notifyDataSetChanged()
    }

    override fun onClose() {
    }

    override fun onConfirm(actionType: Int) {

    }

    override fun onNextPageRequired() {
        if (currentPage < lastPage) {
            if (searchName.trim().isNotEmpty()) {
                getHomeFeaturedAds(isFeatured = true, page = currentPage + 1)
            } else {
                getHomeFeaturedAds(
                    isFeatured = true,
                    textSearch = searchName,
                    page = currentPage + 1
                )
            }
        }
    }

}