package com.arakadds.arak.presentation.activities.stores

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.arakadds.arak.R
import com.arakadds.arak.common.helper.LocaleHelper
import com.arakadds.arak.common.helper.connectivity_checker.ConnectionLiveData
import com.arakadds.arak.common.preferaence.IPreferenceHelper
import com.arakadds.arak.common.preferaence.PreferenceManager
import com.arakadds.arak.databinding.ActivityStoreProductsBinding
import com.arakadds.arak.model.new_mapping_refactore.store.StoreProductsModel
import com.arakadds.arak.model.new_mapping_refactore.store.products.Product
import com.arakadds.arak.presentation.activities.other.BaseActivity
import com.arakadds.arak.presentation.activities.other.NoInternetConnectionActivity
import com.arakadds.arak.presentation.adapters.StoreProductsAdapter
import com.arakadds.arak.presentation.adapters.StoreProductsAdapter.ProductClickEvents
import com.arakadds.arak.presentation.dialogs.ApplicationDialogs
import com.arakadds.arak.presentation.dialogs.ApplicationDialogs.openAlertDialog
import com.arakadds.arak.presentation.viewmodel.StoresProductsViewModel
import com.arakadds.arak.utils.AppEnums.DialogActionTypes.DISMISS
import com.arakadds.arak.utils.AppProperties
import com.arakadds.arak.utils.Constants.BASE_URL
import com.arakadds.arak.utils.Constants.SELECTED_CATEGORY_ID
import com.arakadds.arak.utils.Constants.SELECTED_PRODUCT_ID
import com.arakadds.arak.utils.Constants.SELECTED_STORE_ID
import com.arakadds.arak.utils.Constants.SELECTED_STORE_NAME
import com.arakadds.arak.utils.GlobalMethodsOldClass
import dagger.android.AndroidInjection
import io.paperdb.Paper
import javax.inject.Inject

class StoreProductsActivity : BaseActivity(), ProductClickEvents,
    ApplicationDialogs.AlertDialogCallbacks, StoreProductsAdapter.MyProductsCallBacks {

    private val preferenceHelper: IPreferenceHelper by lazy { PreferenceManager(applicationContext) }
    lateinit var binding: ActivityStoreProductsBinding
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
    private lateinit var storeProductsAdapter: StoreProductsAdapter
    private var storeName: String? = null
    private var storeId = 0
    private var categoryId: Int = 0
    private val productsArrayList = ArrayList<Product>()
    private var currentPage = 1
    private var lastPage: Int = 1

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connectionLiveData = ConnectionLiveData(this)
        binding = ActivityStoreProductsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AndroidInjection.inject(this)
        activityContext = this

        language = preferenceHelper.getLanguage()

        updateStoreProductsView(language)
        initData()
        getIntents()
        initToolbar()
        handleDataRequest()
        setViewsData()
        setListeners()
    }

    private fun handleDataRequest() {
        if (storeId != -1) {
            getStoreProducts(storeId = storeId, page = 1)
        } else {
            getStoreProducts(page = 1, storeCategoryId = categoryId)
        }
    }

    private fun initToolbar() {
        toolbar = binding.appBarLayout.findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        pageTitle = binding.appBarLayout.findViewById(R.id.page_title)
        val backImageView: ImageView = binding.appBarLayout.findViewById(R.id.back_button)
        //shareImageView = binding.appBarLayout.findViewById(R.id.share_button)
        //favoriteImageView = binding.appBarLayout.findViewById(R.id.favorite_button)
        pageTitle.text = storeName
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

    private fun setViewsData() {

    }

    fun setListeners() {

    }

    private fun getIntents() {
        storeId = intent.getIntExtra(SELECTED_STORE_ID, -1)
        categoryId = intent.getIntExtra(SELECTED_CATEGORY_ID, -1)
        storeName = intent.getStringExtra(SELECTED_STORE_NAME)

    }

    private fun updateStoreProductsView(language: String) {
        val context = LocaleHelper.setLocale(this@StoreProductsActivity, language)
        resources = context.resources

        //specialServiceTextView.setText(resources.getString(R.string.Category_Packages_activity_Special_Service));
        this.language = language
    }

    private fun setStoreProductsAdapter(productsArrayList: ArrayList<Product>) {
        storeProductsAdapter = StoreProductsAdapter(
            productsArrayList,
            this@StoreProductsActivity,
            language,
            resources,
            this,
            storeName,
            false,
            preferenceHelper,
            this
        )
        val mLayoutManager =
            LinearLayoutManager(this@StoreProductsActivity, LinearLayoutManager.VERTICAL, false)
        binding.storeProductsListRecyclerViewId.layoutManager = mLayoutManager
        binding.storeProductsListRecyclerViewId.isFocusable = false
        binding.storeProductsListRecyclerViewId.isNestedScrollingEnabled = false
        binding.storeProductsListRecyclerViewId.adapter = storeProductsAdapter
    }

    private fun getStoreProducts(
        page: Int,
        random: Boolean? = null,
        storeId: Int? = null,
        storeCategoryId: Int? = null,
    ) {
        showLoadingDialog(this, "message")
        viewModel.getStoreProducts(token, language, page, random, storeId, storeCategoryId)
        viewModel.storeProductsModelModel.observe(
            this,
            Observer(function = fun(storeProductsModel: StoreProductsModel?) {
                storeProductsModel?.let {
                    if (storeProductsModel.statusCode == 200) {
                        productsArrayList.clear()
                        storeProductsModel.data?.products?.let { it1 ->
                            productsArrayList.addAll(
                                it1
                            )
                        }
                        if (productsArrayList.size > 0) {
                            setStoreProductsAdapter(productsArrayList)
                        } else {
                            binding.storeProductsEmptyViewImageViewId.visibility = View.VISIBLE
                            binding.storeProductsEmptyViewTextViewId.visibility = View.VISIBLE
                        }
                    } else {
                        openAlertDialog(
                            this,
                            resources?.getString(R.string.dialogs_error),
                            storeProductsModel.message,
                            false,
                            DISMISS,
                            this
                        )
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

    override fun onProductClickedCalledBack(position: Int, productId: Int) {
        val intent = Intent(this@StoreProductsActivity, ProductDetailsActivity::class.java)
        intent.putExtra(SELECTED_PRODUCT_ID, productId.toString())
        intent.putExtra("StoreID",storeId)
        startActivity(intent)
    }

    override fun onEditProductClickedCalledBack(position: Int, product: Product?) {}

    override fun onViewProductClickedCalledBack(position: Int, productId: Int) {}

    override fun onRemoveProductClickedCalledBack(position: Int, productId: Int) {}


    override fun onClose() {
    }

    override fun onConfirm(actionType: Int) {

    }

    override fun onNextPageRequired() {
        if (currentPage < lastPage) {
            if (categoryId == 0) {
                getStoreProducts(storeId = storeId,page = currentPage + 1)
            } else {
                getStoreProducts(storeId = storeId,page = currentPage + 1, storeCategoryId = categoryId)
            }
        }
    }
}