package com.arakadds.arak.presentation.activities.stores

import android.Manifest
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.arakadds.arak.R
import com.arakadds.arak.common.helper.ActivityHelper
import com.arakadds.arak.common.helper.LocaleHelper
import com.arakadds.arak.common.helper.TaskHelper
import com.arakadds.arak.common.helper.TaskHelper.TaskHelperCallBack
import com.arakadds.arak.common.helper.connectivity_checker.ConnectionLiveData
import com.arakadds.arak.common.preferaence.IPreferenceHelper
import com.arakadds.arak.common.preferaence.PreferenceManager
import com.arakadds.arak.databinding.ActivityCreateStoreBinding
import com.arakadds.arak.model.new_mapping_refactore.request.store.CreateStoreRequestBody
import com.arakadds.arak.model.new_mapping_refactore.response.banners.cities.CitiesModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.cities.CityResponseData
import com.arakadds.arak.model.new_mapping_refactore.response.banners.countries.CountriesModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.countries.CountriesResponseData
import com.arakadds.arak.model.new_mapping_refactore.store.CreateStoreModel
import com.arakadds.arak.model.new_mapping_refactore.store.StoreCategoriesModel
import com.arakadds.arak.model.new_mapping_refactore.store.StoreObject
import com.arakadds.arak.model.new_mapping_refactore.store.categories.StoreCategory
import com.arakadds.arak.presentation.activities.map.MapActivity
import com.arakadds.arak.presentation.activities.other.BaseActivity
import com.arakadds.arak.presentation.dialogs.AppDialogs
import com.arakadds.arak.presentation.dialogs.AppDialogs.selectSocialMediaDialog
import com.arakadds.arak.presentation.dialogs.ApplicationDialogs
import com.arakadds.arak.presentation.viewmodel.CreateAccountViewModel
import com.arakadds.arak.presentation.viewmodel.CreateStoreViewModel
import com.arakadds.arak.presentation.viewmodel.StoresViewModel
import com.arakadds.arak.utils.AppEnums
import com.arakadds.arak.utils.AppEnums.FirebaseEnums.STORE_PROFILE_IMAGE
import com.arakadds.arak.utils.AppEnums.SocialMediaPlatforms.FACEBOOK
import com.arakadds.arak.utils.AppEnums.SocialMediaPlatforms.INSTAGRAM
import com.arakadds.arak.utils.AppEnums.SocialMediaPlatforms.WEBSITE
import com.arakadds.arak.utils.AppEnums.SocialMediaPlatforms.WHATS_UP
import com.arakadds.arak.utils.AppEnums.SocialMediaPlatforms.YOUTUBE
import com.arakadds.arak.utils.AppProperties
import com.arakadds.arak.utils.Constants
import com.arakadds.arak.utils.Constants.ERROR_DIALOG_REQUEST
import com.arakadds.arak.utils.Constants.IS_EDIT_ID
import com.arakadds.arak.utils.Constants.PICK_IMAGE_CAMERA
import com.arakadds.arak.utils.Constants.PICK_IMAGE_GALLERY
import com.arakadds.arak.utils.Constants.SELECT_IMAGE_PERMISSIONS_CODE
import com.arakadds.arak.utils.Constants.STORE_ID
import com.arakadds.arak.utils.Constants.STORE_OBJECT_ID
import com.arakadds.arak.utils.GlobalMethodsOldClass
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.android.AndroidInjection
import io.paperdb.Paper
import java.io.IOException
import javax.inject.Inject

class CreateStoreActivity : BaseActivity(), TaskHelperCallBack, AppDialogs.DialogCallBack,
    ApplicationDialogs.AlertDialogCallbacks, AppDialogs.DialogSocialMediaPlatformsCallBack {

    private val preferenceHelper: IPreferenceHelper by lazy { PreferenceManager(applicationContext) }
    lateinit var binding: ActivityCreateStoreBinding
    private lateinit var activityContext: Context

    private lateinit var toolbar: Toolbar
    private var connectionLiveData: ConnectionLiveData? = null

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: StoresViewModel by viewModels {
        viewModelFactory
    }
    private val createAccountViewModel: CreateAccountViewModel by viewModels {
        viewModelFactory
    }

    private val createStoreViewModel: CreateStoreViewModel by viewModels {
        viewModelFactory
    }

    private var mStorageReference: StorageReference? = null
    private var databaseReference: DatabaseReference? = null
    private var firebaseStorage: FirebaseStorage? =
        null //we use this to get reference to our images in firebase storage

    private var dialog: ProgressDialog? = null
    var imageUri: Uri? = null
    private var profileImage = ""
    private var coverImage: String? = ""
    private var isProfile = false
    private var isEditing: Boolean = false
    private var longitude: String? = null
    private var latitude: String? = null
    private var lat: String? = null
    private var lon: String? = null
    private var locationName: String? = null
    private var categoryName: String? = null
    private var categoryId = 0
    private var storeId: Int = 0
    private lateinit var storeObject: StoreObject
    private var cityId: Int = 0
    private var countryId: Int = 0
    private var countryCode: String? = null
    private lateinit var token: String
    private lateinit var resources: Resources
    private var selectedPlatformsIdsArrayList = ArrayList<Int>()
    private var aboutLabel: String? = null
    private var pageTitle: String? = null
    private var language = "en"

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connectionLiveData = ConnectionLiveData(this)
        binding = ActivityCreateStoreBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AndroidInjection.inject(this)
        activityContext = this

        language = preferenceHelper.getLanguage()

        updateStoreProfileView(language)
        initUi()
        initData()
        getIntents()
        getStoreCategories()
        getCountries()
        setListeners()
    }

    private fun updateStoreProfileView(language: String) {
        val context = LocaleHelper.setLocale(this@CreateStoreActivity, language)
        resources = context.resources
        binding.createStoreNameEditeTextId.hint =
            resources.getString(R.string.Create_store_activity_Store_name)
        binding.createStoreDescEditeTextId.hint =
            resources.getString(R.string.Create_New_Ad_activity_Description)
        binding.createStoreWebsiteEditTextId.hint =
            resources.getString(R.string.Create_New_Ad_activity_Website)
        binding.createStorePhoneNumberEditTextId.hint =
            resources.getString(R.string.ad_details_phone_number)
        binding.createStoreLocationTextViewId.hint =
            (resources.getString(R.string.Create_New_Ad_activity_Location))
        binding.createStoreContinueButtonId.hint = (resources.getString(R.string.dialogs_Continue))
        this.language = language
    }

    private fun initUi() {
        mStorageReference = FirebaseStorage.getInstance().reference
        databaseReference = FirebaseDatabase.getInstance().reference
        firebaseStorage = FirebaseStorage.getInstance()
        dialog = ProgressDialog(this@CreateStoreActivity)
        dialog!!.setMessage(resources.getString(R.string.dialogs_Uploading_wait))
    }

    private fun initData() {
        token = preferenceHelper.getToken()
        if (token == "non" || token.trim().isEmpty()) {
            GlobalMethodsOldClass.askGuestLogin(resources, activityContext)
            return
        }
    }

    private fun getIntents() {
        val intent = intent
        isEditing = intent.getBooleanExtra(IS_EDIT_ID, false)
        if (isEditing) {
            storeObject = intent.getParcelableExtra(STORE_OBJECT_ID)!!
            if (storeObject != null) {
                setDataToViews(storeObject)
            }
        }
    }


    private fun setDataToViews(storeObject: StoreObject) {
        storeId = storeObject.id
        val options = RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.image_placeholder)
            .error(R.drawable.image_placeholder)
        Glide.with(this@CreateStoreActivity).load(storeObject.coverImgUrl).apply(options)
            .into(binding.createStoreCoverImageViewId)
        Glide.with(this@CreateStoreActivity).load(storeObject.imgUrl).apply(options)
            .into(binding.createStoreProfileImageImageViewId)
        categoryId = storeObject.storeCategoryId

        profileImage = storeObject.imgUrl.toString()
        coverImage = storeObject.coverImgUrl

        binding.createStoreNameEditeTextId.setText(storeObject.name)
        binding.createStoreDescEditeTextId.setText(storeObject.description)
        binding.createStoreWebsiteEditTextId.setText(storeObject.website)
        try {
            binding.createStorePhoneNumberEditTextId.setText(storeObject.phoneNo?.substring(4))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val latLng = storeObject.lat?.let { storeObject.lon?.let { it1 -> LatLng(it, it1) } }
        binding.createStoreLocationTextViewId.text =
            GlobalMethodsOldClass.getCoordinatesAddressName(
                this@CreateStoreActivity,
                latLng
            )
    }

    fun setListeners() {
        binding.createStoreProfileBackImageViewId.setOnClickListener { finish() }

        binding.createStoreAddCoverImageImageViewId.setOnClickListener {
            isProfile = false
            try {
                if (ActivityCompat.checkSelfPermission(
                        this@CreateStoreActivity,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this@CreateStoreActivity,
                        arrayOf<String>(Manifest.permission.READ_EXTERNAL_STORAGE),
                        SELECT_IMAGE_PERMISSIONS_CODE
                    )
                }
                if (ContextCompat.checkSelfPermission(
                        this@CreateStoreActivity,
                        Manifest.permission.CAMERA
                    )
                    != PackageManager.PERMISSION_DENIED
                ) {
                    ActivityCompat.requestPermissions(
                        this@CreateStoreActivity,
                        arrayOf(Manifest.permission.CAMERA),
                        1
                    )
                    return@setOnClickListener
                }

                //addImageDialog(CreateStoreActivity.this, this);
                openGallery()
            } catch (e: java.lang.Exception) {
                Log.d(
                    "CreateStoreActivity.TAG",
                    "onClick: Exception: " + e.message
                )
            }
        }

        binding.createStoreAddProfileImageImageViewId.setOnClickListener {
            isProfile = true
            try {
                if (ActivityCompat.checkSelfPermission(
                        this@CreateStoreActivity,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this@CreateStoreActivity,
                        arrayOf<String>(Manifest.permission.READ_EXTERNAL_STORAGE),
                        SELECT_IMAGE_PERMISSIONS_CODE
                    )
                }
                if (ContextCompat.checkSelfPermission(
                        this@CreateStoreActivity,
                        Manifest.permission.CAMERA
                    )
                    != PackageManager.PERMISSION_DENIED
                ) {
                    ActivityCompat.requestPermissions(
                        this@CreateStoreActivity,
                        arrayOf(Manifest.permission.CAMERA),
                        1
                    )
                    return@setOnClickListener
                }

                //addImageDialog(CreateStoreActivity.this, this);
                openGallery()
            } catch (e: java.lang.Exception) {
                Log.d(
                    "CreateStoreActivity.TAG",
                    "onClick: Exception: " + e.message
                )
            }
        }

        binding.createStoreLocationTextViewId.setOnClickListener {
            if (isServicesOk()) {

                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // Permission is not granted, request the permission
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        Constants.LOCATION_PERMISSION_REQUEST_CODE
                    )
                } else {
                    ActivityHelper.goToActivity(
                        this@CreateStoreActivity,
                        MapActivity::class.java,
                        false
                    )
                }
            }
        }

        binding.createStoreAddSocialLinkLinearLayoutId.setOnClickListener {
            selectSocialMediaDialog(this, resources, selectedPlatformsIdsArrayList, this)
        }

        binding.createStoreContinueButtonId.setOnClickListener(View.OnClickListener { v: View? ->
            val storeName: String =
                binding.createStoreNameEditeTextId.text.toString().trim { it <= ' ' }
            val storeDescription: String =
                binding.createStoreDescEditeTextId.text.toString().trim { it <= ' ' }
            val whatsAppInput: String =
                binding.createStoreWebsiteEditTextId.text.toString().trim { it <= ' ' }
            val facebookInput: String =
                binding.createStoreWebsiteEditTextId.text.toString().trim { it <= ' ' }
            val instagramInput: String =
                binding.createStoreWebsiteEditTextId.text.toString().trim { it <= ' ' }
            val youtubeInput: String =
                binding.createStoreWebsiteEditTextId.text.toString().trim { it <= ' ' }
            val websiteLinkInput: String =
                binding.createStoreWebsiteEditTextId.text.toString().trim { it <= ' ' }

            val storePhoneNumber =
                binding.createStorePhoneNumberEditTextId.text.toString()
                    .trim { it <= ' ' }
            val location: String =
                binding.createStoreLocationTextViewId.text.toString().trim { it <= ' ' }
            if (storeName.isEmpty()) {
                Toast.makeText(
                    activityContext,
                    resources.getString(R.string.toast_Type_store_name),
                    Toast.LENGTH_SHORT
                ).show();
                return@OnClickListener
            }
            if (storeDescription.isEmpty()) {
                Toast.makeText(
                    activityContext,
                    resources.getString(R.string.toast_Type_store_description),
                    Toast.LENGTH_SHORT
                ).show();
                return@OnClickListener
            }
            if (!websiteLinkInput.isEmpty()) {
                if (!ActivityHelper.isValidWebUrl(websiteLinkInput)) {
                    Toast.makeText(
                        activityContext,
                        resources.getString(R.string.toast_Type_store_valid_website),
                        Toast.LENGTH_SHORT
                    ).show();
                    return@OnClickListener
                }
            }
            if (storePhoneNumber.isEmpty()) {
                Toast.makeText(
                    activityContext,
                    resources.getString(R.string.toast_Type_store_phone_number),
                    Toast.LENGTH_SHORT
                ).show();
                return@OnClickListener
            }
            if (coverImage!!.isEmpty() && coverImage!!.isEmpty()) {
                Toast.makeText(
                    activityContext,
                    resources.getString(R.string.toast_Type_store_cover_Image),
                    Toast.LENGTH_SHORT
                ).show();
                return@OnClickListener
            }
            if (profileImage.isEmpty() && profileImage.isEmpty()) {
                Toast.makeText(
                    activityContext,
                    resources.getString(R.string.toast_Type_store_profile_Image),
                    Toast.LENGTH_SHORT
                ).show();
                return@OnClickListener
            }

            /*if (location.isEmpty()) {
                Toaster.show(resources.getString(R.string.toast_Type_store_location));
                return;
            }*/

            countryCode = binding.countryCodePicker.selectedCountryCode
            val createStoreRequestBody = CreateStoreRequestBody()
            createStoreRequestBody.imgUrl = profileImage
            createStoreRequestBody.coverImgUrl = coverImage
            createStoreRequestBody.name = storeName
            createStoreRequestBody.description = storeDescription
            createStoreRequestBody.storeCategoryId = categoryId
            createStoreRequestBody.cityId = cityId
            createStoreRequestBody.lon = longitude
            createStoreRequestBody.lat = latitude
            createStoreRequestBody.locationName =
                binding.createStoreLocationTextViewId.text.toString()
            createStoreRequestBody.phoneNo = countryCode + storePhoneNumber

            websiteLinkInput
            for (i in selectedPlatformsIdsArrayList.indices) {
                when (selectedPlatformsIdsArrayList[i]) {
                    WHATS_UP -> {

                    }

                    FACEBOOK -> {
                        createStoreRequestBody.facebook = facebookInput
                    }

                    INSTAGRAM -> {
                        createStoreRequestBody.instagram = instagramInput
                    }

                    YOUTUBE -> {
                        createStoreRequestBody.youtube = youtubeInput
                    }

                    WEBSITE -> {
                        createStoreRequestBody.website = websiteLinkInput
                    }
                }
            }
            if (isEditing && storeObject != null) {
                updateStore(createStoreRequestBody)
            } else {
                createStore(createStoreRequestBody)
            }

        })
    }

    private fun createStore(createStoreRequestBody: CreateStoreRequestBody) {
        showLoadingDialog(this, "message")
        createStoreViewModel.createStore("Bearer $token", language, createStoreRequestBody)
        createStoreViewModel.createStoreModelModel.observe(
            this,
            Observer(function = fun(createStoreModel: CreateStoreModel?) {
                createStoreModel?.let {
                    if (createStoreModel.statusCode == 201) {

                        preferenceHelper.setUserHasStore(true)
                        /* preferenceHelper.setLat("non")
                         preferenceHelper.setLon("non")*/
                        hideLoadingDialog()
                        AppDialogs.successDialog(
                            this,
                            null,
                            resources,
                            StoreProfileActivity::class.java,
                            resources.getString(R.string.dialogs_store_created_successfully),
                            resources.getString(R.string.dialogs_Go_My_Store),
                            this
                        )

                    } else {
                        ApplicationDialogs.openAlertDialog(
                            this,
                            resources?.getString(R.string.dialogs_error),
                            createStoreModel.message,
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

        createStoreViewModel.throwableResponse.observe(
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


    private fun updateStore(createStoreRequestBody: CreateStoreRequestBody) {
        showLoadingDialog(this, "message")
        createStoreViewModel.updateStore(
            "Bearer $token",
            storeId.toString(),
            createStoreRequestBody
        )
        createStoreViewModel.updateStoreModelModel.observe(
            this,
            Observer(function = fun(createStoreModel: CreateStoreModel?) {
                createStoreModel?.let {
                    if (createStoreModel.statusCode == 201 || createStoreModel.statusCode == 200) {

                        preferenceHelper.setUserHasStore(true)
                        /* preferenceHelper.setLat("non")
                         preferenceHelper.setLon("non")*/
                        hideLoadingDialog()
                        AppDialogs.successDialog(
                            this,
                            null,
                            resources,
                            StoreProfileActivity::class.java,
                            resources.getString(R.string.dialogs_store_created_successfully),
                            resources.getString(R.string.dialogs_Go_My_Store),
                            this
                        )

                    } else {
                        ApplicationDialogs.openAlertDialog(
                            this,
                            resources?.getString(R.string.dialogs_error),
                            createStoreModel.message,
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

        createStoreViewModel.throwableResponse.observe(
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

    fun isServicesOk(): Boolean {
        Log.d("CreateStoreActivity.TAG", "isServicesOk: checking google services version")
        val available = GoogleApiAvailability.getInstance()
            .isGooglePlayServicesAvailable(this@CreateStoreActivity)
        if (available == ConnectionResult.SUCCESS) {
            Log.d("CreateStoreActivity.TAG", "isServicesOk: google play services is working")
            return true
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Log.d("CreateStoreActivity.TAG", "isServicesOk: google play services error occured")
            val dialog = GoogleApiAvailability.getInstance()
                .getErrorDialog(this@CreateStoreActivity, available, ERROR_DIALOG_REQUEST)
            dialog!!.show()
        } else {
            Toast.makeText(
                activityContext,
                resources.getString(R.string.toast_sorry_cant_open_map),
                Toast.LENGTH_SHORT
            ).show();
        }
        return false
    }    // Select image from camera and gallery

    private fun selectImage() {
        try {
            val pm = packageManager
            val hasPerm = pm.checkPermission(
                Manifest.permission.CAMERA,
                packageName
            )
            if (hasPerm == PackageManager.PERMISSION_GRANTED) {
                val options = arrayOf<CharSequence>("Take Photo", "Choose From Gallery", "Cancel")
                val builder = AlertDialog.Builder(this@CreateStoreActivity)
                builder.setTitle("Select Option")
                builder.setItems(options) { dialog, item ->
                    if (options[item] == "Take Photo") {
                        dialog.dismiss()
                        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        startActivityForResult(intent, PICK_IMAGE_CAMERA)
                    } else if (options[item] == "Choose From Gallery") {
                        dialog.dismiss()
                        val pickPhoto =
                            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        startActivityForResult(pickPhoto, PICK_IMAGE_GALLERY)
                    } else if (options[item] == "Cancel") {
                        dialog.dismiss()
                    }
                }
                builder.show()
            } else Toast.makeText(this, "Camera Permission error", Toast.LENGTH_SHORT).show()
        } catch (e: java.lang.Exception) {
            Toast.makeText(this, "Camera Permission error", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        dialog!!.show()
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE_GALLERY && data != null && data.data != null) {
            imageUri = data.data
            try {
                //Bitmap bitmap = MediaStore.Images.Media.getBitmap(CreateStoreActivity.this.getContentResolver(), imageUri);
                //userImageView.setImageBitmap(bitmap);
                TaskHelper.updateImageToFireBase(
                    this@CreateStoreActivity,
                    preferenceHelper.getKeyUserId().toString(),
                    imageUri,
                    STORE_PROFILE_IMAGE,
                    dialog,
                    resources,
                    this
                )
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                Toast.makeText(
                    activityContext,
                    resources.getString(R.string.toast_please_try_again_later),
                    Toast.LENGTH_SHORT
                ).show();
                Log.d("CreateStoreActivity.TAG", "onActivityResult: Exception: " + e.message)
                dialog!!.dismiss()
            }
        }
    }

    private fun getCountries() {
        // showLoadingDialog(this, "message")
        createAccountViewModel.getCountries(language)
        createAccountViewModel.countriesModel.observe(
            this,
            Observer(function = fun(countriesModel: CountriesModel?) {
                countriesModel?.let {
                    if (countriesModel.statusCode == 200) {
                        initCountriesSpinner(countriesModel.countriesData.countriesResponseDataArrayList)
                    } else {
                        ApplicationDialogs.openAlertDialog(
                            this,
                            resources.getString(R.string.dialogs_error),
                            countriesModel.message,
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

        createAccountViewModel.throwableResponse.observe(
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

    private fun getCities(countryId: Int) {
        // showLoadingDialog(this, "message")
        createAccountViewModel.getCities(countryId, language)
        createAccountViewModel.citiesModel.observe(
            this,
            Observer(function = fun(citiesModel: CitiesModel?) {
                citiesModel?.let {
                    if (citiesModel.statusCode == 200) {
                        initCitiesSpinner(citiesModel.cityData.citiesArrayList)
                    } else {
                        ApplicationDialogs.openAlertDialog(
                            this,
                            resources.getString(R.string.dialogs_error),
                            citiesModel.message,
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

        createAccountViewModel.throwableResponse.observe(
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

    private fun getStoreCategories() {
        showLoadingDialog(this, "message")
        viewModel.getStoreCategories("Bearer $token", language)
        viewModel.storeCategoriesModelModel.observe(
            this,
            Observer(function = fun(storeCategoriesModel: StoreCategoriesModel?) {
                storeCategoriesModel?.let {
                    if (storeCategoriesModel.statusCode == 200) {

                        initCategorySpinner(storeCategoriesModel.data.storeCategories)
                        for (i in storeCategoriesModel.data.storeCategories.indices) {
                            if (storeCategoriesModel.data.storeCategories[i].id == categoryId) {
                                binding.createStoreCategorySpinnerId.setSelection(i)
                                break
                            }
                        }
                    } else {
                        ApplicationDialogs.openAlertDialog(
                            this,
                            resources?.getString(R.string.dialogs_error),
                            storeCategoriesModel.message,
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

    private fun initCategorySpinner(storeCategoryArrayList: ArrayList<StoreCategory>) {
        if (storeCategoryArrayList.size > 0) {
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item, storeCategoryArrayList
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.createStoreCategorySpinnerId.adapter = adapter
            binding.createStoreCategorySpinnerId.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    val storeCategoriesList = parent.selectedItem as StoreCategory
                    categoryName = if (language == "ar") {
                        storeCategoriesList.nameAr
                    } else {
                        storeCategoriesList.nameEn
                    }
                    categoryId = storeCategoriesList.id
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
    }

    private fun initCitiesSpinner(citiesArrayList: ArrayList<CityResponseData>) {

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, citiesArrayList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.createStoreCitySpinnerId.adapter = adapter
        binding.createStoreCitySpinnerId.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                val cityResponseData =
                    parent.selectedItem as CityResponseData
                cityId = cityResponseData.id
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun initCountriesSpinner(countriesResponseDataArrayList: ArrayList<CountriesResponseData>) {
        if (countriesResponseDataArrayList.size > 0) {
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item, countriesResponseDataArrayList
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.createStoreCountrySpinnerId.adapter = adapter
            binding.createStoreCountrySpinnerId.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    val countriesResponseData = parent.selectedItem as CountriesResponseData
                    countryId = countriesResponseData.id
                    countryCode = countriesResponseData.countryCode
                    binding.countryCodePicker.setCountryPreference(countryCode)
                    getCities(countryId)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
    }

    override fun onImageUploadedSuccessfullyEvent(uri: Uri) {
        Log.d("CreateStoreActivity.TAG", "onImageUploadedSuccessfullyEvent: called")
        var bitmap: Bitmap? = null
        try {
            bitmap = MediaStore.Images.Media.getBitmap(
                this@CreateStoreActivity.contentResolver,
                imageUri
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }
        if (isProfile) {
            /*profileImageView.setImageURI(null);
            profileImageView.setImageURI(uri);*/
            binding.createStoreProfileImageImageViewId.setImageURI(null)
            binding.createStoreProfileImageImageViewId.setImageBitmap(bitmap)
            profileImage = uri.toString()
        } else {
            /*coverImageView.setImageURI(null);
            coverImageView.setImageURI(uri);*/
            binding.createStoreCoverImageViewId.setImageURI(null)
            binding.createStoreCoverImageViewId.setImageBitmap(bitmap)
            coverImage = uri.toString()
        }
        dialog!!.dismiss()
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

    override fun onImageUploadedFailureEvent(e: java.lang.Exception?) {
        Log.d("CreateStoreActivity.TAG", "onImageUploadedFailureEvent: called")
    }

    override fun openCameraCallback() {
        /*Log.d(TAG, "openCameraCallback: called");
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/ *");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, resources.getString(R.string.dialogs_Select_Picture)), PICK_IMAGE_CAMERA);*/
    }

    override fun openGalleryCallback() {
        Log.d("CreateStoreActivity.TAG", "openGalleryCallback: called")
        //openGallery();
    }

    override fun continuePressedCallback() {
        finish()
    }

    override fun onResume() {
        super.onResume()
        try {
            lat = preferenceHelper.getLat()
            lon = preferenceHelper.getLon()
            locationName = preferenceHelper.getSelectedLocationName()
            if (lat != null && lat != "non" && lon != null && lon != "non") {
                longitude = lon
                latitude = lat
                try {
                    binding.createStoreLocationTextViewId.text = locationName
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                    binding.createStoreLocationTextViewId.text = "$lat : $lon"
                }
            }
        } catch (e: java.lang.Exception) {
            Toast.makeText(
                activityContext,
                resources.getString(R.string.toast_please_try_again_later),
                Toast.LENGTH_SHORT
            ).show();
            Log.d("CreateStoreActivity.TAG", "onCreate: Exception: " + e.message)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        preferenceHelper.setLat("non")
        preferenceHelper.setLon("non")
    }

    override fun onClose() {

    }

    override fun onConfirm(actionType: Int) {
    }

    override fun onSelectSocialMediaPlatformsCallback(selectedPlatformsIdArrayList: ArrayList<Int>) {
        selectedPlatformsIdsArrayList = selectedPlatformsIdArrayList
        resetSocialMediaView()
        for (i in selectedPlatformsIdsArrayList.indices) {
            when (selectedPlatformsIdsArrayList[i]) {
                WHATS_UP -> {
                    binding.createStoreWhatsupLinearLayoutId.visibility = View.VISIBLE
                }

                FACEBOOK -> {
                    binding.createStoreFacebookLinearLayoutId.visibility = View.VISIBLE
                }

                INSTAGRAM -> {
                    binding.createStoreInstagramLinearLayoutId.visibility = View.VISIBLE
                }

                YOUTUBE -> {
                    binding.createStoreYoutubeLinearLayoutId.visibility = View.VISIBLE
                }

                WEBSITE -> {
                    binding.createStoreWebsiteLinearLayoutId.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun resetSocialMediaView() {
        binding.createStoreWhatsupLinearLayoutId.visibility = View.GONE
        binding.createStoreFacebookLinearLayoutId.visibility = View.GONE
        binding.createStoreInstagramLinearLayoutId.visibility = View.GONE
        binding.createStoreYoutubeLinearLayoutId.visibility = View.GONE
        binding.createStoreWebsiteLinearLayoutId.visibility = View.GONE
    }
}