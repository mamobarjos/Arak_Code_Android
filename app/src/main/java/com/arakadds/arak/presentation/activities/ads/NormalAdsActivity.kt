package com.arakadds.arak.presentation.activities.ads

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
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
import com.arakadds.arak.databinding.ActivityNormalAdsBinding
import com.arakadds.arak.model.FilterSpinnerItems
import com.arakadds.arak.model.new_mapping_refactore.home.AdsData
import com.arakadds.arak.model.new_mapping_refactore.home.HomeAdsModel
import com.arakadds.arak.presentation.activities.other.BaseActivity
import com.arakadds.arak.presentation.activities.other.NoInternetConnectionActivity
import com.arakadds.arak.presentation.activities.stores.StoreProfileActivity
import com.arakadds.arak.presentation.adapters.HomeAddsAdapter
import com.arakadds.arak.presentation.adapters.StoresAdapter.StoreClickEvents
import com.arakadds.arak.presentation.dialogs.ApplicationDialogs
import com.arakadds.arak.presentation.dialogs.loader.ProgressDialogUtil
import com.arakadds.arak.presentation.viewmodel.HomeAdsViewModel
import com.arakadds.arak.utils.AppEnums
import com.arakadds.arak.utils.AppEnums.AdsTypeCategories.STORES
import com.arakadds.arak.utils.AppProperties
import com.arakadds.arak.utils.Constants.BASE_URL
import com.arakadds.arak.utils.Constants.IS_HOME
import com.arakadds.arak.utils.Constants.POSITION
import com.arakadds.arak.utils.Constants.PRODUCT_FILES_INFORMATION
import com.arakadds.arak.utils.Constants.SELECTED_STORE_ID
import com.arakadds.arak.utils.Constants.selectedAdObject
import com.arakadds.arak.utils.GlobalMethodsOldClass
import dagger.android.AndroidInjection
import io.paperdb.Paper
import javax.inject.Inject

class NormalAdsActivity : BaseActivity(), StoreClickEvents, ApplicationDialogs.AlertDialogCallbacks,
    HomeAddsAdapter.PaidAdsCallBacks {

    private val preferenceHelper: IPreferenceHelper by lazy { PreferenceManager(applicationContext) }
    lateinit var binding: ActivityNormalAdsBinding
    private lateinit var activityContext: Context

    private lateinit var toolbar: Toolbar
    private lateinit var pageTitle: TextView
    private var connectionLiveData: ConnectionLiveData? = null

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val homeAdsViewModel: HomeAdsViewModel by viewModels {
        viewModelFactory
    }

    private lateinit var token: String
    private lateinit var language: String
    private val adsDataArrayList = ArrayList<AdsData>()
    private val normalAdsUrl = BASE_URL + "ads"
    private lateinit var homeAddsAdapter: HomeAddsAdapter
    private lateinit var activityResources: Resources
    private var filteredCategoryId = 0
    private var selectedFilterItemName: String? = null
    private var currentPage = 1
    private var lastPage: Int = 1

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connectionLiveData = ConnectionLiveData(this)
        binding = ActivityNormalAdsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AndroidInjection.inject(this)
        activityContext = this

        language = preferenceHelper.getLanguage()

        initData()
        initToolbar()
        checkNetworkConnection()
        updateSearchView()
        initCategoriesSpinner()
        setUpNormalAdsAdapter()
        //getHomeAdsData(page = currentPage)
    }

    private fun initData() {
        token = preferenceHelper.getToken()
        if (token == "non" || token.trim().isEmpty()) {
            GlobalMethodsOldClass.askGuestLogin(resources, activityContext)
            return
        }
    }

    private fun initToolbar() {
        toolbar = binding.appBarLayout.findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        pageTitle = binding.appBarLayout.findViewById(R.id.page_title)
        val backImageView: ImageView = binding.appBarLayout.findViewById(R.id.back_button)
        //shareImageView = binding.appBarLayout.findViewById(R.id.share_button)
        //favoriteImageView = binding.appBarLayout.findViewById(R.id.favorite_button)

        pageTitle.text = resources?.getString(R.string.normal_ads_activity_title)
        backImageView.setOnClickListener { finish() }
    }

    private fun updateSearchView() {
        val context = LocaleHelper.setLocale(this@NormalAdsActivity, language)
        activityResources = context.resources
        binding.normalAdsLabelTextViewId.text =
            activityResources.getString(R.string.normal_ads_activity_title)
        this.language = language
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
        binding.homeAddsRecyclerViewId.layoutManager = gridLayoutManager
        binding.homeAddsRecyclerViewId.isFocusable = false
        binding.homeAddsRecyclerViewId.isNestedScrollingEnabled = false
        binding.homeAddsRecyclerViewId.adapter = homeAddsAdapter
    }

    private fun initCategoriesSpinner() {
        val filterSpinnerItemsList: ArrayList<FilterSpinnerItems> = ArrayList()
        val filterSpinnerItems1 = FilterSpinnerItems(0, "All", "الكل", language, false)
        filterSpinnerItemsList.add(filterSpinnerItems1)
        val filterSpinnerItems2 =
            FilterSpinnerItems(AppEnums.AdsTypeCategories.IMAGE, "Images", "الصور", language, false)
        filterSpinnerItemsList.add(filterSpinnerItems2)
        val filterSpinnerItems3 =
            FilterSpinnerItems(AppEnums.AdsTypeCategories.VIDEO, "Videos", "فيديو", language, false)
        filterSpinnerItemsList.add(filterSpinnerItems3)
        val filterSpinnerItems4 =
            FilterSpinnerItems(
                AppEnums.AdsTypeCategories.WEBSITE,
                "Website",
                "موقع ويب",
                language,
                false
            )
        filterSpinnerItemsList.add(filterSpinnerItems4)
        val filterSpinnerItems5 =
            FilterSpinnerItems(
                AppEnums.AdsTypeCategories.STORES,
                "Stores",
                "المتاجر",
                language,
                false
            )
        filterSpinnerItemsList.add(filterSpinnerItems5)

        val adapter: ArrayAdapter<FilterSpinnerItems> = ArrayAdapter<FilterSpinnerItems>(
            this, R.layout.support_simple_spinner_dropdown_item, filterSpinnerItemsList
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.categoryFilterSpinnerId.adapter = adapter
        binding.categoryFilterSpinnerId.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    val filterSpinnerItems: FilterSpinnerItems =
                        parent.selectedItem as FilterSpinnerItems
                    filteredCategoryId = filterSpinnerItems.id
                    selectedFilterItemName = filterSpinnerItems.toString()
                    binding.normalAdsLabelTextViewId.text = selectedFilterItemName
                    adsDataArrayList.clear()
                    if (filteredCategoryId == 0) {
                        getHomeAdsData(page = 1)
                    } else {
                        getHomeAdsData(adCategoryId = filteredCategoryId.toString(), page = 1)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
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

    private fun getHomeAdsData(
        isFeatured: Boolean? = null,
        adCategoryId: String? = null,
        textSearch: String? = null,
        page: Int
    ) {
        //showLoadingDialog("message", activity)
        homeAdsViewModel.getHomeAds(
            language,
            "Bearer $token",
            isFeatured,
            adCategoryId,
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


    override fun onStoreClickedCalledBack(position: Int, storeId: Int) {
        val intent = Intent(this@NormalAdsActivity, StoreProfileActivity::class.java)
        intent.putExtra(SELECTED_STORE_ID, storeId)
        startActivity(intent)
    }

    override fun onClose() {
    }

    override fun onConfirm(actionType: Int) {

    }

    override fun onNextPageRequired() {
        if (currentPage < lastPage) {
            if (filteredCategoryId == 0) {
                getHomeAdsData(page = currentPage + 1)
            } else {
                getHomeAdsData(adCategoryId = filteredCategoryId.toString(), page = currentPage + 1)
            }
        }
    }

    override fun onSelectAd(adsData: AdsData, position: Int) {
        if (adsData.adCategoryId == STORES) {
            val intent = Intent(this@NormalAdsActivity, StoreProfileActivity::class.java)
            intent.putExtra(SELECTED_STORE_ID, adsData.storeId)
            startActivity(intent)
        } else {
            val intent = Intent(this@NormalAdsActivity, AdsStoryActivity::class.java)
            val bundle = Bundle()
            intent.putExtra(POSITION, position)
            bundle.putSerializable(PRODUCT_FILES_INFORMATION, adsData.adFiles)
            intent.putExtras(bundle)
            intent.putExtra(selectedAdObject, adsData as Parcelable)
            intent.putExtra(IS_HOME, true)
            startActivity(intent)
        }
    }
}