package com.arakadds.arak.presentation.activities.other

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import com.arakadds.arak.common.helper.LocaleHelper
import com.arakadds.arak.common.helper.connectivity_checker.ConnectionLiveData
import com.arakadds.arak.common.preferaence.IPreferenceHelper
import com.arakadds.arak.common.preferaence.PreferenceManager
import com.arakadds.arak.databinding.ActivityWebViewBinding
import com.arakadds.arak.utils.AppProperties
import dagger.android.AndroidInjection
import io.paperdb.Paper

class WebViewActivity : BaseActivity() {

    private val preferenceHelper: IPreferenceHelper by lazy { PreferenceManager(applicationContext) }
    lateinit var binding: ActivityWebViewBinding
    private lateinit var activityContext: Context

    private lateinit var toolbar: Toolbar
    private var connectionLiveData: ConnectionLiveData? = null

    private lateinit var token: String
    private lateinit var language: String
    private var resources: Resources? = null
    private  var webUrl: String? =null
    private  var pageTitle:String? = null
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connectionLiveData = ConnectionLiveData(this)
        binding = ActivityWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AndroidInjection.inject(this)
        activityContext = this

        language = preferenceHelper.getLanguage()

        updateLoginView(language)
        checkNetworkConnection()
        //initToolbar()
        initData()
        getIntents()
        handelUi()
        setListeners()

    }

    private fun initData() {
        token = preferenceHelper.getToken()
    }

    private fun getIntents() {
        val intent = intent
        try {
            webUrl = intent.getStringExtra("web_Url")!!
        } catch (e: Exception) {
            e.printStackTrace()
            webUrl = null
        }

        pageTitle = try {
            intent.getStringExtra("page_title")
        } catch (e: Exception) {
            e.printStackTrace()
            null
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
    @SuppressLint("SetJavaScriptEnabled")
    private fun handelUi() {
        if (webUrl != null) {
            binding.webviewId.visibility = View.VISIBLE
            binding.webviewId.settings.javaScriptEnabled = true
            binding.webviewId.settings.domStorageEnabled = true
            binding.webviewId.loadUrl(webUrl!!)
            binding.webviewId.webViewClient = WebViewClient()

            /*binding.webviewId.setWebViewClient(new WebViewClient());
            binding.webviewId.loadUrl();*/
        }
        if (pageTitle != null) {
            binding.settingsTitleTextViewId.text = pageTitle
        }
    }

    fun setListeners() {
       binding.settingsArrowBackImageViewId .setOnClickListener { finish() }
    }

    private fun updateLoginView(language: String) {
        val context = LocaleHelper.setLocale(this@WebViewActivity, language)
        resources = context.resources

        //welcomeTextView.setText(resources.getString(R.string.login_activity_welcome));
        this.language = language
    }
}