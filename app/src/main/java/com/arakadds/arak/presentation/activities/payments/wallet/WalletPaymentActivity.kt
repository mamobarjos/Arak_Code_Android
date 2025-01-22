package com.arakadds.arak.presentation.activities.payments.wallet

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.arakadds.arak.R
import com.arakadds.arak.common.helper.LocaleHelper
import com.arakadds.arak.common.helper.connectivity_checker.ConnectionLiveData
import com.arakadds.arak.common.preferaence.IPreferenceHelper
import com.arakadds.arak.common.preferaence.PreferenceManager
import com.arakadds.arak.databinding.ActivityWalletPaymentBinding
import com.arakadds.arak.model.CreateNewAdDataHolder
import com.arakadds.arak.model.new_mapping_refactore.request.ad.AdFile
import com.arakadds.arak.model.new_mapping_refactore.request.ad.AdRequest
import com.arakadds.arak.model.new_mapping_refactore.request.ad.CreateStoreAdRequest
import com.arakadds.arak.model.new_mapping_refactore.response.banners.auth.user_balance.UserBalanceModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.create_ad.CreateNewAdModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.packages.AdPackages
import com.arakadds.arak.presentation.activities.ads.MyAdsActivity
import com.arakadds.arak.presentation.activities.other.BaseActivity
import com.arakadds.arak.presentation.activities.other.NoInternetConnectionActivity
import com.arakadds.arak.presentation.activities.payments.PaymentWebActivity
import com.arakadds.arak.presentation.dialogs.AppDialogs
import com.arakadds.arak.presentation.dialogs.ApplicationDialogs
import com.arakadds.arak.presentation.dialogs.ApplicationDialogs.openAlertDialog
import com.arakadds.arak.presentation.viewmodel.CreateNewAdViewModel
import com.arakadds.arak.presentation.viewmodel.WalletViewModel
import com.arakadds.arak.utils.AppEnums
import com.arakadds.arak.utils.AppEnums.AdsTypeCategories.IMAGE
import com.arakadds.arak.utils.AppEnums.AdsTypeCategories.VIDEO
import com.arakadds.arak.utils.AppEnums.AdsTypeCategories.WEBSITE
import com.arakadds.arak.utils.AppEnums.DialogActionTypes.CLOSE
import com.arakadds.arak.utils.AppEnums.DialogActionTypes.DISMISS
import com.arakadds.arak.utils.AppEnums.PaymentEnums.WALLET_INSUFFICIENT
import com.arakadds.arak.utils.AppProperties
import com.arakadds.arak.utils.Constants
import com.arakadds.arak.utils.Constants.AD_PACKAGE_ID
import com.arakadds.arak.utils.Constants.CATEGORY_ID
import com.arakadds.arak.utils.Constants.CHECK_OUT_URL_ID
import com.arakadds.arak.utils.Constants.NEW_AD_DATA
import com.arakadds.arak.utils.Constants.UPLOADED_MEDIA_ARRAY_LIST
import com.arakadds.arak.utils.GlobalMethodsOldClass
import com.arakadds.arak.utils.GlobalMethodsOldClass.MediaUrl
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.android.AndroidInjection
import io.paperdb.Paper
import java.io.Serializable
import javax.inject.Inject

class WalletPaymentActivity : BaseActivity(), MediaUrl, AppDialogs.DialogCallBack,
    ApplicationDialogs.AlertDialogCallbacks {

    private val preferenceHelper: IPreferenceHelper by lazy { PreferenceManager(applicationContext) }
    lateinit var binding: ActivityWalletPaymentBinding
    private lateinit var activityContext: Context

    private lateinit var toolbar: Toolbar
    private lateinit var pageTitle: TextView
    private var connectionLiveData: ConnectionLiveData? = null

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: WalletViewModel by viewModels {
        viewModelFactory
    }
    private val createNewAdViewModel: CreateNewAdViewModel by viewModels {
        viewModelFactory
    }

    private lateinit var token: String
    private var insufficientAlertDialog: AlertDialog? = null
    private lateinit var language: String
    private lateinit var activityResources: Resources
    private var createNewAdDataHolder = CreateNewAdDataHolder()
    private var categoryId: String? = null
    private var adPackages = AdPackages()
    private lateinit var dialog: ProgressDialog
    private var mediaUriList: ArrayList<Uri> = ArrayList()
    private var mStorageReference: StorageReference? = null
    private var databaseReference: DatabaseReference? = null
    private val urlStringArrayList = ArrayList<AdFile>()
    private var userBalance = 0f
    private var activity: Activity? = null
    private var videoThumbnail: String? = null

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connectionLiveData = ConnectionLiveData(this)
        binding = ActivityWalletPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AndroidInjection.inject(this)
        activityContext = this

        language = preferenceHelper.getLanguage()

        activity = this
        updateWalletPaymentView(language)
        initToolbar()
        initData()
        checkNetworkConnection()
        getIntentData()
        initViews()
        getUserBalance()
        initUi()
        setListeners()
    }

    private fun initToolbar() {
        toolbar = binding.appBarLayout.findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        pageTitle = binding.appBarLayout.findViewById(R.id.page_title)
        val backImageView: ImageView = binding.appBarLayout.findViewById(R.id.back_button)
        //shareImageView = binding.appBarLayout.findViewById(R.id.share_button)
        //favoriteImageView = binding.appBarLayout.findViewById(R.id.favorite_button)
        pageTitle.text = activityResources.getString(R.string.payment_activity_My_Wallet)
        backImageView.setOnClickListener { finish() }
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


    private fun updateWalletPaymentView(language: String) {
        val context = LocaleHelper.setLocale(this@WalletPaymentActivity, language)
        activityResources = context.resources
        binding.walletPayEarningTextViewId.text =
            activityResources.getString(R.string.wallet_frag_Earning)
        binding.walletPayAdTypeTitleTextViewId.text =
            activityResources.getString(R.string.payment_activity_Ads_Type)
        binding.walletPayAdReachTitleTextViewId.text =
            activityResources.getString(R.string.Category_Packages_activity_Reach)
        binding.walletPayNumberTitleTextViewId.text =
            activityResources.getString(R.string.payment_activity_Number)
        binding.walletPayTimeTitleTextViewId.text =
            activityResources.getString(R.string.payment_activity_Time)
        binding.walletPayCreditDetailsTextViewId.text =
            activityResources.getString(R.string.Summery_activity_cart_details)
        binding.walletPayTotalAmountHeaderTextViewId.text =
            activityResources.getString(R.string.Summery_activity_total_amount)
        this.language = language
    }

    private fun initUi() {
        mStorageReference = FirebaseStorage.getInstance().reference
        databaseReference = FirebaseDatabase.getInstance().reference
        dialog = ProgressDialog(this@WalletPaymentActivity)
        dialog.setMessage(activityResources!!.getString(R.string.dialogs_Uploading_wait))
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
    }

    fun setListeners() {
        binding.walletPayCheckOutButtonId.setOnClickListener { finish() }
        binding.walletPayCheckOutButtonId.setOnClickListener {
            if (categoryId == "4") {
                var createStoreAdRequest = CreateStoreAdRequest()
                createStoreAdRequest.storeId = preferenceHelper.getStoreId()
                createStoreAdRequest.paymentType = null
                createStoreAdRequest.adPackageId = adPackages.id
                createStoreAdRequest.adCategoryId = categoryId!!.toInt()
                createStoreAd(createStoreAdRequest)
            } else {
                mediaUriList.addAll(createNewAdDataHolder.uriList)
                uploadImageToFirebase(mediaUriList)
            }
        }
    }


    private fun uploadImageToFirebase(mediaUriList: List<Uri>) {
        if (mediaUriList.isNotEmpty()) {

            dialog = ProgressDialog(this@WalletPaymentActivity)
            dialog.setMessage(activityResources.getString(R.string.dialogs_loading_wait))
            dialog.show()
            val userId = preferenceHelper.getKeyUserId().toString()
            if (categoryId == "2") {
                if (createNewAdDataHolder.videoThumbnail != null) {
                    GlobalMethodsOldClass.addImagesToFirebase(
                        this@WalletPaymentActivity,
                        createNewAdDataHolder.videoThumbnail,
                        userId,
                        dialog,
                        activityResources,
                        this
                    )
                } else {
                    if (mediaUriList.isNotEmpty()) //for loop if
                        GlobalMethodsOldClass.addVideoToFirebase(
                            this@WalletPaymentActivity,
                            mediaUriList[0], userId, dialog, activityResources, this
                        )
                }
            } else {
                for (i in mediaUriList.indices) {
                    GlobalMethodsOldClass.addImagesToFirebase(
                        this@WalletPaymentActivity,
                        mediaUriList[i], userId, dialog, activityResources, this
                    )
                } //for
            }
        } else {
            Toast.makeText(
                activityContext,
                activityResources.getString(R.string.toast_no_selected_Image),
                Toast.LENGTH_SHORT
            ).show();

        }
    }

    private fun sendNewProductDataToServer(
        urlStringArrayList: ArrayList<AdFile>,
        isInsufficient: Boolean
    ) {
        val productName = createNewAdDataHolder.title
        val productDescription = createNewAdDataHolder.description
        val phoneNumber = preferenceHelper.getUserPhoneNumber()
        val alternativePhoneNumber = createNewAdDataHolder.phoneNumber
        val isCompany = createNewAdDataHolder.isCompany
        val websiteUrl = createNewAdDataHolder.websiteUrl
        val lon = createNewAdDataHolder.lon
        val lat = createNewAdDataHolder.lat
        val duration = createNewAdDataHolder.duration
        val adRequest = AdRequest()
        adRequest.title = productName
        adRequest.description = productDescription
        adRequest.phoneNo = phoneNumber
        adRequest.phoneNo = phoneNumber
        adRequest.alternativePhoneNo = alternativePhoneNumber
        adRequest.websiteUrl = websiteUrl
        adRequest.lon = lon
        adRequest.lat = lat
        adRequest.duration = duration.toInt()
        adRequest.adPackageId = adPackages.id
        adRequest.adCategoryId = categoryId?.toInt() ?: 0
        adRequest.paymentType = AppEnums.PaymentEnums.WALLET
        adRequest.adFiles = urlStringArrayList

        uploadNewAdData(adRequest, urlStringArrayList)
    }


    private fun showInsufficientPopup() {
        insufficientAlertDialog = AlertDialog.Builder(this@WalletPaymentActivity).create()
        insufficientAlertDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val inflater = insufficientAlertDialog!!.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_insufficient, null)
        val dialogTitleTextView =
            dialogView.findViewById<TextView>(R.id.insufficient_title_textView_id)
        val dialogDescTextView =
            dialogView.findViewById<TextView>(R.id.insufficient_desc_textView_id)
        val continueButton = dialogView.findViewById<Button>(R.id.insufficient_Button_id)
        dialogTitleTextView.text =
            activityResources!!.getString(R.string.payment_activity_Balance_Not_Enough)
        dialogDescTextView.text =
            activityResources!!.getString(R.string.payment_activity_continue_with_credit)
        continueButton.text = activityResources!!.getString(R.string.dialogs_Continue)
        continueButton.setOnClickListener {
            sendNewProductDataToServer(urlStringArrayList, true)
            insufficientAlertDialog!!.dismiss()
        }
        insufficientAlertDialog!!.setView(dialogView)
        insufficientAlertDialog!!.show()
    }

    private fun getFileExtension(uri: Uri): String? {
        val contentResolver = contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(contentResolver.getType(uri))
    }

    private fun getIntentData() {
        val intent = intent
        createNewAdDataHolder = intent.getParcelableExtra(NEW_AD_DATA)!!
        categoryId = intent.getStringExtra(CATEGORY_ID)
        adPackages = intent.getParcelableExtra(AD_PACKAGE_ID)!!
    }

    private fun initViews() {
        when (categoryId?.toInt()) {
            IMAGE -> {
                binding.walletPayAdTypeBodyTextViewId.text =
                    activityResources.getString(R.string.Category_Packages_activity_Image_Ads)
            }

            VIDEO -> {
                binding.walletPayAdTypeBodyTextViewId.text =
                    activityResources.getString(R.string.Category_Packages_activity_Video_Ads)
            }

            WEBSITE -> {
                binding.walletPayAdTypeBodyTextViewId.text =
                    activityResources.getString(R.string.Category_Packages_activity_Website_Ads)
            }
        }
        binding.walletPayReachBodyTextViewId.text = adPackages.reach.toString()
        binding.walletPayTimeBodyTextViewId.text = adPackages.seconds.toString()
        binding.walletPayNumberBodyTextViewId.text =
            if (categoryId == "2") activityResources.getString(R.string.payment_activity_Video) else activityResources!!.getString(
                R.string.Category_Packages_activity_Image
            ) + " " + adPackages.numberOfImages
        binding.walletPayTotalAmountBodyTextViewId.text =
            preferenceHelper.getCurrencySymbol() + " " + adPackages.price
        binding.walletPayCheckOutButtonId.text =
            preferenceHelper.getCurrencySymbol() + " " + adPackages.price
        //binding.walletPayNumberBodyTextViewId.setText();
    }

    private fun uploadNewAdData(
        adRequest: AdRequest,
        urlStringArrayList: ArrayList<AdFile>
    ) {
        showLoadingDialog(this, "message")
        createNewAdViewModel.createNewAd(language, "Bearer $token", adRequest)
        createNewAdViewModel.createNewAdModelModel.observe(
            this,
            Observer(function = fun(createNewAdModel: CreateNewAdModel?) {
                createNewAdModel?.let {
                    if (createNewAdModel.statusCode == 201) {
                        //Toaster.show("your new product has been added successfully");
                        if (createNewAdModel.adData
                                .checkoutUrl != null
                        ) {
                            if (createNewAdModel.adData
                                    .checkoutUrl == WALLET_INSUFFICIENT
                            ) {
                                showInsufficientPopup()
                            } else {
                                preferenceHelper.setLat("non")
                                preferenceHelper.setLon("non")
                                val intent = Intent(
                                    this@WalletPaymentActivity,
                                    PaymentWebActivity::class.java
                                )
                                intent.putExtra(NEW_AD_DATA, createNewAdDataHolder)
                                intent.putExtra(CATEGORY_ID, categoryId)
                                intent.putExtra(AD_PACKAGE_ID, adPackages)

                                val args = Bundle()
                                args.putSerializable(
                                    "ARRAYLIST",
                                    urlStringArrayList as Serializable
                                )
                                intent.putExtra(UPLOADED_MEDIA_ARRAY_LIST, args)
                                intent.putExtra(
                                    CHECK_OUT_URL_ID,
                                    createNewAdModel.adData
                                        .checkoutUrl
                                )
                                startActivity(intent)
                                //successDialog();
                                dialog!!.dismiss()
                                finish()
                            }
                        } else {
                            AppDialogs.successDialog(
                                this@WalletPaymentActivity,
                                activity,
                                activityResources,
                                MyAdsActivity::class.java,
                                activityResources.getString(R.string.dialogs_ad_post_soon),
                                activityResources.getString(R.string.dialogs_Go_My_Ads),
                                this
                            )
                        }
                        hideLoadingDialog()
                        dialog.dismiss()
                    } else {
                        openAlertDialog(
                            this,
                            activityResources.getString(R.string.dialogs_error),
                            createNewAdModel.message,
                            false,
                            CLOSE,
                            this
                        )
                        dialog!!.dismiss()
                        hideLoadingDialog()
                    }
                }
                hideLoadingDialog()
            })
        )

        createNewAdViewModel.throwableResponse.observe(
            this,
            Observer(function = fun(throwable: Throwable?) {
                throwable?.let {
                    openAlertDialog(
                        this,
                        activityResources?.getString(R.string.dialogs_error),
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

    private fun getUserBalance() {
        showLoadingDialog(this, "message")
        viewModel.getUserBalance("Bearer $token", language)
        viewModel.userBalanceModelModel.observe(
            this,
            Observer(function = fun(userBalanceModel: UserBalanceModel?) {
                userBalanceModel?.let {
                    if (userBalanceModel.statusCode == 200) {
                        userBalance = userBalanceModel.userBalanceData.balance
                        binding.walletPayWalletBalanceTextViewId.text =
                            userBalanceModel.userBalanceData.balance.toString() + "" + preferenceHelper.getCurrencySymbol()
                        hideLoadingDialog()
                    } else {
                        openAlertDialog(
                            this,
                            activityResources?.getString(R.string.dialogs_error),
                            userBalanceModel.message,
                            false,
                            DISMISS,
                            this
                        )
                        hideLoadingDialog()
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
                        activityResources?.getString(R.string.dialogs_error),
                        throwable.message,
                        false,
                        DISMISS,
                        this
                    )
                    hideLoadingDialog()
                }
                hideLoadingDialog()
            })
        )
    }

    private fun createStoreAd(
        createStoreAdRequest: CreateStoreAdRequest
    ) {
        showLoadingDialog(this, "message")
        createNewAdViewModel.createStoreAd(language, "Bearer $token", createStoreAdRequest)
        createNewAdViewModel.createNewAdModelModel.observe(
            this,
            Observer(function = fun(createNewAdModel: CreateNewAdModel?) {
                createNewAdModel?.let {
                    if (createNewAdModel.statusCode == 201) {
                        AppDialogs.successDialog(
                            this@WalletPaymentActivity,
                            this,
                            resources,
                            MyAdsActivity::class.java,
                            resources.getString(R.string.dialogs_ad_post_soon),
                            resources.getString(R.string.dialogs_Go_My_Ads),
                            this
                        )
                        hideLoadingDialog()
                        dialog.dismiss()
                    } else {
                        ApplicationDialogs.openAlertDialog(
                            this,
                            activityContext.getString(R.string.dialogs_error),
                            createNewAdModel.message,
                            false,
                            AppEnums.DialogActionTypes.DISMISS,
                            this
                        )
                        hideLoadingDialog()
                    }
                }
                hideLoadingDialog()
            })
        )

        createNewAdViewModel.throwableResponse.observe(
            this,
            Observer(function = fun(throwable: Throwable?) {
                throwable?.let {
                    ApplicationDialogs.openAlertDialog(
                        this,
                        activityContext.getString(R.string.dialogs_error),
                        throwable.message,
                        false,
                        AppEnums.DialogActionTypes.DISMISS,
                        this
                    )
                }
                hideLoadingDialog()
            })
        )
    }

    override fun onPause() {
        super.onPause()
        try {
            dialog!!.dismiss()
            insufficientAlertDialog!!.dismiss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun sendUploadedMediaUrl(url: String) {
        if (categoryId?.toInt() == VIDEO) {
            if (videoThumbnail == null) {
                videoThumbnail = url
            }
            val userId = preferenceHelper.getKeyUserId().toString()
            if (mediaUriList.isNotEmpty()) //for loop if
                GlobalMethodsOldClass.addVideoToFirebase(
                    this@WalletPaymentActivity,
                    mediaUriList[0], userId, dialog, activityResources, this
                )
        } else {

            urlStringArrayList.add(AdFile(url))
            if (urlStringArrayList.size > 0 && mediaUriList.size == urlStringArrayList.size) {
                sendNewProductDataToServer(urlStringArrayList, false)
            }
        }
    }

    override fun sendUploadedVideoMediaUrl(url: String?) {
        urlStringArrayList.add(AdFile(url))
        sendNewProductDataToServer(urlStringArrayList, false)
    }

    override fun openCameraCallback() {}

    override fun openGalleryCallback() {}

    override fun continuePressedCallback() {
        finish()
    }

    fun onResponseFailed() {}

    override fun onClose() {
    }

    override fun onConfirm(actionType: Int) {
        when (actionType) {
            CLOSE -> {
                finish()
            }
        }
    }
}