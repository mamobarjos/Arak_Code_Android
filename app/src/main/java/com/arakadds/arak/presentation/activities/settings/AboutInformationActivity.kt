package com.arakadds.arak.presentation.activities.settings

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.text.Html
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
import com.arakadds.arak.databinding.ActivityAboutInformationBinding
import com.arakadds.arak.model.new_mapping_refactore.response.banners.other.AboutModel
import com.arakadds.arak.presentation.activities.other.BaseActivity
import com.arakadds.arak.presentation.activities.other.NoInternetConnectionActivity
import com.arakadds.arak.presentation.dialogs.ApplicationDialogs
import com.arakadds.arak.presentation.dialogs.ApplicationDialogs.openAlertDialog
import com.arakadds.arak.presentation.viewmodel.SettingsViewModel
import com.arakadds.arak.utils.AppEnums.DialogActionTypes.DISMISS
import com.arakadds.arak.utils.AppProperties
import dagger.android.AndroidInjection
import io.paperdb.Paper
import java.lang.Exception
import javax.inject.Inject
class AboutInformationActivity : BaseActivity() , ApplicationDialogs.AlertDialogCallbacks{

    private val preferenceHelper: IPreferenceHelper by lazy { PreferenceManager(applicationContext) }
    lateinit var binding: ActivityAboutInformationBinding
    private lateinit var activityContext: Context

    private lateinit var toolbar: Toolbar
    private var connectionLiveData: ConnectionLiveData? = null

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: SettingsViewModel by viewModels {
        viewModelFactory
    }
    private var token: String? = null
    private var activityResources: Resources? = null
    private var aboutLabel: String? = null
    private var pageTitle: String? = null
    private var language = "en"
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connectionLiveData = ConnectionLiveData(this)
        binding = ActivityAboutInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AndroidInjection.inject(this)
        activityContext = this

        language = preferenceHelper.getLanguage()

        updateAboutInformationView(language)
        checkNetworkConnection()
        initToolbar()
        getIntents()
        initData()
        getAboutInformation()
    }

    private fun getIntents() {
        aboutLabel = intent.getStringExtra("info_label").toString()
        pageTitle = intent.getStringExtra("page_title").toString()
    }

    private fun initToolbar() {
        toolbar = binding.appBarLayout.findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val mTitle: TextView  = binding.appBarLayout.findViewById(R.id.page_title)
        val backImageView: ImageView = binding.appBarLayout.findViewById(R.id.back_button)
        //shareImageView = binding.appBarLayout.findViewById(R.id.share_button)
        //favoriteImageView = binding.appBarLayout.findViewById(R.id.favorite_button)

        when (pageTitle) {
            "Terms and Conditions" -> mTitle.text =
                activityResources?.getString(R.string.settings_activity_terms)

            "About Us" -> mTitle.text = activityResources?.getString(R.string.settings_activity_About_us)
            "Need Help" -> mTitle.text = activityResources?.getString(R.string.settings_activity_Need_Help)
            "privacy" -> mTitle.text = activityResources?.getString(R.string.settings_activity_Privacy)
        }
        backImageView.setOnClickListener { finish() }
    }

    private fun updateAboutInformationView(language: String) {
        val context = LocaleHelper.setLocale(this@AboutInformationActivity, language)
        activityResources = context.resources

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

    private fun initData() {
        token = preferenceHelper.getToken()
    }

    private fun getAboutInformation() {
        showLoadingDialog(this, "message")
        token?.let { viewModel.getAboutInformation(it,language) }
        viewModel.aboutModelModel.observe(
            this,
            Observer(function = fun(aboutModel: AboutModel?) {
                aboutModel?.let {
                    if (aboutModel.statusCode == 200) {

                        when (aboutLabel) {
                            "terms" -> if ((language == "en")) {
                                binding.aboutTextViewId.text = Html.fromHtml(
                                    aboutModel.data.termsEn
                                )
                            } else if ((language == "ar")) {
                                binding.aboutTextViewId.text = Html.fromHtml(
                                    aboutModel.data.termsAr
                                )
                            }

                            "about_us" -> if ((language == "en")) {
                                binding.aboutTextViewId.text = Html.fromHtml(
                                    aboutModel.data.aboutEn
                                )
                            } else if ((language == "ar")) {
                                binding.aboutTextViewId.text = Html.fromHtml(
                                    aboutModel.data.aboutAr
                                )
                            }

                            "privacy" -> if ((language == "en")) {
                                binding.aboutTextViewId.text = Html.fromHtml(
                                    aboutModel.data.privacyEn
                                )
                            } else if ((language == "ar")) {
                                binding.aboutTextViewId.text = Html.fromHtml(
                                    aboutModel.data.privacyAr
                                )
                            }

                            "need_help" -> try {
                                if ((language == "en")) {
                                    binding.aboutTextViewId.text = Html.fromHtml(
                                        aboutModel.data.needHelpEn
                                    )
                                } else if ((language == "ar")) {
                                    binding.aboutTextViewId.text = Html.fromHtml(
                                        aboutModel.data.needHelpAr
                                    )
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    } else {
                        openAlertDialog(
                            this,
                            activityResources?.getString(R.string.dialogs_error),
                            aboutModel.message,
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