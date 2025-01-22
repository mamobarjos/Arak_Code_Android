package com.arakadds.arak.presentation.activities.payments

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import com.arakadds.arak.R
import com.arakadds.arak.common.helper.LocaleHelper
import com.arakadds.arak.common.helper.connectivity_checker.ConnectionLiveData
import com.arakadds.arak.common.preferaence.IPreferenceHelper
import com.arakadds.arak.common.preferaence.PreferenceManager
import com.arakadds.arak.databinding.ActivityPaymentWebBinding
import com.arakadds.arak.model.CreateNewAdDataHolder
import com.arakadds.arak.model.new_mapping_refactore.response.banners.packages.AdPackages
import com.arakadds.arak.presentation.activities.ArakStore.ThankYouActivity
import com.arakadds.arak.presentation.activities.ads.MyAdsActivity
import com.arakadds.arak.presentation.activities.other.BaseActivity
import com.arakadds.arak.presentation.activities.other.NoInternetConnectionActivity
import com.arakadds.arak.presentation.dialogs.AppDialogs
import com.arakadds.arak.utils.AppProperties
import com.arakadds.arak.utils.Constants
import com.arakadds.arak.utils.Constants.CATEGORY_ID
import com.arakadds.arak.utils.Constants.CHECK_OUT_URL_ID
import com.arakadds.arak.utils.Constants.NEW_AD_DATA
import com.arakadds.arak.utils.Constants.UPLOADED_MEDIA_ARRAY_LIST
import com.arakadds.arak.utils.GlobalMethodsOldClass
import dagger.android.AndroidInjection
import io.paperdb.Paper
class PaymentWebActivity : BaseActivity(), AppDialogs.DialogCallBack{

    private val preferenceHelper: IPreferenceHelper by lazy { PreferenceManager(applicationContext) }
    lateinit var binding: ActivityPaymentWebBinding
    private lateinit var activityContext: Context

    private lateinit var toolbar: Toolbar
    private var connectionLiveData: ConnectionLiveData? = null

    private lateinit var token: String
    private var paymentWebUrl: String? = null
    private var imageStringUri: String? = null
    private lateinit var activityResources: Resources
    private lateinit var language: String
    private var createNewAdDataHolder = CreateNewAdDataHolder()
    private var categoryId: String? = null
    private var adPackages = AdPackages()
    private var PAYMENTTYPE: String? = null
    var mediaUriList: List<Uri> = ArrayList()
    private lateinit var mediaStringArrayList: ArrayList<String>
    private var dialogCallBack: AppDialogs.DialogCallBack? = null
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connectionLiveData = ConnectionLiveData(this)
        binding = ActivityPaymentWebBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AndroidInjection.inject(this)
        activityContext = this

        language = preferenceHelper.getLanguage()

        updatePaymentWebView(language)
        getIntentData()
        initUi()
        setListeners()
    }

    private fun getIntentData() {
        val intent = intent
        paymentWebUrl = intent.getStringExtra(CHECK_OUT_URL_ID)
        PAYMENTTYPE=intent.getStringExtra(Constants.PAYMENT_TYPE)

        try {
            val intent = intent
            val args = intent.getBundleExtra(UPLOADED_MEDIA_ARRAY_LIST)
            mediaStringArrayList =
                args!!.getSerializable("ARRAYLIST") as ArrayList<String>
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (!PAYMENTTYPE.equals("Store")) {
            try {
                createNewAdDataHolder =
                    intent.getParcelableExtra<CreateNewAdDataHolder>(NEW_AD_DATA)!!
                categoryId = intent.getStringExtra(CATEGORY_ID)
                adPackages = intent.getParcelableExtra(Constants.AD_PACKAGE_ID)!!
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun initUi() {
        if (paymentWebUrl != null) {
            val webSettings: WebSettings =binding.paymentWebviewId.settings
            webSettings.javaScriptEnabled = true
            // paymentWebView.setWebViewClient(new Callback());
            // paymentWebView.addJavascriptInterface(new Callback.MyJavaScriptInterface(),"android");
            binding.paymentWebviewId.webViewClient = Callback()
            binding.paymentWebviewId.loadUrl(paymentWebUrl!!)
        }
        dialogCallBack = this
    }

    private fun updatePaymentWebView(language: String) {
        val context = LocaleHelper.setLocale(this@PaymentWebActivity, language)
        activityResources = context.resources
        this.language = language
    }

    private fun setListeners() {
        binding.paymentWebviewId.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(binding.paymentWebviewId, url)

                if (url.contains("status=success")) {
                    if (PAYMENTTYPE.equals("Store")) {
                        startActivity(Intent(this@PaymentWebActivity, ThankYouActivity::class.java))
                        finish()
                    }else {
                        AppDialogs.successDialog(
                            this@PaymentWebActivity,
                            this@PaymentWebActivity,
                            activityResources,
                            MyAdsActivity::class.java,
                            activityResources.getString(R.string.dialogs_ad_post_soon),
                            activityResources.getString(R.string.dialogs_Go_My_Ads),
                            dialogCallBack
                        )
                    }
                } else if (url.contains("status=fail")) {
                    GlobalMethodsOldClass.deleteImageFromFirebaseStorage(
                        mediaStringArrayList,
                        this@PaymentWebActivity,
                        activityResources
                    )
                    Toast.makeText(activityContext,activityResources.getString(R.string.toast_Payment_failure), Toast.LENGTH_SHORT).show();
                    return
                }
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
     private class Callback : WebViewClient() {
        // if your minSdkVersion is 24 you can only use this
        override fun shouldOverrideKeyEvent(view: WebView, event: KeyEvent): Boolean {
            val url = view.url
            return false
        } /*  // inject javascript method 'onUrlChange'
        @Override
        public void onPageFinished(WebView view, String url) {
            view.loadUrl("javascript:window.android.onUrlChange(window.location.href);");
        };

        static class MyJavaScriptInterface {
            @JavascriptInterface
            public void onUrlChange(String url) {
                // Log.d("LOG", "current_url: " + url);
            }
        }*/
    }
    override fun onBackPressed() {
        super.onBackPressed()
        try {
            GlobalMethodsOldClass.deleteImageFromFirebaseStorage(mediaStringArrayList, this@PaymentWebActivity, activityResources)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
    override fun openCameraCallback() {}

    override fun openGalleryCallback() {}
    override fun continuePressedCallback() {
        finish()
    }


}