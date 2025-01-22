package com.arakadds.arak.presentation.activities.home.fragments

import android.content.res.Resources
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.arakadds.arak.R
import com.arakadds.arak.common.helper.ActivityHelper
import com.arakadds.arak.common.helper.LocaleHelper
import com.arakadds.arak.common.helper.connectivity_checker.ConnectionLiveData
import com.arakadds.arak.common.preferaence.IPreferenceHelper
import com.arakadds.arak.common.preferaence.PreferenceManager
import com.arakadds.arak.data.CartManager
import com.arakadds.arak.data.CartUpdateEvent
import com.arakadds.arak.databinding.FragmentProfileBinding
import com.arakadds.arak.presentation.activities.ArakStore.CartActivity
import com.arakadds.arak.presentation.activities.ads.MyAdsActivity
import com.arakadds.arak.presentation.activities.authentication.LoginActivity
import com.arakadds.arak.presentation.activities.home.HomeActivity
import com.arakadds.arak.presentation.activities.profile.HistoryActivity
import com.arakadds.arak.presentation.activities.profile.MyDetailsActivity
import com.arakadds.arak.presentation.activities.profile.MyFavoritesActivity
import com.arakadds.arak.presentation.activities.services.ServicesActivity
import com.arakadds.arak.presentation.activities.settings.NotificationActivity
import com.arakadds.arak.presentation.activities.settings.SettingsActivity
import com.arakadds.arak.presentation.activities.stores.MainStoreProfileActivity
import com.arakadds.arak.presentation.activities.stores.StoreProfileActivity
import com.arakadds.arak.presentation.adapters.CartArakAdapter
import com.arakadds.arak.presentation.dialogs.ApplicationDialogs
import com.arakadds.arak.presentation.viewmodel.ValidationViewModel
import com.arakadds.arak.utils.AppProperties
import com.arakadds.arak.utils.GlobalMethodsOldClass
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import dagger.android.support.DaggerFragment
import io.paperdb.Paper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class ProfileFragment : DaggerFragment(R.layout.fragment_profile),
    ApplicationDialogs.AlertDialogCallbacks {

    private lateinit var binding: FragmentProfileBinding
    private val preferenceHelper: IPreferenceHelper by lazy { PreferenceManager(requireActivity().applicationContext) }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: ValidationViewModel by viewModels {
        viewModelFactory
    }

    var connectionLiveData: ConnectionLiveData? = null

    private lateinit var token: String
    private lateinit var activityResources: Resources
    private var language: String = "ar"
    private var userImage: String? = null
    private var userFullName: String? = null
    private var hasStore = false

    // AdMob
    private lateinit var adView: AdView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        connectionLiveData = activity?.let { ConnectionLiveData(it) }
        binding = FragmentProfileBinding.bind(view)
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
        language = preferenceHelper.getLanguage()
        setHasOptionsMenu(true)
        checkNetworkConnection()
        val activity: HomeActivity = activity as HomeActivity

        // Initialize AdMob
        initializeAdMob()

        updateProfileFragView(language)
        initData()
        setListeners()
        updateCartCounter()
    }

    private fun initializeAdMob() {
        // Initialize Mobile Ads SDK
        MobileAds.initialize(requireContext()) {}

        // Load Ad
        adView = binding.adView
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    private fun initData() {
        token = preferenceHelper.getToken()
        hasStore = preferenceHelper.isUserHasStore()
        if (hasStore) {
            binding.storeTextViewId.visibility = View.VISIBLE
            binding.view10.visibility = View.VISIBLE
        } else {
            binding.storeTextViewId.visibility = View.GONE
            binding.view10.visibility = View.GONE
        }

        binding.cartCounterTextViewId.setText(HomeActivity.cartNum)
        binding.notificationCounterTextViewId.setText(HomeActivity.NotificationNum)
    }

    private fun checkNetworkConnection() {
        connectionLiveData?.observe(viewLifecycleOwner) { isNetworkAvailable ->
            // Update UI based on network availability
        }
    }

    private fun updateProfileFragView(language: String) {
        val context = LocaleHelper.setLocale(activity, language)
        val resources = context.resources
        binding.profileMyDetailsTextViewId.text =
            resources.getString(R.string.profile_frag_My_Detail)
        binding.myAdsTextViewId.text = resources.getString(R.string.profile_frag_My_Ads)
        binding.historyTextViewId.text = resources.getString(R.string.profile_frag_History)
        binding.FavoriteTextViewId.text = resources.getString(R.string.profile_frag_My_Favorite)
        binding.storeTextViewId.text = resources.getString(R.string.profile_frag_My_Store)
        binding.settingsTextViewId.text = resources.getString(R.string.settings_activity_settings)
        binding.arakServicesTextViewId.text = resources.getString(R.string.service_frag_Arak_Service)
        binding.logoutTextViewId.text = resources.getString(R.string.profile_frag_Logout)
        this.language = language
    }

    private fun setListeners() {
        binding.homeCartImageViewId.setOnClickListener {
            if (token != "non" && token != null) {
                ActivityHelper.goToActivity(
                    activity,
                    CartActivity::class.java, false
                )
            } else {
                GlobalMethodsOldClass.askGuestLogin(activityResources, activity)
            }
        }
        binding.homeNotificationImageViewId.setOnClickListener {
            if (token.trim().isNotEmpty()) {
                ActivityHelper.goToActivity(
                    activity,
                    NotificationActivity::class.java, false
                )
            } else {
                GlobalMethodsOldClass.askGuestLogin(activityResources, activity)
            }
        }

        binding.profileMyDetailsTextViewId.setOnClickListener {
            ActivityHelper.goToActivity(
                activity,
                MyDetailsActivity::class.java, false
            )
        }
        binding.myAdsTextViewId.setOnClickListener {
            ActivityHelper.goToActivity(
                activity,
                MyAdsActivity::class.java, false
            )
        }
        binding.historyTextViewId.setOnClickListener {
            ActivityHelper.goToActivity(
                activity,
                HistoryActivity::class.java, false
            )
        }
        binding.FavoriteTextViewId.setOnClickListener {
            ActivityHelper.goToActivity(
                activity,
                MyFavoritesActivity::class.java, false
            )
        }
        binding.storeTextViewId.setOnClickListener {
            ActivityHelper.goToActivity(
                activity,
                MainStoreProfileActivity::class.java, false
            )
        }
        binding.settingsTextViewId.setOnClickListener {
            ActivityHelper.goToActivity(
                activity,
                SettingsActivity::class.java, false
            )
        }

        binding.arakServicesTextViewId.setOnClickListener {
            ActivityHelper.goToActivity(
                activity,
                ServicesActivity::class.java, false
            )
        }

        binding.logoutTextViewId.setOnClickListener {
            Thread {
                lifecycleScope.launch {
                    activity?.let { it1 -> CartManager(it1).deleteAllProducts() }
                }
            }.start()
            userLogout()
        }
    }

    private fun setPreferences() {
        preferenceHelper.setToken("non")
        preferenceHelper.setSocialToken(null)
        preferenceHelper.setUserStatus(-1)
        preferenceHelper.setUserRole("")
        preferenceHelper.setKeyUserId(-1)
        preferenceHelper.setUserBalance(-1f)
        preferenceHelper.setUserFullName("non")
        preferenceHelper.setUserEmail(null)
        preferenceHelper.setUserImage(null)
        preferenceHelper.setUserGender(null)
        preferenceHelper.setUserCity(null)
        preferenceHelper.setUserCountry(null)
        preferenceHelper.setUserPhoneNumber(null)
        preferenceHelper.setNotificationStatus(false)
        preferenceHelper.setUserHasStore(false)
    }

    private fun userLogout() {
        preferenceHelper.clearPrefs()
        ActivityHelper.goToActivity(activity, LoginActivity::class.java, false)
    }

    override fun onClose() {
    }

    override fun onConfirm(actionType: Int) {
    }

    private fun updateCartCounter() {
        lifecycleScope.launch {
            val count = CartManager(requireContext()).getCartCount()
            binding.cartCounterTextViewId.apply {
                text = count.toString()
                visibility = if (count > 0) View.VISIBLE else View.GONE
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onCartUpdated(event: CartUpdateEvent) {
        binding.cartCounterTextViewId.apply {
            text = event.itemCount.toString()
            visibility = if (event.itemCount > 0) View.VISIBLE else View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        updateCartCounter()
        // Resume AdView
        adView.resume()
    }

    override fun onPause() {
        super.onPause()
        // Pause AdView
        adView.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Destroy AdView
        adView.destroy()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }
}