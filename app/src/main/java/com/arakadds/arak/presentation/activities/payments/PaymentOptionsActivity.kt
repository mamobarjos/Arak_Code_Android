package com.arakadds.arak.presentation.activities.payments

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
import com.arakadds.arak.databinding.ActivityPaymentOptionsBinding
import com.arakadds.arak.model.CreateNewAdDataHolder
import com.arakadds.arak.model.CustomPackage
import com.arakadds.arak.model.new_mapping_refactore.BaseResponse
import com.arakadds.arak.model.new_mapping_refactore.request.ad.AdFile
import com.arakadds.arak.model.new_mapping_refactore.request.ad.AdRequest
import com.arakadds.arak.model.new_mapping_refactore.request.ad.CreateStoreAdRequest
import com.arakadds.arak.model.new_mapping_refactore.response.banners.create_ad.CreateNewAdModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.packages.AdPackages
import com.arakadds.arak.presentation.activities.ads.MyAdsActivity
import com.arakadds.arak.presentation.activities.other.BaseActivity
import com.arakadds.arak.presentation.activities.other.NoInternetConnectionActivity
import com.arakadds.arak.presentation.activities.payments.wallet.WalletPaymentActivity
import com.arakadds.arak.presentation.dialogs.AppDialogs
import com.arakadds.arak.presentation.dialogs.ApplicationDialogs
import com.arakadds.arak.presentation.viewmodel.ArakStoreViewModel
import com.arakadds.arak.presentation.viewmodel.CreateNewAdViewModel
import com.arakadds.arak.presentation.viewmodel.PaymentViewModel
import com.arakadds.arak.utils.AppEnums
import com.arakadds.arak.utils.AppEnums.PaymentEnums.CARD
import com.arakadds.arak.utils.AppProperties
import com.arakadds.arak.utils.Constants
import com.arakadds.arak.utils.Constants.CATEGORY_ID
import com.arakadds.arak.utils.Constants.NEW_AD_DATA
import com.arakadds.arak.utils.Constants.PACKAGE_TIME_LONG_ID
import com.arakadds.arak.utils.Constants.STORE_ID
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

class PaymentOptionsActivity : BaseActivity(), MediaUrl, ApplicationDialogs.AlertDialogCallbacks,
    AppDialogs.DialogCallBack {

    private val preferenceHelper: IPreferenceHelper by lazy { PreferenceManager(applicationContext) }
    lateinit var binding: ActivityPaymentOptionsBinding
    private lateinit var activityContext: Context

    private lateinit var toolbar: Toolbar
    private lateinit var pageTitle: TextView
    private var connectionLiveData: ConnectionLiveData? = null

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val createNewAdViewModel: CreateNewAdViewModel by viewModels {
        viewModelFactory
    }
    private val paymentViewModel: PaymentViewModel by viewModels {
        viewModelFactory
    }
    private lateinit var token: String
    private var createNewAdDataHolder = CreateNewAdDataHolder()
    private lateinit var categoryId: String
    private var adPackages = AdPackages()
    private lateinit var resources: Resources
    private lateinit var language: String
    var mediaUriList: ArrayList<Uri> = ArrayList()
    private val urlStringArrayList = ArrayList<AdFile>()
    private lateinit var dialog: ProgressDialog
    private var mStorageReference: StorageReference? = null
    private var databaseReference: DatabaseReference? = null
    var insufficientAlertDialog: AlertDialog? = null
    private var isDoublePayment = false
    val count = intArrayOf(0) // the number of images or videos that faild to upload to firebase

    private var videoThumbnail: String? = null
    private val viewModel: ArakStoreViewModel by viewModels {
        viewModelFactory
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connectionLiveData = ConnectionLiveData(this)
        binding = ActivityPaymentOptionsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AndroidInjection.inject(this)
        activityContext = this

        language = preferenceHelper.getLanguage()


        updatePaymentOptionView(language)
        initToolbar()
        checkNetworkConnection()
        initData()
        getIntentData()
        initUi()
        setListeners()
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
        pageTitle.text = resources.getString(R.string.payment_activity_check_out)
        backImageView.setOnClickListener { finish() }
    }

    private fun initUi() {
        mStorageReference = FirebaseStorage.getInstance().reference
        databaseReference = FirebaseDatabase.getInstance().reference
        dialog = ProgressDialog(this@PaymentOptionsActivity)
        dialog.setMessage(resources.getString(R.string.dialogs_Uploading_wait))
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
    }

    fun setListeners() {
        binding.paymentOptionsWalletLinearLayoutId.setOnClickListener {
            val intent = Intent(this@PaymentOptionsActivity, WalletPaymentActivity::class.java)
            intent.putExtra(NEW_AD_DATA, createNewAdDataHolder)
            intent.putExtra(CATEGORY_ID, categoryId)
            intent.putExtra(PACKAGE_TIME_LONG_ID, createNewAdDataHolder.duration)
            intent.putExtra(Constants.AD_PACKAGE_ID, adPackages)
            startActivity(intent)
        }
        binding.paymentOptionsCliqLinearLayoutId.setOnClickListener {
            val intent = Intent(this@PaymentOptionsActivity, CliqPaymentActivity::class.java)
            intent.putExtra(NEW_AD_DATA, createNewAdDataHolder)
            intent.putExtra(CATEGORY_ID, categoryId)
            intent.putExtra(PACKAGE_TIME_LONG_ID, createNewAdDataHolder.duration)
            intent.putExtra(Constants.AD_PACKAGE_ID, adPackages)
            startActivity(intent)
        }
        binding.paymentOptionsCardLinearLayoutId.setOnClickListener {
            if (categoryId.equals(AppEnums.AdsTypeCategories.STORES.toString())) {
                var createStoreAdRequest = CreateStoreAdRequest()
                createStoreAdRequest.storeId = preferenceHelper.getStoreId()
                createStoreAdRequest.paymentType = CARD
                createStoreAdRequest.adPackageId = adPackages.id
                createStoreAdRequest.adCategoryId = categoryId.toInt()
                createStoreAd(createStoreAdRequest)
            } else {
                if (adPackages.id != 0) {
                    mediaUriList.addAll(createNewAdDataHolder.uriList)
                    isDoublePayment = false
                    uploadImageToFirebase(mediaUriList)

                    /*HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("package_id", packageId);
                hashMap.put("type", "1");
                requestPaymentMethod(hashMap);*/
                } else {
                    Toast.makeText(
                        activityContext,
                        resources.getString(R.string.toast_please_try_again_later),
                        Toast.LENGTH_SHORT
                    ).show();
                }
            }
        }
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
                        //Toaster.show("your new product has been added successfully");
                        if (createNewAdModel.adData.checkoutUrl != null) {
                            if (createNewAdModel.adData
                                    .checkoutUrl == AppEnums.PaymentEnums.WALLET_INSUFFICIENT
                            ) {

                            } else {
                                preferenceHelper.setLat("non")
                                preferenceHelper.setLon("non")
                                val intent = Intent(
                                    this@PaymentOptionsActivity,
                                    PaymentWebActivity::class.java
                                )
                                intent.putExtra(CATEGORY_ID, categoryId)
                                intent.putExtra(Constants.AD_PACKAGE_ID, adPackages)
                                intent.putExtra(
                                    Constants.CHECK_OUT_URL_ID,
                                    createNewAdModel.adData
                                        .checkoutUrl
                                )
                                startActivity(intent)
                                //successDialog();
                                dialog.dismiss()
                                finish()
                            }
                        } else {
                            AppDialogs.successDialog(
                                this@PaymentOptionsActivity,
                                this,
                                resources,
                                MyAdsActivity::class.java,
                                resources.getString(R.string.dialogs_ad_post_soon),
                                resources.getString(R.string.dialogs_Go_My_Ads),
                                this
                            )
                        }
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

    private fun uploadImageToFirebase(mediaUriList: List<Uri>) {
        if (mediaUriList.isNotEmpty()) {
            dialog = ProgressDialog(this@PaymentOptionsActivity)
            dialog.setMessage(resources.getString(R.string.dialogs_loading_wait))
            dialog.show()
            val userId = preferenceHelper.getKeyUserId().toString()
            if (categoryId == "2") {
                if (createNewAdDataHolder.videoThumbnail != null) {
                    GlobalMethodsOldClass.addImagesToFirebase(
                        this@PaymentOptionsActivity,
                        createNewAdDataHolder.videoThumbnail,
                        userId,
                        dialog,
                        resources,
                        this
                    )
                } else {
                    if (mediaUriList.isNotEmpty()) //for loop if
                        GlobalMethodsOldClass.addVideoToFirebase(
                            this@PaymentOptionsActivity,
                            mediaUriList[0], userId, dialog, resources, this
                        )
                }
            } else {
                for (i in mediaUriList.indices) {
                    GlobalMethodsOldClass.addImagesToFirebase(
                        this@PaymentOptionsActivity,
                        mediaUriList[i], userId, dialog, resources, this
                    )
                } //for
            }
        } else {
            Toast.makeText(
                activityContext,
                resources.getString(R.string.toast_no_selected_Image),
                Toast.LENGTH_SHORT
            ).show();
        }
    }

    /*    private void addImagesListToFirebase(List<Uri> mediaUriList, int index, String userId) {
        StorageReference fileReference = mStorageReference.child(userId + "/images/" + UUID.randomUUID().toString() + "." + getFileExtension(mediaUriList.get(index)));
        Bitmap bmp = null;
        try {
            bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), mediaUriList.get(index));
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
        byte[] data = baos.toByteArray();
        fileReference.putBytes(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                //Toaster.show("uploaded successfully");
                                Task<Uri> downloadUrl = fileReference.getDownloadUrl();
                                urlStringArrayList.add(String.valueOf(uri));
                                ImageUploadModel imageUploadModel = new ImageUploadModel(String.valueOf(uri));
                                String uploadId = databaseReference.push().getKey();
                                //databaseReference.child(userId).child("Products").setValue(imageUploadModel);
                                //Log.d(TAG, "onSuccess: imageUploadModel: downloadUrl.getResult(): " + String.valueOf(uri.getResult()));
                                Log.d(TAG, "onSuccess: imageUploadModel: uri: " + String.valueOf(uri));
                                //urlArrayList.add(String.valueOf(downloadUrl.getResult()));
                                if (urlStringArrayList.size() == mediaUriList.size() - count[0]) {
                                    //Log.d(TAG, "onSuccess: true if called");
                                    //urlStringArrayList.addAll(imagesArrayList);
                                    sendNewProductDataToServer(urlStringArrayList, false);
                                }
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Toaster.show(e.getMessage());
                        Toaster.show(resources.getString(R.string.toast_Failed_upload_Image));
                        count[0]++;
                        dialog.dismiss();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        dialog.show();
                    }
                });
    }*/
    /*    private void addVideoToFirebase(List<Uri> mediaUriList, int index, String userId) {
        StorageReference fileReference = mStorageReference.child(userId + "/videos/" + UUID.randomUUID().toString() + "." + getFileExtension(mediaUriList.get(index)));

        fileReference.putFile(mediaUriList.get(index))
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                //Toaster.show("uploaded successfully");
                                Task<Uri> downloadUrl = fileReference.getDownloadUrl();
                                urlStringArrayList.add(String.valueOf(uri));
                                ImageUploadModel imageUploadModel = new ImageUploadModel(String.valueOf(uri));
                                String uploadId = databaseReference.push().getKey();
                                //databaseReference.child(userId).child("Products").setValue(imageUploadModel);
                                //Log.d(TAG, "onSuccess: imageUploadModel: downloadUrl.getResult(): " + String.valueOf(uri.getResult()));
                                Log.d(TAG, "onSuccess: imageUploadModel: uri: " + String.valueOf(uri));
                                //urlArrayList.add(String.valueOf(downloadUrl.getResult()));
                                if (urlStringArrayList.size() == mediaUriList.size() - count[0]) {
                                    //Log.d(TAG, "onSuccess: true if called");
                                    //urlStringArrayList.addAll(imagesArrayList);
                                    sendNewProductDataToServer(urlStringArrayList, false);
                                }
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Toaster.show(e.getMessage());
                        Toaster.show(resources.getString(R.string.toast_Failed_upload_Image));
                        count[0]++;
                        dialog.dismiss();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        dialog.show();
                    }
                });
    }*/
    private fun sendNewProductDataToServer(
        urlAdFileArrayList: ArrayList<AdFile>,
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
        adRequest.alternativePhoneNo = alternativePhoneNumber
        adRequest.adCategoryId = categoryId.toInt()
        adRequest.adPackageId = adPackages.id
        adRequest.duration = duration?.toInt() ?: 0
        adRequest.paymentType = CARD
        if (categoryId == "3") {
            adRequest.websiteUrl = websiteUrl
        }

        /*if (categoryId == "2") {
            adRequest.thumbUrl = videoThumbnail ?: ""
        }*/

        if (lat != null && lon != null) {
            adRequest.lon = lon
            adRequest.lat = lat
        }

        /*// 0 for wallet , 1 for credit , 2 for mix
        if (isDoublePayment) {
            dataHashMap["type"] = "2"
        } else {
            dataHashMap["type"] = "1"
        }
        if (isCompany) {
            val companyName = createNewAdDataHolder.companyName
            dataHashMap["company_name"] = companyName
        }*/
        adRequest.adFiles?.addAll(urlAdFileArrayList)
        uploadNewAdData(adRequest, urlAdFileArrayList)
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
                        if (createNewAdModel.adData.checkoutUrl != null) {
                            if (createNewAdModel.adData
                                    .checkoutUrl == AppEnums.PaymentEnums.WALLET_INSUFFICIENT
                            ) {
                                showInsufficientPopup()
                            } else {
                                preferenceHelper.setLat("non")
                                preferenceHelper.setLon("non")
                                val intent = Intent(
                                    this@PaymentOptionsActivity,
                                    PaymentWebActivity::class.java
                                )
                                intent.putExtra(NEW_AD_DATA, createNewAdDataHolder)
                                intent.putExtra(CATEGORY_ID, categoryId)
                                intent.putExtra(Constants.AD_PACKAGE_ID, adPackages)
                                val args = Bundle()
                                args.putSerializable(
                                    "ARRAYLIST",
                                    urlStringArrayList as Serializable
                                )
                                intent.putExtra(Constants.UPLOADED_MEDIA_ARRAY_LIST, args)
                                intent.putExtra(
                                    Constants.CHECK_OUT_URL_ID,
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
                                this@PaymentOptionsActivity,
                                this,
                                resources,
                                MyAdsActivity::class.java,
                                resources.getString(R.string.dialogs_ad_post_soon),
                                resources.getString(R.string.dialogs_Go_My_Ads),
                                this
                            )
                        }
                        hideLoadingDialog()
                        dialog.dismiss()
                    } else {
                        ApplicationDialogs.openAlertDialog(
                            this,
                            resources.getString(R.string.dialogs_error),
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
                        resources.getString(R.string.dialogs_error),
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

    private fun showInsufficientPopup() {
        insufficientAlertDialog = AlertDialog.Builder(this@PaymentOptionsActivity).create()
        insufficientAlertDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val inflater = insufficientAlertDialog!!.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_insufficient, null)
        val dialogTitleTextView =
            dialogView.findViewById<TextView>(R.id.insufficient_title_textView_id)
        val dialogDescTextView =
            dialogView.findViewById<TextView>(R.id.insufficient_desc_textView_id)
        val continueButton = dialogView.findViewById<Button>(R.id.insufficient_Button_id)
        dialogTitleTextView.text = resources.getString(R.string.payment_activity_Balance_Not_Enough)
        dialogDescTextView.text =
            resources.getString(R.string.payment_activity_continue_with_credit)
        continueButton.text = resources.getString(R.string.dialogs_Continue)
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

    private fun requestPaymentMethod(hashMap: HashMap<String, String>) {
        /*  showLoadingDialog(this, "message")
          paymentViewModel.requestPaymentMethod(token, hashMap)
          paymentViewModel.createNewAdResponseModel.observe(
              this,
              Observer(function = fun(createNewAdResponse: CreateNewAdResponse?) {
                  createNewAdResponse?.let {
                      if (createNewAdResponse.statusCode == 201) {
                          val intent =
                              Intent(this@PaymentOptionsActivity, PaymentWebActivity::class.java)
                          intent.putExtra(NEW_AD_DATA, createNewAdDataHolder)
                          intent.putExtra(CATEGORY_ID, categoryId)
                          intent.putExtra(PACKAGE_TIME_LONG_ID, numberOfImages)
                          intent.putExtra(PACKAGE_PRICE, packagePrice)
                          intent.putExtra(PACKAGE_ID, packageId)
                          intent.putExtra(PACKAGE_REACH_NUMBER_ID, reachNumber)
                          // intent.putExtra(CHECK_OUT_URL_ID, createNewAdResponse.userBalance)
                          intent.putExtra(PAYMENT_TYPE_ID, "1")
                          startActivity(intent)
                      } else {
                          openAlertDialog(
                              this,
                              resources?.getString(R.string.dialogs_error),
                              createNewAdResponse.message,
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

        paymentViewModel.throwableResponse.observe(
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
        )*/
    }

    private fun getIntentData() {
        val intent = intent
        try {
            createNewAdDataHolder = intent.getParcelableExtra(NEW_AD_DATA)!!
        } catch (e: Exception) {
            e.printStackTrace()
        }
        categoryId = intent.getStringExtra(CATEGORY_ID).toString()
        try {
            adPackages = intent.getParcelableExtra(Constants.AD_PACKAGE_ID)!!
        } catch (e: Exception) {
            e.printStackTrace()
        }
        //convertFromUriToBitmap(createNewAdDataHolder.getUriList());
    }

    private fun updatePaymentOptionView(language: String) {
        val context = LocaleHelper.setLocale(this@PaymentOptionsActivity, language)
        resources = context.resources
        binding.paymentOptionCardTextViewId.text =
            resources.getString(R.string.payment_activity_Card)
        binding.paymentOptionMyWalletTextViewId.text =
            resources.getString(R.string.payment_activity_My_Wallet)
        this.language = language
    }

    private fun showPaymentCombinedPopup() {
        insufficientAlertDialog = AlertDialog.Builder(this@PaymentOptionsActivity).create()
        insufficientAlertDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val inflater = insufficientAlertDialog!!.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_insufficient, null)
        val dialogTitleTextView =
            dialogView.findViewById<TextView>(R.id.insufficient_title_textView_id)
        val dialogDescTextView =
            dialogView.findViewById<TextView>(R.id.insufficient_desc_textView_id)
        val continueButton = dialogView.findViewById<Button>(R.id.insufficient_Button_id)
        dialogTitleTextView.text = resources.getString(R.string.dialogs_Payment_combined)
        dialogDescTextView.text = resources.getString(R.string.dialogs_Payment_combined_desc)
        continueButton.text = resources.getString(R.string.dialogs_Continue)
        continueButton.setOnClickListener {
            mediaUriList.addAll(createNewAdDataHolder.uriList)
            isDoublePayment = true
            uploadImageToFirebase(mediaUriList)
            insufficientAlertDialog!!.dismiss()
        }
        insufficientAlertDialog!!.setView(dialogView)
        insufficientAlertDialog!!.show()
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

    override fun sendUploadedMediaUrl(url: String) {
        if (categoryId == "2") {
            if (videoThumbnail == null) {
                videoThumbnail = url
            }
            val userId = preferenceHelper.getKeyUserId().toString()
            if (mediaUriList.size > 0) //for loop if
                GlobalMethodsOldClass.addVideoToFirebase(
                    this@PaymentOptionsActivity,
                    mediaUriList[0], userId, dialog, resources, this
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

    /*fun CheckoutCustomPackageData() {

        val params = HashMap<String, Any>()

        params["payment_type"]="CARD"
        params["custom_reach_id"]= NOOFReaches
        params["custom_img_id"]=NOOFIMAGES
        params["custom_second_id"]=NOOFSECONDS

        viewModel.CheckoutCustomPackageData(
            "Bearer $token",
            language,
            params
        )
        this?.let {
            viewModel.baseResponseCustomPackageModel.observe(
                it,
                Observer(function = fun(customPackage: CustomPackage?) {
                    customPackage?.let {
                        if (customPackage.statusCode == 200||customPackage.statusCode == 201) {

                            startActivity(Intent(this@PaymentOptionsActivity,PaymentWebActivity::class.java))

                        } else {
                            this?.let { it1 ->
                                ApplicationDialogs.openAlertDialog(
                                    it1,
                                    this.resources.getString(R.string.dialogs_error),
                                    customPackage.message,
                                    false,
                                    AppEnums.DialogActionTypes.DISMISS,
                                    this
                                )
                            }
                            // hideView(binding.loginProgressBarId*//*progressBar*//*)

                        }
                    }
                    hideLoadingDialog()
                })
            )
        }

        /*  this?.let {
              viewModel.throwableResponse.observe(
                  it,
                  Observer(function = fun(throwable: Throwable?) {
                      throwable?.let {
                          throwable?.let { it1 ->
                              ApplicationDialogs.openAlertDialog(
                                  this,
                                  this.resources.getString(R.string.dialogs_error),
                                  throwable.message,
                                  false,
                                  AppEnums.DialogActionTypes.DISMISS,
                                  this
                              )
                          }
                      }
                      hideLoadingDialog()
                  })
              )
          }*/
    }*/


    override fun onClose() {
    }

    override fun onConfirm(actionType: Int) {

    }

    override fun openCameraCallback() {

    }

    override fun openGalleryCallback() {
    }

    override fun continuePressedCallback() {
    }
}