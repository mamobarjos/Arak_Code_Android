package com.arakadds.arak.presentation.activities.other

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import com.arakadds.arak.R
import com.arakadds.arak.common.helper.ActivityHelper
import com.arakadds.arak.common.helper.LocaleHelper
import com.arakadds.arak.common.helper.connectivity_checker.ConnectionLiveData
import com.arakadds.arak.common.preferaence.IPreferenceHelper
import com.arakadds.arak.common.preferaence.PreferenceManager
import com.arakadds.arak.databinding.ActivitySuccessBinding
import com.arakadds.arak.presentation.activities.authentication.LoginActivity
import com.arakadds.arak.presentation.activities.profile.MyDetailsActivity
import com.arakadds.arak.utils.AppProperties
import com.arakadds.arak.utils.Constants.IS_CHANGE_PASSWORD
import dagger.android.AndroidInjection
import io.paperdb.Paper
class SuccessActivity : BaseActivity() {

    private val preferenceHelper: IPreferenceHelper by lazy { PreferenceManager(applicationContext) }
    lateinit var binding: ActivitySuccessBinding
    private lateinit var activityContext: Context

    private lateinit var toolbar: Toolbar
    private var connectionLiveData: ConnectionLiveData? = null

    private lateinit var token: String
    private lateinit var language: String
    private lateinit var resources: Resources
    private var isChangePassword = "0"
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connectionLiveData = ConnectionLiveData(this)
        binding = ActivitySuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AndroidInjection.inject(this)
        activityContext = this

        language = preferenceHelper.getLanguage()

        updateResetPasswordView(language)
        checkNetworkConnection()
        // initToolbar()
        getIntents()
        initData()
        setListeners()

    }

    private fun initData() {
        token = preferenceHelper.getToken()
    }

    private fun getIntents() {
        try {
            isChangePassword = intent.getStringExtra(IS_CHANGE_PASSWORD)!!
            if (isChangePassword == null) {
                isChangePassword = "0"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            isChangePassword = "0"
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
    private fun updateResetPasswordView(language: String) {
        val context = LocaleHelper.setLocale(this@SuccessActivity, language)
        resources = context.resources
        binding.successTitleTextViewId.text = resources.getString(R.string.Success_activity_Success)
        binding.successDescTextViewId.text = resources.getString(R.string.Success_activity_changed_successfully)
        if (isChangePassword == "1") {
            binding.successLoginButtonId.text = resources.getString(R.string.Success_activity_Continue_to_Profile)
        } else {
            binding.successLoginButtonId.text = resources.getString(R.string.login_activity_Login)
        }
        this.language = language
    }
    fun setListeners() {
        binding.successLoginButtonId.setOnClickListener {
            if (isChangePassword == "1") {
                ActivityHelper.goToActivity(
                    this@SuccessActivity,
                    MyDetailsActivity::class.java, false
                )
                finish()
            } else {
                ActivityHelper.goToActivity(
                    this@SuccessActivity,
                    LoginActivity::class.java, false
                )
                finish()
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

}