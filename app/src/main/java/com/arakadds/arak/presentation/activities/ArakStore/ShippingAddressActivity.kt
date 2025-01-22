package com.arakadds.arak.presentation.activities.ArakStore

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.arakadds.arak.R
import com.arakadds.arak.common.helper.LocaleHelper
import com.arakadds.arak.common.helper.connectivity_checker.ConnectionLiveData
import com.arakadds.arak.common.preferaence.IPreferenceHelper
import com.arakadds.arak.common.preferaence.PreferenceManager
import com.arakadds.arak.data.CartManager
import com.arakadds.arak.databinding.ActivityProductDetailsToCartBinding
import com.arakadds.arak.databinding.ActivityShippingAddressBinding
import com.arakadds.arak.model.ArakStoreMakeOrder
import com.arakadds.arak.model.ProductVariant
import com.arakadds.arak.model.arakStoreModel.StoreProductData
import com.arakadds.arak.model.new_mapping_refactore.response.banners.cities.CitiesModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.cities.CityResponseData
import com.arakadds.arak.model.new_mapping_refactore.response.banners.countries.CountriesModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.countries.CountriesResponseData
import com.arakadds.arak.presentation.activities.other.BaseActivity
import com.arakadds.arak.presentation.activities.payments.PaymentWebActivity
import com.arakadds.arak.presentation.dialogs.ApplicationDialogs
import com.arakadds.arak.presentation.viewmodel.ArakStoreViewModel
import com.arakadds.arak.presentation.viewmodel.CreateAccountViewModel
import com.arakadds.arak.utils.AppEnums
import com.arakadds.arak.utils.AppEnums.LanguagesEnums.ARABIC
import com.arakadds.arak.utils.AppProperties
import com.arakadds.arak.utils.Constants.CHECK_OUT_URL_ID
import com.arakadds.arak.utils.Constants.PAYMENT_TYPE
import com.arakadds.arak.utils.GlobalMethodsOldClass
import dagger.android.AndroidInjection
import io.paperdb.Paper
import kotlinx.coroutines.launch
import javax.inject.Inject

class ShippingAddressActivity : BaseActivity(), ApplicationDialogs.AlertDialogCallbacks {

    lateinit var binding: ActivityShippingAddressBinding

    private val preferenceHelper: IPreferenceHelper by lazy { PreferenceManager(applicationContext) }
    private lateinit var activityResources: Resources
    private lateinit var activityContext: Context
    private lateinit var toolbar: Toolbar
    private lateinit var pageTitle: TextView
    private var connectionLiveData: ConnectionLiveData? = null
    private var cityId = 0
    private var cityName = ""
    private var countryId = 0
    private var countryName = ""
    private var countryCode = ""

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: ArakStoreViewModel by viewModels {
        viewModelFactory
    }

    private val createAccountViewModel: CreateAccountViewModel by viewModels {
        viewModelFactory
    }

    private lateinit var token: String
    private var language: String = "ar"
    private var paymentMethod = ""
    private var orderHashMap: HashMap<String, Any> = HashMap()

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShippingAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AndroidInjection.inject(this)

        activityContext = this
        language = preferenceHelper.getLanguage()
        initToolbar()
        updateShippingAddressView(language)

        initData()
        setTheSpinner()
        listeners()
        getCountries()
    }

    private fun initToolbar() {
        toolbar = binding.appBarLayout.findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        pageTitle = binding.appBarLayout.findViewById(R.id.page_title)
        val backImageView: ImageView = binding.appBarLayout.findViewById(R.id.back_button)
        //shareImageView = binding.appBarLayout.findViewById(R.id.share_button)
        //favoriteImageView = binding.appBarLayout.findViewById(R.id.favorite_button)
        pageTitle.text = activityContext.getString(R.string.ShippingAddress_activity_title)
        backImageView.setOnClickListener { finish() }
    }

    private fun updateShippingAddressView(language: String) {
        val context = LocaleHelper.setLocale(this, language)
        activityResources = context.resources
        binding.firstName.hint =
            activityResources.getString(R.string.ShippingAddress_activity_First_Name)

        binding.lastName.hint =
            activityResources.getString(R.string.ShippingAddress_activity_Last_Name)

        binding.address.hint =
            activityResources.getString(R.string.ShippingAddress_activity_address)

        binding.choosePaymentLabel.text =
            activityResources.getString(R.string.ShippingAddress_activity_Choose_Payment)

        binding.placeOrderButton.text =
            activityResources.getString(R.string.ShippingAddress_activity_Place_Order)

        this.language = language
    }

    private fun initData() {
        token = preferenceHelper.getToken()
        if (token == "non" || token.trim().isEmpty()) {
            GlobalMethodsOldClass.askGuestLogin(resources, activityContext)
            return
        }
    }

    private fun listeners() {
        binding.apply {

            placeOrderButton.setOnClickListener {
                fillDataToSendOrder()
            }
        }
    }

    private fun setTheSpinner() {
        val paymentOptions = arrayOf("Wallet", "Card")

        // Create an ArrayAdapter using the payment options array and a default spinner layout
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item, // Layout for individual items
            paymentOptions // Data source
        )

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Apply the adapter to the Spinner
        binding.paymentMethodSpinner.adapter = adapter

        // Set an OnItemSelectedListener to handle user selections
        binding.paymentMethodSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    // Get the selected item
                    val selectedItem = parent.getItemAtPosition(position).toString()

                    // Display the selected item (or use it as needed)
                    paymentMethod = selectedItem
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Handle the case when no item is selected (optional)
                }
            }

    }

    private fun fillDataToSendOrder() {

// Populate the HashMap
        // Handle payment method
        if (paymentMethod.equals("wallet", ignoreCase = true)) {
            orderHashMap["payment_type"] = "WALLET"  // Adjusted to fit JSON example
            orderHashMap["payment_method"] = "WALLET"  // Adjusted to fit JSON example
            orderHashMap["payment_method_title"] = "Wallet"
            orderHashMap["set_paid"] = true
        } else {
            // Assuming this block is for 'Card'
            orderHashMap["payment_type"] = "CARD"
            orderHashMap["payment_method"] = "CARD"
            orderHashMap["payment_method_title"] = "Credit Card"
            orderHashMap["set_paid"] = true
        }

// Create and populate the billing HashMap
        val billingHashMap: HashMap<String, Any> = HashMap()
        billingHashMap["first_name"] = binding.firstName.text.toString()
        billingHashMap["last_name"] = binding.lastName.text.toString()
        billingHashMap["address_1"] = binding.address.text.toString()
        billingHashMap["city"] = cityName  // Corrected to match JSON structure
        billingHashMap["state"] =
           countryName  // Corrected to match JSON structure
        billingHashMap["postcode"] = binding.postCode.text.toString()
        billingHashMap["country"] = countryId
        billingHashMap["phone"] = binding.phoneNumber.text.toString()

// Create and populate the shipping HashMap
        val shippingHashMap: HashMap<String, Any> = HashMap()
        shippingHashMap["first_name"] = binding.firstName.text.toString()
        shippingHashMap["last_name"] = binding.lastName.text.toString()
        shippingHashMap["address_1"] = binding.address.text.toString()
        shippingHashMap["city"] = cityId  // Corrected to match JSON structure
        shippingHashMap["state"] =
            countryName  // Corrected to match JSON structure
        shippingHashMap["postcode"] = binding.postCode.text.toString()
        shippingHashMap["country"] = countryCode

// Create and populate the line_items list dynamically
        val lineItemsList: ArrayList<HashMap<String, Any>> = ArrayList()

// Assuming you have a list of products, for example:
        Thread {
            lifecycleScope.launch {
                val products = CartManager(this@ShippingAddressActivity).getCartProducts()

// Populate line_items from the products list
                for (product in products) {
                    val lineItemHashMap: HashMap<String, Any> = HashMap()
                    lineItemHashMap["product_id"] = product.id
                    lineItemHashMap["quantity"] = product.quantity.toString()
                    lineItemHashMap["variation_id"] = product.selectedVariant!!.idVariant
                    lineItemsList.add(lineItemHashMap)
                }

// Insert the nested maps and list into the main HashMap
                orderHashMap["billing"] = billingHashMap
                orderHashMap["shipping"] = shippingHashMap
                orderHashMap["line_items"] = lineItemsList

                getArakStoreMakeOrder(orderHashMap)
            }
        }.start()

    }

    fun getArakStoreMakeOrder(orderHashMap: HashMap<String, Any>) {

        viewModel.makeOrderArakStore(
            "Bearer $token",
            language,
            orderHashMap
        )
        this?.let {
            viewModel.baseResponseOrderModel.observe(
                it,
                Observer(function = fun(arakStoreMakeOrder: ArakStoreMakeOrder?) {
                    arakStoreMakeOrder?.let {
                        if (arakStoreMakeOrder.statusCode == 200 || arakStoreMakeOrder.statusCode == 201) {

                            startActivity(
                                Intent(this, PaymentWebActivity::class.java).putExtra(
                                    CHECK_OUT_URL_ID,
                                    arakStoreMakeOrder.data!!.checkoutUrl
                                ).putExtra(PAYMENT_TYPE, "Store")
                            )

                        } else {
                            this?.let { it1 ->
                                ApplicationDialogs.openAlertDialog(
                                    it1,
                                    this.resources.getString(R.string.dialogs_error),
                                    arakStoreMakeOrder.message,
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

        /*  this?.let {
              viewModel.throwableResponse.observe(
                  it,
                  Observer(function = fun(throwable: Throwable?) {
                      throwable?.let {
                          throwable?.let { it1 ->
                              ApplicationDialogs.openAlertDialog(
                                  this,
                                  this.resources.getString(R.string.dialogs_error),
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
          }*/
    }

    private fun initCountrySpinner(countriesResponseDataArrayList: ArrayList<CountriesResponseData>) {
        if (countriesResponseDataArrayList.size > 0) {
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item, countriesResponseDataArrayList
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.country.adapter = adapter
            binding.country.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    val countriesResponseData = parent.selectedItem as CountriesResponseData
                    countryId = countriesResponseData.id
                    countryCode = countriesResponseData.countryCode
                    if (language == ARABIC)
                        countryName = countriesResponseData.nameAr
                    else
                        countryName = countriesResponseData.nameEn


                    binding.ShippingAddressPhoneCodeTextViewId.text = countryCode
                    getCities(countryId)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
    }

    private fun getCountries() {
        showLoadingDialog(this, "message")
        createAccountViewModel.getCountries(language)
        createAccountViewModel.countriesModel.observe(
            this,
            Observer(function = fun(countriesModel: CountriesModel?) {
                countriesModel?.let {
                    if (countriesModel.statusCode == 200) {
                        initCountrySpinner(countriesModel.countriesData.countriesResponseDataArrayList)
                    } else {
                        ApplicationDialogs.openAlertDialog(
                            this,
                            activityResources?.getString(R.string.dialogs_error),
                            countriesModel.message,
                            false,
                            AppEnums.DialogActionTypes.DISMISS,
                            this
                        )
                        // hideView(binding.loginProgressBarId*//*progressBar*//*)
                    }
                }
                hideLoadingDialog()
            })
        )

        createAccountViewModel.throwableResponse.observe(
            this,
            Observer(function = fun(throwable: Throwable?) {
                throwable?.let {
                    ApplicationDialogs.openAlertDialog(
                        this,
                        activityResources?.getString(R.string.dialogs_error),
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

    private fun getCities(countryId: Int) {
        showLoadingDialog(this, "message")
        createAccountViewModel.getCities(countryId, language)
        createAccountViewModel.citiesModel.observe(
            this,
            Observer(function = fun(citiesModel: CitiesModel?) {
                citiesModel?.let {
                    if (citiesModel.statusCode == 200) {
                        initCitiesSpinner(citiesModel.cityData.citiesArrayList)
                    } else {
                        ApplicationDialogs.openAlertDialog(
                            this,
                            activityResources?.getString(R.string.dialogs_error),
                            citiesModel.message,
                            false,
                            AppEnums.DialogActionTypes.DISMISS,
                            this
                        )
                        // hideView(binding.loginProgressBarId*//*progressBar*//*)
                    }
                }
                hideLoadingDialog()
            })
        )

        createAccountViewModel.throwableResponse.observe(
            this,
            Observer(function = fun(throwable: Throwable?) {
                throwable?.let {
                    ApplicationDialogs.openAlertDialog(
                        this,
                        activityResources?.getString(R.string.dialogs_error),
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

    private fun initCitiesSpinner(citiesArrayList: ArrayList<CityResponseData>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, citiesArrayList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.city.adapter = adapter
        binding.city.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                val cityResponseData =
                    parent.selectedItem as CityResponseData
                cityId = cityResponseData.id

                if (language == ARABIC)
                    cityName = cityResponseData.nameAr
                else
                    cityName = cityResponseData.nameEn

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    override fun onClose() {

    }

    override fun onConfirm(actionType: Int) {

    }

}