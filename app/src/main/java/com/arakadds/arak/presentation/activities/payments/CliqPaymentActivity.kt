package com.arakadds.arak.presentation.activities.payments

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
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
import com.arakadds.arak.R
import com.arakadds.arak.common.helper.LocaleHelper
import com.arakadds.arak.common.helper.connectivity_checker.ConnectionLiveData
import com.arakadds.arak.common.preferaence.IPreferenceHelper
import com.arakadds.arak.common.preferaence.PreferenceManager
import com.arakadds.arak.databinding.ActivityCliqBinding
import com.arakadds.arak.model.CreateNewAdDataHolder
import com.arakadds.arak.model.new_mapping_refactore.BaseResponse
import com.arakadds.arak.model.new_mapping_refactore.request.ad.AdFile
import com.arakadds.arak.model.new_mapping_refactore.request.ad.AdRequest
import com.arakadds.arak.model.new_mapping_refactore.request.ad.CreateStoreAdRequest
import com.arakadds.arak.model.new_mapping_refactore.request.payment.CreateCliqPaymentRequest
import com.arakadds.arak.model.new_mapping_refactore.response.banners.create_ad.CreateNewAdModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.packages.AdPackages
import com.arakadds.arak.presentation.activities.ads.MyAdsActivity
import com.arakadds.arak.presentation.activities.other.BaseActivity
import com.arakadds.arak.presentation.activities.other.NoInternetConnectionActivity
import com.arakadds.arak.presentation.dialogs.AppDialogs
import com.arakadds.arak.presentation.dialogs.ApplicationDialogs
import com.arakadds.arak.presentation.dialogs.ApplicationDialogs.openAlertDialog
import com.arakadds.arak.presentation.viewmodel.CreateNewAdViewModel
import com.arakadds.arak.presentation.viewmodel.PaymentViewModel
import com.arakadds.arak.presentation.viewmodel.WalletViewModel
import com.arakadds.arak.utils.AppEnums.AdsTypeCategories.IMAGE
import com.arakadds.arak.utils.AppEnums.AdsTypeCategories.VIDEO
import com.arakadds.arak.utils.AppEnums.DialogActionTypes.DISMISS
import com.arakadds.arak.utils.Constants.AD_PACKAGE_ID
import com.arakadds.arak.utils.Constants.CATEGORY_ID
import com.arakadds.arak.utils.Constants.NEW_AD_DATA
import com.arakadds.arak.utils.Constants.PICK_IMAGE_GALLERY
import com.arakadds.arak.utils.Constants.SELECT_IMAGE_PERMISSIONS_CODE
import com.arakadds.arak.utils.GlobalMethodsOldClass
import com.arakadds.arak.utils.GlobalMethodsOldClass.MediaUrl
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference
import dagger.android.AndroidInjection
import java.io.File
import java.io.FileNotFoundException
import javax.inject.Inject

class CliqPaymentActivity : BaseActivity(), MediaUrl, AppDialogs.DialogCallBack,
    ApplicationDialogs.AlertDialogCallbacks {

    private val preferenceHelper: IPreferenceHelper by lazy { PreferenceManager(applicationContext) }
    lateinit var binding: ActivityCliqBinding
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

    private val paymentViewModel: PaymentViewModel by viewModels {
        viewModelFactory
    }

    private lateinit var token: String
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
    var bitmapArrayList = ArrayList<Bitmap>()
    private var userBalance = 0f
    private var activity: Activity? = null
    private var videoThumbnail: String? = null
    private var attachmentImage = ""
    private var amount: String = ""
    private var isCliqAttachment: Boolean = false

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connectionLiveData = ConnectionLiveData(this)
        binding = ActivityCliqBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AndroidInjection.inject(this)
        activityContext = this

        language = preferenceHelper.getLanguage()

        activity = this
        updateCliqView(language)
        checkNetworkConnection()
        initToolbar()
        initData()
        initUi()
        getIntentData()
        initViews()
        setListeners()
    }

    private fun initToolbar() {
        toolbar = binding.appBarLayout.findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        pageTitle = binding.appBarLayout.findViewById(R.id.page_title)
        val backImageView: ImageView = binding.appBarLayout.findViewById(R.id.back_button)
        //shareImageView = binding.appBarLayout.findViewById(R.id.share_button)
        //favoriteImageView = binding.appBarLayout.findViewById(R.id.favorite_button)
        pageTitle.text = activityResources.getString(R.string.cliq_activity_title)
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

    override fun onActivityResult(reqCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(reqCode, resultCode, data)
        if (resultCode == RESULT_OK && reqCode == PICK_IMAGE_GALLERY && data != null && data.data != null) {
            try {
                val imageUri = data.data
                val file = File(imageUri!!.path)
                val imageStream = contentResolver.openInputStream(imageUri)
                val selectedImage = BitmapFactory.decodeStream(imageStream)
                if (selectedImage != null) {
                    //imageUri.getPath();
                    attachmentImage = imageUri.toString()
                    mediaUriList.add(imageUri)
                    bitmapArrayList.add(selectedImage)
                    binding.cliqAlisAttachmentImageViewId.setImageURI(null)
                    binding.cliqAlisAttachmentImageViewId.setImageBitmap(selectedImage)
                    binding.cliqAlisAttachmentImageViewId.visibility = View.VISIBLE
                    binding.deleteUploadedImageViewId.visibility = View.VISIBLE
                    binding.cliqAddAttachmentLinearLayoutId.visibility = View.GONE
                }
                //image_view.setImageBitmap(selectedImage);
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                Toast.makeText(
                    activityContext,
                    activityResources.getString(R.string.toast_please_try_again_later),
                    Toast.LENGTH_SHORT
                ).show();
            }
        } else {
            Toast.makeText(
                activityContext,
                activityResources.getString(R.string.toast_havent_picked_Image),
                Toast.LENGTH_SHORT
            ).show();
        }
    }

    private fun updateCliqView(language: String) {
        val context = LocaleHelper.setLocale(this@CliqPaymentActivity, language)
        activityResources = context.resources

        binding.cliqDescTextViewId.text =
            activityResources.getString(R.string.cliq_activity_desc)
        binding.cliqCurrancyTextViewId.text =
            preferenceHelper.getCurrencySymbol()
        binding.cliqTotalAmountCurrancyTextViewId.text =
            preferenceHelper.getCurrencySymbol()
        binding.cliqTotalAmountLabelTextViewId.text =
            activityResources.getString(R.string.Summery_activity_total_amount)
        binding.cliqAmountEditTextId.hint =
            activityResources.getString(R.string.withdraw_activity_Amount)
        binding.cliqAddAttachmentTextViewId.text =
            activityResources.getString(R.string.cliq_activity_add_attachment)
        binding.cliqAlisSaveButtonId.text =
            activityResources.getString(R.string.cliq_activity_save)
        this.language = language
    }

    private fun initUi() {
        dialog = ProgressDialog(this@CliqPaymentActivity)
        dialog.setMessage(activityResources!!.getString(R.string.dialogs_Uploading_wait))
    }

    private fun copyToClipboard(text: String) {
        val clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(
            activityResources!!.getString(R.string.cliq_activity_copied_Text),
            text
        )
        clipboardManager.setPrimaryClip(clip)
        Toast.makeText(
            this,
            resources.getString(R.string.cliq_activity_text_copied),
            Toast.LENGTH_SHORT
        ).show()
    }

    fun setListeners() {

        binding.deleteUploadedImageViewId.setOnClickListener {
            handleDeletingMedia()
        }


        binding.cliqCopyAlisNameImageViewId.setOnClickListener {
            val textToCopy: String = binding.cliqAlisTextViewId.text.toString().trim { it <= ' ' }
            if (!textToCopy.isEmpty()) {
                copyToClipboard(textToCopy)
            } else {
                Toast.makeText(
                    this,
                    resources.getString(R.string.cliq_activity_no_text_to_copied),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        binding.cliqAddAttachmentLinearLayoutId.setOnClickListener {
            try {
                if (ActivityCompat.checkSelfPermission(
                        this@CliqPaymentActivity,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this@CliqPaymentActivity,
                        arrayOf<String>(Manifest.permission.READ_EXTERNAL_STORAGE),
                        SELECT_IMAGE_PERMISSIONS_CODE
                    )
                }
                if (ContextCompat.checkSelfPermission(
                        this@CliqPaymentActivity,
                        Manifest.permission.CAMERA
                    )
                    != PackageManager.PERMISSION_DENIED
                ) {
                    ActivityCompat.requestPermissions(
                        this@CliqPaymentActivity,
                        arrayOf<String>(Manifest.permission.CAMERA),
                        1
                    )
                }

                //addImageDialog(CreateStoreActivity.this, this);
                openGallery()
            } catch (e: Exception) {
                Log.d(
                    "CliqPaymentActivity",
                    "onClick: Exception: " + e.message
                )
            }
        }

        binding.cliqAlisSaveButtonId.setOnClickListener {
            amount = binding.cliqAmountEditTextId.text.toString().trim { it <= ' ' }
            if (amount.trim { it <= ' ' }.isNotEmpty() && attachmentImage.trim { it <= ' ' }
                    .isNotEmpty()) {
                binding.cliqAlisSaveButtonId.isEnabled = false
                isCliqAttachment = true
                uploadImageToFirebase(mediaUriList)
            } else {
                Toast.makeText(
                    this,
                    resources.getString(R.string.toast_required_information),
                    Toast.LENGTH_SHORT
                ).show()
                binding.cliqAlisSaveButtonId.isEnabled = true
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.action = Intent.ACTION_PICK
        startActivityForResult(
            Intent.createChooser(
                intent,
                resources.getString(R.string.dialogs_Select_Picture)
            ), PICK_IMAGE_GALLERY
        )
    }

    private fun uploadImageToFirebase(mediaUriList: List<Uri>) {
        if (mediaUriList.isNotEmpty()) {

            dialog = ProgressDialog(this@CliqPaymentActivity)
            dialog.setMessage(activityResources.getString(R.string.dialogs_loading_wait))
            dialog.show()
            val userId = preferenceHelper.getKeyUserId().toString()
            if (isCliqAttachment) {
                GlobalMethodsOldClass.addImagesToFirebase(
                    this@CliqPaymentActivity,
                    mediaUriList[0], userId, dialog, activityResources, this
                )
            } else {
                if (categoryId?.toInt() == VIDEO) {
                    /*if (createNewAdDataHolder.videoThumbnail != null) {
                        GlobalMethodsOldClass.addImagesToFirebase(
                            this@CliqPaymentActivity,
                            createNewAdDataHolder.videoThumbnail,
                            userId,
                            dialog,
                            activityResources,
                            this
                        )
                    } else {
                        if (mediaUriList.isNotEmpty()) //for loop if
                            GlobalMethodsOldClass.addVideoToFirebase(
                                this@CliqPaymentActivity,
                                mediaUriList[0], userId, dialog, activityResources, this
                            )
                    }*/
                    if (mediaUriList.isNotEmpty()) //for loop if
                        GlobalMethodsOldClass.addVideoToFirebase(
                            this@CliqPaymentActivity,
                            mediaUriList[0], userId, dialog, activityResources, this
                        )
                } else {
                    for (i in mediaUriList.indices) {
                        GlobalMethodsOldClass.addImagesToFirebase(
                            this@CliqPaymentActivity,
                            mediaUriList[i], userId, dialog, activityResources, this
                        )
                    } //for
                }
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
        adRequest.paymentType = null
        adRequest.adFiles = urlStringArrayList
        uploadNewAdData(adRequest, urlStringArrayList)
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
        binding.cliqTotalAmountTextViewId.text = adPackages.price.toString()
        //numberBodyTextView.setText();
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
                        AppDialogs.successDialog(
                            this@CliqPaymentActivity,
                            activity,
                            activityResources,
                            MyAdsActivity::class.java,
                            activityResources.getString(R.string.dialogs_ad_post_soon),
                            activityResources.getString(R.string.dialogs_Go_My_Ads),
                            this
                        )
                        hideLoadingDialog()
                        dialog.dismiss()
                    } else {
                        openAlertDialog(
                            this,
                            activityResources.getString(R.string.dialogs_error),
                            createNewAdModel.message,
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
                            this@CliqPaymentActivity,
                            activity,
                            activityResources,
                            MyAdsActivity::class.java,
                            activityResources.getString(R.string.dialogs_ad_post_soon),
                            activityResources.getString(R.string.dialogs_Go_My_Ads),
                            this
                        )
                        hideLoadingDialog()
                        dialog.dismiss()
                    } else {
                        openAlertDialog(
                            this,
                            activityResources.getString(R.string.dialogs_error),
                            createNewAdModel.message,
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

    private fun sendCliqPayment(
        createCliqPaymentRequest: CreateCliqPaymentRequest
    ) {
        showLoadingDialog(this, "message")
        paymentViewModel.createCliqPayment(token, language, createCliqPaymentRequest)
        paymentViewModel.baseResponseModel.observe(
            this,
            Observer(function = fun(baseResponse: BaseResponse?) {
                baseResponse?.let {
                    if (baseResponse.statusCode == 201) {
                        if (categoryId == "4") {
                            var createStoreAdRequest = CreateStoreAdRequest()
                            createStoreAdRequest.storeId = preferenceHelper.getStoreId()
                            createStoreAdRequest.paymentType = null
                            createStoreAdRequest.adPackageId = adPackages.id
                            createStoreAdRequest.adCategoryId = categoryId!!.toInt()
                            createStoreAd(createStoreAdRequest)
                        } else {
                            mediaUriList.clear()
                            mediaUriList.addAll(createNewAdDataHolder.uriList)
                            uploadImageToFirebase(mediaUriList)
                        }

                    } else {
                        binding.cliqAlisSaveButtonId.isEnabled = true
                        openAlertDialog(
                            this,
                            activityResources.getString(R.string.dialogs_error),
                            baseResponse.message,
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

    override fun sendUploadedMediaUrl(url: String) {
        if (isCliqAttachment) {
            isCliqAttachment = false
            sendCliqPayment(CreateCliqPaymentRequest(amount, url))
        } else {
            urlStringArrayList.add(AdFile(url))
            attachmentImage = url
            if (urlStringArrayList.size > 0 && mediaUriList.size == urlStringArrayList.size) {
                sendNewProductDataToServer(urlStringArrayList, false)
            }
        }
    }

    fun handleDeletingMedia() {
        urlStringArrayList.clear()
        bitmapArrayList.clear()
        binding.cliqAlisAttachmentImageViewId.setImageURI(null)
        binding.cliqAlisAttachmentImageViewId.setImageBitmap(null)
        attachmentImage = ""
        binding.cliqAlisAttachmentImageViewId.visibility = View.GONE
        binding.deleteUploadedImageViewId.visibility = View.GONE
        binding.cliqAddAttachmentLinearLayoutId.visibility = View.VISIBLE
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

    }
}