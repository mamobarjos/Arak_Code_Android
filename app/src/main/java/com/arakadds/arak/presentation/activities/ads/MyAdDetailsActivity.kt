package com.arakadds.arak.presentation.activities.ads

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.arakadds.arak.R
import com.arakadds.arak.common.helper.LocaleHelper
import com.arakadds.arak.common.helper.connectivity_checker.ConnectionLiveData
import com.arakadds.arak.common.preferaence.IPreferenceHelper
import com.arakadds.arak.common.preferaence.PreferenceManager
import com.arakadds.arak.databinding.ActivityMyAdDetailsBinding
import com.arakadds.arak.model.new_mapping_refactore.BaseResponse
import com.arakadds.arak.model.new_mapping_refactore.home.AdsData
import com.arakadds.arak.presentation.activities.other.BaseActivity
import com.arakadds.arak.presentation.activities.other.NoInternetConnectionActivity
import com.arakadds.arak.presentation.activities.stores.StoreProfileActivity
import com.arakadds.arak.presentation.dialogs.ApplicationDialogs
import com.arakadds.arak.presentation.viewmodel.AdDetailsViewModel
import com.arakadds.arak.utils.AppEnums
import com.arakadds.arak.utils.AppEnums.AdsStatus.APPROVED
import com.arakadds.arak.utils.AppEnums.AdsStatus.COMPLETED
import com.arakadds.arak.utils.AppEnums.AdsStatus.DECLINED
import com.arakadds.arak.utils.AppEnums.AdsStatus.PENDING
import com.arakadds.arak.utils.AppEnums.AdsTypeCategories.STORES
import com.arakadds.arak.utils.Constants
import com.arakadds.arak.utils.Constants.ADS_STATISTICS
import com.arakadds.arak.utils.Constants.AD_ID
import com.arakadds.arak.utils.Constants.CATEGORY_ID
import com.arakadds.arak.utils.GlobalMethodsOldClass.askGuestLogin
import dagger.android.AndroidInjection
import org.eazegraph.lib.charts.PieChart
import org.eazegraph.lib.models.PieModel
import javax.inject.Inject


class MyAdDetailsActivity : BaseActivity(), ApplicationDialogs.AlertDialogCallbacks {

    private val preferenceHelper: IPreferenceHelper by lazy { PreferenceManager(applicationContext) }
    lateinit var binding: ActivityMyAdDetailsBinding
    private lateinit var activityContext: Context

    private lateinit var toolbar: Toolbar
    private lateinit var pageTitle: TextView
    private var connectionLiveData: ConnectionLiveData? = null

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: AdDetailsViewModel by viewModels {
        viewModelFactory
    }
    private lateinit var token: String
    private lateinit var language: String
    private lateinit var activityResources: Resources
    private var adsData: AdsData? = null


    private var chart: PieChart? = null

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connectionLiveData = ConnectionLiveData(this)
        binding = ActivityMyAdDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AndroidInjection.inject(this)
        activityContext = this

        language = preferenceHelper.getLanguage()

        initUi()
        updateAboutInformationView(language)
        checkNetworkConnection()
        initToolbar()
        initData()
        getIntents()
        setListeners()
    }

    private fun initToolbar() {
        toolbar = binding.appBarLayout.findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        pageTitle = binding.appBarLayout.findViewById(R.id.page_title)
        val backImageView: ImageView = binding.appBarLayout.findViewById(R.id.back_button)
        //shareImageView = binding.appBarLayout.findViewById(R.id.share_button)
        //favoriteImageView = binding.appBarLayout.findViewById(R.id.favorite_button)
        //pageTitle.text = activityResources.getString(R.string.payment_activity_My_Wallet)
        backImageView.setOnClickListener { finish() }
    }

    private fun initData() {
        token = preferenceHelper.getToken()
        if (token == "non" || token.trim().isEmpty()) {
            askGuestLogin(resources, activityContext)
            return
        }
    }

    private fun getIntents() {
        val intent = intent
        adsData = intent.getParcelableExtra(ADS_STATISTICS)


        adsData?.let { setDataToViews(it) }
    }

    private fun initUi() {
        //titleTextView.setText(pageTitle);
        chart = findViewById(R.id.pie_chart)
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

    private fun setDataToViews(adsData: AdsData) {
        try {
            adsData.adPackage?.reach?.let {
                addToPieChart(
                    it,
                    adsData.views
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            //addToPieChart(0,currentPageData.getViews());
        }
        pageTitle.text = adsData.title

        binding.myAdsDetailsAdTimeBodyTextViewId.text = adsData.duration.toString()
        binding.myAdsDetailsAdPriceBodyTextViewId.text =
            adsData.adPackage?.price + preferenceHelper.getCurrencySymbol()
        when (adsData.adCategory?.id) {
            1 -> if (language == "ar") {
                //adTitleTextView.setText("صورة");
                binding.myAdsDetailsAdTypeBodyTextViewId.text = "اعلان صورة"
            } else {
                //adTitleTextView.setText("Image");
                binding.myAdsDetailsAdTypeBodyTextViewId.text = "Image ad"
                binding.myAdsDetailsAdNumberBodyTextViewId.text =
                    adsData.adPackage?.numberOfImages.toString()
            }

            2 -> if (language == "ar") {
                //adTitleTextView.setText("فيديو");
                binding.myAdsDetailsAdTypeBodyTextViewId.text = "اعلان فيديو"
            } else {
                //adTitleTextView.setText("Video");
                binding.myAdsDetailsAdTypeBodyTextViewId.text = "Video ad"
            }

            3 -> if (language == "ar") {
                //adTitleTextView.setText("ويب");
                binding.myAdsDetailsAdTypeBodyTextViewId.text = "اعلان ويب"
            } else {
                //adTitleTextView.setText("Website");
                binding.myAdsDetailsAdTypeBodyTextViewId.text = "Website ad"
                binding.myAdsDetailsAdNumberBodyTextViewId.text =
                    adsData.adPackage?.numberOfImages.toString()
            }
        }
        if (adsData.status.equals(PENDING)) {
            binding.myAdsDetailsAdStatusDotTextViewId.setTextColor(Color.parseColor("#1B245C"))
            if (language == "ar") {
                binding.myAdsDetailsAdStatusTextViewId.text = "قيد المراجعة"
            } else {
                binding.myAdsDetailsAdStatusTextViewId.text = "pending"
            }
        } else if (adsData.status.equals(APPROVED)) {
            binding.myAdsDetailsAdStatusDotTextViewId.setTextColor(Color.parseColor("#35B413"))
            if (language == "ar") {
                binding.myAdsDetailsAdStatusTextViewId.text = "فعال"
            } else {
                binding.myAdsDetailsAdStatusTextViewId.text = "approved"
            }
        } else if (adsData.status.equals(COMPLETED)) {
            binding.myAdsDetailsAdStatusDotTextViewId.setTextColor(Color.parseColor("#35B413"))
            if (language == "ar") {
                binding.myAdsDetailsAdStatusTextViewId.text = "مكتمل"
            } else {
                binding.myAdsDetailsAdStatusTextViewId.text = "completed"
            }
        } else if (adsData.status.equals(DECLINED)) {
            binding.myAdsDetailsAdStatusDotTextViewId.setTextColor(Color.parseColor("#FF3823"))
            if (language == "ar") {
                binding.myAdsDetailsAdStatusTextViewId.text = "ملغي"
            } else {
                binding.myAdsDetailsAdStatusTextViewId.text = "declined"
            }
        }
    }

    fun setListeners() {
        binding.myAdsDetailsDeleteLinearLayoutId.setOnClickListener {
            AlertDialog.Builder(this@MyAdDetailsActivity)
                .setTitle(activityResources.getString(R.string.dialogs_Delete_Product))
                .setMessage(activityResources.getString(R.string.dialogs_sure_to_delete_ad))
                .setPositiveButton(
                    android.R.string.yes
                ) { dialog: DialogInterface?, which: Int ->
                    adsData?.id?.let { it1 ->
                        deleteAd(
                            it1
                        )
                    }
                } // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
        }
        binding.myAdsViewAdImageViewId.setOnClickListener {
            if (token != "non" && token != null) {
                if (adsData?.adCategoryId == AppEnums.AdsTypeCategories.STORES) {
                    val intent = Intent(this@MyAdDetailsActivity, StoreProfileActivity::class.java)
                    intent.putExtra(Constants.SELECTED_STORE_ID, adsData!!.storeId)
                    startActivity(intent)
                } else {
                    val intent =
                        Intent(this@MyAdDetailsActivity, AdDetailUserViewActivity::class.java)
                    intent.putExtra(AD_ID, adsData?.id.toString())
                    intent.putExtra(
                        CATEGORY_ID,
                        adsData?.adCategoryId.toString()
                    )
                    startActivity(intent)
                    finish()
                }

            } else {
                askGuestLogin(activityResources, this@MyAdDetailsActivity)
            }
        }
        /*binding.myAdsDetailsMakeSpetialLinearLayoutId.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@MyAdDetailsActivity, PlansActivity::class.java)
             intent.putExtra(AD_ID, currentPageData.getId())
             intent.putExtra(IS_AD_ID, true)
             startActivity(intent)
         })*/
    }

    private fun deleteAd(id: Int) {
        showLoadingDialog(this, "message")
        viewModel.deleteAd("Bearer $token", language, id)
        viewModel.baseResponseModel.observe(
            this,
            Observer(function = fun(baseResponse: BaseResponse?) {
                baseResponse?.let {
                    if (baseResponse.statusCode == 200) {
                        val intent = Intent(this@MyAdDetailsActivity, MyAdsActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        ApplicationDialogs.openAlertDialog(
                            this,
                            activityResources.getString(R.string.dialogs_error),
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
                        activityResources.getString(R.string.dialogs_error),
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


    private fun updateAboutInformationView(language: String) {
        val context = LocaleHelper.setLocale(this@MyAdDetailsActivity, language)
        activityResources = context.resources
        //emptyViewTextView.setText(resources.getString(R.string.my_ad_details_activity));
        binding.myAdsDetailsAdTypeTitleTextViewId.text =
            resources.getString(R.string.my_ads_activity_Ads_Type)
        binding.myAdsDetailsAdNumberTitleTextViewId.text =
            resources.getString(R.string.my_ads_activity_Number)
        binding.myAdsDetailsAdTimeTitleTextViewId.text =
            resources.getString(R.string.my_ads_activity_Time)
        binding.myAdsDetailsAdPriceTitleTextViewId.text =
            resources.getString(R.string.my_ads_activity_Price)
        binding.myAdsDetailsMakeSpetialTextViewId.text =
            resources.getString(R.string.my_ads_highlight_ad)
        binding.myAdsDetailsDeleteTextViewId.text = resources.getString(R.string.my_ads_delete)
        binding.myAdsDetailsEditTextViewId.text = resources.getString(R.string.my_ads_edit)
        this.language = language
    }

    private fun addToPieChart(packageViews: Int, currentWatchNumber: Int) {
        // add to pie chart
        chart!!.addPieSlice(
            PieModel(
                activityResources.getString(R.string.my_ad_details_activity),
                packageViews.toFloat(),
                activityResources.getColor(R.color.gray)
            )
        )
        chart!!.addPieSlice(
            PieModel(
                activityResources.getString(R.string.my_ad_details_activity),
                currentWatchNumber.toFloat(),
                activityResources.getColor(R.color.verf_line_color)
            )
        )
        binding.myAdsDetailsAdReachBodyTextViewId.text = """
                ${activityResources.getString(R.string.my_ad_details_activity)}
                $currentWatchNumber
                """.trimIndent()
        chart!!.startAnimation()
    }

    override fun onClose() {

    }

    override fun onConfirm(actionType: Int) {
    }

}