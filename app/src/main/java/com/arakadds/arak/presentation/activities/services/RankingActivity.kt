package com.arakadds.arak.presentation.activities.services

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.arakadds.arak.R
import com.arakadds.arak.common.helper.LocaleHelper
import com.arakadds.arak.common.helper.connectivity_checker.ConnectionLiveData
import com.arakadds.arak.common.preferaence.IPreferenceHelper
import com.arakadds.arak.common.preferaence.PreferenceManager
import com.arakadds.arak.databinding.ActivityRankingBinding
import com.arakadds.arak.model.new_mapping_refactore.response.banners.auth.UserObject
import com.arakadds.arak.model.new_mapping_refactore.response.banners.ranking.UsersRankingModel
import com.arakadds.arak.presentation.activities.other.BaseActivity
import com.arakadds.arak.presentation.activities.other.NoInternetConnectionActivity
import com.arakadds.arak.presentation.adapters.RankingAdapter
import com.arakadds.arak.presentation.dialogs.ApplicationDialogs
import com.arakadds.arak.presentation.dialogs.ApplicationDialogs.openAlertDialog
import com.arakadds.arak.presentation.viewmodel.ArakRankingViewModel
import com.arakadds.arak.utils.AppEnums.DialogActionTypes.DISMISS
import com.arakadds.arak.utils.AppProperties
import com.arakadds.arak.utils.Constants.BASE_URL
import dagger.android.AndroidInjection
import io.paperdb.Paper
import javax.inject.Inject

class RankingActivity : BaseActivity(), ApplicationDialogs.AlertDialogCallbacks,
    RankingAdapter.RankingCallBacks {

    private val preferenceHelper: IPreferenceHelper by lazy { PreferenceManager(applicationContext) }
    lateinit var binding: ActivityRankingBinding
    private lateinit var activityContext: Context

    private lateinit var toolbar: Toolbar
    private var connectionLiveData: ConnectionLiveData? = null

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: ArakRankingViewModel by viewModels {
        viewModelFactory
    }
    private lateinit var token: String
    private lateinit var language: String
    private lateinit var activityResources: Resources
    private lateinit var rankingAdapter: RankingAdapter
    private val url = BASE_URL + "users/get-top-ranked-users-by-balance"
    private var rankingArrayList = ArrayList<UserObject>()
    private var currentPage = 1
    private var lastPage: Int = 1
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connectionLiveData = ConnectionLiveData(this)
        binding = ActivityRankingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AndroidInjection.inject(this)
        activityContext = this

        language = preferenceHelper.getLanguage()

        updateRankingView(language)
        checkNetworkConnection()
        setUpRankingAdapter()
        getArakRanking(page = currentPage)
        setListeners()
    }

    private fun setUpRankingAdapter() {
        rankingAdapter = RankingAdapter(
            rankingArrayList,
            this@RankingActivity,
            activityResources,
            language,
            preferenceHelper,
            this
        )
        val mLayoutManager = LinearLayoutManager(
            this@RankingActivity,
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.rankingRecyclerViewId.layoutManager = mLayoutManager
        binding.rankingRecyclerViewId.isFocusable = false
        binding.rankingRecyclerViewId.isNestedScrollingEnabled = false
        binding.rankingRecyclerViewId.adapter = rankingAdapter
    }

    private fun updateRankingView(language: String) {
        val context = LocaleHelper.setLocale(this@RankingActivity, language)
        activityResources = context.resources
        binding.rankingTitleTextViewId.text =
            activityResources.getString(R.string.Ranking_activity_Arak_Ranking)
        this.language = language
    }

    fun setListeners() {
        binding.rankingArrowBackImageViewId.setOnClickListener { finish() }
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

    private fun getArakRanking(
        page: Int
    ) {
        showLoadingDialog(this, "message")
        viewModel.getArakRanking(token, language, page)
        viewModel.usersRankingModelModel.observe(
            this,
            Observer(function = fun(usersRankingModel: UsersRankingModel?) {
                usersRankingModel?.let {
                    if (usersRankingModel.statusCode == 200) {
                        rankingArrayList.addAll(
                            usersRankingModel.data.userObjectList
                        )
                        rankingAdapter.notifyDataSetChanged()
                    } else {
                        openAlertDialog(
                            this,
                            activityResources?.getString(R.string.dialogs_error),
                            usersRankingModel.message,
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

    override fun onClose() {
    }

    override fun onConfirm(actionType: Int) {

    }

    override fun onNextPageRequired() {
        if (currentPage < lastPage) {
            getArakRanking(page = currentPage + 1)
        }
    }
}