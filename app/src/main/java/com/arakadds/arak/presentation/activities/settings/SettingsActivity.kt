package com.arakadds.arak.presentation.activities.settings

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.arakadds.arak.R
import com.arakadds.arak.common.helper.ActivityHelper
import com.arakadds.arak.common.helper.LocaleHelper
import com.arakadds.arak.common.helper.connectivity_checker.ConnectionLiveData
import com.arakadds.arak.common.preferaence.IPreferenceHelper
import com.arakadds.arak.common.preferaence.PreferenceManager
import com.arakadds.arak.databinding.ActivitySettingsBinding
import com.arakadds.arak.presentation.activities.other.BaseActivity
import com.arakadds.arak.presentation.activities.other.NoInternetConnectionActivity
import com.arakadds.arak.presentation.activities.other.SplashScreenActivity
import com.arakadds.arak.presentation.dialogs.ApplicationDialogs
import com.arakadds.arak.presentation.viewmodel.NotificationsViewModel
import com.arakadds.arak.utils.AppEnums.LanguagesEnums.ARABIC
import com.arakadds.arak.utils.AppEnums.LanguagesEnums.ENGLISH
import com.arakadds.arak.utils.AppProperties
import com.arakadds.arak.utils.GlobalMethodsOldClass
import dagger.android.AndroidInjection
import io.paperdb.Paper
import java.util.Locale
import javax.inject.Inject

class SettingsActivity : BaseActivity(), ApplicationDialogs.AlertDialogCallbacks {

    private val preferenceHelper: IPreferenceHelper by lazy { PreferenceManager(applicationContext) }
    lateinit var binding: ActivitySettingsBinding
    private lateinit var activityContext: Context

    private lateinit var toolbar: Toolbar
    private var connectionLiveData: ConnectionLiveData? = null

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: NotificationsViewModel by viewModels {
        viewModelFactory
    }
    private lateinit var token: String
    private lateinit var language: String
    private lateinit var resources: Resources
    private var isOpen = false
    private lateinit var dialog: ProgressDialog

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connectionLiveData = ConnectionLiveData(this)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AndroidInjection.inject(this)
        activityContext = this

        language = preferenceHelper.getLanguage()
        updateSettingsView(language)
        initData()
        initUi()
        setListeners()
    }

    private fun initData() {
        token = preferenceHelper.getToken()
        if (token == "non" || token.trim().isEmpty()) {
            GlobalMethodsOldClass.askGuestLogin(resources, activityContext)
            return
        }
    }

    private fun updateSettingsView(language: String) {
        val context = LocaleHelper.setLocale(this@SettingsActivity, language)
        resources = context.resources
        binding.settingsTitleTextViewId.text =
            resources.getString(R.string.settings_activity_settings)
        binding.settingsGeneralTitleTextViewId.text =
            resources.getString(R.string.settings_activity_General)
        binding.notificationTitleTextViewId.text =
            resources.getString(R.string.settings_activity_Notifications)
        binding.languageTextViewId.text = resources.getString(R.string.settings_activity_Language)
        binding.feedbackOptionsTitleTextViewId.text =
            resources.getString(R.string.settings_activity_Feedback)
        binding.termsTextViewId.text = resources.getString(R.string.settings_activity_terms)
        binding.aboutUsTextViewId.text = resources.getString(R.string.settings_activity_About_us)
        binding.feedbackTextViewId.text = resources.getString(R.string.settings_activity_Feedback)
        binding.needHelpTextViewId.text = resources.getString(R.string.settings_activity_Need_Help)
        binding.legalPotionsTitleTextViewId.text =
            resources.getString(R.string.settings_activity_Legal)
        binding.privacyTextViewId.text = resources.getString(R.string.settings_activity_Privacy)
        this.language = language
    }

    private fun initUi() {
        dialog = ProgressDialog(this@SettingsActivity)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        binding.notificationSwitchId.isChecked = preferenceHelper.getNotificationStatus()
        binding.biometricSwitchId.isChecked = preferenceHelper.isBiometricEnabled()
    }

    fun setListeners() {
        binding.settingsArrowBackImageViewId.setOnClickListener { finish() }
        binding.feedbackTextViewId.setOnClickListener {
            ActivityHelper.goToActivity(
                this@SettingsActivity,
                FeedBackActivity::class.java, false
            )
        }
        binding.languageLinearLayoutId.setOnClickListener {
            isOpen = if (isOpen) {
                binding.settingsEnglishLanguageTextViewId.visibility = View.GONE
                binding.settingsArabicLanguageTextViewId.visibility = View.GONE
                binding.settingsArabicLanguageViewId.visibility = View.GONE
                binding.settingsEnglishLanguageViewId.visibility = View.GONE
                false
            } else {
                binding.settingsEnglishLanguageTextViewId.visibility = View.VISIBLE
                binding.settingsArabicLanguageTextViewId.visibility = View.VISIBLE
                binding.settingsArabicLanguageViewId.visibility = View.VISIBLE
                binding.settingsEnglishLanguageViewId.visibility = View.VISIBLE
                true
            }
        }
        binding.settingsEnglishLanguageTextViewId.setOnClickListener {

            preferenceHelper.setLanguage(ENGLISH)
            ActivityHelper.goToActivity(
                this@SettingsActivity,
                SplashScreenActivity::class.java, false
            )
        }
        binding.settingsArabicLanguageTextViewId.setOnClickListener {

            preferenceHelper.setLanguage(ARABIC)
            ActivityHelper.goToActivity(
                this@SettingsActivity,
                SplashScreenActivity::class.java, false
            )
        }
        binding.termsLinearLayoutId.setOnClickListener {
            val intent = Intent(this@SettingsActivity, AboutInformationActivity::class.java)
            intent.putExtra("info_label", "terms")
            intent.putExtra("page_title", "Terms and Conditions")
            startActivity(intent)
        }
        binding.aboutUsLinearLayoutId.setOnClickListener {
            val intent = Intent(this@SettingsActivity, AboutInformationActivity::class.java)
            intent.putExtra("info_label", "about_us")
            intent.putExtra("page_title", "About Us")
            startActivity(intent)
        }
        binding.privacyLinearLayoutId.setOnClickListener {
            val intent = Intent(this@SettingsActivity, AboutInformationActivity::class.java)
            intent.putExtra("info_label", "privacy")
            intent.putExtra("page_title", "Privacy")
            startActivity(intent)
        }
        binding.needHelpLinearLayoutId.setOnClickListener {
            val intent = Intent(this@SettingsActivity, AboutInformationActivity::class.java)
            intent.putExtra("info_label", "need_help")
            intent.putExtra("page_title", "Need Help")
            startActivity(intent)
        }
        binding.notificationSwitchId.setOnCheckedChangeListener { buttonView, isChecked ->
            preferenceHelper.setNotificationStatus(isChecked)
        }
        binding.biometricSwitchId.setOnCheckedChangeListener { buttonView, isChecked ->
            preferenceHelper.setBiometricEnabled(isChecked)
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

    private fun changeNotificationsStatus() {
        /*  showLoadingDialog(this, "message")
          viewModel.changeNotificationsStatus(token)
          viewModel.notificationsStatusResponseModel.observe(
              this,
              Observer(function = fun(notificationsStatusResponse: NotificationsStatusResponse?) {
                  notificationsStatusResponse?.let {
                      if (notificationsStatusResponse.statusCode == 200) {
                          val notificationStatus = notificationsStatusResponse.userBalance.roundToInt() == 1
                          preferenceHelper.setNotificationStatus(notificationStatus)
                          dialog.dismiss()
                      } else {
                          openAlertDialog(
                              this,
                              resources?.getString(R.string.dialogs_error),
                              notificationsStatusResponse.description,
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
        )*/
    }

    override fun onClose() {
    }

    override fun onConfirm(actionType: Int) {

    }
}