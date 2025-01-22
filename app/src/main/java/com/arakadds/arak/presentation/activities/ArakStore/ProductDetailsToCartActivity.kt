package com.arakadds.arak.presentation.activities.ArakStore

import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.arakadds.arak.R
import com.arakadds.arak.common.helper.LocaleHelper
import com.arakadds.arak.common.helper.connectivity_checker.ConnectionLiveData
import com.arakadds.arak.common.preferaence.IPreferenceHelper
import com.arakadds.arak.common.preferaence.PreferenceManager
import com.arakadds.arak.data.ArakProduct
import com.arakadds.arak.data.CartManager
import com.arakadds.arak.data.ProductVariant
import com.arakadds.arak.databinding.ActivityProductDetailsToCartBinding
import com.arakadds.arak.model.Attributes
import com.arakadds.arak.model.arakStoreModel.StoreProductData
import com.arakadds.arak.presentation.activities.other.BaseActivity
import com.arakadds.arak.presentation.adapters.ProductArakAdapter.Companion.productDetails
import com.arakadds.arak.presentation.dialogs.ApplicationDialogs
import com.arakadds.arak.presentation.viewmodel.ArakStoreViewModel
import com.arakadds.arak.utils.AppEnums
import com.arakadds.arak.utils.AppProperties
import com.arakadds.arak.utils.GlobalMethodsOldClass
import com.bumptech.glide.Glide
import dagger.android.AndroidInjection
import io.paperdb.Paper
import kotlinx.coroutines.launch
import javax.inject.Inject


class ProductDetailsToCartActivity : BaseActivity(), ApplicationDialogs.AlertDialogCallbacks {

    lateinit var binding: ActivityProductDetailsToCartBinding
    var storeProduct: StoreProductData = StoreProductData()

    private val preferenceHelper: IPreferenceHelper by lazy { PreferenceManager(applicationContext) }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: ArakStoreViewModel by viewModels {
        viewModelFactory
    }

    private lateinit var token: String
    private var language: String = "ar"
    private var quantity = 1
    private lateinit var activityResources: Resources
    private lateinit var activityContext: Context
    private lateinit var toolbar: Toolbar
    private lateinit var pageTitle: TextView
    private var connectionLiveData: ConnectionLiveData? = null
    private var variantID: String = ""
    private var variantName: String = ""

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailsToCartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AndroidInjection.inject(this)
        activityContext=this
        language = preferenceHelper.getLanguage()
        updateProductDetailsToCartView(language)
        initData()
        initToolbar()
        storeProduct = productDetails!!

        getArakStoreProductVariant(storeProduct.id.toString())

        initViews()
        handleTheQuantity()
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

    private fun updateProductDetailsToCartView(language: String) {
        val context = LocaleHelper.setLocale(this, language)
        activityResources = context.resources
        binding.chooseTypeLabel.text =
            activityResources.getString(R.string.ShippingAddress_activity_First_Name)

        binding.Labelquantity.text =
            activityResources.getString(R.string.Details_Cart_activity_Quantity)

        binding.priceDisplay.text =
            activityResources.getString(R.string.my_ads_activity_Price) + "0.0" + preferenceHelper.getCurrencySymbol()


        binding.buyButton.text =
            activityResources.getString(R.string.Details_Cart_activity_buyButton)



        this.language = language
    }

    private fun initData() {
        token = preferenceHelper.getToken()
        if (token == "non" || token.trim().isEmpty()) {
            GlobalMethodsOldClass.askGuestLogin(resources, activityContext)
            return
        }
    }

    private fun initViews() {
        binding.apply {


            Glide.with(this@ProductDetailsToCartActivity)
                .load(storeProduct.images.get(0).src.toString())
                .placeholder(R.drawable.image_holder_virtical)
                .into(viewPager)

            productName.text = storeProduct.name.toString()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                // For Android N and above
                specs1.text =
                    Html.fromHtml(storeProduct.description, Html.FROM_HTML_MODE_LEGACY).toString()
            } else {
                // For Android versions below N
                specs1.text = Html.fromHtml(storeProduct.description).toString()
            }

            ratingBar.rating = storeProduct.averageRating!!.toFloat()
            binding.priceDisplay.text =
                activityResources.getString(R.string.my_ads_activity_Price) + " " + storeProduct.price.toString() + " " + preferenceHelper.getCurrencySymbol()

            buyButton.setOnClickListener {
                addToCart(
                    quantity,
                    storeProduct.id!!,
                    variantID.toInt(),
                    variantName,
                    storeProduct.images.get(0).src.toString(),
                    storeProduct.name.toString(),
                    storeProduct.price.toString()
                )
            }
        }
    }

    fun addToCart(
        quantity: Int?,
        productID: Int,
        variantID: Int,
        variantName: String,
        productImage: String,
        productname: String,
        productPrice: String
    ) {

        Thread {
            lifecycleScope.launch {
                CartManager(this@ProductDetailsToCartActivity).insertProduct(
                    ArakProduct(
                        productID,
                        quantity,
                        productImage,
                        productname,
                        productPrice,
                        ProductVariant(variantID, variantName)
                    ), ProductVariant(variantID, variantName)
                )
            }
        }.start()

        Toast.makeText(this, "Product Added To Cart", Toast.LENGTH_SHORT).show()
    }

    fun handleTheQuantity() {
        binding.apply {
            quantityText.text = quantity.toString()

            minisButton.setOnClickListener {
                if (quantity > 1) { // Prevent quantity from going below 1
                    quantity--
                    quantityText.text = quantity.toString()
                }
            }

            plusButton.setOnClickListener {
                quantity++
                quantityText.text = quantity.toString()
            }
        }
    }


    fun setDataToSpinner(attributes: ArrayList<Attributes>) {
        val productAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            attributes.map { it.name } // Extract the names for display
        )

        // Set the layout for the dropdown list
        productAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Attach the adapter to the Spinner
        binding.typeSpinner.adapter = productAdapter

        // Handle item selection
        binding.typeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedProduct = attributes[position]
                variantID = selectedProduct.id.toString()
                variantName = selectedProduct.name.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
    }


    fun getArakStoreProductVariant(ProductID: String) {

        viewModel.getArakStoreProductVariant(
            language,
            token,
            productID = ProductID
        )
        this?.let {
            viewModel.baseResponseProductVariantModel.observe(
                it,
                Observer(function = fun(productvariant: com.arakadds.arak.model.ProductVariant?) {
                    productvariant?.let {
                        if (productvariant.statusCode == 200) {

                            setDataToSpinner(productvariant.data.get(0).attributes)

                        } else {
                            this?.let { it1 ->
                                ApplicationDialogs.openAlertDialog(
                                    it1,
                                    this.resources.getString(R.string.dialogs_error),
                                    productvariant.message,
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

    override fun onClose() {
        TODO("Not yet implemented")
    }

    override fun onConfirm(actionType: Int) {
        TODO("Not yet implemented")
    }


}