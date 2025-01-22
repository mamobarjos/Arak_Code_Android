package com.arakadds.arak.presentation.activities.home.fragments

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.arakadds.arak.R
import com.arakadds.arak.common.helper.connectivity_checker.ConnectionLiveData
import com.arakadds.arak.common.preferaence.IPreferenceHelper
import com.arakadds.arak.common.preferaence.PreferenceManager
import com.arakadds.arak.data.CartManager
import com.arakadds.arak.data.CartUpdateEvent
import com.arakadds.arak.databinding.FragmentArakStoreBinding
import com.arakadds.arak.model.arakStoreModel.StoreCategory
import com.arakadds.arak.model.arakStoreModel.StoreCategoryData
import com.arakadds.arak.model.arakStoreModel.StoreProduct
import com.arakadds.arak.model.arakStoreModel.StoreProductData
import com.arakadds.arak.presentation.activities.ArakStore.CartActivity
import com.arakadds.arak.presentation.activities.home.HomeActivity
import com.arakadds.arak.presentation.activities.settings.NotificationActivity
import com.arakadds.arak.presentation.adapters.CategoryArakAdapter
import com.arakadds.arak.presentation.adapters.ProductArakAdapter
import com.arakadds.arak.presentation.dialogs.ApplicationDialogs
import com.arakadds.arak.presentation.dialogs.loader.ProgressDialogUtil.hideLoadingDialog
import com.arakadds.arak.presentation.viewmodel.ArakStoreViewModel
import com.arakadds.arak.utils.AppEnums
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class ArakStoreFragment : DaggerFragment(R.layout.fragment_arak_store),
    ApplicationDialogs.AlertDialogCallbacks, ProductArakAdapter.ProductCallBacks,
    CategoryArakAdapter.ArakStoreCallBacks {

    private lateinit var binding: FragmentArakStoreBinding
    private val preferenceHelper: IPreferenceHelper by lazy { PreferenceManager(requireActivity().applicationContext) }

    var connectionLiveData: ConnectionLiveData? = null

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val arakStoreViewModel: ArakStoreViewModel by viewModels {
        viewModelFactory
    }

    private lateinit var token: String
    private lateinit var activityResources: Resources
    private var storeCategoryDataArrayList = ArrayList<StoreCategoryData>()
    private var language: String = "ar"
    private var currentPage = 1
    private var lastPage: Int = 1

    var productArray = ArrayList<StoreProductData>()

    private lateinit var productArakAdapter: ProductArakAdapter

    // AdMob Interstitial Ad
    private var interstitialAd: InterstitialAd? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentArakStoreBinding.bind(view)

        // Initialize AdMob
        MobileAds.initialize(requireContext()) {}

        // Load Interstitial Ad
        loadInterstitialAd()

        connectionLiveData = requireActivity().let { ConnectionLiveData(it) }
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
        language = preferenceHelper.getLanguage()
        setHasOptionsMenu(true)
        checkNetworkConnection()

        initData()

        getStoreCategoryData()
        getStoreProductData(pageNum = currentPage, perPage = 40)
        listeners()
        updateCartCounter()
        binding.productRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        productArakAdapter = ProductArakAdapter(
            requireContext(), productArray, currentPage,
            lastPage, preferenceHelper.getCurrencySymbol(), this
        )
        binding.productRecyclerView.adapter = productArakAdapter
    }

    private fun initData() {
        token = preferenceHelper.getToken()
        binding.cartCounterTextViewId.setText(HomeActivity.cartNum)
        binding.notificationCounterTextViewId.setText(HomeActivity.NotificationNum)
    }

    private fun listeners() {
        binding.apply {
            frameLayoutcart.setOnClickListener {
                startActivity(Intent(requireContext(), CartActivity::class.java))
            }

            frameLayoutnot.setOnClickListener {
                startActivity(Intent(requireContext(), NotificationActivity::class.java))
            }
        }
    }

    private fun getStoreCategoryData() {
        arakStoreViewModel.getArakStoreCategory(language, token)
        activity?.let {
            arakStoreViewModel.baseResponseModel.observe(
                it,
                Observer { storeCategory ->
                    storeCategory?.let {
                        if (storeCategory.statusCode == 200) {
                            storeCategoryDataArrayList.clear()
                            val storeCategoryData = StoreCategoryData(
                                -1,
                                "All",
                                "الكل",
                                "icon_url",
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null
                            )
                            storeCategoryDataArrayList.add(0, storeCategoryData)
                            storeCategoryDataArrayList.addAll(storeCategory.data)

                            if (isAdded) { // Check if the fragment is added
                                binding.categoryRecyclerView.adapter = CategoryArakAdapter(
                                    requireContext(),
                                    storeCategoryDataArrayList,
                                    this,
                                    language,
                                    this
                                )
                            }
                        } else {
                            if (isAdded) { // Check if the fragment is added
                                ApplicationDialogs.openAlertDialog(
                                    requireActivity(),
                                    getString(R.string.dialogs_error),
                                    storeCategory.message,
                                    false,
                                    AppEnums.DialogActionTypes.DISMISS,
                                    this
                                )
                            }
                        }
                    }
                    hideLoadingDialog()
                }
            )
        }

        arakStoreViewModel.throwableResponse.observe(
            viewLifecycleOwner,
            Observer { throwable ->
                throwable?.let {
                    if (isAdded) { // Check if the fragment is added
                        ApplicationDialogs.openAlertDialog(
                            requireActivity(),
                            getString(R.string.dialogs_error),
                            throwable.message,
                            false,
                            AppEnums.DialogActionTypes.DISMISS,
                            this
                        )
                    }
                }
                hideLoadingDialog()
            }
        )
    }

    private fun getStoreProductData(category: Int? = null, pageNum: Int, perPage: Int? = null) {
        arakStoreViewModel.getArakStoreProduct(
            language,
            token,
            category,
            pageNum,
            perPage = perPage
        )
        activity?.let {
            arakStoreViewModel.baseResponseProductModel.observe(
                it,
                Observer(function = fun(storeProduct: StoreProduct?) {
                    storeProduct?.let {
                        if (storeProduct.statusCode == 200) {
                            if (currentPage == 1) {
                                productArray.clear()
                            }

                            if (storeProduct.data.size > 0) {
                                productArray.addAll(storeProduct.data)
                                try {
                                    binding.productRecyclerView.adapter!!.notifyDataSetChanged()
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                if (storeProduct.data.size == perPage) {
                                    lastPage += 1
                                } else {
                                }
                            } else {
                                binding.productRecyclerView.adapter!!.notifyDataSetChanged()
                                lastPage = currentPage
                            }

                        } else {
                            activity?.let { it1 ->
                                ApplicationDialogs.openAlertDialog(
                                    it1,
                                    activityResources.getString(R.string.dialogs_error),
                                    storeProduct.message,
                                    false,
                                    AppEnums.DialogActionTypes.DISMISS,
                                    this
                                )
                            }
                        }
                    }
                    hideLoadingDialog()
                })
            )
        }

        arakStoreViewModel.throwableResponse.observe(
            requireActivity(),
            Observer(function = fun(throwable: Throwable?) {
                throwable?.let {
                    ApplicationDialogs.openAlertDialog(
                        requireActivity(),
                        resources?.getString(R.string.dialogs_error),
                        throwable.message,
                        false,
                        AppEnums.DialogActionTypes.DISMISS,
                        this
                    )
                }
                hideLoadingDialog()
            })
        )
    }

    private fun loadInterstitialAd() {
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            requireContext(),
            "ca-app-pub-7303745888048368/2571711493", // Replace with your Ad Unit ID
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    // The InterstitialAd is ready to be shown.
                    interstitialAd = ad
                    showInterstitialAd()
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    // Handle the error.
                    interstitialAd = null
                }
            }
        )
    }

    private fun showInterstitialAd() {
        if (interstitialAd != null) {
            interstitialAd?.show(requireActivity())
        } else {
            // Ad not ready yet, try loading it again.
            loadInterstitialAd()
        }
    }

    override fun onClose() {}

    override fun onConfirm(actionType: Int) {}

    private fun checkNetworkConnection() {}

    override fun onNextPageRequired() {
        if (currentPage < lastPage) {
            currentPage += 1
            getStoreProductData(pageNum = currentPage, perPage = 20)
        }
    }

    override fun onAllCategoriesSelected() {
        currentPage = 1
        lastPage = 1
        getStoreProductData(pageNum = currentPage, perPage = 20)
    }

    override fun onCategorySelected(categoryId: Int) {
        currentPage = 1
        lastPage = 1
        getStoreProductData(category = categoryId, pageNum = currentPage, perPage = 20)
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
    }

    override fun onDestroy() {
        super.onDestroy()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }
}