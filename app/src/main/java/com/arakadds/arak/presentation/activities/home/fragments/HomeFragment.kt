package com.arakadds.arak.presentation.activities.home.fragments

import android.app.Dialog
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.viewModels
import androidx.annotation.NonNull
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.arakadds.arak.R
import com.arakadds.arak.common.helper.ActivityHelper
import com.arakadds.arak.common.helper.LocaleHelper
import com.arakadds.arak.common.helper.connectivity_checker.ConnectionLiveData
import com.arakadds.arak.common.preferaence.IPreferenceHelper
import com.arakadds.arak.common.preferaence.PreferenceManager
import com.arakadds.arak.data.CartManager
import com.arakadds.arak.data.CartUpdateEvent
import com.arakadds.arak.databinding.FragmentHomeBinding
import com.arakadds.arak.model.new_mapping_refactore.home.AdsData
import com.arakadds.arak.model.new_mapping_refactore.response.banners.BannerObject
import com.arakadds.arak.model.new_mapping_refactore.response.banners.ads.AdResponseData
import com.arakadds.arak.model.new_mapping_refactore.response.banners.elected.ElectedPerson
import com.arakadds.arak.model.new_mapping_refactore.response.banners.elected.Governorate
import com.arakadds.arak.model.new_mapping_refactore.store.StoreObject
import com.arakadds.arak.model.new_mapping_refactore.store.products.Product
import com.arakadds.arak.presentation.activities.ArakStore.CartActivity
import com.arakadds.arak.presentation.activities.ads.AdsStoryActivity
import com.arakadds.arak.presentation.activities.ads.NormalAdsActivity
import com.arakadds.arak.presentation.activities.ads.SearchActivity
import com.arakadds.arak.presentation.activities.ads.SpecialAdsActivity
import com.arakadds.arak.presentation.activities.election.ElectionDetailsActivity
import com.arakadds.arak.presentation.activities.services.ArakServicesActivity
import com.arakadds.arak.presentation.activities.settings.NotificationActivity
import com.arakadds.arak.presentation.activities.stores.ProductDetailsActivity
import com.arakadds.arak.presentation.activities.stores.StoreProfileActivity
import com.arakadds.arak.presentation.adapters.*
import com.arakadds.arak.presentation.dialogs.ApplicationDialogs
import com.arakadds.arak.presentation.dialogs.loader.ProgressDialogUtil
import com.arakadds.arak.presentation.viewmodel.*
import com.arakadds.arak.utils.AppEnums
import com.arakadds.arak.utils.Constants
import com.arakadds.arak.utils.GlobalMethodsOldClass
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject
import kotlin.math.abs

class HomeFragment : DaggerFragment(R.layout.fragment_home),
    ProductsStoriesAdapter.ProductClickEventCallBack,
    ApplicationDialogs.AlertDialogCallbacks,
    HomeAddsAdapter.PaidAdsCallBacks,
    HomeSpecialAddsAdapter.HomeSpecialAdsCallBacks,
    EllectionSliderAdapter.EllectionSliderCallBack,
    ElectionAdapter.ElectionClickEventCallBack,
    SuggestedStoresAdapter.SuggestedStoresClickEvents {

    private lateinit var binding: FragmentHomeBinding
    private val preferenceHelper: IPreferenceHelper by lazy { PreferenceManager(requireActivity().applicationContext) }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val homeAdsViewModel: HomeAdsViewModel by viewModels { viewModelFactory }
    private val storesProductsViewModel: StoresProductsViewModel by viewModels { viewModelFactory }
    private val storesViewModel: StoresViewModel by viewModels { viewModelFactory }
    private val electionViewModel: ElectionViewModel by viewModels { viewModelFactory }
    private val adDetailsViewModel: AdDetailsViewModel by viewModels {
        viewModelFactory
    }
    private var connectionLiveData: ConnectionLiveData? = null
    private lateinit var token: String
    private lateinit var activityResources: Resources
    private var language: String = "ar"
    private var governorateId: Int = 0
    private var productsStoriesAdapter: ProductsStoriesAdapter? = null
    private val adsDataArrayList = ArrayList<AdsData>()
    private val specialAdsDataArrayList = ArrayList<AdsData>()
    private var storeProductsResponseArrayList = ArrayList<Product>()
    private val electionPeopleDataArrayList = ArrayList<ElectedPerson>()
    private val storesListArrayList = ArrayList<StoreObject>()
    private val governoratesDataArrayList = ArrayList<Governorate>()
    private val homeBannersArrayList = ArrayList<BannerObject>()
    private lateinit var homeAddsAdapter: HomeAddsAdapter
    private lateinit var homeSpecialAddsAdapter: HomeSpecialAddsAdapter
    private lateinit var electionAdapter: ElectionAdapter
    private lateinit var bannerSliderAdapter: BannerSliderAdapter
    private lateinit var suggestedStoresAdapter: SuggestedStoresAdapter
    lateinit var adView: AdView
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)
        setupInitialViews()
        loadHomeData()
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)

    }

    private fun setupInitialViews() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
        connectionLiveData = ConnectionLiveData(requireContext())
        language = preferenceHelper.getLanguage()
        token = preferenceHelper.getToken()
        activityResources = resources
        updateHomeFragView(language)
        initRecyclerViews()
        setListeners()
    }
    private fun updateHomeFragView(language: String) {
        val context = LocaleHelper.setLocale(activity, language)
        activityResources = context.resources
        binding.homeSearchTextViewId.text = activityResources.getString(R.string.home_frag_search)
        binding.homeEmptyViewTextViewId.text =
            activityResources.getString(R.string.error_messages_no_data_found)
        binding.homeAddsWatchedAdsTitleTextViewId.text =
            activityResources.getString(R.string.home_frag_Watched_Ads)
        binding.homeBannerLearnMoreTextViewId.text =
            activityResources.getString(R.string.home_frag_learn_more)

        binding.homeFeaturedAdsLabelTextViewId.text =
            activityResources.getString(R.string.home_frag_featured_ads)

        binding.homeFeaturedAdsSeeMoreTextViewId.text =
            activityResources.getString(R.string.home_frag_See_more)

        binding.homeNormalAdsLabelTextViewId.text =
            activityResources.getString(R.string.home_frag_Normal_ads)

        binding.homeNormalAdsSeeMoreTextViewId.text =
            activityResources.getString(R.string.home_frag_See_more)

        binding.homeSuggestionsStoresLabelTextViewId.text =
            activityResources.getString(R.string.home_frag_Suggestions_Stores)

        this.language = language
    }
    private fun initRecyclerViews() {
        initRandomStoresProductsRecyclerView()
        //setupEllectionSlider()
        initElectionAdapter()
        setUpBannerSlider()
        setStoresAdapter()
    }

    private fun loadHomeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                withContext(Dispatchers.Main) {
                    ProgressDialogUtil.showLoadingDialog(
                        getString(R.string.dialogs_loading_wait),
                        requireActivity()
                    )
                }

                // Launch all requests in parallel
                coroutineScope {
                    launch { fetchHomeAds() }
                    launch { fetchBanners() }
                    launch { fetchRandomProducts() }
                    launch { fetchStoresList() }
                    launch { fetchElectedPeople() }
                    launch { fetchGovernorates() }
                    launch { fetchSpecialAds() }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showError(e.message ?: getString(R.string.dialogs_error))
                }
            } finally {
                withContext(Dispatchers.Main) {
                    ProgressDialogUtil.hideLoadingDialog()
                }
            }
        }
    }


    private suspend fun fetchHomeAds() {
        withContext(Dispatchers.Main) {
            try {
                // Set up observers first on the main thread
                homeAdsViewModel.homeAdsModelModel.observe(viewLifecycleOwner) { response ->
                    viewLifecycleOwner.lifecycleScope.launch {
                        response?.let { homeAdsModel ->
                            if (homeAdsModel.statusCode == 200) {
                                try {
                                    adsDataArrayList.clear()
                                    homeAdsModel.homeAdsData?.let { homeAdsData ->
                                        adsDataArrayList.addAll(homeAdsData.adsDataArrayList)
                                        setUpNormalAdsAdapter()
                                    }
                                } catch (e: Exception) {
                                    showError(getString(R.string.dialogs_error))
                                }
                            } else {
                                showError(homeAdsModel.message ?: getString(R.string.dialogs_error))
                            }
                        } ?: showError(getString(R.string.dialogs_error))
                    }
                }

                // Then make the API call
                withContext(Dispatchers.IO) {
                    homeAdsViewModel.getHomeAds(
                        lang = language,
                        token = "Bearer $token",
                        isFeatured = false,
                        page = 1
                    )
                }
            } catch (e: Exception) {
                showError(e.message ?: getString(R.string.dialogs_error))
            }
        }
    }
    private suspend fun fetchSpecialAds() {
        withContext(Dispatchers.Main) {
            try {
                // Create a separate observer for special ads
                homeAdsViewModel.specialAdsModelModel.observe(viewLifecycleOwner) { response ->
                    viewLifecycleOwner.lifecycleScope.launch {
                        response?.let { homeAdsModel ->
                            if (homeAdsModel.statusCode == 200) {
                                specialAdsDataArrayList.clear()
                                homeAdsModel.homeAdsData?.let { homeAdsData ->
                                    specialAdsDataArrayList.addAll(homeAdsData.adsDataArrayList)
                                    setUpSpecialAdsAdapter()
                                }
                            } else {
                                showError(homeAdsModel.message ?: getString(R.string.dialogs_error))
                            }
                        } ?: showError(getString(R.string.dialogs_error))
                    }
                }

                // Then make the API call
                withContext(Dispatchers.IO) {
                    homeAdsViewModel.getSpecialAds( // Use a separate method for special ads
                        lang = language,
                        token = "Bearer $token",
                        page = 1
                    )
                }
            } catch (e: Exception) {
                showError(e.message ?: getString(R.string.dialogs_error))
            }
        }
    }
    private suspend fun fetchBanners() {
        withContext(Dispatchers.Main) {
            try {
                // Set up observers first
                homeAdsViewModel.bannersModelModel.observe(viewLifecycleOwner) { response ->
                    viewLifecycleOwner.lifecycleScope.launch {
                        response?.let { bannersModel ->
                            if (bannersModel.statusCode == 200) {
                                homeBannersArrayList.clear()
                                bannersModel.bannersData?.let { bannersData ->
                                    homeBannersArrayList.addAll(bannersData.bannersArrayList)
                                    bannerSliderAdapter.notifyDataSetChanged()
                                    updateBannersVisibility()
                                }
                            } else {
                                showError(bannersModel.message ?: getString(R.string.dialogs_error))
                            }
                        } ?: showError(getString(R.string.dialogs_error))
                    }
                }

                // Then make API call
                withContext(Dispatchers.IO) {
                    homeAdsViewModel.getBanners(
                        lang = language,
                        token = "Bearer $token",
                        bannerType = AppEnums.BannerType.HOMEPAGE
                    )
                }
            } catch (e: Exception) {
                showError(e.message ?: getString(R.string.dialogs_error))
            }
        }
    }

    private suspend fun fetchRandomProducts() {
        withContext(Dispatchers.Main) {
            try {
                // Set up observers first
                storesProductsViewModel.storeProductsDataModelModel.observe(viewLifecycleOwner) { response ->
                    viewLifecycleOwner.lifecycleScope.launch {
                        response?.let { model ->
                            if (model.statusCode == 200) {
                                storeProductsResponseArrayList.clear()
                                model.storeProductsData?.let { data ->
                                    storeProductsResponseArrayList.addAll(data.storeProductsResponseArrayList)
                                    productsStoriesAdapter?.notifyDataSetChanged()
                                    updateProductsVisibility()
                                }
                            } else {
                                showError(model.message ?: getString(R.string.dialogs_error))
                            }
                        }
                    }
                }

                // Then make API call
                withContext(Dispatchers.IO) {
                    storesProductsViewModel.getStoreProduct(
                        token = "Bearer $token",
                        lang = language,
                        isRandomProducts = true
                    )
                }
            } catch (e: Exception) {
                showError(e.message ?: getString(R.string.dialogs_error))
            }
        }
    }

    private suspend fun fetchStoresList() {
        withContext(Dispatchers.Main) {
            try {
                // Set up observers first
                storesViewModel.storesListModelModel.observe(viewLifecycleOwner) { response ->
                    viewLifecycleOwner.lifecycleScope.launch {
                        response?.let { model ->
                            if (model.statusCode == 200) {
                                storesListArrayList.clear()
                                model.data?.let { data ->
                                    storesListArrayList.addAll(data.stores.take(4))
                                    suggestedStoresAdapter.notifyDataSetChanged()
                                }
                            } else {
                                showError(model.message ?: getString(R.string.dialogs_error))
                            }
                        }
                    }
                }

                // Then make API call
                withContext(Dispatchers.IO) {
                    storesViewModel.getStoresList(
                        lang = language,
                        token = "Bearer $token",
                        page = 1,
                        random = true
                    )
                }
            } catch (e: Exception) {
                showError(e.message ?: getString(R.string.dialogs_error))
            }
        }
    }
    private suspend fun fetchElectedPeople(governorateId: Int? = null) {
        withContext(Dispatchers.Main) {
            try {
                // Set up observers first
                electionViewModel.electedPeopleModelModel.observe(viewLifecycleOwner) { response ->
                    viewLifecycleOwner.lifecycleScope.launch {
                        response?.let { model ->
                            if (model.statusCode == 200) {
                                electionPeopleDataArrayList.clear()
                                model.data?.let { data ->
                                    electionPeopleDataArrayList.addAll(data.electedPeople)
                                    electionAdapter.notifyDataSetChanged()
                                    updateElectionVisibility()
                                }
                            } else {
                                showError(model.message ?: getString(R.string.dialogs_error))
                            }
                        }
                    }
                }

                // Then make API call
                withContext(Dispatchers.IO) {
                    electionViewModel.getElectedPeople(
                        token = "Bearer $token",
                        lang = language,
                        governorateId = governorateId
                    )
                }
            } catch (e: Exception) {
                showError(e.message ?: getString(R.string.dialogs_error))
            }
        }
    }


    private suspend fun fetchGovernorates() {
        withContext(Dispatchers.Main) {
            try {
                // Set up observers first
                electionViewModel.governoratesModelModel.observe(viewLifecycleOwner) { response ->
                    viewLifecycleOwner.lifecycleScope.launch {
                        response?.let { model ->
                            if (model.statusCode == 200) {
                                governoratesDataArrayList.clear()
                                model.governoratesData?.let { data ->
                                    governoratesDataArrayList.addAll(data.governorates)
                                }
                            } else {
                                showError(model.message ?: getString(R.string.dialogs_error))
                            }
                        }
                    }
                }

                // Then make API call
                withContext(Dispatchers.IO) {
                    electionViewModel.getGovernorates(
                        token = "Bearer $token",
                        lang = language
                    )
                }
            } catch (e: Exception) {
                showError(e.message ?: getString(R.string.dialogs_error))
            }
        }
    }

    private fun setListeners() {
        binding.homeCartImageViewId.setOnClickListener {
            if (token.trim().isEmpty()) {
                GlobalMethodsOldClass.askGuestLogin(resources, context)
                return@setOnClickListener
            }
            ActivityHelper.goToActivity(activity, CartActivity::class.java, false)
        }

        binding.homeNotificationImageViewId.setOnClickListener {
            if (token.trim().isNotEmpty()) {
                ActivityHelper.goToActivity(activity, NotificationActivity::class.java, false)
            } else {
                GlobalMethodsOldClass.askGuestLogin(activityResources, activity)
            }
        }

        binding.homeLogoImageViewId.setOnClickListener {
            ActivityHelper.goToActivity(activity, ArakServicesActivity::class.java, false)
        }

        binding.homeSearchTextViewId.setOnClickListener {
            ActivityHelper.goToActivity(activity, SearchActivity::class.java, false)
        }

        binding.homeEllectionFilterImageViewId.setOnClickListener {
            showBottomSheetDialog(governoratesDataArrayList)
        }

        binding.homeFeaturedAdsSeeMoreTextViewId.setOnClickListener {
            ActivityHelper.goToActivity(activity, SpecialAdsActivity::class.java, false)
        }

        binding.homeNormalAdsSeeMoreTextViewId.setOnClickListener {
            ActivityHelper.goToActivity(activity, NormalAdsActivity::class.java, false)
        }
    }

    private fun initRandomStoresProductsRecyclerView() {
        productsStoriesAdapter =
            ProductsStoriesAdapter(storeProductsResponseArrayList, this, activity)
        val mLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        binding.storesProductsItemsRecyclerViewId.layoutManager = mLayoutManager
        binding.storesProductsItemsRecyclerViewId.isFocusable = false
        binding.storesProductsItemsRecyclerViewId.isNestedScrollingEnabled = false
        binding.storesProductsItemsRecyclerViewId.adapter = productsStoriesAdapter
    }

    private fun setUpBannerSlider() {
        try {
            bannerSliderAdapter = BannerSliderAdapter(homeBannersArrayList, requireActivity())
            binding.homeBannerSliderViewId.apply {
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = bannerSliderAdapter
                // Optional: Add PagerSnapHelper for ViewPager-like behavior
                val snapHelper = PagerSnapHelper()
                snapHelper.attachToRecyclerView(this)
            }
            startAutoScroll()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Add these properties
    private var isAutoScrolling = false
    private var autoScrollJob: Job? = null

    // Remove the autoScrollRunnable

    // Update the startAutoScroll function
    private fun startAutoScroll() {
        if (isAutoScrolling) return

        isAutoScrolling = true

        autoScrollJob = viewLifecycleOwner.lifecycleScope.launch {
            while (isActive && isAutoScrolling) {
                try {
                    if (homeBannersArrayList.isNotEmpty()) {
                        withContext(Dispatchers.Main) {
                            val layoutManager = binding.homeBannerSliderViewId.layoutManager as LinearLayoutManager
                            val currentPosition = layoutManager.findFirstVisibleItemPosition()
                            val nextPosition = if (currentPosition >= homeBannersArrayList.size - 1) 0 else currentPosition + 1
                            binding.homeBannerSliderViewId.smoothScrollToPosition(nextPosition)
                        }
                    }
                    delay(3000) // Wait for 3 seconds before scrolling to next item
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    // Update the stopAutoScroll function
    private fun stopAutoScroll() {
        isAutoScrolling = false
        autoScrollJob?.cancel()
        autoScrollJob = null
    }
    private fun setUpNormalAdsAdapter() {
        val limitNum = if (adsDataArrayList.size > 6) 6 else adsDataArrayList.size
        homeAddsAdapter =
            HomeAddsAdapter(adsDataArrayList, limitNum, activity, language, true, this)
        val gridLayoutManager = GridLayoutManager(activity, 2)
        binding.homeAddsRecyclerViewId.layoutManager = gridLayoutManager
        binding.homeAddsRecyclerViewId.isFocusable = false
        binding.homeAddsRecyclerViewId.isNestedScrollingEnabled = false
        binding.homeAddsRecyclerViewId.adapter = homeAddsAdapter
    }

    private fun setUpSpecialAdsAdapter() {
        val limitNum = if (specialAdsDataArrayList.size > 5) 5 else specialAdsDataArrayList.size
        homeSpecialAddsAdapter = HomeSpecialAddsAdapter(
            specialAdsDataArrayList,
            limitNum,
            activity,
            language,
            false,
            true,
            activityResources,
            this
        )
        val mLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        binding.homeSpecialAdsRecyclerViewId.layoutManager = mLayoutManager
        binding.homeSpecialAdsRecyclerViewId.isFocusable = false
        binding.homeSpecialAdsRecyclerViewId.isNestedScrollingEnabled = false
        binding.homeSpecialAdsRecyclerViewId.adapter = homeSpecialAddsAdapter
    }

    private fun setStoresAdapter() {
        suggestedStoresAdapter =
            SuggestedStoresAdapter(storesListArrayList, activity, language, activityResources, this)
        val gridLayoutManager = GridLayoutManager(activity, 2)
        binding.homeSuggestionsStoresRecyclerViewId.layoutManager = gridLayoutManager
        binding.homeSuggestionsStoresRecyclerViewId.isFocusable = false
        binding.homeSuggestionsStoresRecyclerViewId.isNestedScrollingEnabled = false
        binding.homeSuggestionsStoresRecyclerViewId.adapter = suggestedStoresAdapter
    }

    /* fun setupEllectionSlider() {
         val ellectionSliderAdapter = EllectionSliderAdapter(electedBannersArrayList, this, activity)
         // Setup slider adapter and animations if needed
     }*/

    private fun initElectionAdapter() {
        electionAdapter = ElectionAdapter(electionPeopleDataArrayList, this, activity)
        val gridLayoutManager = GridLayoutManager(context, 2, GridLayoutManager.HORIZONTAL, false)
        binding.homeEllectionItemsRecyclerViewId.layoutManager = gridLayoutManager
        binding.homeEllectionItemsRecyclerViewId.isFocusable = false
        binding.homeEllectionItemsRecyclerViewId.isNestedScrollingEnabled = false
        binding.homeEllectionItemsRecyclerViewId.adapter = electionAdapter
    }

    private fun showBottomSheetDialog(governorateArrayList: ArrayList<Governorate>) {
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.bottomsheetlayout)
        val title = dialog.findViewById<TextView>(R.id.buttomsheet_title_TextView_id)
        val cityTitle = dialog.findViewById<TextView>(R.id.buttomsheet_city_textView_id)
        val citySpinner = dialog.findViewById<Spinner>(R.id.buttomsheet_city_Spinner_id)
        val districtTitle = dialog.findViewById<TextView>(R.id.buttomsheet_district_textView_id)
        val districtSpinner = dialog.findViewById<Spinner>(R.id.buttomsheet_district_Spinner_id1)
        val searchButton = dialog.findViewById<Button>(R.id.buttomsheet_button_id)
        title.text = resources.getString(R.string.home_frag_filtration_options)
        cityTitle.text = resources.getString(R.string.home_frag_electoral_district)
        districtTitle.text = resources.getString(R.string.home_frag_electoral_district)
        val selectedCityId = IntArray(1)
        val selectedDistrictsId = IntArray(1)
        val governoratesAdapter = ArrayAdapter(
            requireActivity(),
            android.R.layout.simple_spinner_item,
            governorateArrayList
        )
        governoratesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        citySpinner.adapter = governoratesAdapter
        citySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                val governorate = parent.selectedItem as Governorate
                selectedCityId[0] = governorate.id
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        searchButton.setOnClickListener {
            governorateId = selectedCityId[0]
            viewLifecycleOwner.lifecycleScope.launch {
                if (selectedCityId[0] == 0 && selectedDistrictsId[0] == 0) {
                    fetchElectedPeople()
                } else {
                    fetchElectedPeople(governorateId = governorateId)
                }
            }
            dialog.dismiss()
        }
        dialog.show()
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.attributes.windowAnimations = R.style.Animation_Design_BottomSheetDialog
        dialog.window!!.setGravity(Gravity.BOTTOM)
    }

    private fun showError(message: String) {
        activity?.let {
            ApplicationDialogs.openAlertDialog(
                it,
                getString(R.string.dialogs_error),
                message,
                false,
                AppEnums.DialogActionTypes.DISMISS,
                this
            )
        }
    }

    private fun updateBannersVisibility() {
        binding.homeBannerSliderViewId.visibility =
            if (homeBannersArrayList.isEmpty()) View.GONE else View.VISIBLE
        binding.homeBannerCardViewId.visibility =
            if (homeBannersArrayList.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun updateProductsVisibility() {
        binding.storesProductsItemsRecyclerViewId.visibility =
            if (storeProductsResponseArrayList.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun updateElectionVisibility() {
        binding.apply {
            val visibility = if (electionPeopleDataArrayList.isEmpty()) View.GONE else View.VISIBLE
            homeEllectionItemsRecyclerViewId.visibility = visibility
            homeEllectionLabelTextViewId.visibility = visibility
            homeEllectionFilterImageViewId.visibility = visibility
        }
    }

    override fun onPause() {
        super.onPause()
        stopAutoScroll()
    }

    override fun onResume() {
        super.onResume()
        updateCartCounter()
        startAutoScroll()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAutoScroll()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
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

    override fun onClose() {}

    override fun onConfirm(actionType: Int) {}

    override fun onProductSelectedCallBack(productId: Int) {
        val intent = Intent(activity, ProductDetailsActivity::class.java)
        intent.putExtra(Constants.SELECTED_PRODUCT_ID, productId.toString())
        startActivity(intent)
    }

    override fun onStoreClickedCalledBack(position: Int, storeId: Int) {
        val intent = Intent(activity, StoreProfileActivity::class.java)
        intent.putExtra(Constants.SELECTED_STORE_ID, storeId)
        startActivity(intent)
    }

    override fun onNextPageRequired() {}


    private fun getSelectedAdDetails(adsData: AdsData?,id: Int) {
        adDetailsViewModel.getSingleAdDetails(language, "Bearer $token", id.toString())
        adDetailsViewModel.adDataModel.observe(
            this,
            Observer(function = fun(adResponseData: AdResponseData?) {
                adResponseData?.let {
                    if (adResponseData.statusCode == 200) {
                        val intent = Intent(requireActivity(), StoreProfileActivity::class.java)
                        if (adsData != null) {
                            intent.putExtra(Constants.SELECTED_STORE_ID, adsData.storeId)
                        }
                        startActivity(intent)
                    }
                }
            })
        )

        adDetailsViewModel.throwableResponse.observe(
            this,
            Observer(function = fun(throwable: Throwable?) {

            })
        )
    }


    override fun onSelectAd(adsData: AdsData?, position: Int) {
        if (adsData?.adCategoryId == AppEnums.AdsTypeCategories.STORES) {
            getSelectedAdDetails(adsData,adsData.id)
        } else {
            val intent = Intent(requireActivity(), AdsStoryActivity::class.java)
            val bundle = Bundle()
            intent.putExtra(Constants.POSITION, position)
            bundle.putSerializable(Constants.PRODUCT_FILES_INFORMATION, adsData?.adFiles)
            intent.putExtras(bundle)
            intent.putExtra(Constants.selectedAdObject, adsData as Parcelable)
            intent.putExtra(Constants.AD_VEDIO, adsData.adFiles[0].url)
            intent.putExtra(Constants.IS_HOME, true)
            startActivity(intent)
        }
    }

    override fun onEllectionSliderClickEvent(electionPeopleData: BannerObject?, position: Int) {
        try {
            ActivityHelper.goToActivity(
                activity,
                ElectionDetailsActivity::class.java,
                false,
                AppEnums.IntentsFlags.PERSON_ID,
                electionPeopleData?.userId.toString()
            )
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    override fun onElectionSelectedCallBack(electionPeopleData: ElectedPerson?, productId: Int) {
        try {
            ActivityHelper.goToActivity(
                activity,
                ElectionDetailsActivity::class.java,
                false,
                AppEnums.IntentsFlags.PERSON_ID,
                electionPeopleData?.id.toString()
            )
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}