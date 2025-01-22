package com.arakadds.arak.presentation.activities.payments.wallet

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.arakadds.arak.R
import com.arakadds.arak.common.helper.GoogleAdsHelper
import com.arakadds.arak.common.helper.LocaleHelper
import com.arakadds.arak.common.helper.connectivity_checker.ConnectionLiveData
import com.arakadds.arak.common.preferaence.IPreferenceHelper
import com.arakadds.arak.common.preferaence.PreferenceManager
import com.arakadds.arak.databinding.ActivityCashWithdrawBinding
import com.arakadds.arak.model.new_mapping_refactore.BaseResponse
import com.arakadds.arak.model.new_mapping_refactore.response.banners.auth.user_balance.UserBalanceModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.wallets.DigitalWallet
import com.arakadds.arak.model.new_mapping_refactore.response.banners.wallets.DigitalWalletsModel
import com.arakadds.arak.presentation.activities.other.BaseActivity
import com.arakadds.arak.presentation.activities.other.NoInternetConnectionActivity
import com.arakadds.arak.presentation.dialogs.ApplicationDialogs
import com.arakadds.arak.presentation.dialogs.ApplicationDialogs.openAlertDialog
import com.arakadds.arak.presentation.viewmodel.WalletViewModel
import com.arakadds.arak.utils.AppEnums.DialogActionTypes.DISMISS
import com.arakadds.arak.utils.AppProperties
import com.arakadds.arak.utils.GlobalMethodsOldClass
import dagger.android.AndroidInjection
import io.paperdb.Paper
import javax.inject.Inject

class CashWithdrawActivity : BaseActivity(), ApplicationDialogs.AlertDialogCallbacks {

    private val preferenceHelper: IPreferenceHelper by lazy { PreferenceManager(applicationContext) }
    lateinit var binding: ActivityCashWithdrawBinding
    private lateinit var activityContext: Context

    private lateinit var toolbar: Toolbar
    private lateinit var pageTitle: TextView
    private var connectionLiveData: ConnectionLiveData? = null

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: WalletViewModel by viewModels {
        viewModelFactory
    }

    private lateinit var token: String
    private lateinit var language: String
    private var digitalWalletsName: String? = null
    private var digitalWalletsId = 0
    private var userBalance = 0f
    private lateinit var activityResources: Resources


    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connectionLiveData = ConnectionLiveData(this)
        binding = ActivityCashWithdrawBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AndroidInjection.inject(this)
        activityContext = this

        language = preferenceHelper.getLanguage()

        updateCashWithdrawView(language)
        checkNetworkConnection()
        initToolbar()
        initData()
        GoogleAdsHelper.initInterstitialGoogleAdView(this@CashWithdrawActivity)
        getUserBalance()
        getDigitalWallets()
        setListeners()
    }

    private fun initData() {
        token = preferenceHelper.getToken()
        binding.withdrawPhoneNumberEditTextId.setText(
            preferenceHelper.getUserPhoneNumber()?.substring(4)
        )
        if (token == "non" || token.trim().isEmpty()) {
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

        pageTitle.text = activityResources.getString(R.string.withdraw_activity_Withdraw)
        backImageView.setOnClickListener { finish() }
    }

    private fun updateCashWithdrawView(language: String) {
        val context = LocaleHelper.setLocale(this@CashWithdrawActivity, language)
        activityResources = context.resources

        binding.withdrawEarningTextViewId.text =
            activityResources?.getString(R.string.wallet_frag_Earning)
        binding.withdrawLabelTextViewId.text =
            activityResources?.getString(R.string.withdraw_activity_Withdraw)
        binding.withdrawAmountLabelTextViewId.text =
            activityResources?.getString(R.string.withdraw_activity_Amount)
        binding.withdrawNameLabelTextViewId.text =
            activityResources?.getString(R.string.registration_activity_full_name)
        binding.withdrawPhoneNumberLabelTextViewId.text =
            activityResources?.getString(R.string.registration_activity_phone_number)
        binding.withdrawWalletTypeLabelTextViewId.text =
            activityResources?.getString(R.string.withdraw_activity_Wallet_Type)
        binding.withdrawCurrencyTextViewId.text =
            preferenceHelper.getCurrencySymbol()
        binding.withdrawWalletContinueButtonId.text =
            activityResources?.getString(R.string.Create_New_Ad_activity_Continue)
        binding.withdrawAmountEditTextId.hint =
            activityResources?.getString(R.string.withdraw_activity_Amount)
        binding.withdrawNameEditTextId.hint =
            activityResources?.getString(R.string.registration_activity_full_name)

        binding.withdrawCountryCodeTextViewId.text = preferenceHelper.getCountryCode()

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

    private fun withrawRequestValidation() {

        if (userBalance < 3) {
            Toast.makeText(
                activityContext,
                activityResources!!.getString(R.string.toast_minimum_amount_withdrawal),
                Toast.LENGTH_SHORT
            ).show();
            return
        }
        val amount: String = binding.withdrawAmountEditTextId.text.toString()
        val name: String = binding.withdrawNameEditTextId.text.toString()
        val phoneNumber: String = binding.withdrawPhoneNumberEditTextId.text.toString()
        val walletType: String = binding.withdrawWalletTypeSpinnerId.selectedItem.toString()
        if (amount.trim { it <= ' ' } != "" && name.trim { it <= ' ' } != "" && phoneNumber.trim { it <= ' ' } != "") {
            if (amount.toInt() < 3) {
                Toast.makeText(
                    activityContext,
                    activityResources.getString(R.string.toast_minimum_amount_withdrawal),
                    Toast.LENGTH_SHORT
                ).show();
                return
            }
            if (phoneNumber.length < 9) {
                binding.withdrawPhoneNumberEditTextId.error =
                    activityResources!!.getString(R.string.toast_Insert_full_phone_number)
                binding.withdrawPhoneNumberEditTextId.requestFocus()
                return
            }
            val hashMap = HashMap<String, String>()
            hashMap["amount"] = amount
            hashMap["name"] = name
            hashMap["phone_no"] = preferenceHelper.getCountryCode() + phoneNumber
            hashMap["wallet_type"] = walletType
            requestWithDraw(hashMap)
        } else {
            if (phoneNumber.trim { it <= ' ' }.isEmpty()) {
                binding.withdrawPhoneNumberEditTextId.error =
                    activityResources!!.getString(R.string.toast_Insert_phone_number)
                binding.withdrawPhoneNumberEditTextId.requestFocus()
            }
            if (name.trim { it <= ' ' }.isEmpty()) {
                binding.withdrawNameEditTextId.error =
                    activityResources!!.getString(R.string.toast_Insert_full_name)
                binding.withdrawNameEditTextId.requestFocus()
            }
            if (amount.trim { it <= ' ' }.isEmpty()) {
                binding.withdrawAmountEditTextId.error =
                    activityResources!!.getString(R.string.toast_Insert_requested_amount)
                binding.withdrawAmountEditTextId.requestFocus()
            }
        }

    }

    fun setListeners() {
        binding.withdrawWalletContinueButtonId.setOnClickListener {
            withrawRequestValidation()
        }
    }

    private fun requestWithDraw(hashMap: HashMap<String, String>) {

        showLoadingDialog(this, "message")
        viewModel.requestWithDraw("Bearer $token", language, hashMap)
        viewModel.baseResponseModel.observe(
            this,
            Observer(function = fun(baseResponse: BaseResponse?) {
                baseResponse?.let {
                    if (baseResponse.statusCode == 201) {
                        hideLoadingDialog()
                        successDialog()
                    } else {
                        openAlertDialog(
                            this,
                            activityResources?.getString(R.string.dialogs_error),
                            baseResponse.message,
                            false,
                            DISMISS,
                            this
                        )
                        hideLoadingDialog()
                    }
                }

            })
        )

        viewModel.throwableResponse.observe(
            this,
            Observer(function = fun(throwable: Throwable?) {
                throwable?.let {
                    openAlertDialog(
                        this,
                        activityResources?.getString(R.string.dialogs_error),
                        throwable.message,
                        false,
                        DISMISS,
                        this
                    )
                    hideLoadingDialog()
                }
            })
        )
    }

    private fun getUserBalance() {
        showLoadingDialog(this, "message")
        viewModel.getUserBalance("Bearer $token", language)
        viewModel.userBalanceModelModel.observe(
            this,
            Observer(function = fun(userBalanceModel: UserBalanceModel?) {
                userBalanceModel?.let {
                    if (userBalanceModel.statusCode == 200) {
                        userBalance = userBalanceModel.userBalanceData.balance
                        binding.withdrawWalletBalanceTextViewId.text =
                            userBalanceModel.userBalanceData.balance.toString() + "" + preferenceHelper.getCurrencySymbol()
                        hideLoadingDialog()
                    } else {
                        openAlertDialog(
                            this,
                            activityResources?.getString(R.string.dialogs_error),
                            userBalanceModel.message,
                            false,
                            DISMISS,
                            this
                        )
                        hideLoadingDialog()
                    }
                }
            })
        )

        viewModel.throwableResponse.observe(
            this,
            Observer(function = fun(throwable: Throwable?) {
                throwable?.let {
                    openAlertDialog(
                        this,
                        activityResources?.getString(R.string.dialogs_error),
                        throwable.message,
                        false,
                        DISMISS,
                        this
                    )
                    hideLoadingDialog()
                }
            })
        )
    }

    private fun getDigitalWallets() {
        //showLoadingDialog(this, "message")
        viewModel.getDigitalWallets(language, "Bearer $token")
        viewModel.digitalWalletsModelModel.observe(
            this,
            Observer(function = fun(digitalWalletsModel: DigitalWalletsModel?) {
                digitalWalletsModel?.let {
                    if (digitalWalletsModel.statusCode == 200) {
                        initDigitalWalletsSpinner(digitalWalletsModel.data.digitalWallets)
                        //hideLoadingDialog()
                    } else {
                        openAlertDialog(
                            this,
                            activityResources.getString(R.string.dialogs_error),
                            digitalWalletsModel.message,
                            false,
                            DISMISS,
                            this
                        )
                        // hideLoadingDialog()
                    }
                }
            })
        )

        viewModel.throwableResponse.observe(
            this,
            Observer(function = fun(throwable: Throwable?) {
                throwable?.let {
                    openAlertDialog(
                        this,
                        activityResources?.getString(R.string.dialogs_error),
                        throwable.message,
                        false,
                        DISMISS,
                        this
                    )
                }
                // hideLoadingDialog()
            })
        )
    }

    private fun initDigitalWalletsSpinner(digitalWallets: ArrayList<DigitalWallet>) {
        if (digitalWallets.size > 0) {
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item, digitalWallets
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.withdrawWalletTypeSpinnerId.adapter = adapter
            binding.withdrawWalletTypeSpinnerId.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    val countriesAndCitiesResponseData =
                        parent.selectedItem as DigitalWallet
                    digitalWalletsName = countriesAndCitiesResponseData.nameEn
                    digitalWalletsId = countriesAndCitiesResponseData.id
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
    }

    private fun successDialog() {
        val alertDialog = AlertDialog.Builder(this@CashWithdrawActivity).create()
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val inflater = alertDialog.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_sucess, null)
        val goToMyAdsTextView = dialogView.findViewById<TextView>(R.id.go_to_my_ads_textView_id)
        val successDescTextView = dialogView.findViewById<TextView>(R.id.success_desc_textView_id)
        val successTitleTextView = dialogView.findViewById<TextView>(R.id.seccess_title_textView_id)
        successTitleTextView.text = activityResources!!.getString(R.string.dialogs_Success)
        goToMyAdsTextView.text = activityResources!!.getString(R.string.dialogs_Back_to_wallet)
        successDescTextView.text = activityResources!!.getString(R.string.dialogs_receive_payment)
        goToMyAdsTextView.setOnClickListener { v: View? ->
            finish()
            alertDialog.dismiss()
        }
        alertDialog.setCancelable(false)
        alertDialog.setView(dialogView)
        alertDialog.show()
    }

    override fun onClose() {
    }

    override fun onConfirm(actionType: Int) {

    }
}