package com.arakadds.arak.presentation.activities.ads

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.arakadds.arak.R
import com.arakadds.arak.common.helper.LocaleHelper
import com.arakadds.arak.common.helper.connectivity_checker.ConnectionLiveData
import com.arakadds.arak.common.preferaence.IPreferenceHelper
import com.arakadds.arak.common.preferaence.PreferenceManager
import com.arakadds.arak.databinding.ActivitySearchBinding
import com.arakadds.arak.model.new_mapping_refactore.home.AdsData
import com.arakadds.arak.model.new_mapping_refactore.home.HomeAdsModel
import com.arakadds.arak.model.new_mapping_refactore.store.StoreObject
import com.arakadds.arak.model.new_mapping_refactore.store.StoresListModel

import com.arakadds.arak.presentation.activities.other.BaseActivity
import com.arakadds.arak.presentation.activities.other.NoInternetConnectionActivity
import com.arakadds.arak.presentation.activities.stores.StoreProfileActivity

import com.arakadds.arak.presentation.adapters.HomeAddsAdapter
import com.arakadds.arak.presentation.adapters.StoresAdapter
import com.arakadds.arak.presentation.dialogs.ApplicationDialogs
import com.arakadds.arak.presentation.dialogs.loader.ProgressDialogUtil
import com.arakadds.arak.presentation.viewmodel.HomeAdsViewModel
import com.arakadds.arak.presentation.viewmodel.StoresViewModel
import com.arakadds.arak.utils.AppEnums
import com.arakadds.arak.utils.AppProperties
import com.arakadds.arak.utils.Constants
import com.arakadds.arak.utils.Constants.BASE_URL
import com.arakadds.arak.utils.Constants.IS_STORE_SEARCH
import com.arakadds.arak.utils.Constants.SELECTED_STORE_ID
import dagger.android.AndroidInjection
import io.paperdb.Paper
import javax.inject.Inject

class SearchActivity : BaseActivity(), StoresAdapter.StoreClickEvents,
    ApplicationDialogs.AlertDialogCallbacks,
    HomeAddsAdapter.PaidAdsCallBacks {

    private val preferenceHelper: IPreferenceHelper by lazy { PreferenceManager(applicationContext) }
    lateinit var binding: ActivitySearchBinding
    private lateinit var activityContext: Context

    private lateinit var toolbar: Toolbar
    private lateinit var pageTitle: TextView
    private var connectionLiveData: ConnectionLiveData? = null

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val homeAdsViewModel: HomeAdsViewModel by viewModels {
        viewModelFactory
    }
    private val storesViewModel: StoresViewModel by viewModels {
        viewModelFactory
    }
    private lateinit var token: String
    private val adsDataArrayList = ArrayList<AdsData>()

    //private var storesArrayList = ArrayList<StoresList>()
    private var storesAdapter: StoresAdapter? = null
    private lateinit var language: String
    private val searchUrl = BASE_URL + "ads/"
    private val searchStoresUrl = BASE_URL + "stores/search-stores/"
    private var isStoreSearch = false
    private var storesListArrayList = ArrayList<StoreObject>()
    private lateinit var activityResources: Resources
    private lateinit var homeAddsAdapter: HomeAddsAdapter
    private var currentPage = 1
    private var lastPage: Int = 1
    private var searchName: String = ""

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connectionLiveData = ConnectionLiveData(this)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AndroidInjection.inject(this)
        activityContext = this

        language = preferenceHelper.getLanguage()

        initData()
        updateSearchView(language)
        checkNetworkConnection()
        initToolbar()
        setUpNormalAdsAdapter()
        setStoresAdapter()
        getIntents()
        setListeners()

    }

    private fun initToolbar() {
        toolbar = binding.appBarLayout.findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        pageTitle = binding.appBarLayout.findViewById(R.id.page_title)
        val backImageView: ImageView = binding.appBarLayout.findViewById(R.id.back_button)
        //shareImageView = binding.appBarLayout.findViewById(R.id.share_button)
        //favoriteImageView = binding.appBarLayout.findViewById(R.id.favorite_button)

        pageTitle.text = resources?.getString(R.string.home_search)
        backImageView.setOnClickListener { finish() }
    }

    private fun initData() {
        token = preferenceHelper.getToken()
    }

    private fun getIntents() {
        isStoreSearch = intent.getBooleanExtra(IS_STORE_SEARCH, false)
        if (isStoreSearch) {
            getStoresList(page = currentPage)
        } else {
            getHomeAdsData(page = currentPage)
        }
    }

    private fun updateSearchView(language: String) {
        val context = LocaleHelper.setLocale(this@SearchActivity, language)
        activityResources = context.resources
        if (isStoreSearch) {
            binding.searchSearchTextViewId.hint =
                activityResources.getString(R.string.store_frag_Search_in_Stores)
        } else {
            binding.searchSearchTextViewId.hint =
                activityResources.getString(R.string.home_frag_search)
        }
        binding.searchEmptyViewTextViewId.text =
            activityResources.getString(R.string.error_messages_no_data_found)
        this.language = language
    }

    fun setListeners() {
        binding.searchSearchButtonId.setOnClickListener {
            searchName = binding.searchSearchTextViewId.text.toString()
            if (searchName.trim { it <= ' ' } != "") {
                currentPage = 1
                adsDataArrayList.clear()
                if (isStoreSearch) {
                    storesListArrayList.clear()
                    getStoresList(name = searchName, page = currentPage)

                } else {
                    adsDataArrayList.clear()
                    getHomeAdsData(textSearch = searchName, page = currentPage)
                }
            } else {
                currentPage = 1
                if (isStoreSearch) {
                    getStoresList(page = currentPage)
                } else {
                    getHomeAdsData(page = currentPage)
                }
            }
        }
    }

      private fun setStoresAdapter() {
            storesAdapter =
                StoresAdapter(
                    storesListArrayList,
                    this@SearchActivity,
                    language,
                    activityResources,
                    this
                )
            val mLayoutManager =
                LinearLayoutManager(this@SearchActivity, LinearLayoutManager.VERTICAL, false)
            binding.searchAddsRecyclerViewId.layoutManager = mLayoutManager
            binding.searchAddsRecyclerViewId.isFocusable = false
            binding.searchAddsRecyclerViewId.isNestedScrollingEnabled = false
            binding.searchAddsRecyclerViewId.adapter = storesAdapter
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

    private fun setUpNormalAdsAdapter() {
        homeAddsAdapter = HomeAddsAdapter(
            adsDataArrayList,
            null,
            this,
            language,
            true,
            this
        )
        val gridLayoutManager =
            GridLayoutManager(activityContext, 2)
        binding.searchAddsRecyclerViewId.layoutManager = gridLayoutManager
        binding.searchAddsRecyclerViewId.isFocusable = false
        binding.searchAddsRecyclerViewId.isNestedScrollingEnabled = false
        binding.searchAddsRecyclerViewId.adapter = homeAddsAdapter
    }

    private fun getHomeAdsData(
        isFeatured: Boolean? = null,
        filterPackageId: String? = null,
        textSearch: String? = null,
        page: Int
    ) {
        //showLoadingDialog("message", activity)
        homeAdsViewModel.getHomeAds(
            language,
            "Bearer $token",
            isFeatured,
            filterPackageId,
            textSearch,
            page
        )
        let {
            homeAdsViewModel.homeAdsModelModel.observe(
                it,
                Observer(function = fun(homeAdsModel: HomeAdsModel?) {
                    homeAdsModel?.let {
                        if (homeAdsModel.statusCode == 200) {
                            currentPage = homeAdsModel.homeAdsData.page
                            lastPage = homeAdsModel.homeAdsData.lastPage
                            adsDataArrayList.clear()
                            adsDataArrayList.addAll(
                                homeAdsModel.homeAdsData.adsDataArrayList
                            )
                            homeAddsAdapter.notifyDataSetChanged()
                            if (adsDataArrayList.size > 0) {
                                binding.searchEmptyViewImageViewId.visibility = View.GONE
                                binding.searchEmptyViewTextViewId.visibility = View.GONE
                            } else {
                                binding.searchEmptyViewImageViewId.visibility = View.VISIBLE
                                binding.searchEmptyViewTextViewId.visibility = View.VISIBLE
                            }

                        } else {
                            let { it1 ->
                                ApplicationDialogs.openAlertDialog(
                                    it1,
                                    activityResources.getString(R.string.dialogs_error),
                                    homeAdsModel.message,
                                    false,
                                    AppEnums.DialogActionTypes.DISMISS,
                                    this
                                )
                            }
                            // hideView(binding.loginProgressBarId*//*progressBar*//*)

                        }
                    }
                    ProgressDialogUtil.hideLoadingDialog()
                })
            )
        }

        let {
            homeAdsViewModel.throwableResponse.observe(
                it,
                Observer(function = fun(throwable: Throwable?) {
                    throwable?.let {
                        let { it1 ->
                            ApplicationDialogs.openAlertDialog(
                                it1,
                                activityResources.getString(R.string.dialogs_error),
                                throwable.message,
                                false,
                                AppEnums.DialogActionTypes.DISMISS,
                                this
                            )
                        }
                    }
                    ProgressDialogUtil.hideLoadingDialog()
                })
            )
        }
    }

    private fun getStoresList(
        page: Int,
        random: Boolean? = null,
        name: String? = null,
        phoneNumber: String? = null,
        isFeatured: Boolean? = null,
        storeCategoryId: Int? = null,
        hasVisa: Boolean? = null,
        hasMastercard: Boolean? = null,
        hasPaypal: Boolean? = null,
        hasCash: Boolean? = null,
    ) {
        ProgressDialogUtil.showLoadingDialog("message", this)
        storesViewModel.getStoresList(
            language,
            "Bearer $token",
            page,
            random,
            name,
            phoneNumber,
            isFeatured,
            storeCategoryId,
            hasVisa,
            hasMastercard,
            hasPaypal,
            hasCash
        )
        storesViewModel.storesListModelModel.observe(
            this,
            Observer(function = fun(storesListModel: StoresListModel?) {
                storesListModel?.let {
                    if (storesListModel.statusCode == 200) {
                        currentPage = storesListModel.data.page
                        lastPage = storesListModel.data.lastPage
                        storesListArrayList.clear()
                        storesListArrayList.addAll(
                            storesListModel.data.stores
                        )
                        storesAdapter!!.notifyDataSetChanged()

                        if (storesListArrayList.isEmpty()) {
                            binding.searchEmptyViewImageViewId.visibility = View.VISIBLE
                            binding.searchEmptyViewImageViewId.visibility = View.VISIBLE
                        } else {
                            binding.searchEmptyViewImageViewId.visibility = View.GONE
                            binding.searchEmptyViewImageViewId.visibility = View.GONE
                        }
                    } else {
                        ApplicationDialogs.openAlertDialog(
                            this,
                            activityResources.getString(R.string.dialogs_error),
                            storesListModel.message,
                            false,
                            AppEnums.DialogActionTypes.DISMISS,
                            this
                        )

                        // hideView(binding.loginProgressBarId*//*progressBar*//*)

                    }
                }
                ProgressDialogUtil.hideLoadingDialog()
            })
        )

        storesViewModel.throwableResponse.observe(
            this,
            Observer(function = fun(throwable: Throwable?) {
                throwable?.let {
                    ApplicationDialogs.openAlertDialog(
                        this,
                        activityResources.getString(R.string.dialogs_error),
                        throwable.message,
                        false,
                        AppEnums.DialogActionTypes.DISMISS,
                        this
                    )

                }
                ProgressDialogUtil.hideLoadingDialog()
            })
        )
    }

    override fun onStoreClickedCalledBack(position: Int, storeId: Int) {
        val intent = Intent(this@SearchActivity, StoreProfileActivity::class.java)
        intent.putExtra(SELECTED_STORE_ID, storeId)
        startActivity(intent)
    }

    override fun onClose() {
    }

    override fun onConfirm(actionType: Int) {

    }

    override fun onNextPageRequired() {
        if (isStoreSearch) {
            if (currentPage < lastPage) {
                if (searchName.trim().isNotEmpty()) {
                    getStoresList(page = currentPage + 1)
                } else {
                    getStoresList(name = searchName, page = currentPage + 1)
                }
            }
        } else {
            if (currentPage < lastPage) {
                if (searchName.trim().isNotEmpty()) {
                    getHomeAdsData(page = currentPage + 1)
                } else {
                    getHomeAdsData(textSearch = searchName, page = currentPage + 1)
                }
            }
        }
    }

    override fun onSelectAd(adsData: AdsData?, position: Int) {
        if (adsData?.adCategoryId == AppEnums.AdsTypeCategories.STORES) {
            val intent = Intent(this@SearchActivity, StoreProfileActivity::class.java)
            intent.putExtra(Constants.SELECTED_STORE_ID, adsData.storeId)
            startActivity(intent)
        } else {
            val intent = Intent(this@SearchActivity, AdsStoryActivity::class.java)
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