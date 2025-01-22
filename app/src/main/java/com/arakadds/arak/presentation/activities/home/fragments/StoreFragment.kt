package com.arakadds.arak.presentation.activities.home.fragments

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.arakadds.arak.R
import com.arakadds.arak.common.helper.ActivityHelper
import com.arakadds.arak.common.helper.GoogleAdsHelper
import com.arakadds.arak.common.helper.LocaleHelper
import com.arakadds.arak.common.helper.connectivity_checker.ConnectionLiveData
import com.arakadds.arak.common.preferaence.IPreferenceHelper
import com.arakadds.arak.common.preferaence.PreferenceManager
import com.arakadds.arak.data.CartManager
import com.arakadds.arak.data.CartUpdateEvent
import com.arakadds.arak.databinding.FragmentStoreBinding
import com.arakadds.arak.model.new_mapping_refactore.response.banners.BannerObject
import com.arakadds.arak.model.new_mapping_refactore.response.banners.BannersModel
import com.arakadds.arak.model.new_mapping_refactore.store.StoreCategoriesModel
import com.arakadds.arak.model.new_mapping_refactore.store.StoreObject
import com.arakadds.arak.model.new_mapping_refactore.store.StoresListModel
import com.arakadds.arak.model.new_mapping_refactore.store.categories.StoreCategory
import com.arakadds.arak.presentation.activities.ArakStore.CartActivity
import com.arakadds.arak.presentation.activities.ads.SearchActivity
import com.arakadds.arak.presentation.activities.ads.SelectAdsTypeActivity
import com.arakadds.arak.presentation.activities.home.HomeActivity
import com.arakadds.arak.presentation.activities.services.ArakServicesActivity
import com.arakadds.arak.presentation.activities.settings.NotificationActivity
import com.arakadds.arak.presentation.activities.settings.SettingsActivity
import com.arakadds.arak.presentation.activities.stores.CreateProductActivity
import com.arakadds.arak.presentation.activities.stores.CreateStoreActivity
import com.arakadds.arak.presentation.activities.stores.StoreProfileActivity
import com.arakadds.arak.presentation.adapters.BannerSliderAdapter
import com.arakadds.arak.presentation.adapters.CategoriesAdapter
import com.arakadds.arak.presentation.adapters.CategoriesAdapter.CategorySelectedEvents
import com.arakadds.arak.presentation.adapters.StoresAdapter
import com.arakadds.arak.presentation.adapters.StoresAdapter.StoreClickEvents
import com.arakadds.arak.presentation.dialogs.ApplicationDialogs
import com.arakadds.arak.presentation.dialogs.loader.ProgressDialogUtil.hideLoadingDialog
import com.arakadds.arak.presentation.dialogs.loader.ProgressDialogUtil.showLoadingDialog
import com.arakadds.arak.presentation.viewmodel.HomeAdsViewModel
import com.arakadds.arak.presentation.viewmodel.StoresViewModel
import com.arakadds.arak.utils.AppEnums
import com.arakadds.arak.utils.AppProperties
import com.arakadds.arak.utils.Constants.BASE_URL
import com.arakadds.arak.utils.Constants.IS_STORE_SEARCH
import com.arakadds.arak.utils.Constants.SELECTED_STORE_ID
import com.arakadds.arak.utils.GlobalMethodsOldClass
import dagger.android.support.DaggerFragment
import io.paperdb.Paper
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class StoreFragment : DaggerFragment(R.layout.fragment_store),
    StoreClickEvents, CategorySelectedEvents, ApplicationDialogs.AlertDialogCallbacks {

    private lateinit var binding: FragmentStoreBinding
    private val preferenceHelper: IPreferenceHelper by lazy { PreferenceManager(requireActivity().applicationContext) }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val storesViewModel: StoresViewModel by viewModels {
        viewModelFactory
    }
    private val homeAdsViewModel: HomeAdsViewModel by viewModels {
        viewModelFactory
    }

    var connectionLiveData: ConnectionLiveData? = null

    private lateinit var token: String
    private lateinit var activityResources: Resources
    private var language: String = "ar"
    private var storesAdapter: StoresAdapter? = null
    private lateinit var categoriesAdapter: CategoriesAdapter
    private lateinit var bannerSliderAdapter: BannerSliderAdapter
    private var storesListArrayList = ArrayList<StoreObject>()
    private val bannersArrayList = ArrayList<BannerObject>()
    private var categoriesListArrayList = ArrayList<StoreCategory>()
    private var storeBannersResponseArrayList = ArrayList<BannerObject>()
    private var hasStore = false
    private var currentPage = 1
    private var lastPage: Int = 1
    private val autoScrollHandler = Handler(Looper.getMainLooper())
    private val autoScrollRunnable = object : Runnable {
        override fun run() {
            autoScroll()
            autoScrollHandler.postDelayed(this, 3000)  // Scroll every 3 seconds
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        connectionLiveData = activity?.let { ConnectionLiveData(it) }
        binding = FragmentStoreBinding.bind(view)
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
        language = preferenceHelper.getLanguage()
        setHasOptionsMenu(true)
        checkNetworkConnection()
        val activity: HomeActivity = activity as HomeActivity
        //this call to recive data from activity to my fragment
        /*val results: Bundle = activity.getMyData()
        subscriptionId = results.getInt(Constants.SELECTED_SUBSCRIPTION_ID)*/

        (Paper.book().read<Any>("language") as String)

        updateStoreFragView(language)
        initData()
        GoogleAdsHelper.initBannerGoogleAdView(requireActivity(), binding.storeBannerViewAdViewId)
        setStoresAdapter(storesListArrayList)
        setCategoriesAdapter()
        setupBannerSlider(storeBannersResponseArrayList)
        getStoreCategories()
        getStoresList(page = currentPage)
        getBanners()
        setListeners()
        updateCartCounter()
    }

    private fun initData() {
        token = preferenceHelper.getToken()
        hasStore = preferenceHelper.isUserHasStore()
        if (!hasStore) {
            binding.storesCreateStoreImageViewId.visibility = View.VISIBLE
        }

        binding.cartCounterTextViewId.text = HomeActivity.cartNum
        binding.notificationCounterTextViewId.text = HomeActivity.NotificationNum
    }

    private fun checkNetworkConnection() {
        connectionLiveData?.observe(viewLifecycleOwner) { isNetworkAvailable ->
            //update ui
//            if (!isNetworkAvailable) {
//                Toast.makeText(activity, "connected", Toast.LENGTH_SHORT).show()
//            } else {
//                Toast.makeText(activity, "disconnected", Toast.LENGTH_SHORT).show()
//            }
        }
    }

    private fun updateStoreFragView(language: String) {
        val context = LocaleHelper.setLocale(activity, language)
        activityResources = context.resources
        binding.storeSearchTextViewId.text =
            activityResources.getString(R.string.store_frag_Search_Stores)
        this.language = language
    }

    private fun setListeners() {
        binding.storesCreateStoreImageViewId.setOnClickListener {
            if (token != "non" && token.isNotEmpty()) {
                ActivityHelper.goToActivity(
                    activity,
                    CreateStoreActivity::class.java,
                    false
                )
            } else {
                GlobalMethodsOldClass.askGuestLogin(resources, activity)
            }
        }

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
            ActivityHelper.goToActivity(
                activity,
                NotificationActivity::class.java, false
            )
        }

        binding.homeLogoImageViewId.setOnClickListener {
            ActivityHelper.goToActivity(
                activity,
                ArakServicesActivity::class.java, false
            )
        }

        binding.storeSearchTextViewId.setOnClickListener {
            val intent = Intent(
                activity,
                SearchActivity::class.java
            )
            intent.putExtra(IS_STORE_SEARCH, true)
            startActivity(intent)
        }
    }

    private fun setStoresAdapter(storesListArrayList: ArrayList<StoreObject>) {
        storesAdapter =
            StoresAdapter(storesListArrayList, activity, language, activityResources, this)
        val mLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.storeStoresRecyclerViewId.layoutManager = mLayoutManager
        binding.storeStoresRecyclerViewId.isFocusable = false
        binding.storeStoresRecyclerViewId.isNestedScrollingEnabled = false
        binding.storeStoresRecyclerViewId.adapter = storesAdapter
    }

    private fun setCategoriesAdapter() {
        categoriesAdapter =
            CategoriesAdapter(categoriesListArrayList, activity, language, activityResources, this)
        val mLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        binding.storeCategoriesRecyclerViewId.layoutManager = mLayoutManager
        binding.storeCategoriesRecyclerViewId.isFocusable = false
        binding.storeCategoriesRecyclerViewId.isNestedScrollingEnabled = false
        binding.storeCategoriesRecyclerViewId.adapter = categoriesAdapter
    }

    private fun setupBannerSlider(storeBannersResponseArrayList: ArrayList<BannerObject>) {
        bannerSliderAdapter = BannerSliderAdapter(storeBannersResponseArrayList, requireActivity())
        binding.storeBannerRecyclerViewId.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.storeBannerRecyclerViewId.adapter = bannerSliderAdapter
        startAutoScroll()
    }

    private fun startAutoScroll() {
        autoScrollHandler.postDelayed(autoScrollRunnable, 3000)  // Start scrolling after 3 seconds
    }

    private fun autoScroll() {
        val layoutManager = binding.storeBannerRecyclerViewId.layoutManager as LinearLayoutManager
        val nextItem = layoutManager.findFirstVisibleItemPosition() + 1
        if (nextItem < bannerSliderAdapter.itemCount) {
            binding.storeBannerRecyclerViewId.smoothScrollToPosition(nextItem)
        } else {
            binding.storeBannerRecyclerViewId.smoothScrollToPosition(0)
        }
    }

    private fun getBanners() {
        //showLoadingDialog("message", activity)
        homeAdsViewModel.getBanners(language, "Bearer $token", AppEnums.BannerType.STORE)
        activity?.let {
            homeAdsViewModel.bannersModelModel.observe(
                it,
                Observer(function = fun(bannersModel: BannersModel?) {
                    bannersModel?.let {
                        if (bannersModel.statusCode == 200) {
                            storeBannersResponseArrayList.clear()
                            storeBannersResponseArrayList.addAll(bannersModel.bannersData.bannersArrayList)
                            try {
                                bannerSliderAdapter?.notifyDataSetChanged()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                            if (storeBannersResponseArrayList.isNotEmpty()) {
                                binding.storeBannerCardViewId.visibility = View.VISIBLE
                            } else {
                                binding.storeBannerCardViewId.visibility = View.GONE
                            }
                        } else {
                            activity?.let { it1 ->
                                ApplicationDialogs.openAlertDialog(
                                    it1,
                                    activityResources.getString(R.string.dialogs_error),
                                    bannersModel.message,
                                    false,
                                    AppEnums.DialogActionTypes.DISMISS,
                                    this
                                )
                            }
                            // hideView(binding.loginProgressBarId*//*progressBar*//*)

                        }
                    }
                    hideLoadingDialog()
                })
            )
        }

        activity?.let {
            homeAdsViewModel.throwableResponse.observe(
                it,
                Observer(function = fun(throwable: Throwable?) {
                    throwable?.let {
                        activity?.let { it1 ->
                            ApplicationDialogs.openAlertDialog(
                                it1,
                                activityResources.getString(R.string.dialogs_error),
                                throwable.message,
                                false,
                                AppEnums.DialogActionTypes.DISMISS,
                                this
                            )
                        }
                    }
                    hideLoadingDialog()
                })
            )
        }
    }

    private fun getStoreCategories() {
        //showLoadingDialog(this, "message")
        storesViewModel.getStoreCategories("Bearer $token", language)
        activity?.let {
            storesViewModel.storeCategoriesModelModel.observe(
                it,
                Observer(function = fun(storeCategoriesModel: StoreCategoriesModel?) {
                    storeCategoriesModel?.let {
                        if (storeCategoriesModel.statusCode == 200) {

                            categoriesListArrayList.clear()
                            val storeCategory = StoreCategory(
                                -1,
                                "الكل",
                                "All",
                                "icon_url",
                                null,
                                null,
                                null,
                            )
                            categoriesListArrayList.add(0, storeCategory)
                            categoriesListArrayList.addAll(storeCategoriesModel.data.storeCategories)
                            categoriesAdapter.notifyDataSetChanged()

                        } else {
                            activity?.let { it1 ->
                                ApplicationDialogs.openAlertDialog(
                                    it1,
                                    resources?.getString(R.string.dialogs_error),
                                    storeCategoriesModel.message,
                                    false,
                                    AppEnums.DialogActionTypes.DISMISS,
                                    this
                                )
                            }
                            // hideView(binding.loginProgressBarId*//*progressBar*//*)
                        }
                    }
                    hideLoadingDialog()
                })
            )
        }

        activity?.let {
            storesViewModel.throwableResponse.observe(
                it,
                Observer(function = fun(throwable: Throwable?) {
                    throwable?.let {
                        activity?.let { it1 ->
                            ApplicationDialogs.openAlertDialog(
                                it1,
                                resources?.getString(R.string.dialogs_error),
                                throwable.message,
                                false,
                                AppEnums.DialogActionTypes.DISMISS,
                                this
                            )
                        }
                    }
                    hideLoadingDialog()
                })
            )
        }
    }

    private fun getStoresList(
        page: Int,
        random: Boolean? = null,
        name: String? = null,
        phoneNumber: String? = null,
        isFeatured: Boolean? = null,
        storeCategoryId: Int? = null,
        hasVisa: Boolean? = null,
        hasMastercard: Boolean? = null,
        hasPaypal: Boolean? = null,
        hasCash: Boolean? = null,
    ) {
        showLoadingDialog("message", activity)
        storesViewModel.getStoresList(
            language,
            "Bearer $token",
            page,
            random,
            name,
            phoneNumber,
            isFeatured,
            storeCategoryId,
            hasVisa,
            hasMastercard,
            hasPaypal,
            hasCash
        )
        activity?.let {
            storesViewModel.storesListModelModel.observe(
                it,
                Observer(function = fun(storesListModel: StoresListModel?) {
                    storesListModel?.let {
                        if (storesListModel.statusCode == 200) {
                            storesListArrayList.clear()
                            storesListArrayList.addAll(
                                storesListModel.data.stores
                            )
                            storesAdapter!!.notifyDataSetChanged()

                            if (storesListArrayList.isEmpty()) {
                                binding.storesEmptyViewImageViewId.visibility = View.VISIBLE
                                binding.storesEmptyViewTextViewId.visibility = View.VISIBLE
                            } else {
                                binding.storesEmptyViewImageViewId.visibility = View.GONE
                                binding.storesEmptyViewTextViewId.visibility = View.GONE
                            }


                        } else {
                            activity?.let { it1 ->
                                ApplicationDialogs.openAlertDialog(
                                    it1,
                                    activityResources.getString(R.string.dialogs_error),
                                    storesListModel.message,
                                    false,
                                    AppEnums.DialogActionTypes.DISMISS,
                                    this
                                )
                            }
                            // hideView(binding.loginProgressBarId*//*progressBar*//*)

                        }
                    }
                    hideLoadingDialog()
                })
            )
        }

        activity?.let {
            storesViewModel.throwableResponse.observe(
                it,
                Observer(function = fun(throwable: Throwable?) {
                    throwable?.let {
                        activity?.let { it1 ->
                            ApplicationDialogs.openAlertDialog(
                                it1,
                                activityResources.getString(R.string.dialogs_error),
                                throwable.message,
                                false,
                                AppEnums.DialogActionTypes.DISMISS,
                                this
                            )
                        }
                    }
                    hideLoadingDialog()
                })
            )
        }
    }


    override fun onStoreClickedCalledBack(position: Int, storeId: Int) {
        val intent = Intent(activity, StoreProfileActivity::class.java)
        intent.putExtra(SELECTED_STORE_ID, storeId)
        startActivity(intent)
    }

    override fun onCategorySelectedCalledBack(position: Int, categoryId: Int) {
        storesListArrayList.clear()
        storesAdapter!!.notifyDataSetChanged()
        currentPage = 1
        if (categoryId == -1) {
            getStoresList(page = currentPage)
        } else {
            getStoresList(page = currentPage, storeCategoryId = categoryId)
        }
    }

    override fun onClose() {
    }

    override fun onConfirm(actionType: Int) {
    }

    override fun onNextPageRequired() {
        if (currentPage < lastPage) {
            getStoresList(page = currentPage + 1)
            //todo .. handle date filtration
            /*if (dateFrom.trim().isNotEmpty() && dateTo.trim().isNotEmpty()) {
                getMyAds(page = currentPage + 1)
            } else {
                getMyAds(dateFrom = searchName, dateTo = dateTo, page = currentPage + 1)
            }*/
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

    override fun onPause() {
        super.onPause()
        autoScrollHandler.removeCallbacks(autoScrollRunnable)
    }

    override fun onResume() {
        super.onResume()
        startAutoScroll()
        updateCartCounter()
    }

    override fun onDestroy() {
        super.onDestroy()
        autoScrollHandler.removeCallbacks(autoScrollRunnable)
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }
}