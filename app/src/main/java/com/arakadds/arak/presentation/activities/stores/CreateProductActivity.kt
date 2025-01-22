package com.arakadds.arak.presentation.activities.stores

import android.Manifest
import android.app.ProgressDialog
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
import androidx.recyclerview.widget.RecyclerView
import com.arakadds.arak.R
import com.arakadds.arak.common.helper.LocaleHelper
import com.arakadds.arak.common.helper.connectivity_checker.ConnectionLiveData
import com.arakadds.arak.common.preferaence.IPreferenceHelper
import com.arakadds.arak.common.preferaence.PreferenceManager
import com.arakadds.arak.databinding.ActivityCreateProductBinding
import com.arakadds.arak.model.new_mapping_refactore.BaseResponse
import com.arakadds.arak.model.new_mapping_refactore.request.StoreProductFileRequest
import com.arakadds.arak.model.new_mapping_refactore.request.create_store_product.CreateProductRequestBody
import com.arakadds.arak.model.new_mapping_refactore.store.SingleProductModel
import com.arakadds.arak.model.new_mapping_refactore.store.products.Product
import com.arakadds.arak.model.new_mapping_refactore.store.products.ProductFile
import com.arakadds.arak.presentation.activities.other.BaseActivity
import com.arakadds.arak.presentation.activities.other.NoInternetConnectionActivity
import com.arakadds.arak.presentation.adapters.SelectedImagesAdapter
import com.arakadds.arak.presentation.adapters.SelectedImagesAdapter.CallbackDeleteMedia
import com.arakadds.arak.presentation.adapters.SelectedImagesAdapter.CallbackInterface
import com.arakadds.arak.presentation.dialogs.AppDialogs
import com.arakadds.arak.presentation.dialogs.ApplicationDialogs
import com.arakadds.arak.presentation.viewmodel.StoresProductsViewModel
import com.arakadds.arak.utils.AppEnums
import com.arakadds.arak.utils.AppEnums.AdsTypeCategories.IMAGE
import com.arakadds.arak.utils.AppEnums.AdsTypeCategories.VIDEO
import com.arakadds.arak.utils.AppEnums.AdsTypeCategories.WEBSITE
import com.arakadds.arak.utils.AppProperties
import com.arakadds.arak.utils.Constants
import com.arakadds.arak.utils.Constants.IS_EDIT_ID
import com.arakadds.arak.utils.Constants.RESULT_LOAD_IMG
import com.arakadds.arak.utils.Constants.RESULT_LOAD_VIDEO
import com.arakadds.arak.utils.Constants.SELECTED_PRODUCT_OBJECT_ID
import com.arakadds.arak.utils.GlobalMethodsOldClass
import com.arakadds.arak.utils.GlobalMethodsOldClass.MediaUrl
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.android.AndroidInjection
import io.paperdb.Paper
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.MultipartBody.Part.Companion.createFormData
import okhttp3.RequestBody
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import javax.inject.Inject

class CreateProductActivity : BaseActivity(), CallbackInterface, CallbackDeleteMedia, MediaUrl,
    AppDialogs.DialogCallBack, ApplicationDialogs.AlertDialogCallbacks {

    private val preferenceHelper: IPreferenceHelper by lazy { PreferenceManager(applicationContext) }
    lateinit var binding: ActivityCreateProductBinding
    private lateinit var activityContext: Context

    private lateinit var toolbar: Toolbar
    private lateinit var pageTitle: TextView
    private var connectionLiveData: ConnectionLiveData? = null

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: StoresProductsViewModel by viewModels {
        viewModelFactory
    }

    private lateinit var uploadedImagesRecyclerView: RecyclerView

    var bitmapArrayList = ArrayList<Bitmap>()
    private var selectedImagesAdapter: SelectedImagesAdapter? = null
    var hashMap = HashMap<String, RequestBody>()
    private var mediaUriList: ArrayList<Uri> = ArrayList()
    var currentPhotoPath: String? = null
    private var mStorageReference: StorageReference? = null
    private var databaseReference: DatabaseReference? = null
    private lateinit var uploadBytes: ByteArray
    var progress = 0.0
    var postIdArrayList = ArrayList<String>()
    private val imagesNumber = "5"
    private var dialog: ProgressDialog? = null
    private val urlStringArrayList = ArrayList<StoreProductFileRequest>()

    // private lateinit var createProductHashMap: HashMap<String, Any>
    private var createProductRequestBody = CreateProductRequestBody()

    private var product: Product? = null
    private var isEdit = false
    private val adFilesArrayList = ArrayList<ProductFile>()
    private var productId = 0

    private lateinit var token: String
    private lateinit var resources: Resources
    private var aboutLabel: String? = null

    private var language = "en"
    private var storeId = ""

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connectionLiveData = ConnectionLiveData(this)
        binding = ActivityCreateProductBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AndroidInjection.inject(this)
        activityContext = this

        language = preferenceHelper.getLanguage()
        getIntentData()
        updateCreateProductView(language)
        initToolbar()
        checkNetworkConnection()
        initUi()
        initRecyclerView()
        setListeners()
    }


    private fun initToolbar() {
        toolbar = binding.appBarLayout.findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        pageTitle = binding.appBarLayout.findViewById(R.id.page_title)
        val backImageView: ImageView = binding.appBarLayout.findViewById(R.id.back_button)
        //shareImageView = binding.appBarLayout.findViewById(R.id.share_button)
        //favoriteImageView = binding.appBarLayout.findViewById(R.id.favorite_button)
        pageTitle.text = resources.getString(R.string.Create_New_product_activity_Create_product)
        backImageView.setOnClickListener { finish() }
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

    private fun updateCreateProductView(language: String) {
        val context = LocaleHelper.setLocale(this@CreateProductActivity, language)
        resources = context.resources
        binding.createProductProductNameEditTextId.hint =
            resources.getString(R.string.Create_New_product_activity_product_name)
        binding.createProductDescriptionEditTextId.hint =
            resources.getString(R.string.Create_New_Ad_activity_Description)
        binding.createProductPriceEditTextId.hint =
            resources.getString(R.string.Create_New_product_activity_Price)
        binding.productDiscountEdittext.hint =
            resources.getString(R.string.Create_New_product_activity_Discount_value)
        binding.createProductCreateButtonId.text =
            resources.getString(R.string.Create_New_product_activity_create)
        binding.createProductCurrancyTextViewId.text = preferenceHelper.getCurrencySymbol()
        this.language = language
    }

    private fun initUi() {
        mStorageReference = FirebaseStorage.getInstance().reference
        databaseReference = FirebaseDatabase.getInstance().reference
        dialog = ProgressDialog(this@CreateProductActivity)
        dialog!!.setMessage(resources.getString(R.string.dialogs_Uploading_wait))
        dialog!!.setCancelable(false)
        dialog!!.setCanceledOnTouchOutside(false)
    }

    private fun initRecyclerView() {
        uploadedImagesRecyclerView =
            findViewById<RecyclerView>(R.id.create_product_upload_image_recyclerView_id)
        val mLayoutManager =
            LinearLayoutManager(this@CreateProductActivity, LinearLayoutManager.HORIZONTAL, false)
        uploadedImagesRecyclerView.layoutManager = mLayoutManager
        selectedImagesAdapter = SelectedImagesAdapter(
            null,
            bitmapArrayList,
            this@CreateProductActivity,
            "1",
            resources,
            this
        )
        uploadedImagesRecyclerView.adapter = selectedImagesAdapter
    }

    private fun getIntentData() {
        token = preferenceHelper.getToken()
        val intent = intent
        product =
            intent.getParcelableExtra<Product>(SELECTED_PRODUCT_OBJECT_ID)
        isEdit = intent.getBooleanExtra(IS_EDIT_ID, false)
        product?.let { setDataToViews(it) }

        try {
            storeId = intent.getStringExtra("StoreID").toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (token == "non" || token.trim().isEmpty()) {
            GlobalMethodsOldClass.askGuestLogin(resources, activityContext)
            return
        }
    }

    private fun setDataToViews(product: Product) {
        productId = product.id
        binding.createProductProductNameEditTextId.setText(product.name)
        binding.createProductDescriptionEditTextId.setText(product.description)
        binding.createProductPriceEditTextId.setText(product.price.toString())
        if (product.storeProductFiles != null) {
            adFilesArrayList.addAll(product.storeProductFiles)
        }
    }

    private fun prepareFilePart(partName: String, fileUri: Uri): MultipartBody.Part {
        val file = File(fileUri.path)
        try {
            if (!file.exists()) {
                file.mkdirs()
                file.createNewFile()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file.name)
        return createFormData(partName, file.name, requestFile)
    }

    fun setListeners() {
        binding.newAdUploadImageViewId.setOnClickListener {
            handelMediaSelection()
        }
        binding.createProductCreateButtonId.setOnClickListener(View.OnClickListener {
            val productName: String = binding.createProductProductNameEditTextId.text.toString()
            val description: String = binding.createProductDescriptionEditTextId.text.toString()
            val price: String = binding.createProductPriceEditTextId.text.toString()
            val salePrice = binding.productDiscountEdittext.text.toString()
            if (productName.trim { it <= ' ' } != ""
                && description.trim { it <= ' ' } != "") {
                if (mediaUriList.size == 0) {
                    Toast.makeText(
                        activityContext,
                        resources.getString(R.string.toast_add_media_post),
                        Toast.LENGTH_SHORT
                    ).show();
                    return@OnClickListener
                }

                if (price.trim().isNotEmpty()) {
                    createProductRequestBody.price = price.toDouble()
                } else {
                    binding.createProductPriceEditTextId.requestFocus()
                    binding.createProductPriceEditTextId.error =
                        getString(R.string.toast_Insert_product_price)
                    return@OnClickListener
                }


                createProductRequestBody.name = productName
                createProductRequestBody.description = description

                if (salePrice.trim().isNotEmpty() && salePrice.toDouble() > 0) {
                    createProductRequestBody.salePrice = salePrice.toDouble()
                } else {
                    createProductRequestBody.salePrice = 0.0
                }

                createProductRequestBody.storeId = storeId.toInt()

                uploadImageToFirebase(mediaUriList)
            } else {
                Toast.makeText(
                    activityContext,
                    resources.getString(R.string.toast_fill_out_fields),
                    Toast.LENGTH_SHORT
                ).show();
            }
        })
    }

    private fun handelMediaSelection() {


        try {
            if (ActivityCompat.checkSelfPermission(
                    this@CreateProductActivity,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this@CreateProductActivity,
                    arrayOf<String>(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.SELECT_IMAGE_PERMISSIONS_CODE
                )
            }
            if (ContextCompat.checkSelfPermission(
                    this@CreateProductActivity,
                    Manifest.permission.CAMERA
                )
                != PackageManager.PERMISSION_DENIED
            ) {
                ActivityCompat.requestPermissions(
                    this@CreateProductActivity,
                    arrayOf(Manifest.permission.CAMERA),
                    1
                )
                return
            }


            openGallery()


        } catch (e: java.lang.Exception) {
            Log.d(
                "CreateStoreActivity.TAG",
                "onClick: Exception: " + e.message
            )
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
            ), RESULT_LOAD_IMG
        )
    }

    override fun onActivityResult(reqCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(reqCode, resultCode, data)
        if (resultCode == RESULT_OK && reqCode == RESULT_LOAD_IMG && data != null && data.data != null) {
            try {
                val imageUri = data.data
                val file = File(imageUri!!.path)
                val imageStream = contentResolver.openInputStream(imageUri)
                val selectedImage = BitmapFactory.decodeStream(imageStream)
                if (selectedImage != null) {
                    if (bitmapArrayList == null) {
                        bitmapArrayList = java.util.ArrayList()
                    }

                    //imageUri.getPath();
                    mediaUriList.add(imageUri)
                    bitmapArrayList.add(selectedImage)

                    uploadedImagesRecyclerView.visibility = View.VISIBLE
                    if (selectedImagesAdapter == null) {
                        selectedImagesAdapter = SelectedImagesAdapter(
                            null,
                            bitmapArrayList,
                            this@CreateProductActivity,
                            "1",
                            resources,
                            this
                        )

                        uploadedImagesRecyclerView.adapter = selectedImagesAdapter
                    } else {
                        selectedImagesAdapter!!.notifyDataSetChanged()
                    }
                }
                //image_view.setImageBitmap(selectedImage);
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                Toast.makeText(
                    activityContext,
                    resources.getString(R.string.toast_please_try_again_later),
                    Toast.LENGTH_SHORT
                ).show();
            }
        } else {
            Toast.makeText(
                activityContext,
                resources.getString(R.string.toast_havent_picked_Image),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun createProduct(createProductRequestBody: CreateProductRequestBody) {
        showLoadingDialog(this, "message")

        viewModel.createProduct("Bearer $token", language, createProductRequestBody)
        viewModel.singleProductModelModel.observe(
            this,
            Observer(function = fun(singleProductModel: SingleProductModel?) {
                singleProductModel?.let {
                    if (singleProductModel.statusCode == 201) {
                        AppDialogs.successDialog(
                            this@CreateProductActivity,
                            null,
                            resources,
                            StoreProfileActivity::class.java,
                            resources.getString(R.string.dialogs_Product_created_successfully),
                            resources.getString(R.string.dialogs_Go_My_Products),
                            this
                        )
                        dialog!!.dismiss()
                        hideLoadingDialog()
                    } else {
                        ApplicationDialogs.openAlertDialog(
                            this,
                            resources?.getString(R.string.dialogs_error),
                            singleProductModel.message,
                            false,
                            AppEnums.DialogActionTypes.DISMISS,
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
                    ApplicationDialogs.openAlertDialog(
                        this,
                        resources?.getString(R.string.dialogs_error),
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

    private fun updateProduct(id: Int, createProductRequestBody: CreateProductRequestBody) {
        showLoadingDialog(this, "message")
        viewModel.updateProduct(token, language, id, createProductRequestBody)
        viewModel.baseResponseModel.observe(
            this,
            Observer(function = fun(baseResponse: BaseResponse?) {
                baseResponse?.let {
                    if (baseResponse.statusCode == 200 || baseResponse.statusCode == 201) {
                        hideLoadingDialog()
                        finish()
                    } else {
                        ApplicationDialogs.openAlertDialog(
                            this,
                            resources.getString(R.string.dialogs_error),
                            baseResponse.message,
                            false,
                            AppEnums.DialogActionTypes.DISMISS,
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
                    ApplicationDialogs.openAlertDialog(
                        this,
                        resources?.getString(R.string.dialogs_error),
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
            showLoadingDialog(this, "message")
            val userId = preferenceHelper.getKeyUserId().toString()
            for (i in mediaUriList.indices) {
                GlobalMethodsOldClass.addImagesToFirebase(
                    this@CreateProductActivity,
                    mediaUriList[i], userId, dialog, resources, this
                )
            }
        } else {
            Toast.makeText(
                activityContext,
                resources.getString(R.string.toast_no_selected_Image),
                Toast.LENGTH_SHORT
            ).show();
        }
    }

    override fun onHandleSelection(position: Int, intent: Intent?, resultCode: Int) {
        startActivityForResult(intent!!, resultCode)
    }

    override fun onHandleDeletingMedia(position: Int, isImage: Boolean) {
        bitmapArrayList.removeAt(position)

        mediaUriList.removeAt(position)
    }
    override fun openCameraCallback() {

    }
    override fun openGalleryCallback() {

    }
    override fun continuePressedCallback() {
        finish()
    }
    override fun sendUploadedMediaUrl(url: String) {
        hideLoadingDialog()
        val storeProductsResponse = StoreProductFileRequest(url)
        urlStringArrayList.add(storeProductsResponse)
        if (urlStringArrayList.size == mediaUriList.size) {
            if (createProductRequestBody.storeProductFiles == null)
                createProductRequestBody.storeProductFiles = ArrayList<StoreProductFileRequest>()
            createProductRequestBody.storeProductFiles?.add(storeProductsResponse)
            if (isEdit) {
                updateProduct(productId, createProductRequestBody)
            } else {
                createProduct(createProductRequestBody)
            }
        }
    }
    override fun sendUploadedVideoMediaUrl(url: String?) {
    }
    override fun onClose() {

    }
    override fun onConfirm(actionType: Int) {

    }
}