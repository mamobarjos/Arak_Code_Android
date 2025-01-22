package com.arakadds.arak.presentation.activities.ArakStore

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import com.arakadds.arak.R
import com.arakadds.arak.common.helper.LocaleHelper
import com.arakadds.arak.common.helper.connectivity_checker.ConnectionLiveData
import com.arakadds.arak.common.preferaence.IPreferenceHelper
import com.arakadds.arak.common.preferaence.PreferenceManager
import com.arakadds.arak.data.ArakProduct
import com.arakadds.arak.data.CartManager
import com.arakadds.arak.databinding.ActivityCartBinding
import com.arakadds.arak.presentation.adapters.CartArakAdapter
import com.arakadds.arak.utils.GlobalMethodsOldClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.lang.Math.round

class CartActivity : AppCompatActivity() {
    private val preferenceHelper: IPreferenceHelper by lazy { PreferenceManager(applicationContext) }

    lateinit var binding: ActivityCartBinding
    private lateinit var language: String
    private lateinit var resources: Resources
    private lateinit var activityContext: Context

    private lateinit var toolbar: Toolbar
    private lateinit var pageTitle: TextView
    private lateinit var token: String
    private lateinit var currancy: String
    private lateinit var productsList: ArrayList<ArakProduct>

    private var connectionLiveData: ConnectionLiveData? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        activityContext = this
        language = preferenceHelper.getLanguage()
        initToolbar()
        updateCartView(language)
        initData()
        getCartProducts()
        listeners()
    }

    private fun initData() {
        token = preferenceHelper.getToken()
        currancy = preferenceHelper.getCurrencySymbol()
        if (token.trim().isEmpty()) {
            GlobalMethodsOldClass.askGuestLogin(resources, activityContext)
            return
        }
    }

    private fun initToolbar() {
        toolbar = binding.appBarLayout.findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        pageTitle = binding.appBarLayout.findViewById(R.id.page_title)
        val backImageView: ImageView = binding.appBarLayout.findViewById(R.id.back_button)
        //shareImageView = binding.appBarLayout.findViewById(R.id.share_button)
        //favoriteImageView = binding.appBarLayout.findViewById(R.id.favorite_button)
        pageTitle.text = activityContext.getString(R.string.cart_activity_Special_title)
        backImageView.setOnClickListener { finish() }
    }

    private fun updateCartView(language: String) {
        val context = LocaleHelper.setLocale(this, language)
        resources = context.resources
        binding.specialNoteLabel.text =
            resources.getString(R.string.cart_activity_Special_note)

        binding.specialNoteLabel.hint =
            resources.getString(R.string.cart_activity_note)

        binding.paymentSummaryLabel.text =
            resources.getString(R.string.cart_activity_note)

        binding.cartTotalLabel.text =
            resources.getString(R.string.cart_activity_cart_total)

        binding.discountLabel.text =
            resources.getString(R.string.cart_activity_discount)
        binding.taxLabel.text =
            resources.getString(R.string.cart_activity_tax)

        binding.totalAmountLabel.text =
            resources.getString(R.string.cart_activity_total_amount)

        binding.placeOrderButton.text =
            resources.getString(R.string.cart_activity_Place_order)
        this.language = language
    }

    fun getCartProducts() {
        Thread {
            lifecycleScope.launch {
                productsList =
                    CartManager(this@CartActivity).getCartProducts() as ArrayList<ArakProduct>

                withContext(Dispatchers.Main) {
                    binding.recyclerViewCart.adapter =
                        CartArakAdapter(
                            this@CartActivity,
                            productsList,
                            currancy,
                            this@CartActivity
                        )
                }
            }
        }.start()
    }

    fun listeners() {
        binding.apply {
            placeOrderButton.setOnClickListener {
                startActivity(Intent(this@CartActivity, ShippingAddressActivity::class.java))
                finish()
            }
        }
    }

    fun stringToDouble(string: String): Double {
        val doubleValue = string.toDoubleOrNull() ?: 0.0
        return doubleValue.roundTo4DecimalPlaces()
    }

    fun onTotalPriceUpdated(cartTotalProducts: Int, newTotalPrice: Double) {
        val roundedTotal = newTotalPrice.roundTo4DecimalPlaces()

        // Update cart total
        binding.cartTotalValue?.text = "$roundedTotal $currancy"

        // Calculate tax (if any)
        val taxRate = 0.0 // Update with your tax rate
        val taxAmount = (roundedTotal * taxRate).roundTo4DecimalPlaces()
        binding.taxValue?.text = "$taxAmount $currancy"

        // Calculate discount (if any)
        val discountAmount = 0.0 // Update with your discount logic
        binding.discountValue?.text = "$discountAmount $currancy"

        // Calculate final total
        val finalTotal = (roundedTotal + taxAmount - discountAmount).roundTo4DecimalPlaces()
        binding.totalAmountValue?.text = "$finalTotal $currancy"

        // Update UI if cart is empty
        if (cartTotalProducts == 0) {
            binding.placeOrderButton.isEnabled = false
            // Optionally show empty cart message
        } else {
            binding.placeOrderButton.isEnabled = true
        }
    }

    fun Double.roundTo4DecimalPlaces(): Double {
        return round(this * 10000) / 10000.0
    }

    fun onRemoveProduct(position: Int) {
        productsList.removeAt(position)
        binding.recyclerViewCart.adapter?.notifyDataSetChanged()
    }

    fun onDecreaseProductQuantity(arakProduct: ArakProduct) {
        lifecycleScope.launch {
            CartManager(this@CartActivity).decreaseProductQuantity(arakProduct)
        }
    }

    fun onAddProduct(arakProduct: ArakProduct) {
        lifecycleScope.launch {
            CartManager(this@CartActivity).increaseProductQuantity(arakProduct)
        }
    }
    override fun onResume() {
        super.onResume()

    }

}