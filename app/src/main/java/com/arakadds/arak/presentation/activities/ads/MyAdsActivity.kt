package com.arakadds.arak.presentation.activities.ads

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arakadds.arak.R
import com.arakadds.arak.common.helper.ActivityHelper
import com.arakadds.arak.common.helper.LocaleHelper
import com.arakadds.arak.common.helper.connectivity_checker.ConnectionLiveData
import com.arakadds.arak.common.preferaence.IPreferenceHelper
import com.arakadds.arak.common.preferaence.PreferenceManager
import com.arakadds.arak.databinding.ActivityMyAdsBinding
import com.arakadds.arak.model.new_mapping_refactore.home.AdsData
import com.arakadds.arak.model.new_mapping_refactore.home.HomeAdsModel
import com.arakadds.arak.presentation.activities.home.HomeActivity
import com.arakadds.arak.presentation.activities.other.BaseActivity
import com.arakadds.arak.presentation.activities.other.NoInternetConnectionActivity
import com.arakadds.arak.presentation.adapters.MyAdsAdapter
import com.arakadds.arak.presentation.dialogs.ApplicationDialogs
import com.arakadds.arak.presentation.viewmodel.UserAdsViewModel
import com.arakadds.arak.utils.AppEnums
import com.arakadds.arak.utils.AppProperties
import com.arakadds.arak.utils.Constants
import com.arakadds.arak.utils.Constants.ADS_STATISTICS
import com.arakadds.arak.utils.GlobalMethodsOldClass
import dagger.android.AndroidInjection
import io.paperdb.Paper
import java.io.Serializable
import java.util.Calendar
import javax.inject.Inject

class MyAdsActivity : BaseActivity(), ApplicationDialogs.AlertDialogCallbacks,
    MyAdsAdapter.AdsCallBacks {

    private val preferenceHelper: IPreferenceHelper by lazy { PreferenceManager(applicationContext) }
    lateinit var binding: ActivityMyAdsBinding
    private lateinit var activityContext: Context

    private lateinit var toolbar: Toolbar
    private var connectionLiveData: ConnectionLiveData? = null

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: UserAdsViewModel by viewModels {
        viewModelFactory
    }
    private lateinit var token: String
    private var dateSetListener: OnDateSetListener? = null
    private var myAdsAdapter: MyAdsAdapter? = null
    private var myAdsRecyclerView: RecyclerView? = null
    private lateinit var language: String
    private lateinit var activityResources: Resources
    private var adResponseArrayList = ArrayList<AdsData>()
    private var currentPage = 1
    private var lastPage: Int = 1
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connectionLiveData = ConnectionLiveData(this)
        binding = ActivityMyAdsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AndroidInjection.inject(this)
        activityContext = this

        language = preferenceHelper.getLanguage()

        updateMyAdsView(language)
        checkNetworkConnection()
        initUi()
        initData()
        setListeners()
        setupMyAdsAdapter()
        getMyAds(page = currentPage)
    }
    private fun initData() {
        token = preferenceHelper.getToken()
        if (token == "non" || token.trim().isEmpty()) {
            GlobalMethodsOldClass.askGuestLogin(resources, activityContext)
            return
        }
    }
    private fun updateMyAdsView(language: String) {
        val context = LocaleHelper.setLocale(this@MyAdsActivity, language)
        activityResources = context.resources
        binding.myAdsEmptyViewTextViewId.text =
            activityResources.getString(R.string.error_messages_no_data_found)
        binding.myAdsTitleTextViewId.text =
            activityResources.getString(R.string.my_ads_activity_page_title)
        binding.myAdsPageTitleTextViewId.text =
            activityResources.getString(R.string.my_ads_activity_page_title)
        binding.myDetailsFilterDateTextViewId.text =
            activityResources.getString(R.string.my_ads_activity_Date)
        this.language = language
    }

    private fun initUi() {
        myAdsRecyclerView = findViewById(R.id.my_ads_recycler_view_id)
    }

    fun setListeners() {
        binding.myAdsArrowBackImageViewId.setOnClickListener(View.OnClickListener { v: View? ->
            try {
                ActivityHelper.goToActivity(
                    this@MyAdsActivity,
                    HomeActivity::class.java, false
                )
                finish()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        })
        binding.myDetailsFilterLinearLayoutId.setOnClickListener(View.OnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar[Calendar.YEAR]
            val month = calendar[Calendar.MONTH]
            val day = calendar[Calendar.DAY_OF_MONTH]
            val datePickerDialog = DatePickerDialog(
                this@MyAdsActivity, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                dateSetListener, year, month, day
            )
            //datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePickerDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            datePickerDialog.show()
        })
        dateSetListener =
            OnDateSetListener { datePicker, year, month, dayOfMonth ->
                var month = month
                month += 1
                val date: String
                var day: String? = null
                val monthOfYear: String = if (month < 10) {
                    "0$month"
                } else {
                    "" + month
                }
                day = if (dayOfMonth < 10) {
                    "0$dayOfMonth"
                } else {
                    "" + dayOfMonth
                }
                date = "$year-$monthOfYear"

                binding.myDetailsFilterDateTextViewId.text = date
                adResponseArrayList = ArrayList()
                //getMyFilteredAds("$filterUrl$year/$month")
            }
    }

    private fun getMyAds(
        dateFrom: String? = null,
        dateTo: String? = null,
        page: Int? = null
    ) {
        showLoadingDialog(this, "message")
        viewModel.getMyAds("Bearer $token", language, dateFrom, dateTo, page)
        viewModel.homeAdsModelModel.observe(
            this,
            Observer(function = fun(homeAdsModel: HomeAdsModel?) {
                homeAdsModel?.let {
                    if (homeAdsModel.statusCode == 200) {
                        adResponseArrayList.addAll(homeAdsModel.homeAdsData.adsDataArrayList)
                        currentPage = homeAdsModel.homeAdsData.page
                        lastPage = homeAdsModel.homeAdsData.lastPage
                        myAdsAdapter?.notifyDataSetChanged()
                        if (adResponseArrayList.size == 0) {
                            binding.myAdsEmptyViewImageViewId.visibility = View.VISIBLE
                            binding.myAdsEmptyViewTextViewId.visibility = View.VISIBLE
                        } else {
                            binding.myAdsEmptyViewImageViewId.visibility = View.GONE
                            binding.myAdsEmptyViewTextViewId.visibility = View.GONE
                        }
                    } else {
                        ApplicationDialogs.openAlertDialog(
                            this,
                            activityResources?.getString(R.string.dialogs_error),
                            homeAdsModel.message,
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
                        activityResources?.getString(R.string.dialogs_error),
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

    private fun setupMyAdsAdapter() {
        myAdsAdapter =
            MyAdsAdapter(adResponseArrayList, this@MyAdsActivity, language, activityResources,preferenceHelper,this)
        val mLayoutManager =
            LinearLayoutManager(this@MyAdsActivity, LinearLayoutManager.VERTICAL, false)
        myAdsRecyclerView!!.layoutManager = mLayoutManager
        myAdsRecyclerView!!.isFocusable = false
        myAdsRecyclerView!!.isNestedScrollingEnabled = false
        myAdsRecyclerView!!.adapter = myAdsAdapter
    }
    /*
        private fun getMyFilteredAds(url: String) {
            showLoadingDialog(this, "message")
            viewModel.getMyFilteredAds(token, url)
            viewModel.homeAdsResponseDataModel.observe(
                this,
                Observer(function = fun(homeAdsResponseData: HomeAdsResponseData?) {
                    homeAdsResponseData?.let {
                        if (homeAdsResponseData.statusCode == 200) {
                            adResponseArrayList.addAll(homeAdsResponseData.data.dataArrayList)

                            val myAdsAdapter = MyAdsAdapter(
                                adResponseArrayList,
                                this@MyAdsActivity,
                                language,
                                activityResources
                            )
                            val mLayoutManager = LinearLayoutManager(
                                this@MyAdsActivity,
                                LinearLayoutManager.VERTICAL,
                                false
                            )
                            myAdsRecyclerView!!.layoutManager = mLayoutManager
                            myAdsRecyclerView!!.isFocusable = false
                            myAdsRecyclerView!!.isNestedScrollingEnabled = false
                            myAdsRecyclerView!!.adapter = myAdsAdapter
                            myAdsAdapter.notifyDataSetChanged()
                            binding.myAdsNestedScrollViewId.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                                if (scrollY == v.getChildAt(0)
                                        .measuredHeight - v.measuredHeight
                                ) {
                                    try {
                                        if (homeAdsResponseData.data.adsObjectDataArrayList
                                                .nextPageUrl != null
                                        ) {
                                            getMyAds(
                                                homeAdsResponseData.data.adsObjectDataArrayList
                                                    .nextPageUrl
                                            )
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                            })
                            if (adResponseArrayList.size == 0) {
                                binding.myAdsEmptyViewImageViewId.visibility = View.VISIBLE
                                binding.myAdsEmptyViewTextViewId.visibility = View.VISIBLE
                            } else {
                                binding.myAdsEmptyViewImageViewId.visibility = View.GONE
                                binding.myAdsEmptyViewTextViewId.visibility = View.GONE
                            }
                        } else {
                            ApplicationDialogs.openAlertDialog(
                                this,
                                activityResources?.getString(R.string.dialogs_error),
                                homeAdsResponseData.description,
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
                        activityResources?.getString(R.string.dialogs_error),
                        throwable.message,
                        false,
                        AppEnums.DialogActionTypes.DISMISS,
                        this
                    )
                }
                hideLoadingDialog()
            })
        )
    }*/

    override fun onBackPressed() {
        super.onBackPressed()
        try {
            ActivityHelper.goToActivity(
                this@MyAdsActivity,
                HomeActivity::class.java, false
            )
            finish()
        } catch (e: java.lang.Exception) {
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

    override fun onClose() {

    }

    override fun onConfirm(actionType: Int) {
    }

    override fun onNextPageRequired() {
        if (currentPage < lastPage) {
            getMyAds(page = currentPage + 1)
            //todo .. handle date filtration
            /*if (dateFrom.trim().isNotEmpty() && dateTo.trim().isNotEmpty()) {
                getMyAds(page = currentPage + 1)
            } else {
                getMyAds(dateFrom = searchName, dateTo = dateTo, page = currentPage + 1)
            }*/
        }
    }

    override fun onAdSelected(adsData: AdsData) {
        val intent = Intent(this@MyAdsActivity, MyAdDetailsActivity::class.java)
        intent.putExtra(ADS_STATISTICS, adsData as Parcelable)
        //intent.putExtra(Constants.CATEGORY_ID, adsData.adCategoryId)
        startActivity(intent)
    }
}