package com.arakadds.arak.presentation.activities.profile

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.arakadds.arak.R
import com.arakadds.arak.common.helper.LocaleHelper
import com.arakadds.arak.common.helper.connectivity_checker.ConnectionLiveData
import com.arakadds.arak.common.preferaence.IPreferenceHelper
import com.arakadds.arak.common.preferaence.PreferenceManager
import com.arakadds.arak.databinding.ActivityMyFavoritesBinding
import com.arakadds.arak.model.new_mapping_refactore.home.AdsData
import com.arakadds.arak.model.new_mapping_refactore.home.HomeAdsModel
import com.arakadds.arak.presentation.activities.ads.AdsStoryActivity
import com.arakadds.arak.presentation.activities.other.BaseActivity
import com.arakadds.arak.presentation.activities.other.NoInternetConnectionActivity
import com.arakadds.arak.presentation.activities.stores.StoreProfileActivity
import com.arakadds.arak.presentation.adapters.HomeAddsAdapter
import com.arakadds.arak.presentation.dialogs.ApplicationDialogs
import com.arakadds.arak.presentation.dialogs.ApplicationDialogs.openAlertDialog
import com.arakadds.arak.presentation.viewmodel.UserAdsViewModel
import com.arakadds.arak.utils.AppEnums
import com.arakadds.arak.utils.AppEnums.DialogActionTypes.DISMISS
import com.arakadds.arak.utils.AppProperties
import com.arakadds.arak.utils.Constants
import com.arakadds.arak.utils.Constants.BASE_URL
import com.arakadds.arak.utils.GlobalMethodsOldClass
import dagger.android.AndroidInjection
import io.paperdb.Paper
import javax.inject.Inject

class MyFavoritesActivity : BaseActivity(), ApplicationDialogs.AlertDialogCallbacks,
    HomeAddsAdapter.PaidAdsCallBacks {

    private val preferenceHelper: IPreferenceHelper by lazy { PreferenceManager(applicationContext) }
    lateinit var binding: ActivityMyFavoritesBinding
    private lateinit var activityContext: Context
    private lateinit var toolbar: Toolbar
    private lateinit var pageTitle: TextView
    private var connectionLiveData: ConnectionLiveData? = null

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: UserAdsViewModel by viewModels {
        viewModelFactory
    }
    private lateinit var token: String
    private lateinit var homeAddsAdapter: HomeAddsAdapter
    private lateinit var language: String
    private lateinit var resources: Resources
    private var adsDataArrayList = ArrayList<AdsData>()
    private val url = BASE_URL + "favorites/get-user-favorites"
    private var currentPage = 1
    private var lastPage: Int = 1
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connectionLiveData = ConnectionLiveData(this)
        binding = ActivityMyFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AndroidInjection.inject(this)
        activityContext = this

        language = preferenceHelper.getLanguage()

        updateLoginView(language)
        initData()
        initToolbar()
        checkNetworkConnection()
        setupFavoritesAdapter()
        getUserFavorites(page = currentPage)

    }
    private fun initToolbar() {
        toolbar = binding.appBarLayout.findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        pageTitle = binding.appBarLayout.findViewById(R.id.page_title)
        val backImageView: ImageView = binding.appBarLayout.findViewById(R.id.back_button)
        //shareImageView = binding.appBarLayout.findViewById(R.id.share_button)
        //favoriteImageView = binding.appBarLayout.findViewById(R.id.favorite_button)
        pageTitle.text = resources.getString(R.string.profile_frag_My_Favorite)
        backImageView.setOnClickListener { finish() }
    }
    private fun initData() {
        token = preferenceHelper.getToken()
        if (token == "non" || token.trim().isEmpty()) {
            GlobalMethodsOldClass.askGuestLogin(resources, activityContext)
            return
        }
    }


    private fun updateLoginView(language: String) {
        val context = LocaleHelper.setLocale(this@MyFavoritesActivity, language)
        resources = context.resources
        binding.myFavoritesEmptyViewTextViewId.text =
            resources.getString(R.string.error_messages_no_data_found)
        this.language = language
    }

    private fun getUserFavorites(page: Int) {
        showLoadingDialog(this, "message")
        viewModel.getUserFavorites("Bearer $token", language, page)
        viewModel.homeAdsModelModel.observe(
            this,
            Observer(function = fun(homeAdsModel: HomeAdsModel?) {
                homeAdsModel?.let {
                    if (homeAdsModel.statusCode == 200) {
                        adsDataArrayList.addAll(homeAdsModel.homeAdsData.adsDataArrayList)
                        currentPage = homeAdsModel.homeAdsData.page
                        lastPage = homeAdsModel.homeAdsData.lastPage
                        homeAddsAdapter.notifyDataSetChanged()
                        if (adsDataArrayList.size == 0) {
                            binding.myFavoritesEmptyViewImageViewId.visibility = View.VISIBLE
                            binding.myFavoritesEmptyViewTextViewId.visibility = View.VISIBLE
                        } else {
                            binding.myFavoritesEmptyViewImageViewId.visibility = View.GONE
                            binding.myFavoritesEmptyViewTextViewId.visibility = View.GONE
                        }
                    } else {
                        openAlertDialog(
                            this,
                            resources.getString(R.string.dialogs_error),
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

    private fun setupFavoritesAdapter() {
        homeAddsAdapter = HomeAddsAdapter(
            adsDataArrayList,
            adsDataArrayList.size,
            this@MyFavoritesActivity,
            language,
            false,
            this
        )
        val gridLayoutManager =
            GridLayoutManager(this@MyFavoritesActivity, 2)
        binding.myFavoritesRecyclerViewId!!.layoutManager = gridLayoutManager
        binding.myFavoritesRecyclerViewId!!.isFocusable = false
        binding.myFavoritesRecyclerViewId!!.isNestedScrollingEnabled = false
        binding.myFavoritesRecyclerViewId!!.adapter = homeAddsAdapter
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


    override fun onClose() {
    }

    override fun onConfirm(actionType: Int) {

    }

    override fun onNextPageRequired() {
        if (currentPage < lastPage) {
            getUserFavorites(page = currentPage + 1)

        }
    }

    override fun onSelectAd(adsData: AdsData?, position: Int) {
        if (adsData?.adCategoryId == AppEnums.AdsTypeCategories.STORES) {
            val intent = Intent(this@MyFavoritesActivity, StoreProfileActivity::class.java)
            intent.putExtra(Constants.SELECTED_STORE_ID, adsData.storeId)
            startActivity(intent)
        } else {
            val intent = Intent(this@MyFavoritesActivity, AdsStoryActivity::class.java)
            val bundle = Bundle()
            intent.putExtra(Constants.POSITION, position)
            bundle.putSerializable(Constants.PRODUCT_FILES_INFORMATION, adsData?.adFiles)
            intent.putExtras(bundle)
            intent.putExtra(Constants.selectedAdObject, adsData as Parcelable)
            intent.putExtra(Constants.IS_HOME, true)
            startActivity(intent)
        }
    }
}