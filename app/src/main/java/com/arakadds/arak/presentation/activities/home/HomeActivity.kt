package com.arakadds.arak.presentation.activities.home

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.arakadds.arak.R
import com.arakadds.arak.common.helper.ActivityHelper
import com.arakadds.arak.common.helper.LocaleHelper
import com.arakadds.arak.common.helper.connectivity_checker.ConnectionLiveData
import com.arakadds.arak.common.preferaence.IPreferenceHelper
import com.arakadds.arak.common.preferaence.PreferenceManager
import com.arakadds.arak.databinding.ActivityHomeBinding
import com.arakadds.arak.presentation.activities.ads.SelectAdsTypeActivity
import com.arakadds.arak.presentation.activities.other.NoInternetConnectionActivity
import com.arakadds.arak.presentation.activities.stores.CreateProductActivity
import com.arakadds.arak.presentation.activities.stores.CreateStoreActivity
import com.arakadds.arak.presentation.dialogs.ApplicationDialogs
import com.arakadds.arak.presentation.viewmodel.LoginViewModel
import com.arakadds.arak.presentation.viewmodel.NotificationsViewModel
import com.arakadds.arak.utils.GlobalMethodsOldClass
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import dagger.android.AndroidInjection
import javax.inject.Inject

class HomeActivity : AppCompatActivity(), ApplicationDialogs.AlertDialogCallbacks {

    private val preferenceHelper: IPreferenceHelper by lazy { PreferenceManager(applicationContext) }
    private lateinit var binding: ActivityHomeBinding
    private lateinit var navController: NavController
    private lateinit var activityContext: Context
    private var connectionLiveData: ConnectionLiveData? = null

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: NotificationsViewModel by viewModels { viewModelFactory }
    private val loginViewModel: LoginViewModel by viewModels { viewModelFactory }
    private lateinit var token: String
    private var isCreateProduct = false

    // تهيئة InterstitialAd
    private var mInterstitialAd: InterstitialAd? = null

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        activityContext = this

        // تهيئة AdMob
        MobileAds.initialize(this) {}

        // تحميل إعلان Interstitial
        loadInterstitialAd()

        token = preferenceHelper.getToken()
        setupNavigation()
        setupListeners()
        updateLanguage(preferenceHelper.getLanguage())
        checkNetworkConnection()

        // الضغط على الشعار
        binding.logo.setOnClickListener {
            showInterstitialAd()
        }
    }

    private fun loadInterstitialAd() {
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            this,
            "ca-app-pub-7303745888048368/4505272121", // استبدل بمعرّف الإعلان الخاص بك
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                    Log.d("AdMob", "Ad loaded successfully")
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    mInterstitialAd = null
                    Log.e("AdMob", "Ad failed to load: ${adError.message}")
                }
            }
        )
    }

    private fun showInterstitialAd() {
        if (mInterstitialAd != null) {
            mInterstitialAd?.show(this)
        } else {
            // إذا لم يكن الإعلان جاهزًا، انتقل مباشرة إلى صفحة الخدمات
            startActivity(Intent(this, SelectAdsTypeActivity::class.java))
            Log.d("AdMob", "Ad not loaded, skipping to SelectAdsTypeActivity")
        }
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Setup bottom navigation with NavController
        binding.navBottomId.setupWithNavController(navController)

        // Handle navigation changes
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment -> {
                    binding.homeAddNewStoreTextViewId.visibility = View.GONE
                    binding.createAdFloatingButtonId.visibility = View.VISIBLE
                    isCreateProduct = false
                }
                R.id.walletFragment -> {
                    if (token != "non" && token.isNotEmpty()) {
                        binding.homeAddNewStoreTextViewId.visibility = View.GONE
                        binding.createAdFloatingButtonId.visibility = View.VISIBLE
                        isCreateProduct = false
                    } else {
                        GlobalMethodsOldClass.askGuestLogin(resources, this)
                    }
                }
                R.id.storeFragment -> {
                    binding.homeAddNewStoreTextViewId.visibility = View.GONE
                    binding.createAdFloatingButtonId.visibility = View.GONE
                }
                R.id.arakStoreFragment -> {
                    binding.homeAddNewStoreTextViewId.visibility = View.GONE
                    binding.createAdFloatingButtonId.visibility = View.GONE
                    isCreateProduct = false
                }
                R.id.profileFragment -> {
                    if (token != "non" && token.isNotEmpty()) {
                        binding.homeAddNewStoreTextViewId.visibility = View.GONE
                        binding.createAdFloatingButtonId.visibility = View.VISIBLE
                        isCreateProduct = false
                    } else {
                        GlobalMethodsOldClass.askGuestLogin(resources, this)
                    }
                }
            }
        }
    }

    private fun setupListeners() {
        binding.homeAddNewStoreTextViewId.setOnClickListener {
            ActivityHelper.goToActivity(this, CreateStoreActivity::class.java, false)
        }

        binding.createAdFloatingButtonId.setOnClickListener {
            if (token != "non") {
                if (isCreateProduct) {
                    ActivityHelper.goToActivity(this, CreateProductActivity::class.java, false)
                } else {
                    showInterstitialAd()
                }
            } else {
                GlobalMethodsOldClass.askGuestLogin(resources, this)
            }
        }
    }

    private fun updateLanguage(language: String) {
        val context = LocaleHelper.setLocale(this, language)
        val updatedResources = context.resources // Use a local variable instead
        binding.navBottomId.menu.findItem(R.id.homeFragment).title = updatedResources.getString(R.string.home_activity_home)
        binding.navBottomId.menu.findItem(R.id.walletFragment).title = updatedResources.getString(R.string.home_activity_Wallet)
        binding.navBottomId.menu.findItem(R.id.storeFragment).title = updatedResources.getString(R.string.home_activity_Stores)
        binding.navBottomId.menu.findItem(R.id.arakStoreFragment).title = updatedResources.getString(R.string.home_activity_Products)
        binding.navBottomId.menu.findItem(R.id.profileFragment).title = updatedResources.getString(R.string.home_activity_More)
    }

    private fun checkNetworkConnection() {
        connectionLiveData?.observe(this) { isNetworkAvailable ->
            if (!isNetworkAvailable) {
                startActivity(Intent(this, NoInternetConnectionActivity::class.java))
            }
        }
    }

    override fun onClose() {}

    override fun onConfirm(actionType: Int) {}

    override fun onBackPressed() {
        if (!navController.navigateUp()) {
            super.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        // Update any necessary UI components or data
    }

    companion object {
        var cartNum = "0"
        var NotificationNum = "0"
    }
}