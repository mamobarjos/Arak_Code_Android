package com.arakadds.arak.presentation.activities.stores

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.arakadds.arak.R
import com.arakadds.arak.common.helper.LocaleHelper
import com.arakadds.arak.common.helper.connectivity_checker.ConnectionLiveData
import com.arakadds.arak.common.preferaence.IPreferenceHelper
import com.arakadds.arak.common.preferaence.PreferenceManager
import com.arakadds.arak.databinding.ActivityMainStoreProfileBinding
import com.arakadds.arak.model.new_mapping_refactore.BaseResponse
import com.arakadds.arak.model.new_mapping_refactore.home.User
import com.arakadds.arak.model.new_mapping_refactore.request.AddAdReviewRequest
import com.arakadds.arak.model.new_mapping_refactore.request.AddStoreProductReviewRequest
import com.arakadds.arak.model.new_mapping_refactore.request.AddStoreReviewRequest
import com.arakadds.arak.model.new_mapping_refactore.store.SingleStoreModel
import com.arakadds.arak.model.new_mapping_refactore.store.StoreObject
import com.arakadds.arak.model.new_mapping_refactore.store.StoreProductReview
import com.arakadds.arak.model.new_mapping_refactore.store.StoreProductsModel
import com.arakadds.arak.model.new_mapping_refactore.store.products.Product
import com.arakadds.arak.model.new_mapping_refactore.store.reviews.CreateReviewModel
import com.arakadds.arak.presentation.activities.home.HomeActivity
import com.arakadds.arak.presentation.activities.other.BaseActivity
import com.arakadds.arak.presentation.activities.other.NoInternetConnectionActivity
import com.arakadds.arak.presentation.activities.profile.MyProductsActivity
import com.arakadds.arak.presentation.adapters.StoreProductsAdapter
import com.arakadds.arak.presentation.adapters.StoreProductsAdapter.ProductClickEvents
import com.arakadds.arak.presentation.adapters.UsersReviewsAdapter
import com.arakadds.arak.presentation.adapters.UsersReviewsAdapter.ReviewsEvents
import com.arakadds.arak.presentation.dialogs.AppDialogs
import com.arakadds.arak.presentation.dialogs.ApplicationDialogs
import com.arakadds.arak.presentation.dialogs.ApplicationDialogs.openAlertDialog
import com.arakadds.arak.presentation.viewmodel.MyProductsViewModel
import com.arakadds.arak.presentation.viewmodel.StoresProductsViewModel
import com.arakadds.arak.presentation.viewmodel.StoresProfileViewModel
import com.arakadds.arak.utils.AppEnums.DialogActionTypes.DISMISS
import com.arakadds.arak.utils.AppProperties
import com.arakadds.arak.utils.Constants.SELECTED_PRODUCT_ID
import com.arakadds.arak.utils.Constants.SELECTED_STORE_ID
import com.arakadds.arak.utils.GlobalMethodsOldClass
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import dagger.android.AndroidInjection
import io.paperdb.Paper
import javax.inject.Inject

class MainStoreProfileActivity : BaseActivity(), ProductClickEvents, ReviewsEvents,
    ApplicationDialogs.AlertDialogCallbacks, StoreProductsAdapter.MyProductsCallBacks,
    AppDialogs.DialogStoreReviewCallBack {

    private val preferenceHelper: IPreferenceHelper by lazy { PreferenceManager(applicationContext) }
    lateinit var binding: ActivityMainStoreProfileBinding
    private lateinit var activityContext: Context

    private lateinit var toolbar: Toolbar
    private var connectionLiveData: ConnectionLiveData? = null

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: StoresProfileViewModel by viewModels {
        viewModelFactory
    }
    private val storesProductsViewModel: StoresProductsViewModel by viewModels {
        viewModelFactory
    }


    private val myProductsViewModel: MyProductsViewModel by viewModels {
        viewModelFactory
    }
    private lateinit var token: String
    private lateinit var language: String
    private lateinit var resources: Resources
    private var storeProductsAdapter: StoreProductsAdapter? = null
    private var usersReviewsAdapter: UsersReviewsAdapter? = null
    private var facebookLink: String? = null
    private var instagramLink: String? = null
    private var twitterLink: String? = null
    private var linkedinLink: String? = null
    private var snapchatLink: String? = null
    private var youtubeLink: String? = null
    private var phoneNumber: String? = null
    private var lat: String? = null
    private var lon: String? = null
    private var websiteLink: String? = null
    private var storeName: String? = null
    private var storeId = 0
    private val storeProductReviewArrayList = ArrayList<StoreProductReview>()
    private val storeProductArrayList = ArrayList<Product>()
    private lateinit var storeObject: StoreObject

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        connectionLiveData = ConnectionLiveData(this)
        binding = ActivityMainStoreProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AndroidInjection.inject(this)
        activityContext = this

        language = preferenceHelper.getLanguage()

        initData()
        updateStoreProfileView(language)
        checkNetworkConnection()
        /* GoogleAdsHelper.initBannerGoogleAdView(
             this@StoreProfileActivity,
             binding.storeProfileBannerViewAdViewId
         )*/
        getIntents()
        setListeners()
        getStoreProducts(page = 1, storeId = storeId)


    }

    private fun initData() {
        token = preferenceHelper.getToken()
        if (token == "non" || token.trim().isEmpty()) {
            GlobalMethodsOldClass.askGuestLogin(resources, activityContext)
            return
        }
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

    private fun getIntents() {
        storeId = intent.getIntExtra(SELECTED_STORE_ID, -1)
        if (storeId != 0) {
            if (storeId == -1) {
                getMyStore()
            } else {
                getSingleStore(storeId)
            }
        } else {
            preferenceHelper.setUserHasStore(false)
            startActivity(Intent(this@MainStoreProfileActivity, HomeActivity::class.java))
            finish()
        }
    }

    private fun updateStoreProfileView(language: String) {
        val context = LocaleHelper.setLocale(this@MainStoreProfileActivity, language)
        resources = context.resources

        binding.mainStoreMyStore.text =
            resources.getString(R.string.Create_store_activity_My_Store)

        binding.mainStoreAddProduct.text =
            resources.getString(R.string.Create_store_activity_Add_product)

        binding.mainStoreMyProducts.text =
            resources.getString(R.string.Create_store_activity_My_Products)
        this.language = language
    }

    fun setListeners() {
        binding.storeProfileBackImageViewId.setOnClickListener { finish() }

        binding.cardAddProduct.setOnClickListener {
            startActivity(Intent(this@MainStoreProfileActivity, CreateProductActivity::class.java).putExtra("StoreID",storeId.toString()))
        }

        binding.cardMyProductRequests.setOnClickListener {
            startActivity(Intent(this@MainStoreProfileActivity, MyProductsActivity::class.java))
        }

        binding.cardSalesBalance.setOnClickListener {
            startActivity(Intent(this@MainStoreProfileActivity, StoreProfileActivity::class.java))
        }

        binding.storeProfileLocationImageViewId.setOnClickListener {
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

        /*binding.storeProfileFeedbackSubmitButtonId.setOnClickListener(View.OnClickListener {
            val content: String =
                binding.storeProfileFeedbackEditTextId.text.toString().trim { it <= ' ' }
            val rate: Float = binding.storeProfileReviewRatingBarId.rating
            if (content.isEmpty()) {
                Toast.makeText(
                    activityContext,
                    resources.getString(R.string.store_profile_activity_enter_Review),
                    Toast.LENGTH_SHORT
                ).show();
                return@OnClickListener
            }
            if (rate == 0f) {
                Toast.makeText(
                    activityContext,
                    resources.getString(R.string.store_profile_activity_rate_store),
                    Toast.LENGTH_SHORT
                ).show();
                return@OnClickListener
            }
            val reviewHashMap =
                HashMap<String, Any>()
            reviewHashMap["content"] = content
            reviewHashMap["rating"] = rate
            reviewHashMap["store_id"] = storeId
            addStoreReview(reviewHashMap)
        })*/

    }


    private fun setViewsData(storeObject: StoreObject?, isMyStore: Boolean) {
        val userId = preferenceHelper.getKeyUserId()
        if (storeObject == null) {
            Toast.makeText(
                activityContext,
                resources.getString(R.string.toast_please_try_again_later),
                Toast.LENGTH_SHORT
            ).show();
            return
        }
        val options = RequestOptions()
            .centerCrop()
            .placeholder(R.color.pink)
            .error(R.color.pink)
        try {
            Glide.with(this@MainStoreProfileActivity)
                .load(storeObject.imgUrl).apply(options)
                .into(binding.storeProfileImageImageViewId)

            Glide.with(this@MainStoreProfileActivity)
                .load(storeObject.coverImgUrl).apply(options)
                .into(binding.storeCoverImageViewId)
            storeId = storeObject.id
            binding.storeNameTextViewId.text = storeObject.name
            val createdDate = storeObject.createdAt?.split("T".toRegex())
                ?.dropLastWhile { it.isEmpty() }
                ?.toTypedArray()
            /*binding.storeProfileCreateDateTextViewId.text =
                resources.getString(R.string.store_profile_activity_member_since) + " " + createdDate*/

            facebookLink = storeObject.facebook
            instagramLink = storeObject.instagram
            //twitterLink = storeObject.x
            linkedinLink = storeObject.linkedin
            snapchatLink = storeObject.snapchat
            youtubeLink = storeObject.youtube
            phoneNumber = storeObject.phoneNo
            lat = storeObject.lat.toString()
            lon = storeObject.lon.toString()
            websiteLink = storeObject.website
            storeName = storeObject.name

            storeProductReviewArrayList.addAll(storeObject.storeReviews)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    private fun getSingleStore(storeId: Int) {
        if (storeId == -1) {
            Toast.makeText(
                activityContext,
                resources.getString(R.string.toast_cant_complete_request),
                Toast.LENGTH_SHORT
            ).show();
            return
        }
        showLoadingDialog(this, "message")
        viewModel.getSingleStore("Bearer $token", language, storeId)
        viewModel.singleStoreModel.observe(
            this,
            Observer(function = fun(singleStoreModel: SingleStoreModel?) {
                singleStoreModel?.let {
                    if (singleStoreModel.statusCode == 200) {
                        setViewsData(singleStoreModel.storeObject, false)
                    } else {
                        openAlertDialog(
                            this,
                            resources.getString(R.string.dialogs_error),
                            singleStoreModel.message,
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

    private fun getMyStore() {
        showLoadingDialog(this, "message")
        viewModel.getMyStore("Bearer $token", language)
        viewModel.singleStoreModel.observe(
            this,
            Observer(function = fun(singleStoreModel: SingleStoreModel?) {
                singleStoreModel?.let {
                    if (singleStoreModel.statusCode == 200) {
                        storeObject = singleStoreModel.storeObject
                        if (singleStoreModel.storeObject != null) {
                            getStoreProducts(page = 1, storeId = storeId)
                            setViewsData(singleStoreModel.storeObject, true)
                        } else {
                            preferenceHelper.setUserHasStore(false)
                            startActivity(
                                Intent(
                                    this@MainStoreProfileActivity,
                                    HomeActivity::class.java
                                )
                            )
                            finish()
                        }
                    } else {
                        openAlertDialog(
                            this,
                            resources.getString(R.string.dialogs_error),
                            singleStoreModel.message,
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

    private fun getStoreProducts(
        page: Int,
        random: Boolean? = null,
        storeId: Int? = null,
        storeCategoryId: Int? = null,
    ) {
        //showLoadingDialog(this, "message")
        storesProductsViewModel.getStoreProducts(
            "Bearer $token",
            language,
            page,
            random,
            storeId,
            storeCategoryId
        )
        storesProductsViewModel.storeProductsModelModel.observe(
            this,
            Observer(function = fun(storeProductsModel: StoreProductsModel?) {
                storeProductsModel?.let {
                    if (storeProductsModel.statusCode == 200) {
                        storeProductArrayList.clear()
                        storeProductsModel.data?.products?.let { it1 ->
                            if (it1.size > 5)
                                storeProductArrayList.addAll(
                                    it1.subList(0, 5)
                                )
                            else
                                storeProductArrayList.addAll(
                                    it1
                                )
                        }
                        storeProductsAdapter?.notifyDataSetChanged()
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

    private fun addStoreReview(addStoreReviewRequest: AddStoreReviewRequest) {
        //showLoadingDialog(this, "message")
        viewModel.addStoreReview("Bearer $token", language, addStoreReviewRequest)
        viewModel.createReviewModelModel.observe(
            this,
            Observer(function = fun(createReviewModel: CreateReviewModel?) {
                createReviewModel?.let {
                    if (createReviewModel.statusCode == 201) {

                        val user = User(
                            id = preferenceHelper.getKeyUserId(),
                            fullName = preferenceHelper.getUserFullName(),
                            phoneNo = preferenceHelper.getUserPhoneNumber(),
                            cityId = null,
                            imgAvatar = preferenceHelper.getUserImage()
                        )
                        createReviewModel.storeProductReview.user = user
                        storeProductReviewArrayList.add(createReviewModel.storeProductReview)
                        usersReviewsAdapter?.notifyDataSetChanged()
                        hideLoadingDialog()
                    } else {
                        openAlertDialog(
                            this,
                            resources.getString(R.string.dialogs_error),
                            createReviewModel.message,
                            false,
                            DISMISS,
                            this
                        )
                        //hideLoadingDialog()
                    }
                }

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
                    // hideLoadingDialog()
                }

            })
        )
    }

    private fun deleteStoreReview(reviewId: Int, position: Int, deleteUserReview: ImageView) {
        showLoadingDialog(this, "message")
        viewModel.deleteStoreReview("Bearer $token", language, reviewId)
        viewModel.baseResponseModel.observe(
            this,
            Observer(function = fun(baseResponse: BaseResponse?) {
                baseResponse?.let {
                    if (baseResponse.statusCode == 201) {
                        try {
                            storeProductReviewArrayList.removeAt(position)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        //binding.storeProfileCreateReviewCardViewId .setVisibility(View.VISIBLE);
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
        val intent = Intent(this@MainStoreProfileActivity, ProductDetailsActivity::class.java)
        intent.putExtra(SELECTED_PRODUCT_ID, productId.toString())
        startActivity(intent)
    }

    override fun onEditProductClickedCalledBack(position: Int, product: Product?) {}

    override fun onViewProductClickedCalledBack(position: Int, productId: Int) {}

    override fun onRemoveProductClickedCalledBack(position: Int, productId: Int) {}


    override fun onDeleteStoreReviewCallback(
        id: Int,
        position: Int,
        deleteUserReview: ImageView
    ) {
        deleteStoreReview(id, position, deleteUserReview)
    }

    override fun onDeleteProductReviewCallback(
        id: Int,
        position: Int,
        deleteUserReview: ImageView?
    ) {

    }


    override fun onBackPressed() {
        super.onBackPressed()
    }


    override fun onClose() {
    }

    override fun onConfirm(actionType: Int) {

    }

    override fun onNextPageRequired() {

    }

    override fun onSubmitStoreReviewCallback(addStoreReviewRequest: AddStoreReviewRequest) {
        addStoreReview(addStoreReviewRequest)
    }

    override fun onSubmitStoreProductReviewCallback(addStoreProductReviewRequest: AddStoreProductReviewRequest) {
    }

    override fun onSubmitAdReviewCallback(addAdReviewRequest: AddAdReviewRequest?) {
    }
}