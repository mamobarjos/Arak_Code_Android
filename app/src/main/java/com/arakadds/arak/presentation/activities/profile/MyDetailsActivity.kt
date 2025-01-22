package com.arakadds.arak.presentation.activities.profile

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.arakadds.arak.R
import com.arakadds.arak.common.helper.ActivityHelper
import com.arakadds.arak.common.helper.LocaleHelper
import com.arakadds.arak.common.helper.connectivity_checker.ConnectionLiveData
import com.arakadds.arak.common.preferaence.IPreferenceHelper
import com.arakadds.arak.common.preferaence.PreferenceManager
import com.arakadds.arak.databinding.ActivityMyDetailsBinding
import com.arakadds.arak.model.ImageUploadModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.edit_user_info.EditUserInformation
import com.arakadds.arak.presentation.activities.authentication.ForgetPasswordActivity
import com.arakadds.arak.presentation.activities.authentication.ResetPasswordActivity
import com.arakadds.arak.presentation.activities.other.BaseActivity
import com.arakadds.arak.presentation.activities.other.NoInternetConnectionActivity
import com.arakadds.arak.presentation.activities.stores.ProductDetailsActivity
import com.arakadds.arak.presentation.dialogs.ApplicationDialogs
import com.arakadds.arak.presentation.dialogs.ApplicationDialogs.openAlertDialog
import com.arakadds.arak.presentation.viewmodel.UserProfileViewModel
import com.arakadds.arak.utils.AppEnums
import com.arakadds.arak.utils.AppEnums.DialogActionTypes.DISMISS
import com.arakadds.arak.utils.AppEnums.LanguagesEnums.ENGLISH
import com.arakadds.arak.utils.AppProperties
import com.arakadds.arak.utils.Constants
import com.arakadds.arak.utils.Constants.ERROR_DIALOG_REQUEST
import com.arakadds.arak.utils.Constants.IS_CHANGE_PASSWORD
import com.arakadds.arak.utils.Constants.RESULT_LOAD_IMG
import com.arakadds.arak.utils.GlobalMethodsOldClass
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.android.AndroidInjection
import io.paperdb.Paper
import java.util.UUID
import javax.inject.Inject

class MyDetailsActivity : BaseActivity(), ApplicationDialogs.AlertDialogCallbacks {

    private val preferenceHelper: IPreferenceHelper by lazy { PreferenceManager(applicationContext) }
    lateinit var binding: ActivityMyDetailsBinding
    private lateinit var activityContext: Context

    private lateinit var toolbar: Toolbar
    private lateinit var pageTitle: TextView
    private var connectionLiveData: ConnectionLiveData? = null

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: UserProfileViewModel by viewModels {
        viewModelFactory
    }
    private lateinit var token: String
    private lateinit var language: String
    private lateinit var activityResources: Resources
    private var city: String? = null
    private var country: String? = null
    private lateinit var dialog: ProgressDialog
    private var imageUri: Uri? = null
    private var userImage: String? = null
    private var mStorageReference: StorageReference? = null
    private var databaseReference: DatabaseReference? = null
    private var firebaseStorage: FirebaseStorage? =
        null //we use this to get reference to our images in firebase storage
    private val countryName: String? = null
    private var cityName: String? = null
    private val countryId = 0
    private var userId = 0

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connectionLiveData = ConnectionLiveData(this)
        binding = ActivityMyDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AndroidInjection.inject(this)
        activityContext = this

        language = preferenceHelper.getLanguage()

        updateMyDetailsView(language)
        initToolbar()
        initUi()
        setupUi()
        //getCountries();
        setDataToView()
        setListeners()
    }

    private fun initToolbar() {
        toolbar = binding.appBarLayout.findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        pageTitle = binding.appBarLayout.findViewById(R.id.page_title)
        val backImageView: ImageView = binding.appBarLayout.findViewById(R.id.back_button)
        //shareImageView = binding.appBarLayout.findViewById(R.id.share_button)
        //favoriteImageView = binding.appBarLayout.findViewById(R.id.favorite_button)
        pageTitle.text = activityResources.getString(R.string.title_profile)
        backImageView.setOnClickListener { finish() }
    }

    private fun setupUi() {
        try {
            val socialToken = preferenceHelper.getSocialToken()
            if (socialToken != null) {
                binding.myDetailsGenderLinearLayoutId.visibility = View.GONE
                binding.myDetailsCompanyNameLinearLayoutId.visibility = View.GONE
                binding.myDetailsCountryLinearLayoutId.visibility = View.GONE
                binding.myDetailsCityLinearLayoutId.visibility = View.GONE
                binding.myDetailsPasswordLinearLayoutId.visibility = View.GONE
                binding.genderViewId.visibility = View.GONE
                binding.comapnyNameViewId.visibility = View.GONE
                binding.countryViewId.visibility = View.GONE
                binding.cityViewId.visibility = View.GONE
                binding.passwordViewId.visibility = View.GONE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setDataToView() {
        val fullName = preferenceHelper.getUserFullName()
        val email = preferenceHelper.getUserEmail()
        val gender = preferenceHelper.getUserGender()
        val phoneNumber = preferenceHelper.getUserPhoneNumber()
        val companyName = preferenceHelper.getCompanyName()
        val birthdate = preferenceHelper.getBirthOfDate()
        userId = preferenceHelper.getKeyUserId()
        country = preferenceHelper.getUserCountry()
        city = preferenceHelper.getUserCity()

        binding.profileUserNameTextViewId.text = fullName
        if (fullName != "non") {
            binding.myDetailsNameEditTextId.setText(fullName)
        }
        if (birthdate != null) {
            binding.myDetailsBirthdateTextViewId.text = birthdate.split("T")[0]
        }
        if (gender != null) {
            binding.myDetailsGenderTextViewId.text = gender
        }
        if (phoneNumber != null) {
            binding.myDetailsPhoneNumberTextViewId.text = phoneNumber
        }
        if (companyName != null) {
            binding.myDetailsBusniessNameEditTextId.setText(companyName)
        } else {
            binding.myDetailsCompanyNameLinearLayoutId.visibility = View.GONE
        }
        if (language.equals(ENGLISH)) {
            binding.myDetailsCountryTextViewId.text = preferenceHelper.getCountryNameEn()
            binding.myDetailsCityTextViewId.text = preferenceHelper.getCityNameEn()
        } else {
            binding.myDetailsCountryTextViewId.text = preferenceHelper.getCountryNameAr()
            binding.myDetailsCityTextViewId.text = preferenceHelper.getCityNameAr()
        }
    }

    private fun updateMyDetailsView(language: String) {
        val context = LocaleHelper.setLocale(this@MyDetailsActivity, language)
        activityResources = context.resources
        binding.myDetailsPasswordTextViewId.text =
            activityResources.getString(R.string.profile_frag_Change_Password)
        binding.profileFragTitleImageViewId.text =
            activityResources.getString(R.string.profile_frag_My_Detail)
        binding.myDetailsSaveChangesButtonId.text =
            activityResources.getString(R.string.My_Detail_activity_save_changes)
        this.language = language
    }

    private fun initUi() {
        token = preferenceHelper.getToken()
        mStorageReference = FirebaseStorage.getInstance().reference
        databaseReference = FirebaseDatabase.getInstance().reference
        firebaseStorage = FirebaseStorage.getInstance()
        dialog = ProgressDialog(this@MyDetailsActivity)
        dialog.setMessage(activityResources.getString(R.string.dialogs_Uploading_wait))
        setImages()
        if (token == "non" || token.trim().isEmpty()) {
            GlobalMethodsOldClass.askGuestLogin(resources, activityContext)
            return
        }
    }

    fun setListeners() {
        binding.myDetailsPhoneNumberLinearLayoutId.setOnClickListener {

            val intent = Intent(this@MyDetailsActivity, ForgetPasswordActivity::class.java)
            intent.putExtra(Constants.IS_RESET_PHONE_NUMBER, true)
            startActivity(intent)

        }
        binding.myDetailsPasswordTextViewId.setOnClickListener {
            val intent = Intent(this@MyDetailsActivity, ResetPasswordActivity::class.java)
            intent.putExtra(Constants.IS_CHANGE_PASSWORD, true)
            startActivity(intent)
        }
        binding.profileEditUserImageImageViewId.setOnClickListener {
            try {
                if (ActivityCompat.checkSelfPermission(
                        this@MyDetailsActivity,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this@MyDetailsActivity,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        1000
                    )
                }
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                intent.action = Intent.ACTION_PICK
                startActivityForResult(
                    Intent.createChooser(
                        intent,
                        activityResources.getString(R.string.dialogs_Select_Picture)
                    ), RESULT_LOAD_IMG
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        binding.myDetailsSaveChangesButtonId.setOnClickListener(View.OnClickListener {
            val fullname: String = binding.myDetailsNameEditTextId.text.toString()
            val busniessName: String = binding.myDetailsBusniessNameEditTextId.text.toString()
            val gender: String = binding.myDetailsGenderTextViewId.text.toString()
            /*String country=countrySpinner.getSelectedItem().toString();
                String city=citiesSpinner.getSelectedItem().toString();*/if (fullname.trim { it <= ' ' } == "") {
            Toast.makeText(
                activityContext,
                activityResources.getString(R.string.toast_Insert_full_name),
                Toast.LENGTH_SHORT
            ).show();
            return@OnClickListener
        }
            if (fullname.trim { it <= ' ' } == "") {
                Toast.makeText(
                    activityContext,
                    activityResources.getString(R.string.toast_Insert_full_name),
                    Toast.LENGTH_SHORT
                ).show();
                return@OnClickListener
            }
            val hashMap = HashMap<String, String>()
            hashMap["fullname"] = fullname
            hashMap["country"] = country!!
            hashMap["city"] = city!!
            hashMap["gender"] = gender
            hashMap["company_name"] = busniessName
            editUserInformation(hashMap)
        })
    }

    fun isServicesOk(): Boolean {
        val available = GoogleApiAvailability.getInstance()
            .isGooglePlayServicesAvailable(this@MyDetailsActivity)
        if (available == ConnectionResult.SUCCESS) {
            return true
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            val dialog = GoogleApiAvailability.getInstance()
                .getErrorDialog(this@MyDetailsActivity, available, ERROR_DIALOG_REQUEST)
            dialog!!.show()
        } else {
            Toast.makeText(activityContext, "sorry you can't open map", Toast.LENGTH_SHORT).show();
        }
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        dialog.show()
        if (resultCode == RESULT_OK && requestCode == RESULT_LOAD_IMG && data != null && data.data != null) {
            imageUri = data.data
            try {
                /*Bitmap bitmap = MediaStore.Images.Media.getBitmap(MyDetailsActivity.this.getContentResolver(), imageUri);
                userImageView.setImageBitmap(bitmap);*/
                if (userImage != "non") {
                    deleteImageFromFirebaseStorage(userImage!!, imageUri!!)
                } else {
                    updateImageToFireBase(imageUri!!)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    activityContext,
                    activityResources.getString(R.string.toast_please_try_again_later),
                    Toast.LENGTH_SHORT
                ).show();
                dialog.dismiss()
            }
        }
    }

    private fun deleteImageFromFirebaseStorage(imageUri: String, newUploadedImageUri: Uri) {
        dialog.show()
        val firebaseStorage = FirebaseStorage.getInstance()
        val photoRef = firebaseStorage.getReferenceFromUrl(imageUri)
        photoRef.delete().addOnSuccessListener {

            updateImageToFireBase(newUploadedImageUri)
        }.addOnFailureListener {   // Uh-oh, an error occurred!
            dialog.dismiss()
            Toast.makeText(
                activityContext,
                activityResources.getString(R.string.toast_please_try_again_later),
                Toast.LENGTH_SHORT
            ).show();
        }
    }

    private fun updateImageToFireBase(imageUri: Uri) {
        dialog.show()
        val userId = preferenceHelper.getKeyUserId().toString()
        val fileReference = mStorageReference!!.child(
            userId + "/Profile/" + UUID.randomUUID().toString() + "." + getFileExtension(imageUri)
        )
        fileReference.putFile(imageUri)
            .addOnSuccessListener {
                fileReference.downloadUrl.addOnSuccessListener { uri: Uri ->
                    val downloadUrl =
                        fileReference.downloadUrl
                    //urlArrayList.add(String.valueOf(uri));
                    val imageUploadModel = ImageUploadModel(uri.toString())
                    val uploadId = databaseReference!!.push().key
                    //databaseReference.child(userId).child("Products").setValue(imageUploadModel);

                    //editUserProfileImage(uri);
                    //setImages();
                    editUserImage(uri)
                }
            }
            .addOnFailureListener {
                Toast.makeText(
                    activityContext,
                    activityResources.getString(R.string.toast_please_try_again_later),
                    Toast.LENGTH_SHORT
                ).show();
                dialog.dismiss()
                //dialog.dismiss();
            }
            .addOnProgressListener { snapshot ->
                val progress = 100.0 * snapshot.bytesTransferred / snapshot.totalByteCount
            }
    }

    private fun getFileExtension(uri: Uri): String? {
        val contentResolver = this@MyDetailsActivity.contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(contentResolver.getType(uri))
    }

    private fun setImages() {
        userImage = preferenceHelper.getUserImage()

        if (userImage != null) {
            val options = RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_placeholder)
            Glide.with(this@MyDetailsActivity).load(userImage).apply(options)
                .into(binding.profileUserImageImageViewId)
        } else {
            binding.profileUserImageImageViewId.setImageResource(R.drawable.image_placeholder)
        }
    }

    private fun editUserInformation(hashMap: HashMap<String, String>) {
        showLoadingDialog(this, "message")
        viewModel.editUserInformation(token, language, userId, hashMap)
        viewModel.editUserInformationModel.observe(
            this,
            Observer(function = fun(editUserInformation: EditUserInformation?) {
                editUserInformation?.let {
                    if (editUserInformation.statusCode == 201) {
                        preferenceHelper.setUserFullName(editUserInformation.userObject.fullname)
                        preferenceHelper.setUserCountry(editUserInformation.userObject.countryName)
                        preferenceHelper.setUserCity(editUserInformation.userObject.cityName)
                        preferenceHelper.setUserImage(editUserInformation.userObject.imgAvatar)

                        setDataToView()
                        dialog.dismiss()
                        //successDialog();
                    } else {
                        openAlertDialog(
                            this,
                            activityResources?.getString(R.string.dialogs_error),
                            editUserInformation.message,
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

    private fun editUserImage(uri: Uri) {
        showLoadingDialog(this, "message")

        val hashMap = java.util.HashMap<String, String>()
        hashMap["img_avatar"] = uri.toString()

        viewModel.editUserImage(token, language, userId, hashMap)
        viewModel.editUserInformationModel.observe(
            this,
            Observer(function = fun(editUserInformation: EditUserInformation?) {
                editUserInformation?.let {
                    if (editUserInformation.statusCode == 201) {
                        preferenceHelper.setUserImage(editUserInformation.userObject.imgAvatar)
                        setImages()
                        dialog.dismiss()
                    } else {
                        openAlertDialog(
                            this,
                            activityResources?.getString(R.string.dialogs_error),
                            editUserInformation.message,
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


    override fun onClose() {
    }

    override fun onConfirm(actionType: Int) {

    }
}