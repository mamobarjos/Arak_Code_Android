package com.arakadds.arak.presentation.activities.settings

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arakadds.arak.R
import com.arakadds.arak.common.helper.LocaleHelper
import com.arakadds.arak.common.helper.connectivity_checker.ConnectionLiveData
import com.arakadds.arak.common.preferaence.IPreferenceHelper
import com.arakadds.arak.common.preferaence.PreferenceManager
import com.arakadds.arak.databinding.ActivityNotificationBinding
import com.arakadds.arak.model.new_mapping_refactore.response.banners.notification.NotificationObject
import com.arakadds.arak.model.new_mapping_refactore.response.banners.notification.NotificationsModel
import com.arakadds.arak.presentation.activities.home.HomeActivity.Companion.NotificationNum
import com.arakadds.arak.presentation.activities.other.BaseActivity
import com.arakadds.arak.presentation.activities.other.NoInternetConnectionActivity
import com.arakadds.arak.presentation.adapters.NotificationsAdapter
import com.arakadds.arak.presentation.dialogs.ApplicationDialogs
import com.arakadds.arak.presentation.dialogs.ApplicationDialogs.openAlertDialog
import com.arakadds.arak.presentation.viewmodel.NotificationsViewModel
import com.arakadds.arak.utils.AppEnums.DialogActionTypes.DISMISS
import com.arakadds.arak.utils.AppProperties
import com.arakadds.arak.utils.Constants.BASE_URL
import dagger.android.AndroidInjection
import io.paperdb.Paper
import javax.inject.Inject

class NotificationActivity : BaseActivity(), ApplicationDialogs.AlertDialogCallbacks,
    NotificationsAdapter.NotificationsCallBacks {

    private val preferenceHelper: IPreferenceHelper by lazy { PreferenceManager(applicationContext) }
    lateinit var binding: ActivityNotificationBinding
    private lateinit var activityContext: Context

    private lateinit var toolbar: Toolbar
    private lateinit var pageTitle: TextView
    private var connectionLiveData: ConnectionLiveData? = null

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: NotificationsViewModel by viewModels {
        viewModelFactory
    }
    private lateinit var token: String
    private lateinit var language: String
    private lateinit var activityResources: Resources
    private var recyclerView: RecyclerView? = null
    private lateinit var  notificationsAdapter: NotificationsAdapter
    private val notificationsArrayList = ArrayList<NotificationObject>()
    private var currentPage = 1
    private var lastPage: Int = 1
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connectionLiveData = ConnectionLiveData(this)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AndroidInjection.inject(this)
        activityContext = this

        language = preferenceHelper.getLanguage()

        updateNotificationView(language)
        initToolbar()
        initUi()
        initData()
        setUpNotificationsAdapter()
        getNotifications(currentPage)

        NotificationNum=notificationsArrayList.size.toString()
    }

    private fun initToolbar() {
        toolbar = binding.appBarLayout.findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        pageTitle = binding.appBarLayout.findViewById(R.id.page_title)
        val backImageView: ImageView = binding.appBarLayout.findViewById(R.id.back_button)
        //shareImageView = binding.appBarLayout.findViewById(R.id.share_button)
        //favoriteImageView = binding.appBarLayout.findViewById(R.id.favorite_button)
        pageTitle.text = activityResources.getString(R.string.notification_activity_page_title)
        backImageView.setOnClickListener { finish() }
    }
    private fun initData() {
        token = preferenceHelper.getToken()
    }
    private fun updateNotificationView(language: String) {
        val context = LocaleHelper.setLocale(this@NotificationActivity, language)
        activityResources = context.resources

        this.language = language
    }

    private fun initUi() {
        recyclerView = findViewById(R.id.notification_RecyclerView_id)
    }

    private fun getNotifications(page: Int) {
        showLoadingDialog(this, "message")
        viewModel.getNotifications("Bearer $token", language, page)
        viewModel.notificationsModelModel.observe(
            this,
            Observer(function = fun(notificationsModel: NotificationsModel?) {
                notificationsModel?.let {
                    if (notificationsModel.statusCode == 200) {
                        currentPage = notificationsModel.data.page
                        lastPage = notificationsModel.data.lastPage
                        notificationsArrayList.addAll(
                            notificationsModel.data.data.notifications
                        )
                        notificationsAdapter.notifyDataSetChanged()
                        if (notificationsArrayList.size == 0) {
                            binding.notificationEmptyViewImageViewId.visibility = View.VISIBLE
                            binding.notificationEmptyViewTextViewId.visibility = View.VISIBLE
                        } else {
                            binding.notificationEmptyViewImageViewId.visibility = View.GONE
                            binding.notificationEmptyViewTextViewId.visibility = View.GONE
                        }
                    } else {
                        openAlertDialog(
                            this,
                            activityResources?.getString(R.string.dialogs_error),
                            notificationsModel.message,
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
    private fun setUpNotificationsAdapter() {
        notificationsAdapter = NotificationsAdapter(
            notificationsArrayList,
            this@NotificationActivity,
            language,
            activityResources,
            this
        )
        val mLayoutManager = LinearLayoutManager(
            this@NotificationActivity,
            LinearLayoutManager.VERTICAL,
            false
        )
        recyclerView!!.layoutManager = mLayoutManager
        recyclerView!!.isFocusable = false
        recyclerView!!.isNestedScrollingEnabled = false
        recyclerView!!.adapter = notificationsAdapter
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
            getNotifications(page = currentPage + 1)
        }
    }
}