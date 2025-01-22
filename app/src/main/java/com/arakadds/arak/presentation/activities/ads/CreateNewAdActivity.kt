package com.arakadds.arak.presentation.activities.ads

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.arakadds.arak.R
import com.arakadds.arak.common.helper.ActivityHelper
import com.arakadds.arak.common.helper.LocaleHelper
import com.arakadds.arak.common.helper.connectivity_checker.ConnectionLiveData
import com.arakadds.arak.common.preferaence.IPreferenceHelper
import com.arakadds.arak.common.preferaence.PreferenceManager
import com.arakadds.arak.databinding.ActivtiyNewAdDetailsBinding
import com.arakadds.arak.model.CreateNewAdDataHolder
import com.arakadds.arak.model.ImageUploadModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.packages.AdPackages
import com.arakadds.arak.presentation.activities.map.MapActivity
import com.arakadds.arak.presentation.activities.other.BaseActivity
import com.arakadds.arak.presentation.activities.other.NoInternetConnectionActivity
import com.arakadds.arak.presentation.adapters.SelectedImagesAdapter
import com.arakadds.arak.presentation.adapters.SelectedImagesAdapter.CallbackDeleteMedia
import com.arakadds.arak.presentation.adapters.SelectedImagesAdapter.CallbackInterface
import com.arakadds.arak.presentation.dialogs.ApplicationDialogs
import com.arakadds.arak.utils.AppEnums.AdsTypeCategories.IMAGE
import com.arakadds.arak.utils.AppEnums.AdsTypeCategories.VIDEO
import com.arakadds.arak.utils.AppEnums.AdsTypeCategories.WEBSITE
import com.arakadds.arak.utils.AppProperties
import com.arakadds.arak.utils.Constants
import com.arakadds.arak.utils.Constants.AD_PACKAGE_ID
import com.arakadds.arak.utils.Constants.CATEGORY_ID
import com.arakadds.arak.utils.Constants.ERROR_DIALOG_REQUEST
import com.arakadds.arak.utils.Constants.LOCATION_PERMISSION_REQUEST_CODE
import com.arakadds.arak.utils.Constants.NEW_AD_DATA

import com.arakadds.arak.utils.Constants.RESULT_LOAD_IMG
import com.arakadds.arak.utils.Constants.RESULT_LOAD_VIDEO
import com.arakadds.arak.utils.GlobalMethodsOldClass
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.android.AndroidInjection
import io.paperdb.Paper
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CreateNewAdActivity : BaseActivity(), ApplicationDialogs.AlertDialogCallbacks,
    CallbackInterface, CallbackDeleteMedia {

    private val preferenceHelper: IPreferenceHelper by lazy { PreferenceManager(applicationContext) }
    lateinit var binding: ActivtiyNewAdDetailsBinding
    private lateinit var activityContext: Context

    private lateinit var toolbar: Toolbar
    private lateinit var pageTitle: TextView
    private var connectionLiveData: ConnectionLiveData? = null

    private lateinit var token: String
    var bitmapArrayList = ArrayList<Bitmap>()
    private var selectedImagesAdapter: SelectedImagesAdapter? = null
    var phoneNumber: String? = null
    private var videoThumbnail: Uri? = null
    var hashMap = HashMap<String, RequestBody>()

    private var mediaUriList: ArrayList<Uri> = ArrayList()
    private var videoUriList: ArrayList<Uri> = ArrayList()
    var currentPhotoPath: String? = null
    private var mStorageReference: StorageReference? = null
    private var databaseReference: DatabaseReference? = null
    private lateinit var uploadBytes: ByteArray
    var progress = 0.0
    var postIdArrayList = ArrayList<String>()

    private val MY_PERMISSIONS_REQUEST_STORAGE = 1
    private val storage_permissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    private lateinit var categoryId: String
    private var adPackages = AdPackages()
    private var isCompany = true
    private var longitude: String? = null
    private var latitude: String? = null
    private var lat: String? = null
    private var lon: String? = null
    private var locationName: String? = null
    private lateinit var language: String
    private var videoDuration: Long = 0
    private lateinit var activityResources: Resources

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connectionLiveData = ConnectionLiveData(this)
        binding = ActivtiyNewAdDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AndroidInjection.inject(this)
        activityContext = this

        language = preferenceHelper.getLanguage()

        updateCreateNewAdView(language)
        checkNetworkConnection()
        initData()
        initToolbar()
        initRecyclerView()
        setListeners()
    }

    private fun initRecyclerView() {
        selectedImagesAdapter = SelectedImagesAdapter(
            videoUriList,
            bitmapArrayList,
            null,
            categoryId,
            activityResources,
            this
        )
        val mLayoutManager =
            LinearLayoutManager(this@CreateNewAdActivity, LinearLayoutManager.HORIZONTAL, false)
        binding.uploadImageRecyclerViewId.layoutManager = mLayoutManager
        binding.uploadImageRecyclerViewId.isFocusable = false
        binding.uploadImageRecyclerViewId.isNestedScrollingEnabled = false
        binding.uploadImageRecyclerViewId.adapter = selectedImagesAdapter
    }

    private fun initData() {
        token = preferenceHelper.getToken()
        val intent = intent
        categoryId = intent.getStringExtra(CATEGORY_ID).toString()
        /*timeLong = intent.getStringExtra(PACKAGE_TIME_LONG_ID)
        packagePrice = intent.getStringExtra(PACKAGE_PRICE)
        packageId = intent.getStringExtra(PACKAGE_ID)
        reachNumber = intent.getStringExtra(PACKAGE_REACH_NUMBER_ID)
        imagesNumber = intent.getStringExtra(NUMBER_OF_IMAGES_ID)*/
        try {
            adPackages = intent.getParcelableExtra(AD_PACKAGE_ID)!!
        } catch (e: Exception) {
            e.printStackTrace()
        }
        phoneNumber = preferenceHelper.getUserPhoneNumber()
        if (phoneNumber != null)
            binding.newAdPhoneNumberEditTextId.setText(phoneNumber)

        mStorageReference = FirebaseStorage.getInstance().reference
        databaseReference = FirebaseDatabase.getInstance().reference
        if (categoryId == WEBSITE.toString()) {
            binding.newAdWebsiteUrlEditTextId.visibility = View.VISIBLE
        }

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

        when (categoryId) {
            IMAGE.toString() -> pageTitle.text =
                activityResources!!.getString(R.string.Category_Packages_activity_Image_Ads)

            VIDEO.toString() -> pageTitle.text =
                activityResources!!.getString(R.string.Category_Packages_activity_Video_Ads)

            WEBSITE.toString() -> pageTitle.text =
                activityResources!!.getString(R.string.Category_Packages_activity_Website_Ads)
        }

        backImageView.setOnClickListener { finish() }
    }

    private fun updateCreateNewAdView(language: String) {
        val context = LocaleHelper.setLocale(this@CreateNewAdActivity, language)
        activityResources = context.resources

        binding.newAdTitleEditTextId.hint =
            activityResources.getString(R.string.Create_New_Ad_activity_Title)
        binding.newAdDescriptionEditTextId.hint =
            activityResources.getString(R.string.Create_New_Ad_activity_Description)
        binding.newAdPhoneNumberEditTextId.hint =
            activityResources.getString(R.string.registration_activity_phone_number)
        binding.newAdLocationTextViewId.hint =
            activityResources.getString(R.string.Create_New_Ad_activity_Location)
        binding.newAdContinueButtonId.text =
            activityResources.getString(R.string.Create_New_Ad_activity_Continue)
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

    @Throws(IOException::class)
    private fun createImageFile(): File? {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.absolutePath
        return image
    }

    private fun handelMediaSelection(categoryId: Int) {

        when (categoryId) {
            IMAGE, WEBSITE -> {
                val numberOfImages = adPackages.numberOfImages
                try {
                    if (ActivityCompat.checkSelfPermission(
                            this@CreateNewAdActivity,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                        != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            this@CreateNewAdActivity,
                            arrayOf<String>(Manifest.permission.READ_EXTERNAL_STORAGE),
                            Constants.SELECT_IMAGE_PERMISSIONS_CODE
                        )
                    }
                    if (ContextCompat.checkSelfPermission(
                            this@CreateNewAdActivity,
                            Manifest.permission.CAMERA
                        )
                        != PackageManager.PERMISSION_DENIED
                    ) {
                        ActivityCompat.requestPermissions(
                            this@CreateNewAdActivity,
                            arrayOf(Manifest.permission.CAMERA),
                            1
                        )
                        return
                    }

                    if (bitmapArrayList.size < numberOfImages) {
                        openGallery()
                    } else {
                        Toast.makeText(
                            this,
                            resources.getString(R.string.toast_cant_upload_more) + numberOfImages + resources.getString(
                                R.string.toast_images
                            ),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                } catch (e: java.lang.Exception) {
                    Log.d(
                        "CreateStoreActivity.TAG",
                        "onClick: Exception: " + e.message
                    )
                }
            }

            VIDEO -> {
                if (ActivityCompat.checkSelfPermission(
                        this@CreateNewAdActivity,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this@CreateNewAdActivity,
                        arrayOf<String>(Manifest.permission.READ_EXTERNAL_STORAGE),
                        Constants.SELECT_IMAGE_PERMISSIONS_CODE
                    )
                }
                if (ContextCompat.checkSelfPermission(
                        this@CreateNewAdActivity,
                        Manifest.permission.CAMERA
                    )
                    != PackageManager.PERMISSION_DENIED
                ) {
                    ActivityCompat.requestPermissions(
                        this@CreateNewAdActivity,
                        arrayOf(Manifest.permission.CAMERA),
                        1
                    )
                    return
                }

                if (videoUriList.size < 1) {
                    val photoPickerIntent = Intent(Intent.ACTION_PICK)
                    photoPickerIntent.type = "video/*"
                    startActivityForResult(photoPickerIntent, RESULT_LOAD_VIDEO)
                } else {
                    Toast.makeText(
                        this,
                        resources.getString(R.string.toast_cant_upload_more_one_Video),
                        Toast.LENGTH_SHORT
                    ).show()
                }
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
            ), RESULT_LOAD_IMG
        )
    }

    private fun setListeners() {
        binding.newAdUploadImageViewId.setOnClickListener {
            handelMediaSelection(categoryId.toInt())
        }
        binding.newAdContinueButtonId.setOnClickListener(View.OnClickListener {
            val title: String = binding.newAdTitleEditTextId.text.toString()
            val description: String = binding.newAdDescriptionEditTextId.text.toString()
            val phoneNumber: String = binding.newAdPhoneNumberEditTextId.text.toString()
            val location: String = binding.newAdLocationTextViewId.text.toString()
            val websiteUrl: String = binding.newAdWebsiteUrlEditTextId.text.toString()
            if (title.trim { it <= ' ' } != ""
                && description.trim { it <= ' ' } != "" && phoneNumber.trim { it <= ' ' } != "") {

                if (phoneNumber.length < 10) {
                    Toast.makeText(
                        activityContext,
                        activityResources.getString(R.string.toast_check_phone_number),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@OnClickListener
                }

                /*if (longitude == null || latitude == null) {
                        Toaster.show("Please Set Your Location");
                        // continueButton.setEnabled(true);
                        progressBar.setVisibility(View.GONE);
                        return;
                    }*/if (mediaUriList.isEmpty()) {
                    Toast.makeText(
                        activityContext,
                        activityResources.getString(R.string.toast_add_media_post),
                        Toast.LENGTH_SHORT
                    ).show();
                    return@OnClickListener
                }
                val createNewAdDataHolder = CreateNewAdDataHolder()
                //createNewAdDataHolder.setBitmapArrayList(bitmapArrayList);
                createNewAdDataHolder.uriList = mediaUriList
                createNewAdDataHolder.title = title
                createNewAdDataHolder.description = description
                createNewAdDataHolder.phoneNumber = phoneNumber
                createNewAdDataHolder.location = location
                createNewAdDataHolder.isCompany = isCompany
                if (latitude != null && longitude != null) {
                    createNewAdDataHolder.lon = longitude
                    createNewAdDataHolder.lat = latitude
                }
                if (categoryId == "3") {
                    if (!isValidUrl(websiteUrl.trim { it <= ' ' }) || websiteUrl.trim { it <= ' ' } == "") {
                        binding.newAdWebsiteUrlEditTextId.error =
                            activityResources.getString(R.string.toast_check_website_url)
                        binding.newAdWebsiteUrlEditTextId.requestFocus()
                        //progressBar.setVisibility(View.GONE)
                        return@OnClickListener
                    } else {
                        createNewAdDataHolder.websiteUrl = websiteUrl
                    }
                }
                if (categoryId == "2") {
                    createNewAdDataHolder.duration = videoDuration.toString()
                    if (videoThumbnail != null) {
                        createNewAdDataHolder.videoThumbnail = videoThumbnail
                    }
                } else {
                    createNewAdDataHolder.duration = adPackages.seconds.toString()
                }
                val intent = Intent(this@CreateNewAdActivity, NewAdSummeryActivity::class.java)
                intent.putExtra(NEW_AD_DATA, createNewAdDataHolder)
                intent.putExtra(CATEGORY_ID, categoryId)
                /*intent.putExtra(PACKAGE_TIME_LONG_ID, timeLong)
                intent.putExtra(PACKAGE_PRICE, packagePrice)
                intent.putExtra(PACKAGE_ID, packageId)
                intent.putExtra(PACKAGE_REACH_NUMBER_ID, reachNumber)
                intent.putExtra(NUMBER_OF_IMAGES_ID, imagesNumber)*/
                intent.putExtra(AD_PACKAGE_ID, adPackages)
                startActivity(intent)
            } else {
                Toast.makeText(
                    activityContext,
                    activityResources.getString(R.string.toast_fill_out_fields),
                    Toast.LENGTH_SHORT
                ).show();
            }
        })

        binding.newAdLocationTextViewId.setOnClickListener {
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
                        LOCATION_PERMISSION_REQUEST_CODE
                    )
                } else {
                    ActivityHelper.goToActivity(
                        this@CreateNewAdActivity,
                        MapActivity::class.java, false
                    )
                }
            }
        }
    }

    fun isServicesOk(): Boolean {
        val available = GoogleApiAvailability.getInstance()
            .isGooglePlayServicesAvailable(this@CreateNewAdActivity)
        if (available == ConnectionResult.SUCCESS) {
            return true
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            val dialog = GoogleApiAvailability.getInstance()
                .getErrorDialog(this@CreateNewAdActivity, available, ERROR_DIALOG_REQUEST)
            dialog!!.show()
        } else {
            Toast.makeText(
                activityContext,
                activityResources!!.getString(R.string.toast_sorry_cant_open_map),
                Toast.LENGTH_SHORT
            ).show();
        }
        return false
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
                    //imageUri.getPath();
                    mediaUriList.add(imageUri)
                    bitmapArrayList.add(selectedImage)

                    binding.uploadImageRecyclerViewId.visibility = View.VISIBLE
                    selectedImagesAdapter!!.notifyDataSetChanged()
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
        } else if (resultCode == RESULT_OK && reqCode == RESULT_LOAD_VIDEO && data != null && data.data != null) {
            try {
                if (videoUriList == null) {
                    videoUriList = java.util.ArrayList()
                }
                if (videoUriList.size == 1) {
                    Toast.makeText(
                        activityContext,
                        activityResources.getString(R.string.toast_cant_upload_more_the_one_videos),
                        Toast.LENGTH_SHORT
                    ).show();
                    return
                }
                val retriever = MediaMetadataRetriever()
                //use one of overloaded setDataSource() functions to set your data source
                retriever.setDataSource(this@CreateNewAdActivity, data.data)
                val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                val timeInMillisec = time!!.toLong()
                videoDuration = timeInMillisec / 1000
                retriever.release()

                if (timeInMillisec <= adPackages.seconds * 1000 + 1000) {
                    videoUriList.add(data.data!!)
                    mediaUriList.add(data.data!!)
                    videoThumbnail = createThumbnail(this@CreateNewAdActivity, videoUriList[0])
                    binding.uploadImageRecyclerViewId.visibility = View.VISIBLE
                    selectedImagesAdapter = SelectedImagesAdapter(
                        videoUriList,
                        bitmapArrayList,
                        null,
                        categoryId,
                        activityResources,
                        this
                    )
                    binding.uploadImageRecyclerViewId.adapter = selectedImagesAdapter
                    selectedImagesAdapter!!.notifyDataSetChanged()
                } else {
                    Toast.makeText(
                        activityContext,
                        activityResources.getString(R.string.toast_sorry_your_video_must_not_longer) + adPackages.seconds + " " + activityResources.getString(
                            R.string.toast_second
                        ), Toast.LENGTH_SHORT
                    ).show();

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            Toast.makeText(
                activityContext,
                activityResources.getString(R.string.toast_havent_picked_Image),
                Toast.LENGTH_SHORT
            ).show();
        }
    }

    private fun createThumbnail(context: Context, uriPath: Uri): Uri? {
        return try {
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = context.contentResolver.query(uriPath, filePathColumn, null, null, null)
            cursor!!.moveToFirst()
            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
            val picturePath = cursor.getString(columnIndex)
            cursor.close()
            val bitmap = ThumbnailUtils.createVideoThumbnail(
                picturePath,
                MediaStore.Video.Thumbnails.MICRO_KIND
            )

            /*ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                  bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);*/
            val path =
                MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Title", null)
            Uri.parse(path)
        } catch (e: java.lang.Exception) {
            null
        }
    }


    override fun onHandleDeletingMedia(index: Int, isImage: Boolean) {
        bitmapArrayList.isNotEmpty()
        if (isImage) {
            if (bitmapArrayList.isNotEmpty())
                bitmapArrayList.removeAt(index)
        } else {
            if (videoUriList.isNotEmpty())
                videoUriList.removeAt(index)
        }
        if (mediaUriList.isNotEmpty())
            mediaUriList.removeAt(index)

        selectedImagesAdapter?.notifyDataSetChanged()
    }

    private fun executeUploadTask() {
        val postId = FirebaseDatabase.getInstance().reference.push().key
        val storageReference =
            FirebaseStorage.getInstance().reference.child(postId!!) /*.setValue(imageUploadModel)*/
        //databaseR eference.child(uploadId).setValue(imageUploadModel);
        val uploadTask = storageReference.putBytes(uploadBytes)
        uploadTask.addOnSuccessListener { taskSnapshot ->

            Toast.makeText(activityContext, "post success", Toast.LENGTH_SHORT).show();

            val firebaseUri = taskSnapshot.storage.downloadUrl

            val imageUpload = ImageUploadModel(taskSnapshot.storage.downloadUrl.toString())
            //String uploadId=databaseReference.push().getKey();
            databaseReference!!.child(postId).setValue(imageUpload)
            postIdArrayList.add(postId)

        }.addOnFailureListener {
            Toast.makeText(
                activityContext,
                "could not upload photo",
                Toast.LENGTH_SHORT
            ).show()
        }
            .addOnProgressListener { snapshot ->
                val currentProgress = 100.0 * snapshot.bytesTransferred / snapshot.totalByteCount
                if (currentProgress > progress + 15) {
                    progress = 100.0 * snapshot.bytesTransferred / snapshot.totalByteCount

                    Toast.makeText(activityContext, "$progress%", Toast.LENGTH_SHORT).show();
                }
            }
    }

    fun getBytesFromBitmap(bitmap: Bitmap, quality: Int): ByteArray? {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
        return stream.toByteArray()
    }

    private fun isValidUrl(url: String): Boolean {
        val p = Patterns.WEB_URL
        val m = p.matcher(url.lowercase(Locale.getDefault()))
        return m.matches()
    }

    /* override fun onResume() {
         super.onResume()
         try {
             lat = preferenceHelper.getLat()
             lon = preferenceHelper.getLon()

             if (lat != null && lat != "non" && lon != null && lon != "non") {
                 longitude = lon
                 latitude = lat
                 binding.newAdLocationTextViewId.text = "$lat : $lon"
             }
         } catch (e: java.lang.Exception) {
             Toast.makeText(
                 activityContext,
                 activityResources.getString(R.string.toast_please_try_again_later),
                 Toast.LENGTH_SHORT
             ).show();
         }
     }*/

    override fun onBackPressed() {
        super.onBackPressed()
        preferenceHelper.setLat("non")
        preferenceHelper.setLon("non")
    }

    override fun onClose() {
    }

    override fun onConfirm(actionType: Int) {

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
                    binding.newAdLocationTextViewId.text = locationName
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                    binding.newAdLocationTextViewId.text = "$lat : $lon"
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

    override fun onHandleSelection(position: Int, intent: Intent?, resultCode: Int) {

    }


}