package com.arakadds.arak.presentation.activities.ads

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.URLUtil.isValidUrl
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
import com.arakadds.arak.common.helper.connectivity_checker.ConnectionLiveData
import com.arakadds.arak.common.preferaence.IPreferenceHelper
import com.arakadds.arak.common.preferaence.PreferenceManager
import com.arakadds.arak.databinding.ActivityAdDetailsUserViewBinding
import com.arakadds.arak.model.new_mapping_refactore.BaseResponse
import com.arakadds.arak.model.new_mapping_refactore.request.AddAdReviewRequest
import com.arakadds.arak.model.new_mapping_refactore.request.AddStoreProductReviewRequest
import com.arakadds.arak.model.new_mapping_refactore.request.AddStoreReviewRequest
import com.arakadds.arak.model.new_mapping_refactore.response.banners.ads.AdResponseData
import com.arakadds.arak.model.new_mapping_refactore.response.banners.ads.AdReviewsModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.ads.ad_reviews.AdReviews
import com.arakadds.arak.model.new_mapping_refactore.response.banners.ads.ad_reviews.CreateAdReviewModel
import com.arakadds.arak.model.new_mapping_refactore.store.StoreProductFile
import com.arakadds.arak.presentation.activities.other.BaseActivity
import com.arakadds.arak.presentation.activities.other.NoInternetConnectionActivity
import com.arakadds.arak.presentation.activities.other.ReviewsActivity
import com.arakadds.arak.presentation.activities.other.WebViewActivity
import com.arakadds.arak.presentation.adapters.NewAdSummeryAdapter
import com.arakadds.arak.presentation.adapters.UsersAdsReviewsAdapter
import com.arakadds.arak.presentation.dialogs.AppDialogs
import com.arakadds.arak.presentation.dialogs.ApplicationDialogs
import com.arakadds.arak.presentation.dialogs.ApplicationDialogs.openAlertDialog
import com.arakadds.arak.presentation.viewmodel.AdDetailsViewModel
import com.arakadds.arak.presentation.viewmodel.AdsReviewsModel
import com.arakadds.arak.presentation.viewmodel.FavoriteAdViewModel
import com.arakadds.arak.utils.AppEnums.DialogActionTypes.DELETE
import com.arakadds.arak.utils.AppEnums.DialogActionTypes.DISMISS
import com.arakadds.arak.utils.AppProperties
import com.arakadds.arak.utils.Constants
import com.arakadds.arak.utils.Constants.AD_ID
import com.arakadds.arak.utils.Constants.CATEGORY_ID
import com.arakadds.arak.utils.Constants.MY_PERMISSIONS_REQUEST_CALL_PHONE
import com.arakadds.arak.utils.GlobalMethodsOldClass
import dagger.android.AndroidInjection
import io.paperdb.Paper
import javax.inject.Inject

class AdDetailUserViewActivity : BaseActivity(), ApplicationDialogs.AlertDialogCallbacks,
    UsersAdsReviewsAdapter.ReviewsEvents, AppDialogs.DialogStoreReviewCallBack {

    private val preferenceHelper: IPreferenceHelper by lazy { PreferenceManager(applicationContext) }
    private lateinit var binding: ActivityAdDetailsUserViewBinding
    private lateinit var activityContext: Context

    private lateinit var toolbar: Toolbar
    private lateinit var pageTitle: TextView
    private lateinit var favoriteImageView: ImageView
    private var connectionLiveData: ConnectionLiveData? = null

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val adDetailsViewModel: AdDetailsViewModel by viewModels {
        viewModelFactory
    }
    private val favoriteAdViewModel: FavoriteAdViewModel by viewModels {
        viewModelFactory
    }

    private val adsReviewsModel: AdsReviewsModel by viewModels {
        viewModelFactory
    }
    private lateinit var token: String
    private var newAdSummeryAdapter: NewAdSummeryAdapter? = null
    private lateinit var id: String
    private var categoryId: String? = null
    private lateinit var adFilesList: ArrayList<StoreProductFile>
    private var lat = ""
    private var lon: String? = ""
    private lateinit var language: String
    private lateinit var activityResources: Resources
    lateinit var phoneNumber: String
    private var isFavourite = false
    private lateinit var webUrl: String
    private lateinit var usersAdsReviewsAdapter: UsersAdsReviewsAdapter
    private val adReviewsArrayList = ArrayList<AdReviews>()
    private var selectedAdReviewId: Int = 0
    private var selectedAdReviewPosition: Int = 0
    // Add this property
    private var title: String = ""
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connectionLiveData = ConnectionLiveData(this)
        binding = ActivityAdDetailsUserViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AndroidInjection.inject(this)
        activityContext = this

        language = preferenceHelper.getLanguage()

        updateAdDetailView(language)
        checkNetworkConnection()
        initToolbar()
        initData()
        setListeners()
        getSelectedAdDetails()
        getAdReviews(id)
    }

    private fun setAdReviewsAdapter() {
        // Check if list is empty
        if (adReviewsArrayList.isEmpty()) {
            binding.adDetailsUserViewAdsReviewsRecyclerViewId.visibility = View.GONE
            binding.adDetailsUserViewViewAllTextViewId.visibility = View.GONE
        } else {
            binding.adDetailsUserViewAdsReviewsRecyclerViewId.visibility = View.VISIBLE
            binding.adDetailsUserViewViewAllTextViewId.visibility = View.VISIBLE
        }

        usersAdsReviewsAdapter = UsersAdsReviewsAdapter(
            adReviewsArrayList,
            preferenceHelper.getKeyUserId(),
            this@AdDetailUserViewActivity,
            resources,
            this,
            false
        )
        binding.adDetailsUserViewAdsReviewsRecyclerViewId.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            isFocusable = false
            isNestedScrollingEnabled = false
            adapter = usersAdsReviewsAdapter
        }
    }

    private fun setListeners() {

        binding.adDetailsUserViewWhatsAppImageViewId.setOnClickListener {
            val url = "https://api.whatsapp.com/send?phone=$phoneNumber"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }

        binding.adDetailsUserViewPhoneCallImageViewId.setOnClickListener {
            val number = "tel:$phoneNumber"
            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse(number)
            // Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(
                    this@AdDetailUserViewActivity,
                    Manifest.permission.CALL_PHONE
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this@AdDetailUserViewActivity,
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

        binding.adDetailsUserViewLocationImageViewId.setOnClickListener {
            if (lat != null && lon != null && lat != "" && lon != "") {

                val gmmIntentUri =
                    Uri.parse("google.navigation:q=$lat,$lon")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                startActivity(mapIntent)
            } else {
                Toast.makeText(
                    activityContext,
                    activityResources.getString(R.string.toast_sorry_advertiser_doset_address),
                    Toast.LENGTH_SHORT
                ).show();
            }
        }

        binding.adDetailsUserViewWebsiteBodyTextViewId.setOnClickListener {
            webUrl =
                binding.adDetailsUserViewWebsiteBodyTextViewId.text.toString().trim { it <= ' ' }
            if (webUrl != null) {
                if (isValidUrl(webUrl)) {
                    val intent = Intent(this@AdDetailUserViewActivity, WebViewActivity::class.java)
                    intent.putExtra("web_Url", webUrl)
                    intent.putExtra(
                        "page_title",
                        binding.adDetailsUserViewTitleTextTextViewId.text.toString()
                    )
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        activityContext,
                        activityResources.getString(R.string.toast_website_not_valid),
                        Toast.LENGTH_SHORT
                    ).show();
                }
            }
        }

        binding.adDetailsUserViewViewAllTextViewId.setOnClickListener {
            try {
                val intent = Intent(this, ReviewsActivity::class.java).apply {
                    putExtra(Constants.IS_NORMAL_AD, true)
                    putExtra(AD_ID, id)
                    putExtra(Constants.AD_NAME, title)
                    // Create a new ArrayList to avoid concurrent modification
                    putParcelableArrayListExtra(Constants.REVIEWS, ArrayList(adReviewsArrayList))
                }
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, getString(R.string.dialogs_error), Toast.LENGTH_SHORT).show()
            }
        }

        binding.adDetailsUserViewAddReviewLinearLayoutId.setOnClickListener {
            AppDialogs.addStoreReviewDialog(
                this@AdDetailUserViewActivity,
                resources,
                null,
                null,
                id.toInt(),
                this
            )
        }
    }

    private fun initData() {
        id = intent.getStringExtra(AD_ID).toString()
        categoryId = intent.getStringExtra(CATEGORY_ID)
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
        favoriteImageView = binding.appBarLayout.findViewById(R.id.favorite_button)
        favoriteImageView.visibility = View.VISIBLE
        //pageTitle.text = resources.getText(R.string.notifications_activity_title_label)
        favoriteImageView.setOnClickListener { favoriteAdAction(id) }
        backImageView.setOnClickListener { finish() }
    }

    private fun updateAdDetailView(language: String) {
        val context = LocaleHelper.setLocale(this@AdDetailUserViewActivity, language)
        activityResources = context.resources

        binding.adDetailsUserViewReviewTitleTextViewId.text =
            activityResources.getString(R.string.store_profile_activity_Store_Reviews)
        binding.adDetailsUserViewViewAllTextViewId.text =
            activityResources.getString(R.string.Create_store_activity_View_All)
        binding.adDetailsUserViewFeedbackTitleTextViewId.text =
            activityResources.getString(R.string.store_profile_activity_Add_Review)

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

    private fun getSelectedAdDetails() {
        showLoadingDialog(this, "message")
        adDetailsViewModel.getSingleAdDetails(language, "Bearer $token", id)
        adDetailsViewModel.adDataModel.observe(
            this,
            Observer(function = fun(adResponseData: AdResponseData?) {
                adResponseData?.let {
                    if (adResponseData.statusCode == 200) {

                        adFilesList = adResponseData.adData.adFiles
                        id= adResponseData.adData.id.toString()
                        title= adResponseData.adData.title.toString()
                        setDataToView(
                            adResponseData.adData.title!!,
                            adResponseData.adData.description!!,
                            adResponseData.adData.websiteURL
                        )
                        lat = adResponseData.adData.lat.toString()
                        lon = adResponseData.adData.lon
                        isFavourite = adResponseData.adData.isFavorite
                        if (isFavourite) {
                            favoriteImageView.setImageResource(R.drawable.favorite_ad_icon)
                        } else {
                            favoriteImageView.setImageResource(R.drawable.favorite_icon)
                        }
                        phoneNumber = adResponseData.adData.phoneNumber!!
                        newAdSummeryAdapter = if (categoryId == "2") {
                            NewAdSummeryAdapter(
                                adFilesList,
                                this@AdDetailUserViewActivity,
                                categoryId,
                                true,
                                true,
                                "1"
                            )

                        } else {
                            NewAdSummeryAdapter(
                                adFilesList,
                                this@AdDetailUserViewActivity,
                                categoryId,
                                false,
                                false,
                                "1"
                            )
                        }
                        val mLayoutManager = LinearLayoutManager(
                            this@AdDetailUserViewActivity,
                            LinearLayoutManager.HORIZONTAL,
                            false
                        )
                        binding.adDetailsUserViewRecyclerViewId!!.layoutManager = mLayoutManager
                        binding.adDetailsUserViewRecyclerViewId.adapter = newAdSummeryAdapter

                        if (adFilesList.size > 0) {
                            binding.adDetailsNumberImagesTextViewId.visibility = View.VISIBLE
                            binding.adDetailsNumberImagesTextViewId.text =
                                adFilesList.size.toString()
                        }
                        if (adResponseData.adData.isReviewed) {
                            binding.adDetailsUserViewAddReviewLinearLayoutId.visibility =
                                View.GONE
                        } else {
                            binding.adDetailsUserViewAddReviewLinearLayoutId.visibility =
                                View.VISIBLE
                        }
                    } else {
                        openAlertDialog(
                            this,
                            activityResources.getString(R.string.dialogs_error),
                            adResponseData.message,
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

        adDetailsViewModel.throwableResponse.observe(
            this,
            Observer(function = fun(throwable: Throwable?) {
                throwable?.let {
                    openAlertDialog(
                        this,
                        activityResources.getString(R.string.dialogs_error),
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

    private fun setDataToView(
        title: String,
        desc: String,
        website: String?
    ) {
        binding.adDetailsUserViewTitleTextTextViewId.text = title
        binding.adDetailsUserViewDescriptionBodyTextViewId.text = desc
        pageTitle.text = title

        if (website == null) {
            binding.adDetailsUserViewWebsiteBodyTextViewId.visibility = View.GONE
        } else {
            binding.adDetailsUserViewWebsiteBodyTextViewId.visibility = View.VISIBLE
            binding.adDetailsUserViewWebsiteBodyTextViewId.text = website
        }
    }

    private fun favoriteAdAction(id: String) {
        showLoadingDialog(this, "message")
        favoriteAdViewModel.favoriteAdAction("Bearer $token", language, id)
        favoriteAdViewModel.baseResponseModel.observe(
            this,
            Observer(function = fun(baseResponse: BaseResponse?) {
                baseResponse?.let {
                    if (baseResponse.statusCode == 201) {

                        if (isFavourite) {
                            isFavourite = false
                            favoriteImageView.setImageResource(R.drawable.favorite_icon)
                        } else {
                            isFavourite = true
                            favoriteImageView.setImageResource(R.drawable.favorite_ad_icon)
                        }
                    } else {
                        openAlertDialog(
                            this,
                            activityResources.getString(R.string.dialogs_error),
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

        favoriteAdViewModel.throwableResponse.observe(
            this,
            Observer(function = fun(throwable: Throwable?) {
                throwable?.let {
                    openAlertDialog(
                        this,
                        activityResources.getString(R.string.dialogs_error),
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

    private fun getAdReviews(adId: String) {
        //showLoadingDialog(this, "message")
        adsReviewsModel.getAdReviews("Bearer $token", language, adId)
        adsReviewsModel.adReviewsModelModel.observe(
            this,
            Observer(function = fun(adReviewsModel: AdReviewsModel?) {
                adReviewsModel?.let {
                    if (adReviewsModel.statusCode == 200) {
                        adReviewsArrayList.clear()
                        adReviewsArrayList.addAll(adReviewsModel.adReviewsdata.storeProductReviewArrayList)
                        setAdReviewsAdapter()
                    } else {
                        openAlertDialog(
                            this,
                            resources?.getString(R.string.dialogs_error),
                            adReviewsModel.message,
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

        adsReviewsModel.throwableResponse.observe(
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

    private fun createAdReview(addAdReviewRequest: AddAdReviewRequest) {
        showLoadingDialog(this, getString(R.string.dialogs_loading_wait))

        adsReviewsModel.createAdReview("Bearer $token", language, addAdReviewRequest)
        adsReviewsModel.createAdReviewModelModel.observe(this) { createAdReviewModel ->
            hideLoadingDialog()

            createAdReviewModel?.let {
                if (createAdReviewModel.statusCode == 201) {
                    try {
                        adReviewsArrayList.add(0, createAdReviewModel.storeProductReview) // Add at the beginning
                        binding.adDetailsUserViewAdsReviewsRecyclerViewId.visibility = View.VISIBLE
                        binding.adDetailsUserViewViewAllTextViewId.visibility = View.VISIBLE
                        usersAdsReviewsAdapter.notifyDataSetChanged()
                        binding.adDetailsUserViewAddReviewLinearLayoutId.visibility = View.GONE

                        // Show success message
                        Toast.makeText(this, getString(R.string.dialogs_Success), Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(this, getString(R.string.dialogs_error), Toast.LENGTH_SHORT).show()
                    }
                } else {
                    openAlertDialog(
                        this,
                        getString(R.string.dialogs_error),
                        createAdReviewModel.message,
                        false,
                        DISMISS,
                        this
                    )
                }
            }
        }
    }



    private fun deleteAdReview(adReviewId: Int) {
        showLoadingDialog(this, getString(R.string.dialogs_loading_wait))

        adsReviewsModel.deleteAdReview("Bearer $token", language, adReviewId)
        adsReviewsModel.baseResponseModel.observe(this) { baseResponse ->
            hideLoadingDialog()

            baseResponse?.let {
                if (baseResponse.statusCode == 200) {
                    try {
                        adReviewsArrayList.removeAt(selectedAdReviewPosition)
                        usersAdsReviewsAdapter.notifyDataSetChanged()

                        // Show/hide views based on list state
                        if (adReviewsArrayList.isEmpty()) {
                            binding.adDetailsUserViewAdsReviewsRecyclerViewId.visibility = View.GONE
                            binding.adDetailsUserViewViewAllTextViewId.visibility = View.GONE
                        }

                        binding.adDetailsUserViewAddReviewLinearLayoutId.visibility = View.VISIBLE
                        Toast.makeText(this, getString(R.string.dialogs_Success), Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(this, getString(R.string.dialogs_error), Toast.LENGTH_SHORT).show()
                    }
                } else {
                    openAlertDialog(
                        this,
                        getString(R.string.dialogs_error),
                        baseResponse.message,
                        false,
                        DISMISS,
                        this
                    )
                }
            }
        }
    }
    override fun onClose() {
    }

    override fun onConfirm(actionType: Int) {

        when (actionType) {
            DELETE -> {
                deleteAdReview(adReviewsArrayList[selectedAdReviewPosition].id)
            }
        }
    }

    override fun onDeleteStoreReviewCallback(id: Int, position: Int, deleteUserReview: ImageView?) {

    }

    override fun onDeleteProductReviewCallback(
        id: Int,
        position: Int,
        deleteUserReview: ImageView?
    ) {
        binding.adDetailsUserViewAddReviewLinearLayoutId.visibility =
            View.VISIBLE
    }

    override fun onDeleteAdReviewCallback(
        id: Int,
        position: Int,
        deleteUserReview: ImageView?
    ) {
        selectedAdReviewId = id
        selectedAdReviewPosition = position


        openAlertDialog(
            this,
            resources.getString(R.string.dialogs_Delete_entry),
            resources.getString(R.string.dialogs_delete_this_entry),
            true,
            DELETE,
            this
        )
        /**/
    }

    override fun onSubmitStoreReviewCallback(addStoreReviewRequest: AddStoreReviewRequest?) {

    }

    override fun onSubmitStoreProductReviewCallback(addStoreProductReviewRequest: AddStoreProductReviewRequest?) {

    }

    override fun onSubmitAdReviewCallback(addAdReviewRequest: AddAdReviewRequest) {
        if (token.isBlank() || token == "non") {
            GlobalMethodsOldClass.askGuestLogin(resources, this)
            return
        }
        createAdReview(addAdReviewRequest)
    }
}