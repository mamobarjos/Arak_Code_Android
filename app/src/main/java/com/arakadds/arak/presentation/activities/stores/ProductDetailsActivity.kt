package com.arakadds.arak.presentation.activities.stores

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.URLUtil
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.arakadds.arak.R
import com.arakadds.arak.common.helper.LocaleHelper
import com.arakadds.arak.common.helper.MethodHelper.shareAd
import com.arakadds.arak.common.helper.connectivity_checker.ConnectionLiveData
import com.arakadds.arak.common.preferaence.IPreferenceHelper
import com.arakadds.arak.common.preferaence.PreferenceManager
import com.arakadds.arak.databinding.ActivityProductDetailsBinding
import com.arakadds.arak.model.new_mapping_refactore.BaseResponse
import com.arakadds.arak.model.new_mapping_refactore.request.AddAdReviewRequest
import com.arakadds.arak.model.new_mapping_refactore.request.AddStoreProductReviewRequest
import com.arakadds.arak.model.new_mapping_refactore.request.AddStoreReviewRequest
import com.arakadds.arak.model.new_mapping_refactore.store.SingleProductModel
import com.arakadds.arak.model.new_mapping_refactore.store.StoreProductFile
import com.arakadds.arak.model.new_mapping_refactore.store.StoreProductReview
import com.arakadds.arak.model.new_mapping_refactore.store.products.Product
import com.arakadds.arak.model.new_mapping_refactore.store.products.RelatedStoreProductsModel
import com.arakadds.arak.model.new_mapping_refactore.store.reviews.CreateReviewModel
import com.arakadds.arak.presentation.activities.other.BaseActivity
import com.arakadds.arak.presentation.activities.other.NoInternetConnectionActivity
import com.arakadds.arak.presentation.activities.other.ReviewsActivity
import com.arakadds.arak.presentation.activities.other.WebViewActivity
import com.arakadds.arak.presentation.adapters.NewAdSummeryAdapter
import com.arakadds.arak.presentation.adapters.RelatedProductsAdapter
import com.arakadds.arak.presentation.adapters.RelatedProductsAdapter.RelatedProductClickEvents
import com.arakadds.arak.presentation.adapters.UsersReviewsAdapter
import com.arakadds.arak.presentation.adapters.UsersReviewsAdapter.ReviewsEvents
import com.arakadds.arak.presentation.dialogs.AppDialogs
import com.arakadds.arak.presentation.dialogs.ApplicationDialogs
import com.arakadds.arak.presentation.dialogs.ApplicationDialogs.openAlertDialog
import com.arakadds.arak.presentation.viewmodel.StoresProductsViewModel
import com.arakadds.arak.utils.AppEnums.DialogActionTypes.DISMISS
import com.arakadds.arak.utils.Constants
import com.arakadds.arak.utils.Constants.MY_PERMISSIONS_REQUEST_CALL_PHONE
import com.arakadds.arak.utils.Constants.SELECTED_CATEGORY_ID
import com.arakadds.arak.utils.Constants.SELECTED_PRODUCT_ID
import com.arakadds.arak.utils.GlobalMethodsOldClass
import dagger.android.AndroidInjection
import javax.inject.Inject

class ProductDetailsActivity : BaseActivity(), RelatedProductClickEvents, ReviewsEvents,
    ApplicationDialogs.AlertDialogCallbacks, AppDialogs.DialogStoreReviewCallBack {

    private val preferenceHelper: IPreferenceHelper by lazy { PreferenceManager(applicationContext) }
    lateinit var binding: ActivityProductDetailsBinding
    private lateinit var activityContext: Context

    private lateinit var toolbar: Toolbar
    private var connectionLiveData: ConnectionLiveData? = null

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val storesProductsViewModel: StoresProductsViewModel by viewModels {
        viewModelFactory
    }
    private lateinit var token: String
    private lateinit var resources: Resources
    private lateinit var language: String
    private lateinit var pageTitle: TextView
    private lateinit var shareImageView: ImageView
    private lateinit var favoriteImageView: ImageView
    private var usersReviewsAdapter: UsersReviewsAdapter? = null
    private var productId: String? = null
    private val adFilesList = ArrayList<StoreProductFile>()
    private var newAdSummeryAdapter: NewAdSummeryAdapter? = null
    private var storeId: Int = 0
    private var productName: String? = null
    private var productDescription: String? = null
    private var categoryId = 0
    private var relatedProductsAdapter: RelatedProductsAdapter? = null
    private var phoneNumber: String? = null
    private var lon: String? = null
    private var lat: String? = null
    private var storeWebsiteUrl: String? = null
    private var storeName: String? = null
    private val storeProductReviewArrayList = ArrayList<StoreProductReview>()

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connectionLiveData = ConnectionLiveData(this)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AndroidInjection.inject(this)
        activityContext = this

        language = preferenceHelper.getLanguage()

        updateProductDetailsView(language)
        checkNetworkConnection()
        initToolbar()
        initData()
        getIntents()
        setListeners()
    }

    private fun initToolbar() {
        toolbar = binding.appBarLayout.findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        pageTitle = binding.appBarLayout.findViewById(R.id.page_title)
        val backImageView: ImageView = binding.appBarLayout.findViewById(R.id.back_button)
        shareImageView = binding.appBarLayout.findViewById(R.id.share_button)
        favoriteImageView = binding.appBarLayout.findViewById(R.id.favorite_button)

        //pageTitle.text = resources.getText(R.string.notifications_activity_title_label)
        backImageView.setOnClickListener { finish() }
    }

    private fun getIntents() {
        productId = intent.getStringExtra(SELECTED_PRODUCT_ID)
        productId?.let { getSingleProduct(it) }
    }

    private fun updateProductDetailsView(language: String) {
        val context = LocaleHelper.setLocale(this@ProductDetailsActivity, language)
        resources = context.resources
        binding.storeProductRelatedProductsTitleTextViewId.text =
            resources.getString(R.string.Create_store_activity_Related_Products)
        binding.storeProductRelatedProductsViewAllTextViewId.text =
            resources.getString(R.string.Create_store_activity_View_All)
        binding.storeProductReviewTitleTextViewId.text =
            resources.getString(R.string.Create_store_activity_Review)
        binding.storeProfileFeedbackTitleTextViewId.text =
            resources.getString(R.string.store_profile_activity_Add_Review)

        binding.storeProductRelatedProductsViewAllTextViewId.text =
            resources.getString(R.string.Create_store_activity_View_All)

        binding.storeProductReviewsViewAllTextViewId.text =
            resources.getString(R.string.Create_store_activity_View_All)
        this.language = language
    }

    private fun setListeners() {
        binding.storeProductsBackImageViewId.setOnClickListener { finish() }

        binding.productDetailsWhatsAppImageViewId.setOnClickListener {
            val url = "https://api.whatsapp.com/send?phone=$phoneNumber"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }
        binding.productDetailsPhoneCallImageViewId.setOnClickListener {
            val number = "tel:$phoneNumber"
            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse(number)
            // Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(
                    this@ProductDetailsActivity,
                    Manifest.permission.CALL_PHONE
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this@ProductDetailsActivity,
                    arrayOf<String>(Manifest.permission.CALL_PHONE),
                    MY_PERMISSIONS_REQUEST_CALL_PHONE
                )

                // MY_PERMISSIONS_REQUEST_CALL_PHONE is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            } else {
                //You already have permission
                try {
                    startActivity(intent)
                } catch (e: SecurityException) {
                    e.printStackTrace()
                }
            }
        }
        binding.productDetailsLocationImageViewId.setOnClickListener {
            if (lat != null && lon != null && lat != "" && lon != "") {
                val gmmIntentUri =
                    Uri.parse("google.navigation:q=$lat,$lon")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                startActivity(mapIntent)
            } else {
                Toast.makeText(
                    activityContext,
                    resources.getString(R.string.toast_sorry_advertiser_doset_address),
                    Toast.LENGTH_SHORT
                ).show();
            }
        }
        binding.productDetailsWebsiteImageViewId.setOnClickListener {
            if (storeWebsiteUrl != null) {
                if (URLUtil.isValidUrl(storeWebsiteUrl)) {
                    val intent =
                        Intent(this@ProductDetailsActivity, WebViewActivity::class.java)
                    intent.putExtra("web_Url", storeWebsiteUrl)
                    intent.putExtra("page_title", storeName)
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        activityContext,
                        resources.getString(R.string.toast_website_not_valid),
                        Toast.LENGTH_SHORT
                    ).show();
                }
            }else{
                Toast.makeText(
                    activityContext,
                    resources.getString(R.string.toast_sorry_advertiser_doset_website),
                    Toast.LENGTH_SHORT
                ).show();
            }
        }
        binding.storeProductAddReviewLinearLayoutId.setOnClickListener {
            AppDialogs.addStoreReviewDialog(
                this,
                resources,
                null,
                productId?.toInt(),
                null,
                this
            )
        }
        binding.storeProductRelatedProductsViewAllTextViewId.setOnClickListener {
            val intent = Intent(
                this@ProductDetailsActivity,
                StoreProductsActivity::class.java
            )
            intent.putExtra(SELECTED_CATEGORY_ID, categoryId)
            startActivity(intent)
        }

        shareImageView.setOnClickListener {
            productDescription?.let { it1 -> productName?.let { it2 -> shareAd(it2, it1,"",this) } }
        }

        binding.storeProductReviewsViewAllTextViewId.setOnClickListener {
            val intent = Intent(
                this@ProductDetailsActivity,
                ReviewsActivity::class.java
            )
            intent.putExtra(Constants.STORE_ID, storeId)
            intent.putExtra(Constants.REVIEWS, storeProductReviewArrayList)
            startActivity(intent)
        }

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

    private fun setProductFilesAdapter() {
        newAdSummeryAdapter =
            NewAdSummeryAdapter(adFilesList, this@ProductDetailsActivity, "1", false, false, "1")
        val mLayoutManager =
            LinearLayoutManager(this@ProductDetailsActivity, LinearLayoutManager.HORIZONTAL, false)
        binding.storeProductMediaRecyclerViewId!!.layoutManager = mLayoutManager
        binding.storeProductMediaRecyclerViewId!!.adapter = newAdSummeryAdapter
    }

    private fun setDataToView(singleProductModel: SingleProductModel) {
        storeId = singleProductModel.singleProductDataModel.storeId
        productName = singleProductModel.singleProductDataModel.name
        productDescription =  singleProductModel.singleProductDataModel.description
        pageTitle.text = singleProductModel.singleProductDataModel.name
        binding.storeProductProductNameTextViewId.text =
            singleProductModel.singleProductDataModel.name
        binding.storeProductDescriptionTextReadMoreTextViewId.text =
            singleProductModel.singleProductDataModel.description


        try {
            if (singleProductModel.singleProductDataModel.salePrice != null && singleProductModel.singleProductDataModel.salePrice.toDouble() > 0.0) {
                binding.storeProductProductPriceTextViewId.text =
                    singleProductModel.singleProductDataModel.salePrice + " " +preferenceHelper.getCurrencySymbol()
                binding.storeProductProductSalePriceTextViewId.text =
                    singleProductModel.singleProductDataModel.price + " " + preferenceHelper.getCurrencySymbol()
            } else {
                binding.storeProductProductPriceTextViewId.text =
                    singleProductModel.singleProductDataModel.price + " " +preferenceHelper.getCurrencySymbol()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            binding.storeProductProductPriceTextViewId.text =
                singleProductModel.singleProductDataModel.price + " " +preferenceHelper.getCurrencySymbol()
        }

        binding.storeProductRateTextViewId.text =
            singleProductModel.singleProductDataModel.rating
        if (singleProductModel.singleProductDataModel.rating != null) {
            binding.storeProductRatingBarId.rating =
                singleProductModel.singleProductDataModel.rating.toFloat()
        }
        binding.storeProductStoreNameTextViewId.text =
            singleProductModel.singleProductDataModel.storeObject?.name
        storeName = singleProductModel.singleProductDataModel.name
        /*lat = singleProductModel.singleProductData.storeProductsResponse.storesList.storeLat
        lon = singleProductModel.singleProductData.storeProductsResponse.storesList.storeLon*/
        //todo when store object return fill this data below
        //isFavourite = singleAdData.getData().getIsFavourite();
        /*if (isFavourite == 1) {
                            favoriteImageView.setImageResource(R.drawable.favorite_icon);
                        } else {
                            favoriteImageView.setImageResource(R.drawable.unfaforite_icon);
                        }*/phoneNumber =
            singleProductModel.singleProductDataModel.storeObject?.phoneNo

        singleProductModel.singleProductDataModel.productReviews?.let {
            storeProductReviewArrayList.addAll(
                it
            )
        }
        // storeWebsiteUrl = singleProductModel.singleProductData.storeProductsResponse.storesList.storeWebsite
    }

    private fun setRelatedProductsAdapter(productArrayList: ArrayList<Product>) {
        relatedProductsAdapter = RelatedProductsAdapter(
            productArrayList, this@ProductDetailsActivity, language, resources,preferenceHelper, this
        )
        val mLayoutManager =
            LinearLayoutManager(this@ProductDetailsActivity, LinearLayoutManager.HORIZONTAL, false)
        binding.productDetailsRelatedProductsRecyclerViewId.layoutManager = mLayoutManager
        binding.productDetailsRelatedProductsRecyclerViewId.isFocusable = false
        binding.productDetailsRelatedProductsRecyclerViewId.isNestedScrollingEnabled = false
        binding.productDetailsRelatedProductsRecyclerViewId.adapter = relatedProductsAdapter
    }

    private fun setStoreReviewsAdapter(reviewResponseDataArrayList: ArrayList<StoreProductReview>) {
        val limitNum = if (reviewResponseDataArrayList.size > 2) 2 else reviewResponseDataArrayList.size
        usersReviewsAdapter = UsersReviewsAdapter(
            reviewResponseDataArrayList,
            productName,
            preferenceHelper.getKeyUserId(),
            limitNum,
            this@ProductDetailsActivity,
            resources,
            this,
            true
        )
        val mLayoutManager =
            LinearLayoutManager(this@ProductDetailsActivity, LinearLayoutManager.VERTICAL, false)
        binding.productDetailsReviewRecyclerViewId.layoutManager = mLayoutManager
        binding.productDetailsReviewRecyclerViewId.isFocusable = false
        binding.productDetailsReviewRecyclerViewId.isNestedScrollingEnabled = false
        binding.productDetailsReviewRecyclerViewId.adapter = usersReviewsAdapter
    }

    private fun addProductReview(addStoreProductReviewRequest: AddStoreProductReviewRequest) {
        //showLoadingDialog(this, "message")
        storesProductsViewModel.addProductReview("Bearer $token", language, addStoreProductReviewRequest)
        storesProductsViewModel.createReviewModelModel.observe(
            this,
            Observer(function = fun(createReviewModel: CreateReviewModel?) {
                createReviewModel?.let {
                    if (createReviewModel.statusCode == 201) {
                        storeProductReviewArrayList.add(createReviewModel.storeProductReview)
                        usersReviewsAdapter!!.notifyDataSetChanged()
                        binding.storeProductAddReviewLinearLayoutId.visibility = View.GONE
                    } else {
                        openAlertDialog(
                            this,
                            resources?.getString(R.string.dialogs_error),
                            createReviewModel.message,
                            false,
                            DISMISS,
                            this
                        )
                        // hideView(binding.loginProgressBarId*//*progressBar*//*)
                    }
                }
                //hideLoadingDialog()
            })
        )

        storesProductsViewModel.throwableResponse.observe(
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
                //hideLoadingDialog()
            })
        )
    }

    private fun getSingleProduct(id: String) {
        showLoadingDialog(this, "message")
        storesProductsViewModel.getSingleProduct("Bearer $token", language, id)
        storesProductsViewModel.singleProductModelModel.observe(
            this,
            Observer(function = fun(singleProductModel: SingleProductModel?) {
                singleProductModel?.let {
                    if (singleProductModel.statusCode == 200) {
                        categoryId =
                            singleProductModel.singleProductDataModel.storeObject?.storeCategory?.id
                                ?: 0
                        singleProductModel.singleProductDataModel.adFilesArrayList?.let { it1 ->
                            adFilesList.addAll(
                                it1
                            )
                        }

                        setDataToView(singleProductModel)
                        setProductFilesAdapter()
                        getRelatedStoreProducts(singleProductModel.singleProductDataModel.productId.toString())
                        singleProductModel.singleProductDataModel.productReviews?.let { it1 ->
                            setStoreReviewsAdapter(
                                it1
                            )
                        }
                        /*if (adFilesList.size() > 0) {
            numberOfImagesTextView.setVisibility(View.VISIBLE);
            numberOfImagesTextView.setText(String.valueOf(adFilesList.size()));
        }*/
                    } else {
                        openAlertDialog(
                            this,
                            resources?.getString(R.string.dialogs_error),
                            singleProductModel.message,
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

        storesProductsViewModel.throwableResponse.observe(
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

    private fun getRelatedStoreProducts(id: String) {
        //showLoadingDialog(this, "message")
        storesProductsViewModel.getRelatedStoreProducts("Bearer $token", language, id)
        storesProductsViewModel.relatedStoreProductsModelModel.observe(
            this,
            Observer(function = fun(relatedStoreProductsModel: RelatedStoreProductsModel?) {
                relatedStoreProductsModel?.let {
                    if (relatedStoreProductsModel.statusCode == 200) {

                        setRelatedProductsAdapter(
                            relatedStoreProductsModel.productArrayList
                        )
                    } else {
                        openAlertDialog(
                            this,
                            resources?.getString(R.string.dialogs_error),
                            relatedStoreProductsModel.message,
                            false,
                            DISMISS,
                            this
                        )
                        // hideView(binding.loginProgressBarId*//*progressBar*//*)

                    }
                }
                //hideLoadingDialog()
            })
        )

        storesProductsViewModel.throwableResponse.observe(
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
                //hideLoadingDialog()
            })
        )
    }

    private fun deleteProductReview(reviewId: Int, position: Int, deleteUserReview: ImageView) {
        //showLoadingDialog(this, "message")
        storesProductsViewModel.deleteProductReview("Bearer $token", language, reviewId)
        storesProductsViewModel.baseResponseModel.observe(
            this,
            Observer(function = fun(baseResponse: BaseResponse?) {
                baseResponse?.let {
                    if (baseResponse.statusCode == 201) {
                        try {
                            storeProductReviewArrayList.removeAt(position)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        //binding.productDetailsProductReviewsCardViewId.visibility = View.VISIBLE
                        deleteUserReview.visibility = View.GONE
                        usersReviewsAdapter!!.notifyDataSetChanged()

                    } else {
                        openAlertDialog(
                            this,
                            resources?.getString(R.string.dialogs_error),
                            baseResponse.message,
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

        storesProductsViewModel.throwableResponse.observe(
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

    override fun onDeleteStoreReviewCallback(
        id: Int,
        position: Int,
        deleteUserReview: ImageView?
    ) {
    }

    override fun onDeleteProductReviewCallback(
        id: Int,
        position: Int,
        deleteUserReview: ImageView
    ) {
        deleteProductReview(id, position, deleteUserReview)
    }

    override fun onRelatedProductClickedCalledBack(
        position: Int,
        product: Product
    ) {
        val intent = Intent(this@ProductDetailsActivity, ProductDetailsActivity::class.java)
        intent.putExtra(SELECTED_PRODUCT_ID, product.id.toString())
        startActivity(intent)
    }

    override fun onClose() {
    }

    override fun onConfirm(actionType: Int) {

    }

    override fun onSubmitStoreReviewCallback(addStoreReviewRequest: AddStoreReviewRequest) {
    }

    override fun onSubmitStoreProductReviewCallback(addStoreProductReviewRequest: AddStoreProductReviewRequest) {
        addProductReview(addStoreProductReviewRequest)
    }

    override fun onSubmitAdReviewCallback(addAdReviewRequest: AddAdReviewRequest?) {
    }
}