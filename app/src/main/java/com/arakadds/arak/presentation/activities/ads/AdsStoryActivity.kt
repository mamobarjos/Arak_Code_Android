package com.arakadds.arak.presentation.activities.ads

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView.ScaleType
import androidx.annotation.RequiresApi
import com.arakadds.arak.R
import com.arakadds.arak.common.helper.LocaleHelper
import com.arakadds.arak.common.helper.connectivity_checker.ConnectionLiveData
import com.arakadds.arak.common.preferaence.IPreferenceHelper
import com.arakadds.arak.common.preferaence.PreferenceManager
import com.arakadds.arak.databinding.ActivityAdsStoryBinding
import com.arakadds.arak.model.new_mapping_refactore.home.AdsData
import com.arakadds.arak.model.new_mapping_refactore.store.StoreProductFile
import com.arakadds.arak.presentation.activities.other.BaseActivity
import com.arakadds.arak.presentation.activities.other.NoInternetConnectionActivity
import com.arakadds.arak.presentation.adapters.SelectedProductImagesViewPagerAdapter
import com.arakadds.arak.presentation.dialogs.ApplicationDialogs
import com.arakadds.arak.utils.AppProperties
import com.arakadds.arak.utils.Constants.AD_ID
import com.arakadds.arak.utils.Constants.AD_VEDIO
import com.arakadds.arak.utils.Constants.CATEGORY_ID
import com.arakadds.arak.utils.Constants.IS_HOME
import com.arakadds.arak.utils.Constants.POSITION
import com.arakadds.arak.utils.Constants.PRODUCT_FILES_INFORMATION
import com.arakadds.arak.utils.Constants.selectedAdObject
import com.arakadds.arak.utils.CountDownTimer
import com.arakadds.arak.utils.GlobalMethodsOldClass
import com.bumptech.glide.request.RequestOptions
import dagger.android.AndroidInjection
import io.paperdb.Paper

class AdsStoryActivity : BaseActivity(), ApplicationDialogs.AlertDialogCallbacks {


    inner class CountDown
    /**
     * @param millisInFuture    The number of millis in the future from the call
     * to [.start] until the countdown is done and [.onFinish]
     * is called.
     * @param countDownInterval The interval along the way to receive
     * [.onTick] callbacks.
     */
        (millisInFuture: Long, countDownInterval: Long) :
        CountDownTimer(millisInFuture, countDownInterval) {
        override fun onTick(millisUntilFinished: Long) {
            val minutes = millisUntilFinished / 60000
            val seconds = millisUntilFinished % 60000 / 1000
            var timeLeftText: String
            timeLeftText = "" + minutes
            timeLeftText += ":"
            if (seconds < 10) timeLeftText += "0"
            timeLeftText += seconds
            binding.timerTextViewId.text = timeLeftText

        }

        override fun onFinish() {
            binding.learnMoreTextViewId.visibility = View.VISIBLE
            binding.timerTextViewId.visibility = View.GONE
            isPlaying = false

        }
    }

    private val preferenceHelper: IPreferenceHelper by lazy { PreferenceManager(applicationContext) }
    lateinit var binding: ActivityAdsStoryBinding
    private lateinit var activityContext: Context

    private var connectionLiveData: ConnectionLiveData? = null

    private lateinit var token: String
    private var currentPageData: AdsData? = null
    private var adVideoUrl: String? = null
    private var timeLeftInMilliSeconds: Long = 0
    private lateinit var selectedProductImagesViewPagerAdapter: SelectedProductImagesViewPagerAdapter
    private var title: String? = null
    private var id = 0
    private lateinit var language: String
    private lateinit var activityResources: Resources
    private var adFilesArrayList: java.util.ArrayList<AdsData>? = null
    private var isHomeAd = true
    var runnable: Runnable? = null
    var handler: Handler? = null
    private var isPlaying = true
    private var isVideoStart: Boolean = false
    private var isTimerPaused: Boolean = false
    var countDownTimer: CountDownTimer? = null
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connectionLiveData = ConnectionLiveData(this)
        binding = ActivityAdsStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AndroidInjection.inject(this)
        activityContext = this

        language = preferenceHelper.getLanguage()

        updateAdsStoryView(language)
        checkNetworkConnection()

        initData()
        initViews()
        setListeners()
    }

    private fun setListeners() {

        binding.existImageViewId.setOnClickListener { v: View? -> finish() }
        binding.learnMoreTextViewId.setOnClickListener {
            if (token != "non") {
                val intent = Intent(this@AdsStoryActivity, AdDetailUserViewActivity::class.java)
                intent.putExtra(AD_ID, currentPageData!!.id.toString())
                intent.putExtra(CATEGORY_ID, currentPageData!!.adCategoryId.toString())
                startActivity(intent)
                finish()
            } else {
                GlobalMethodsOldClass.askGuestLogin(activityResources, this@AdsStoryActivity)
            }
        }
    }

    private fun initData() {
        val intent = intent
        val bundle = intent.extras
        currentPageData = intent.getParcelableExtra(selectedAdObject)
        adVideoUrl = intent.getStringExtra(AD_VEDIO)
        adFilesArrayList = intent.getParcelableArrayListExtra(selectedAdObject)
        isHomeAd = try {
            getIntent().extras!!.getBoolean(IS_HOME)
        } catch (e: Exception) {
            e.printStackTrace()
            true
        }

        try {
            timeLeftInMilliSeconds = currentPageData!!.duration.toLong() * 1000
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }
        token = preferenceHelper.getToken()
    }



    private fun updateAdsStoryView(language: String) {
        val context = LocaleHelper.setLocale(this@AdsStoryActivity, language)
        activityResources = context.resources

        binding.learnMoreTextViewId.text = activityResources.getString(R.string.ad_story_activity_Learn_More)

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

    private fun initViews() {
        if (isHomeAd) {
            binding.learnMoreTextViewId.visibility = View.GONE
        } else {
            binding.learnMoreTextViewId.visibility = View.VISIBLE
            binding.timerTextViewId.visibility = View.GONE
        }

        //floatingActionButton.setVisibility(View.GONE);
        val options = RequestOptions()
            .centerCrop()
        title = currentPageData!!.title
        id = currentPageData!!.adCategoryId
        if (id == 1) {
            binding.adsStoryImageImageViewId.visibility = View.VISIBLE
            binding.adsStoryImageImageViewId.scaleType = ScaleType.FIT_CENTER
            options.placeholder(R.drawable.image_placeholder)
            options.error(R.drawable.image_placeholder)
            try {
                //Glide.with(AdsStoryActivity.this).load(currentPageData.getAdFilesArrayList().get(0).getPath()).apply(options).into(selectedImageImageView);
                setDataToViews()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        } else if (id == 2) {
            binding.adsStoryImageViewPagerId!!.visibility = View.GONE
            try {
                binding.videoPlayVideoViewId.visibility = View.VISIBLE
                binding.videoPlayVideoViewId.setVideoPath(adVideoUrl)
                binding.videoPlayVideoViewId.start()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        } else if (id == 3) {
            binding.adsStoryImageViewPagerId!!.visibility = View.GONE
            binding.websiteWebViewId.visibility = View.VISIBLE
            //-----------------------
            binding.websiteWebViewId.getSettings().setJavaScriptEnabled(true)
            binding.websiteWebViewId.settings.domStorageEnabled = true
            currentPageData!!.websiteURL?.let { binding.websiteWebViewId.loadUrl(it) }
            binding.websiteWebViewId.webViewClient = loadWebView()

            //---------------------------
            /*websiteWebView.setWebChromeClient(new WebChromeClient());
            //websiteWebView.setWebViewClient(new WebViewClient());
            websiteWebView.loadUrl(currentPageData.getWebsite_url());*/
        }
        if (isHomeAd) {
            countDownTimer = CountDown(timeLeftInMilliSeconds, 1000)
            if (id == 2) {
                binding.adStoryProgressBarId.setVisibility(View.VISIBLE)
                val onInfoToPlayStateListener =
                    MediaPlayer.OnInfoListener { mp: MediaPlayer?, what: Int, extra: Int ->
                        when (what) {
                            MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START -> {
                                binding.adStoryProgressBarId.visibility = View.GONE
                                if (!isVideoStart) {
                                    isVideoStart = true
                                    countDownTimer!!.start()
                                } else {
                                    countDownTimer!!.resume()
                                }
                                countDownTimer!!.onTick(timeLeftInMilliSeconds - 1000)
                                return@OnInfoListener true
                            }

                            MediaPlayer.MEDIA_INFO_BUFFERING_END -> {
                                binding.adStoryProgressBarId.visibility = View.GONE
                                if (isVideoStart) {
                                    countDownTimer!!.resume()
                                }
                                return@OnInfoListener true
                            }

                            MediaPlayer.MEDIA_INFO_BUFFERING_START -> {
                                binding.adStoryProgressBarId.visibility = View.VISIBLE
                                countDownTimer!!.pause()
                                return@OnInfoListener true
                            }
                        }
                        false
                    }
                binding.videoPlayVideoViewId.setOnInfoListener(onInfoToPlayStateListener)
            } else if (id == 3) {
                binding.websiteWebViewId.webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView, url: String) {
                        countDownTimer!!.start()
                    }
                }
            } else {
                countDownTimer!!.start()
            }
        }
    }
    private fun setDataToViews() {
        try {
            val position = intent.getIntExtra(POSITION, 0)
            val bundle = intent.extras
            var productFilesInformationArrayList: ArrayList<StoreProductFile?>? = ArrayList()
            productFilesInformationArrayList =
                bundle!!.getSerializable(PRODUCT_FILES_INFORMATION) as ArrayList<StoreProductFile?>?
            if (productFilesInformationArrayList!!.size > 1) {
                binding.adsStoryBackArrowImageViewId.visibility = View.VISIBLE
                binding.adsStoryForwardArrowImageViewId.visibility = View.VISIBLE
            }
            selectedProductImagesViewPagerAdapter = SelectedProductImagesViewPagerAdapter(
                productFilesInformationArrayList, this@AdsStoryActivity, true, position, true
            )
            binding.adsStoryImageViewPagerId!!.adapter = selectedProductImagesViewPagerAdapter
            selectedProductImagesViewPagerAdapter.notifyDataSetChanged()
        } catch (e:Exception) {
            e.printStackTrace()
        }
    }

    private class loadWebView : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return true
        }
    }

    override fun onClose() {
    }

    override fun onConfirm(actionType: Int) {

    }
    override fun onPause() {
        super.onPause()
        isTimerPaused = true
        if (countDownTimer != null) {
            countDownTimer!!.pause()
        }
    }

    override fun onResume() {
        super.onResume()
        if (isTimerPaused) {
            if (countDownTimer != null) {
                countDownTimer!!.resume()
            }
        }
    }
}