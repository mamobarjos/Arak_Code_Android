package com.arakadds.arak.presentation.activities.profile

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
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
import androidx.recyclerview.widget.RecyclerView
import com.arakadds.arak.R
import com.arakadds.arak.common.helper.LocaleHelper
import com.arakadds.arak.common.helper.connectivity_checker.ConnectionLiveData
import com.arakadds.arak.common.preferaence.IPreferenceHelper
import com.arakadds.arak.common.preferaence.PreferenceManager
import com.arakadds.arak.databinding.ActivityStoreProductsBinding
import com.arakadds.arak.model.new_mapping_refactore.BaseResponse
import com.arakadds.arak.model.new_mapping_refactore.store.products.Product
import com.arakadds.arak.model.new_mapping_refactore.store.products.my_products.MyStoreProductsModel
import com.arakadds.arak.presentation.activities.other.BaseActivity
import com.arakadds.arak.presentation.activities.other.NoInternetConnectionActivity
import com.arakadds.arak.presentation.activities.stores.CreateProductActivity
import com.arakadds.arak.presentation.activities.stores.ProductDetailsActivity
import com.arakadds.arak.presentation.adapters.StoreProductsAdapter
import com.arakadds.arak.presentation.adapters.StoreProductsAdapter.ProductClickEvents
import com.arakadds.arak.presentation.dialogs.ApplicationDialogs
import com.arakadds.arak.presentation.dialogs.ApplicationDialogs.openAlertDialog
import com.arakadds.arak.presentation.viewmodel.MyProductsViewModel
import com.arakadds.arak.presentation.viewmodel.StoresProductsViewModel
import com.arakadds.arak.utils.AppEnums.DialogActionTypes.DISMISS
import com.arakadds.arak.utils.AppProperties
import com.arakadds.arak.utils.Constants.BASE_URL
import com.arakadds.arak.utils.Constants.IS_EDIT_ID
import com.arakadds.arak.utils.Constants.SELECTED_PRODUCT_ID
import com.arakadds.arak.utils.Constants.SELECTED_PRODUCT_OBJECT_ID
import com.arakadds.arak.utils.GlobalMethodsOldClass
import dagger.android.AndroidInjection
import io.paperdb.Paper
import javax.inject.Inject

class MyProductsActivity : BaseActivity(), ProductClickEvents,
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
    private val myProductsViewModel: MyProductsViewModel by viewModels {
        viewModelFactory
    }
    private lateinit var token: String
    private var storeProductsAdapter: StoreProductsAdapter? = null

    private lateinit var language: String
    private var storeName: String? = null
    private var storeId = 0
    private lateinit var resources: Resources
    private val storeProductsResponseArrayList = ArrayList<Product>()
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

        updateMyProductsView(language)
        initData()
        initToolbar()
        getMyProducts(page = currentPage)
        setListeners()
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

    private fun setListeners() {
    }

    private fun initData() {
        token = preferenceHelper.getToken()
        storeName = preferenceHelper.getStoreFullName()
        storeId = preferenceHelper.getStoreId()

        if (token == "non" || token.trim().isEmpty()) {
            GlobalMethodsOldClass.askGuestLogin(resources, activityContext)
            return
        }
    }

    private fun updateMyProductsView(language: String) {
        val context = LocaleHelper.setLocale(this@MyProductsActivity, language)
        resources = context.resources
        /* binding.storeProductsStoreTitleTextViewId.text =
             resources.getString(R.string.store_profile_activity_My_Products)*/
        /* descriptionEditText.setHint(resources.getString(R.string.Create_New_Ad_activity_Description));
        priceEditText.setHint(resources.getString(R.string.Create_New_product_activity_Price));
        createButton.setText(resources.getString(R.string.Create_New_product_activity_create));
        pageTitleTextView.setText(resources.getString(R.string.Create_New_product_activity_Create_product));*/
        this.language = language
    }

    private fun setStoreProductsAdapter(storeProductsResponseArrayList: ArrayList<Product>) {
        storeProductsAdapter = StoreProductsAdapter(
            storeProductsResponseArrayList,
            this@MyProductsActivity,
            language,
            resources,
            this,
            storeName,
            true,
            preferenceHelper,
            this
        )
        val mLayoutManager =
            LinearLayoutManager(this@MyProductsActivity, LinearLayoutManager.VERTICAL, false)
        binding.storeProductsListRecyclerViewId.layoutManager = mLayoutManager
        binding.storeProductsListRecyclerViewId.isFocusable = false
        binding.storeProductsListRecyclerViewId.isNestedScrollingEnabled = false
        binding.storeProductsListRecyclerViewId.adapter = storeProductsAdapter
    }

    private fun getMyProducts(page: Int) {
        showLoadingDialog(this, "message")
        viewModel.getMyStoreProducts("Bearer $token", language, page)
        viewModel.myStoreProductsModelModel.observe(
            this,
            Observer(function = fun(myStoreProductsModel: MyStoreProductsModel?) {
                myStoreProductsModel?.let {
                    if (myStoreProductsModel.statusCode == 200) {
                        storeProductsResponseArrayList.clear()
                        storeProductsResponseArrayList.addAll(
                            myStoreProductsModel.data.products
                        )
                        if (storeProductsResponseArrayList.size > 0) {
                            setStoreProductsAdapter(storeProductsResponseArrayList)
                        } else {
                            binding.storeProductsEmptyViewImageViewId.visibility = View.VISIBLE
                            binding.storeProductsEmptyViewTextViewId.visibility = View.VISIBLE
                        }
                    } else {
                        binding.storeProductsEmptyViewImageViewId.visibility = View.VISIBLE
                        binding.storeProductsEmptyViewTextViewId.visibility = View.VISIBLE
                        openAlertDialog(
                            this,
                            resources?.getString(R.string.dialogs_error),
                            myStoreProductsModel.message,
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
                        resources.getString(R.string.dialogs_error),
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

    private fun deleteProduct(productId: Int, position: Int) {
        showLoadingDialog(this, "message")
        viewModel.deleteProduct(token, language, productId)
        viewModel.baseResponseModel.observe(
            this,
            Observer(function = fun(baseResponse: BaseResponse?) {
                baseResponse?.let {
                    if (baseResponse.statusCode == 200) {
                        try {
                            storeProductsResponseArrayList.removeAt(position)
                            storeProductsAdapter!!.notifyItemRemoved(position)
                            storeProductsAdapter!!.notifyDataSetChanged()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        openAlertDialog(
                            this,
                            resources.getString(R.string.dialogs_error),
                            baseResponse.message,
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

    override fun onProductClickedCalledBack(position: Int, productId: Int) {

        val intent = Intent(this@MyProductsActivity, ProductDetailsActivity::class.java)
        intent.putExtra(SELECTED_PRODUCT_ID, productId.toString())
        intent.putExtra("StoreID",storeId)
        startActivity(intent)

    }
    override fun onEditProductClickedCalledBack(
        position: Int,
        product: Product?
    ) {
        val intent = Intent(this@MyProductsActivity, CreateProductActivity::class.java)
        intent.putExtra(SELECTED_PRODUCT_OBJECT_ID, product)
        intent.putExtra(IS_EDIT_ID, true)
        intent.putExtra("StoreID", storeId)
        startActivity(intent)
    }

    override fun onViewProductClickedCalledBack(position: Int, productId: Int) {
        val intent = Intent(this@MyProductsActivity, ProductDetailsActivity::class.java)
        intent.putExtra(SELECTED_PRODUCT_ID, productId.toString())
        startActivity(intent)
    }

    override fun onRemoveProductClickedCalledBack(position: Int, productId: Int) {
        AlertDialog.Builder(this@MyProductsActivity)
            .setTitle(resources.getString(R.string.dialogs_Delete_Product))
            .setMessage(resources.getString(R.string.dialogs_sure_to_delete_product))
            .setPositiveButton(
                android.R.string.yes
            ) { dialog: DialogInterface?, which: Int ->
                deleteProduct(
                    productId,
                    position
                )
            } // A null listener allows the button to dismiss the dialog and take no further action.
            .setNegativeButton(android.R.string.no, null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }

    override fun onClose() {
    }

    override fun onConfirm(actionType: Int) {

    }

    override fun onNextPageRequired() {
        if (currentPage < lastPage) {
            getMyProducts(page = currentPage + 1)
            //todo .. handle date filtration
            /*if (dateFrom.trim().isNotEmpty() && dateTo.trim().isNotEmpty()) {
                getMyAds(page = currentPage + 1)
            } else {
                getMyAds(dateFrom = searchName, dateTo = dateTo, page = currentPage + 1)
            }*/
        }
    }
}