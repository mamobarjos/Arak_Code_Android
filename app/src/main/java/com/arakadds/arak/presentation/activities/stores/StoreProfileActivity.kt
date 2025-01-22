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
import com.arakadds.arak.common.helper.ActivityHelper
import com.arakadds.arak.common.helper.IntentHelper
import com.arakadds.arak.common.helper.LocaleHelper
import com.arakadds.arak.common.helper.connectivity_checker.ConnectionLiveData
import com.arakadds.arak.common.preferaence.IPreferenceHelper
import com.arakadds.arak.common.preferaence.PreferenceManager
import com.arakadds.arak.databinding.ActivityStoreProfileBinding
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
import com.arakadds.arak.presentation.activities.other.ReviewsActivity
import com.arakadds.arak.presentation.activities.other.WebViewActivity
import com.arakadds.arak.presentation.adapters.StoreProductsAdapter
import com.arakadds.arak.presentation.adapters.StoreProductsAdapter.ProductClickEvents
import com.arakadds.arak.presentation.adapters.UsersReviewsAdapter
import com.arakadds.arak.presentation.adapters.UsersReviewsAdapter.ReviewsEvents
import com.arakadds.arak.presentation.dialogs.AppDialogs
import com.arakadds.arak.presentation.dialogs.AppDialogs.addStoreReviewDialog
import com.arakadds.arak.presentation.dialogs.ApplicationDialogs
import com.arakadds.arak.presentation.dialogs.ApplicationDialogs.openAlertDialog
import com.arakadds.arak.presentation.viewmodel.MyProductsViewModel
import com.arakadds.arak.presentation.viewmodel.StoresProductsViewModel
import com.arakadds.arak.presentation.viewmodel.StoresProfileViewModel
import com.arakadds.arak.utils.AppEnums.DialogActionTypes.DISMISS
import com.arakadds.arak.utils.AppProperties
import com.arakadds.arak.utils.Constants.IS_EDIT_ID
import com.arakadds.arak.utils.Constants.MY_PERMISSIONS_REQUEST_CALL_PHONE
import com.arakadds.arak.utils.Constants.REVIEWS
import com.arakadds.arak.utils.Constants.SELECTED_PRODUCT_ID
import com.arakadds.arak.utils.Constants.SELECTED_STORE_ID
import com.arakadds.arak.utils.Constants.SELECTED_STORE_NAME
import com.arakadds.arak.utils.Constants.STORE_ID
import com.arakadds.arak.utils.Constants.STORE_OBJECT_ID
import com.arakadds.arak.utils.GlobalMethodsOldClass
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import dagger.android.AndroidInjection
import io.paperdb.Paper
import javax.inject.Inject

class StoreProfileActivity : BaseActivity(), ProductClickEvents, ReviewsEvents,
    ApplicationDialogs.AlertDialogCallbacks, StoreProductsAdapter.MyProductsCallBacks,
    AppDialogs.DialogStoreReviewCallBack {

    private val preferenceHelper: IPreferenceHelper by lazy { PreferenceManager(applicationContext) }
    lateinit var binding: ActivityStoreProfileBinding
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
        binding = ActivityStoreProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AndroidInjection.inject(this)
        activityContext = this

        language = preferenceHelper.getLanguage()
        updateStoreProfileView(language)
        initData()

        checkNetworkConnection()
        /* GoogleAdsHelper.initBannerGoogleAdView(
             this@StoreProfileActivity,
             binding.storeProfileBannerViewAdViewId
         )*/
        getIntents()
        setStoreProductsAdapter()
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
            startActivity(Intent(this@StoreProfileActivity, HomeActivity::class.java))
            finish()
        }
    }

    private fun updateStoreProfileView(language: String) {
        val context = LocaleHelper.setLocale(this@StoreProfileActivity, language)
        resources = context.resources
        binding.storeProfileProductsStoreTitleTextViewId.text =
            resources.getString(R.string.store_profile_activity_Store_Products)
        binding.storeProfileReviewTitleTextViewId.text =
            resources.getString(R.string.store_profile_activity_Store_Reviews)
        binding.storeProfileFeedbackTitleTextViewId.text =
            resources.getString(R.string.store_profile_activity_Add_Review)
        /*binding.storeProfileFeedbackEditTextId.hint =
            resources.getString(R.string.store_profile_activity_Enter_review)
        binding.storeProfileFeedbackSubmitButtonId.text =
            resources.getString(R.string.rest_password_activity_Submit)*/
        binding.storeProfileViewAllTextViewId.text =
            resources.getString(R.string.Create_store_activity_View_All)

        binding.storeProfileReviewsViewAllTextViewId.text =
            resources.getString(R.string.Create_store_activity_View_All)

        binding.textView7.text =
            resources.getString(R.string.Create_store_activity_Social_media_accounts)

        this.language = language
    }

    fun setListeners() {
        binding.storeProfileBackImageViewId.setOnClickListener { finish() }
        binding.storeProfileFacebookImageFilterButtonId.setOnClickListener {
            if (ActivityHelper.isValidWebUrl(facebookLink)) {
                val intent = Intent(this@StoreProfileActivity, WebViewActivity::class.java)
                intent.putExtra("web_Url", facebookLink)
                intent.putExtra(
                    "page_title",
                    resources.getString(R.string.store_profile_activity_Arak)
                )
                startActivity(intent)
            } else {
                Toast.makeText(
                    activityContext,
                    resources.getString(R.string.toast_Facebook_link_not_valid),
                    Toast.LENGTH_SHORT
                ).show();
            }
        }
        binding.storeProfileInstagramImageFilterButtonId.setOnClickListener {
            if (ActivityHelper.isValidWebUrl(instagramLink)) {
                IntentHelper.startWebActivity(
                    this@StoreProfileActivity,
                    instagramLink,
                    resources.getString(R.string.store_profile_activity_Arak)
                )
            } else {
                Toast.makeText(
                    activityContext,
                    resources.getString(R.string.toast_instagram_link_not_valid),
                    Toast.LENGTH_SHORT
                ).show();
            }
        }
        /*binding.storeProfileTwitterImageFilterButtonId.setOnClickListener {
            if (ActivityHelper.isValidWebUrl(twitterLink)) {
                IntentHelper.startWebActivity(
                    this@StoreProfileActivity,
                    twitterLink,
                    resources.getString(R.string.store_profile_activity_Arak)
                )
            } else {
                Toast.makeText(
                    activityContext,
                    resources.getString(R.string.toast_Twitter_link_not_valid),
                    Toast.LENGTH_SHORT
                ).show();
            }
        }*/
        /*binding.storeProfileLinkImageFilterButtonId.setOnClickListener(View.OnClickListener { v: View? ->
            if (ActivityHelper.isValidWebUrl(linkedinLink)) {
                IntentHelper.startWebActivity(
                    this@StoreProfileActivity,
                    linkedinLink,
                    resources.getString(R.string.store_profile_activity_Arak)
                )
            } else {
                Toast.makeText(
                    activityContext,
                    resources.getString(R.string.toast_linkedin_link_not_valid),
                    Toast.LENGTH_SHORT
                ).show();
            }
        })*/
        /*binding.storeProfileSnapchatImageFilterButtonId.setOnClickListener(View.OnClickListener { v: View? ->
            if (ActivityHelper.isValidWebUrl(snapchatLink)) {
                IntentHelper.startWebActivity(
                    this@StoreProfileActivity,
                    snapchatLink,
                    resources.getString(R.string.store_profile_activity_Arak)
                )
            } else {
                Toast.makeText(
                    activityContext,
                    resources.getString(R.string.toast_Snapchat_link_not_valid),
                    Toast.LENGTH_SHORT
                ).show();
            }
        })*/
        binding.storeProfileYoutubeImageFilterButtonId.setOnClickListener(View.OnClickListener { v: View? ->
            if (URLUtil.isValidUrl(youtubeLink)) {
                IntentHelper.startWebActivity(
                    this@StoreProfileActivity,
                    youtubeLink,
                    resources.getString(R.string.store_profile_activity_Arak)
                )
            } else {
                Toast.makeText(
                    activityContext,
                    resources.getString(R.string.toast_youtube_link_not_valid),
                    Toast.LENGTH_SHORT
                ).show();
            }
        })
        binding.storeProfileAddNewProductTextViewId.setOnClickListener(View.OnClickListener { v: View? ->
            startActivity(Intent(this@StoreProfileActivity,CreateProductActivity::class.java).putExtra("StoreID",storeId))
        })
        binding.storeProfileWhatsAppImageViewId.setOnClickListener(View.OnClickListener { v: View? ->
            val url = "https://api.whatsapp.com/send?phone=$phoneNumber"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        })
        /*binding.storeProfileChatImageViewId.setOnClickListener(View.OnClickListener { *//*if (serviceProviderId != userId) {
                    SharedPreferencesHelper.putSharedPreferencesInt(
                            SharedPreferencesKeys.PROVIDER_ID, productsObjectData.getSingleProductsInformation().getProviderInformation().getProviderId());
                    SharedPreferencesHelper.putSharedPreferencesString(
                            SharedPreferencesKeys.PROVIDER_EMAIL_ID, productsObjectData.getSingleProductsInformation().getProviderInformation().getProviderEmail());
                    SharedPreferencesHelper.putSharedPreferencesString(
                            SharedPreferencesKeys.PROVIDER_FULL_NAME_ID, productsObjectData.getSingleProductsInformation().getProviderInformation().getProviderFullName());
                    SharedPreferencesHelper.putSharedPreferencesString(
                            SharedPreferencesKeys.PROVIDER_IMG_AVATAR_ID, productsObjectData.getSingleProductsInformation().getProviderInformation().getProviderImgAvatar());
                    SharedPreferencesHelper.putSharedPreferencesString(
                            SharedPreferencesKeys.PRODUCT_ID, String.valueOf(productsObjectData.getSingleProductsInformation().getServiceId()));
                    Intent intent = new Intent(SelectedItemDetailsActivity.this, ChatActivity.class);
                    startActivity(intent);
                } else {
                    Toaster.show(resources.getString(R.string.toast_sorry_cant_start_chat));
                }*//*
        })*/
        binding.storeProfilePhoneCallImageViewId.setOnClickListener(View.OnClickListener { v: View? ->
            val number = "tel:$phoneNumber"
            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse(number)
            // Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(
                    this@StoreProfileActivity,
                    Manifest.permission.CALL_PHONE
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this@StoreProfileActivity,
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
        })
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
        binding.storeProfileWebsiteImageViewId.setOnClickListener {
            if (websiteLink != null) {
                if (URLUtil.isValidUrl(websiteLink)) {
                    val intent = Intent(this@StoreProfileActivity, WebViewActivity::class.java)
                    intent.putExtra("web_Url", websiteLink)
                    intent.putExtra("page_title", storeName)
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        activityContext,
                        resources.getString(R.string.toast_website_not_valid),
                        Toast.LENGTH_SHORT
                    ).show();
                }
            }
        }
        binding.storeProfileViewAllTextViewId.setOnClickListener(View.OnClickListener {
            if (storeId == -1) {
                Toast.makeText(
                    activityContext,
                    resources.getString(R.string.toast_cant_complete_request),
                    Toast.LENGTH_SHORT
                ).show();
                return@OnClickListener
            }
            val intent = Intent(
                this@StoreProfileActivity,
                StoreProductsActivity::class.java
            )
            intent.putExtra(SELECTED_STORE_ID, storeId)
            intent.putExtra(SELECTED_STORE_NAME, storeName)
            startActivity(intent)
        })

        binding.storeProfileAddReviewLinearLayoutId.setOnClickListener { v ->
            addStoreReviewDialog(
                this@StoreProfileActivity,
                resources,
                storeId,
                null,
                null,
                this
            )
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
        binding.storeProfileEditImageViewId.setOnClickListener {
            val intent = Intent(
                this@StoreProfileActivity,
                CreateStoreActivity::class.java
            )
            intent.putExtra(STORE_OBJECT_ID, storeObject)
            intent.putExtra(IS_EDIT_ID, true)
            startActivity(intent)
        }

        binding.storeProfileReviewsViewAllTextViewId.setOnClickListener {
            val intent = Intent(
                this@StoreProfileActivity,
                ReviewsActivity::class.java
            )
            intent.putExtra(STORE_ID, storeId)
            intent.putExtra(REVIEWS, storeProductReviewArrayList)
            startActivity(intent)
        }
    }

    private fun setStoreReviewsAdapter() {
        val limitNum = if (storeProductReviewArrayList.size > 2) 2 else storeProductReviewArrayList.size

        usersReviewsAdapter = UsersReviewsAdapter(
            storeProductReviewArrayList,storeName, preferenceHelper.getKeyUserId(),limitNum, this@StoreProfileActivity,
            resources, this, false
        )
        val mLayoutManager =
            LinearLayoutManager(this@StoreProfileActivity, LinearLayoutManager.VERTICAL, false)
        binding.storeProfileReviewsStoreRecyclerViewId.layoutManager = mLayoutManager
        binding.storeProfileReviewsStoreRecyclerViewId.isFocusable = false
        binding.storeProfileReviewsStoreRecyclerViewId.isNestedScrollingEnabled = false
        binding.storeProfileReviewsStoreRecyclerViewId.adapter = usersReviewsAdapter
    }

    private fun setStoreProductsAdapter() {
        storeProductsAdapter = StoreProductsAdapter(
            storeProductArrayList,
            this@StoreProfileActivity,
            language,
            resources,
            this,
            storeName,
            false,
            preferenceHelper,
            this
        )
        val mLayoutManager =
            LinearLayoutManager(this@StoreProfileActivity, LinearLayoutManager.VERTICAL, false)
        binding.storeProfileProductsStoreRecyclerViewId.layoutManager = mLayoutManager
        binding.storeProfileProductsStoreRecyclerViewId.isFocusable = false
        binding.storeProfileProductsStoreRecyclerViewId.isNestedScrollingEnabled = false
        binding.storeProfileProductsStoreRecyclerViewId.adapter = storeProductsAdapter
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
            Glide.with(this@StoreProfileActivity)
                .load(storeObject.imgUrl).apply(options)
                .into(binding.storeProfileImageImageViewId)

            Glide.with(this@StoreProfileActivity)
                .load(storeObject.coverImgUrl).apply(options)
                .into(binding.storeCoverImageViewId)
            storeId = storeObject.id
            binding.storeNameTextViewId.text = storeObject.name
            val createdDate = storeObject.createdAt?.split("T".toRegex())
                ?.dropLastWhile { it.isEmpty() }
                ?.toTypedArray()
            /*binding.storeProfileCreateDateTextViewId.text =
                resources.getString(R.string.store_profile_activity_member_since) + " " + createdDate*/
            binding.storeProfileDescriptionTextReadMoreTextViewId.text =
                storeObject.description
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
            initSocialViews()
            if (storeObject.storeProducts.size > 3) {
                binding.storeProfileViewAllTextViewId.visibility = View.VISIBLE
            }
            /*if (storeObject.isReviewed == 1) {
                binding.storeProfileCreateReviewCardViewId.visibility = View.GONE
            }*/
            if (storeObject.userId == userId) {
                binding.storeProfileAddReviewLinearLayoutId.visibility = View.GONE
            }
            if (isMyStore) {
                binding.storeProfileEditImageViewId.visibility = View.VISIBLE
                binding.storeProfileAddNewProductTextViewId.visibility = View.VISIBLE
            } else {
                //addToFavoriteImageView.setVisibility(View.VISIBLE);
                binding.storeProfileEditImageViewId.visibility = View.GONE
                binding.storeProfileAddNewProductTextViewId.visibility = View.GONE
            }
            storeProductReviewArrayList.addAll(storeObject.storeReviews)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        storeProductsAdapter?.notifyDataSetChanged()
        setStoreReviewsAdapter()
    }

    private fun initSocialViews() {
        if (facebookLink != null) {
            binding.storeProfileFacebookImageFilterButtonId.visibility = View.VISIBLE
        } else {
            binding.storeProfileFacebookImageFilterButtonId.visibility = View.GONE
        }
        if (instagramLink != null) {
            binding.storeProfileInstagramImageFilterButtonId.visibility = View.VISIBLE
        } else {
            binding.storeProfileInstagramImageFilterButtonId.visibility = View.GONE
        }
        /* if (twitterLink != null) {
             binding.storeProfileTwitterImageFilterButtonId.visibility = View.VISIBLE
         } else {
             binding.storeProfileTwitterImageFilterButtonId.visibility = View.GONE
         }
         if (linkedinLink != null) {
             binding.storeProfileLinkImageFilterButtonId.visibility = View.VISIBLE
         } else {
             binding.storeProfileLinkImageFilterButtonId.visibility = View.GONE
         }
         if (snapchatLink != null) {
             binding.storeProfileSnapchatImageFilterButtonId.visibility = View.VISIBLE
         } else {
             binding.storeProfileSnapchatImageFilterButtonId.visibility = View.GONE
         }*/
        if (youtubeLink != null) {
            binding.storeProfileYoutubeImageFilterButtonId.visibility = View.VISIBLE
        } else {
            binding.storeProfileYoutubeImageFilterButtonId.visibility = View.GONE
        }

        if (phoneNumber != null) {
            binding.storeProfileWhatsAppImageViewId.visibility = View.VISIBLE
        } else {
            binding.storeProfileWhatsAppImageViewId.visibility = View.GONE
        }

        if (websiteLink != null) {
            binding.storeProfileWebsiteImageViewId.visibility = View.VISIBLE
        } else {
            binding.storeProfileWhatsAppImageViewId.visibility = View.GONE
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
                                    this@StoreProfileActivity,
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
                        binding.storeProfileAddReviewLinearLayoutId.visibility = View.GONE
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
        val intent = Intent(this@StoreProfileActivity, ProductDetailsActivity::class.java)
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

    override fun onSubmitStoreProductReviewCallback(addStoreProductReviewRequest: AddStoreProductReviewRequest?) {
    }

    override fun onSubmitAdReviewCallback(addAdReviewRequest: AddAdReviewRequest?) {
    }
}