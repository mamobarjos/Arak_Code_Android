package com.arakadds.arak.presentation.activities.ads

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arakadds.arak.R
import com.arakadds.arak.common.helper.LocaleHelper
import com.arakadds.arak.common.helper.connectivity_checker.ConnectionLiveData
import com.arakadds.arak.common.preferaence.IPreferenceHelper
import com.arakadds.arak.common.preferaence.PreferenceManager
import com.arakadds.arak.databinding.ActivityNewAdSummeryBinding
import com.arakadds.arak.model.CreateNewAdDataHolder
import com.arakadds.arak.model.new_mapping_refactore.response.banners.packages.AdPackages
import com.arakadds.arak.presentation.activities.other.BaseActivity
import com.arakadds.arak.presentation.activities.other.NoInternetConnectionActivity
import com.arakadds.arak.presentation.activities.payments.PaymentOptionsActivity
import com.arakadds.arak.presentation.adapters.NewAdSummeryAdapter
import com.arakadds.arak.utils.AppEnums.AdsTypeCategories.VIDEO
import com.arakadds.arak.utils.AppProperties
import com.arakadds.arak.utils.Constants.AD_PACKAGE_ID
import com.arakadds.arak.utils.Constants.CATEGORY_ID
import com.arakadds.arak.utils.Constants.NEW_AD_DATA
import com.arakadds.arak.utils.Constants.PACKAGE_TIME_LONG_ID
import com.arakadds.arak.utils.GlobalMethodsOldClass
import dagger.android.AndroidInjection
import io.paperdb.Paper
import java.io.IOException

class NewAdSummeryActivity : BaseActivity() {

    private val preferenceHelper: IPreferenceHelper by lazy { PreferenceManager(applicationContext) }
    lateinit var binding: ActivityNewAdSummeryBinding
    private lateinit var activityContext: Context

    private lateinit var toolbar: Toolbar
    private lateinit var pageTitle: TextView
    private var connectionLiveData: ConnectionLiveData? = null

    private lateinit var token: String
    private lateinit var summeryImagesRecyclerView: RecyclerView
    private var newAdSummeryAdapter: NewAdSummeryAdapter? = null
    private var createNewAdDataHolder = CreateNewAdDataHolder()
    private var categoryId: String? = null
    private var adPackages = AdPackages()
    private val bitmapArrayList = ArrayList<Bitmap>()
    private val videoUriList = ArrayList<Uri>()
    private lateinit var language: String
    private lateinit var activityResources: Resources

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connectionLiveData = ConnectionLiveData(this)
        binding = ActivityNewAdSummeryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AndroidInjection.inject(this)
        activityContext = this

        language = preferenceHelper.getLanguage()

        updateNewAdSummeryView(language)
        initToolbar()
        getIntentData()
        initUi()
        initData()
        setDataToViews()
        setListeners()
    }

    private fun initToolbar() {
        toolbar = binding.appBarLayout.findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        pageTitle = binding.appBarLayout.findViewById(R.id.page_title)
        val backImageView: ImageView = binding.appBarLayout.findViewById(R.id.back_button)
        //shareImageView = binding.appBarLayout.findViewById(R.id.share_button)
        //favoriteImageView = binding.appBarLayout.findViewById(R.id.favorite_button)
        pageTitle.text = activityResources.getString(R.string.Summery_activity_Summery)
        backImageView.setOnClickListener { finish() }
    }

    private fun updateNewAdSummeryView(language: String) {
        val context = LocaleHelper.setLocale(this@NewAdSummeryActivity, language)
        activityResources = context.resources
        binding.totalAmountHeaderSummeryTextViewId.text =
            activityResources.getString(R.string.Create_New_Ad_activity_Title)
        binding.summeryTitleTextViewId.text =
            activityResources.getString(R.string.Create_New_Ad_activity_Title)
        binding.descriptionHeaderSummeryTextViewId.text =
            activityResources.getString(R.string.Create_New_Ad_activity_Description)
        binding.phoneNumberHeaderTextViewId.text =
            activityResources.getString(R.string.registration_activity_phone_number)
        binding.locationHeaderSummeryTextViewId.text =
            activityResources.getString(R.string.Create_New_Ad_activity_Location)
        binding.websiteHeaderSummeryTextViewId.text =
            activityResources.getString(R.string.Create_New_Ad_activity_Website)
        binding.criditDetailsSummeryTextViewId.text =
            activityResources.getString(R.string.Summery_activity_cart_details)
        binding.totalAmountHeaderSummeryTextViewId.text =
            activityResources.getString(R.string.Summery_activity_total_amount)
        binding.checkoutButtonId.text =
            activityResources.getString(R.string.payment_activity_check_out)
        this.language = language
    }

    private fun initUi() {
        summeryImagesRecyclerView = findViewById(R.id.summery_RecyclerView_id)
        val mLayoutManager =
            LinearLayoutManager(this@NewAdSummeryActivity, LinearLayoutManager.HORIZONTAL, false)
        summeryImagesRecyclerView.layoutManager = mLayoutManager
        newAdSummeryAdapter = if (categoryId == "2") {
            NewAdSummeryAdapter(videoUriList, this@NewAdSummeryActivity, categoryId, true)
        } else {
            NewAdSummeryAdapter(bitmapArrayList, this@NewAdSummeryActivity, categoryId)
        }
        summeryImagesRecyclerView.adapter = newAdSummeryAdapter
    }

    fun setListeners() {
        binding.checkoutButtonId.setOnClickListener {
            val intent = Intent(this@NewAdSummeryActivity, PaymentOptionsActivity::class.java)
            intent.putExtra(NEW_AD_DATA, createNewAdDataHolder)
            intent.putExtra(CATEGORY_ID, categoryId)
            intent.putExtra(PACKAGE_TIME_LONG_ID, createNewAdDataHolder.duration)
            intent.putExtra(AD_PACKAGE_ID, adPackages)
            startActivity(intent)
        }
    }

    private fun getIntentData() {
        val intent = intent
        createNewAdDataHolder = intent.getParcelableExtra(NEW_AD_DATA)!!
        categoryId = intent.getStringExtra(CATEGORY_ID)
        adPackages = intent.getParcelableExtra(AD_PACKAGE_ID)!!
        if (categoryId?.toInt() == VIDEO) {
            videoUriList.addAll(createNewAdDataHolder.uriList)
        } else {
            convertFromUriToBitmap(createNewAdDataHolder.uriList)
        }
    }

    private fun initData() {
        token = preferenceHelper.getToken()
        if (token == "non" || token.trim().isEmpty()) {
            GlobalMethodsOldClass.askGuestLogin(resources, activityContext)
            return
        }
    }

    private fun setDataToViews() {
        var location: String? = null
        binding.titleTextSummeryTextViewId.text = createNewAdDataHolder.title
        binding.descriptionBodySummeryTextViewId.text = createNewAdDataHolder.description
        binding.phoneNumberBodySummeryTextViewId.text = createNewAdDataHolder.phoneNumber
        binding.locationBodySummeryTextViewId.text = createNewAdDataHolder.location
        binding.totalAmountBodySummeryTextViewId.text =
            preferenceHelper.getCurrencySymbol() + adPackages.price

        if (categoryId == "3") {
            binding.websiteBodySummeryTextViewId.text = createNewAdDataHolder.websiteUrl
            binding.websiteHeaderSummeryTextViewId.visibility = View.VISIBLE
            binding.websiteBodySummeryTextViewId.visibility = View.VISIBLE
        }

        location = binding.locationBodySummeryTextViewId.text.toString()
        if (location.isEmpty()) {
            binding.locationHeaderSummeryTextViewId.visibility = View.GONE
            binding.locationBodySummeryTextViewId.visibility = View.GONE
        }
        newAdSummeryAdapter = if (categoryId == "2") {
            NewAdSummeryAdapter(videoUriList, this@NewAdSummeryActivity, categoryId, true)
        } else {
            NewAdSummeryAdapter(bitmapArrayList, this@NewAdSummeryActivity, categoryId)
        }
        summeryImagesRecyclerView.adapter = newAdSummeryAdapter
        newAdSummeryAdapter!!.notifyDataSetChanged()
    }

    private fun convertFromUriToBitmap(uriList: List<Uri>) {
        try {
            for (i in uriList.indices) {
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uriList[i])
                bitmapArrayList.add(bitmap)
            }
        } catch (e: IOException) {
            e.printStackTrace()

        } catch (e: Exception) {
            e.printStackTrace()
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
}