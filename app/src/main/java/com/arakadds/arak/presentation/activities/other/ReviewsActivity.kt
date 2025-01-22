package com.arakadds.arak.presentation.activities.other

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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.arakadds.arak.R
import com.arakadds.arak.common.helper.LocaleHelper
import com.arakadds.arak.common.helper.connectivity_checker.ConnectionLiveData
import com.arakadds.arak.common.preferaence.IPreferenceHelper
import com.arakadds.arak.common.preferaence.PreferenceManager
import com.arakadds.arak.databinding.ActivityReviewsBinding
import com.arakadds.arak.model.new_mapping_refactore.response.banners.ads.ad_reviews.AdReviews

import com.arakadds.arak.model.new_mapping_refactore.store.StoreProductReview
import com.arakadds.arak.presentation.adapters.AdsReviewsAdapter
import com.arakadds.arak.presentation.adapters.UsersReviewsAdapter
import com.arakadds.arak.presentation.viewmodel.StoresProductsViewModel
import com.arakadds.arak.utils.AppProperties
import com.arakadds.arak.utils.Constants
import dagger.android.AndroidInjection
import io.paperdb.Paper
import javax.inject.Inject

class ReviewsActivity : BaseActivity(), UsersReviewsAdapter.ReviewsEvents,
    AdsReviewsAdapter.AdsReviewsEvents {

    private val preferenceHelper: IPreferenceHelper by lazy { PreferenceManager(applicationContext) }
    lateinit var binding: ActivityReviewsBinding
    private lateinit var activityContext: Context

    private lateinit var toolbar: Toolbar
    private lateinit var pageTitle: TextView
    private var connectionLiveData: ConnectionLiveData? = null

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: StoresProductsViewModel by viewModels {
        viewModelFactory
    }
    private lateinit var token: String
    private lateinit var language: String
    private lateinit var resources: Resources
    private var usersReviewsAdapter: UsersReviewsAdapter? = null
    private var adsReviewsAdapter: AdsReviewsAdapter? = null
    private var storeProductReviewArrayList = ArrayList<StoreProductReview>()
    private var adReviewsArrayList = ArrayList<AdReviews>()
    private var name: String? = null
    private var id = 0
    private var currentPage = 1
    private var lastPage: Int = 1

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connectionLiveData = ConnectionLiveData(this)
        binding = ActivityReviewsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AndroidInjection.inject(this)
        activityContext = this

        language = preferenceHelper.getLanguage()

        updateStoreProductsView(language)
        checkNetworkConnection()
        initToolbar()
        initData()
        getIntents()

    }

    private fun initToolbar() {
        toolbar = binding.appBarLayout.findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        pageTitle = binding.appBarLayout.findViewById(R.id.page_title)
        val backImageView: ImageView = binding.appBarLayout.findViewById(R.id.back_button)
        //shareImageView = binding.appBarLayout.findViewById(R.id.share_button)
        //favoriteImageView = binding.appBarLayout.findViewById(R.id.favorite_button)
        //pageTitle.text = activityResources.getString(R.string.profile_frag_History)
        backImageView.setOnClickListener { finish() }
    }

    private fun initData() {
        token = preferenceHelper.getToken()
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

    private fun setStoreReviewsAdapter() {
        usersReviewsAdapter = UsersReviewsAdapter(
            storeProductReviewArrayList,
            name,
            preferenceHelper.getKeyUserId(),
            storeProductReviewArrayList.size,
            this@ReviewsActivity,
            resources,
            this,
            false
        )
        val mLayoutManager =
            LinearLayoutManager(this@ReviewsActivity, LinearLayoutManager.VERTICAL, false)
        binding.storeReviewsListRecyclerViewId.layoutManager = mLayoutManager
        binding.storeReviewsListRecyclerViewId.isFocusable = false
        binding.storeReviewsListRecyclerViewId.isNestedScrollingEnabled = false
        binding.storeReviewsListRecyclerViewId.adapter = usersReviewsAdapter
    }

    private fun setAdsReviewsAdapter() {
        adsReviewsAdapter = AdsReviewsAdapter(
            adReviewsArrayList,
            name,
            preferenceHelper.getKeyUserId(),
            adReviewsArrayList.size,
            this@ReviewsActivity,
            resources,
            this,
            false
        )
        val mLayoutManager =
            LinearLayoutManager(this@ReviewsActivity, LinearLayoutManager.VERTICAL, false)
        binding.storeReviewsListRecyclerViewId.layoutManager = mLayoutManager
        binding.storeReviewsListRecyclerViewId.isFocusable = false
        binding.storeReviewsListRecyclerViewId.isNestedScrollingEnabled = false
        binding.storeReviewsListRecyclerViewId.adapter = adsReviewsAdapter
    }

    private fun getIntents() {
        if (intent.getBooleanExtra(Constants.IS_NORMAL_AD, false)) {
            id = intent.getIntExtra(Constants.AD_ID, -1)
            name = intent.getStringExtra(Constants.AD_NAME)
            adReviewsArrayList = intent.getParcelableArrayListExtra<AdReviews>(Constants.REVIEWS)!!
            if (adReviewsArrayList.isNotEmpty()) {
                setAdsReviewsAdapter()
                adsReviewsAdapter?.notifyDataSetChanged()
            }



        } else {
            id = intent.getIntExtra(Constants.SELECTED_STORE_ID, -1)
            name = intent.getStringExtra(Constants.SELECTED_STORE_NAME)
            storeProductReviewArrayList =
                intent.getParcelableArrayListExtra<StoreProductReview>(Constants.REVIEWS)!!
            if (storeProductReviewArrayList.isNotEmpty()) {
                setStoreReviewsAdapter()
                usersReviewsAdapter?.notifyDataSetChanged()
            }
        }
        pageTitle.text = name
    }

    private fun updateStoreProductsView(language: String) {
        val context = LocaleHelper.setLocale(this, language)
        resources = context.resources

        //specialServiceTextView.setText(resources.getString(R.string.Category_Packages_activity_Special_Service));
        this.language = language
    }

    override fun onDeleteStoreReviewCallback(id: Int, position: Int, deleteUserReview: ImageView?) {

    }

    override fun onDeleteProductReviewCallback(
        id: Int,
        position: Int,
        deleteUserReview: ImageView?
    ) {

    }

    /*override fun onNextPageRequired() {
        if (currentPage < lastPage) {
            if (categoryId == 0) {
                getStoreProducts(page = currentPage + 1)
            } else {
                getStoreProducts(page = currentPage + 1, storeCategoryId = categoryId)
            }
        }
    }*/
}