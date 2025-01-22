package com.arakadds.arak.presentation.activities.settings

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.widget.Toast
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
import com.arakadds.arak.databinding.ActivityFeedbackBinding
import com.arakadds.arak.model.new_mapping_refactore.response.banners.other.FeedbackModel
import com.arakadds.arak.presentation.activities.other.BaseActivity
import com.arakadds.arak.presentation.activities.other.NoInternetConnectionActivity
import com.arakadds.arak.presentation.dialogs.ApplicationDialogs
import com.arakadds.arak.presentation.dialogs.ApplicationDialogs.openAlertDialog
import com.arakadds.arak.presentation.viewmodel.SettingsViewModel
import com.arakadds.arak.utils.AppEnums.DialogActionTypes.DISMISS
import com.arakadds.arak.utils.AppProperties
import com.arakadds.arak.utils.Constants
import com.arakadds.arak.utils.GlobalMethodsOldClass
import dagger.android.AndroidInjection
import io.paperdb.Paper
import javax.inject.Inject

class FeedBackActivity : BaseActivity() , ApplicationDialogs.AlertDialogCallbacks {

    private val preferenceHelper: IPreferenceHelper by lazy { PreferenceManager(applicationContext) }
    lateinit var binding: ActivityFeedbackBinding
    private lateinit var activityContext: Context

    private lateinit var toolbar: Toolbar
    private var connectionLiveData: ConnectionLiveData? = null

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: SettingsViewModel by viewModels {
        viewModelFactory
    }
    private lateinit var token: String
    private lateinit var activityResources: Resources
    private lateinit var dialog: ProgressDialog
    private lateinit var language: String
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connectionLiveData = ConnectionLiveData(this)
        binding = ActivityFeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AndroidInjection.inject(this)
        activityContext = this

        language = preferenceHelper.getLanguage()

        updateFeedBackView(language)
        initUi()
        initData()
        checkNetworkConnection()
        setListeners()

    }

    private fun initData() {
        token = preferenceHelper.getToken()
        if (token == "non" || token.trim().isEmpty()) {
            GlobalMethodsOldClass.askGuestLogin(resources, activityContext)
            return
        }
    }

    private fun updateFeedBackView(language: String) {
        val context = LocaleHelper.setLocale(this, language)
        activityResources = context.resources
        binding.feedbackDescTextViewId.text = activityResources.getString(R.string.feedback_activity_feedback)
        binding.feedbackContentEditTextId.hint = activityResources.getString(R.string.feedback_activity_inter_your_feedback)
        binding.feedbackSubmitButtonId.text = activityResources.getString(R.string.rest_password_activity_Submit)
        this.language = language
    }

    private fun initUi() {
        dialog = ProgressDialog(this)
        dialog.setMessage(activityResources!!.getString(R.string.dialogs_Uploading_Sending))
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
    }
    fun setListeners() {
        binding.feedbackExitImageViewId.setOnClickListener { finish() }
        binding.feedbackSubmitButtonId.setOnClickListener {
            val feedbackContent: String = binding.feedbackContentEditTextId.text.toString()
            val feedbackStarsRate: Float = binding.feedbackRateRatingBarId.rating
            if (feedbackStarsRate > 0 && feedbackContent != "") {
                sendUserFeedback(feedbackContent, feedbackStarsRate.toString())
            } else {
                Toast.makeText(activityContext,
                    activityResources.getString(R.string.toast_complete_rating), Toast.LENGTH_SHORT).show();
            }
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

    private fun sendUserFeedback(content: String, rate: String) {
        showLoadingDialog(this, "message")
        val hashMap = HashMap<String, String>()
        hashMap["content"] = content
        hashMap["rating"] = rate
        viewModel.sendUserFeedback(language, "Bearer $token", hashMap)
        viewModel.feedbackModelModel.observe(
            this,
            Observer(function = fun(feedbackModel: FeedbackModel?) {
                feedbackModel?.let {
                    if (feedbackModel.statusCode == 201) {
                        Toast.makeText(activityContext,
                            activityResources.getString(R.string.toast_Thank_You), Toast.LENGTH_SHORT).show();
                        dialog.dismiss()
                        finish()
                    } else {
                        openAlertDialog(
                            this,
                            activityResources?.getString(R.string.dialogs_error),
                            feedbackModel.message,
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


}