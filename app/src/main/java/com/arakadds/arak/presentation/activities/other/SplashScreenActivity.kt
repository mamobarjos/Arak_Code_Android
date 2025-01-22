package com.arakadds.arak.presentation.activities.other

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.os.Handler
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import com.arakadds.arak.R
import com.arakadds.arak.common.helper.ActivityHelper
import com.arakadds.arak.common.helper.LocaleHelper
import com.arakadds.arak.common.helper.connectivity_checker.ConnectionLiveData
import com.arakadds.arak.common.preferaence.IPreferenceHelper
import com.arakadds.arak.common.preferaence.PreferenceManager
import com.arakadds.arak.databinding.ActivitySplashScreenBinding
import com.arakadds.arak.presentation.activities.authentication.LoginActivity
import com.arakadds.arak.presentation.activities.home.HomeActivity
import com.arakadds.arak.utils.AppEnums.LanguagesEnums.ENGLISH
import com.arakadds.arak.utils.AppProperties
import dagger.android.AndroidInjection
import io.paperdb.Paper
import java.util.Locale

class SplashScreenActivity : BaseActivity() {

    private val preferenceHelper: IPreferenceHelper by lazy { PreferenceManager(applicationContext) }
    lateinit var binding: ActivitySplashScreenBinding
    private lateinit var activityContext: Context

    private lateinit var toolbar: Toolbar
    private var connectionLiveData: ConnectionLiveData? = null

    private var token: String? = null
    private lateinit var language: String
    private lateinit var resources: Resources
    private val SPLASH_TIME_OUT = 100

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connectionLiveData = ConnectionLiveData(this)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AndroidInjection.inject(this)
        activityContext = this
        language = preferenceHelper.getLanguage()
        if (language.isEmpty() || language == null) {
            preferenceHelper.setLanguage(ENGLISH)
        }
        updateAboutInformationView(language)
        checkNetworkConnection()
        //initToolbar()
        initData()
        callSplashScreen()

    }

    private fun initData() {
        token = preferenceHelper.getToken()
    }

    /* private fun initToolbar() {
         toolbar = binding.appBarLayout.findViewById(R.id.toolbar_id)

         setSupportActionBar(toolbar)
         val mTitle: TextView =
             binding.appBarLayout.findViewById(R.id.toolbar_title_TextView_id)
         val backImageView: ImageView =
             binding.appBarLayout.findViewById(R.id.toolbar_category_icon_ImageView_id)

         mTitle.text = resources?.getString(R.string.service_frag_Arak_Service)
         backImageView.setOnClickListener { finish() }
     }*/

    private fun updateAboutInformationView(language: String) {
        val context = LocaleHelper.setLocale(this@SplashScreenActivity, language)
        resources = context.resources

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

    private fun callSplashScreen() {
        val videoPath = "android.resource://" + packageName + "/" + R.raw.splash_video
        binding.splashScreenVideoVideoViewId.setOnPreparedListener { mp ->
            val videoRatio = mp.videoWidth / mp.videoHeight.toFloat()
            val screenRatio: Float =
                binding.splashScreenVideoVideoViewId.width / binding.splashScreenVideoVideoViewId.height.toFloat()
            val scaleX = videoRatio / screenRatio
            if (scaleX >= 1f) {
                binding.splashScreenVideoVideoViewId.scaleX = scaleX
            } else {
                binding.splashScreenVideoVideoViewId.scaleY = 1f / scaleX
            }
        }
        binding.splashScreenVideoVideoViewId.setVideoPath(videoPath)
        binding.splashScreenVideoVideoViewId.start()
        waiteForIntroScreen()
    }

    private fun waiteForIntroScreen() {
        val handler = Handler()
        handler.postDelayed({
            if (token != "" && token != "non") {
                if (preferenceHelper.isBiometricEnabled()) {
                    ActivityHelper.goToActivity(
                        this@SplashScreenActivity,
                        LoginActivity::class.java, false
                    )
                } else {
                    ActivityHelper.goToActivity(
                        this@SplashScreenActivity,
                        HomeActivity::class.java, false
                    )
                }
            } else {
                if (Locale.getDefault()
                        .displayLanguage == "English" || Locale.getDefault()
                        .displayLanguage == "En" || Locale.getDefault()
                        .displayLanguage == "en"
                ) {
                    Paper.book().write("language", "en")
                } else if (Locale.getDefault()
                        .displayLanguage == "العربية" || Locale.getDefault()
                        .displayLanguage == "Ar" || Locale.getDefault()
                        .displayLanguage == "ar" || Locale.getDefault()
                        .displayLanguage == "arabic" || Locale.getDefault()
                        .displayLanguage == "Arabic"
                ) {
                    Paper.book().write("language", "ar")
                } else {
                    Paper.book().write("language", "en")
                }
                ActivityHelper.goToActivity(
                    this@SplashScreenActivity,
                    LoginActivity::class.java, false
                )
            }
            binding.splashScreenVideoVideoViewId.stopPlayback()
            finish()
        }, SPLASH_TIME_OUT.toLong())
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val a = Intent(Intent.ACTION_MAIN)
        a.addCategory(Intent.CATEGORY_HOME)
        a.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(a)
    }

    override fun onPause() {
        super.onPause()
        //SPLASH_TIME_OUT = 0;
    }
}