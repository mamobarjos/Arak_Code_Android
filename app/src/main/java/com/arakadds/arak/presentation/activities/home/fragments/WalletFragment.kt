package com.arakadds.arak.presentation.activities.home.fragments

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.ProgressDialog
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.arakadds.arak.R
import com.arakadds.arak.common.helper.ActivityHelper
import com.arakadds.arak.common.helper.LocaleHelper
import com.arakadds.arak.common.helper.connectivity_checker.ConnectionLiveData
import com.arakadds.arak.common.preferaence.IPreferenceHelper
import com.arakadds.arak.common.preferaence.PreferenceManager
import com.arakadds.arak.data.CartManager
import com.arakadds.arak.data.CartUpdateEvent
import com.arakadds.arak.databinding.FragmentWalletBinding
import com.arakadds.arak.model.new_mapping_refactore.BaseResponse
import com.arakadds.arak.model.new_mapping_refactore.response.banners.auth.LoginResponseData
import com.arakadds.arak.model.new_mapping_refactore.response.banners.auth.user_balance.UserBalanceModel
import com.arakadds.arak.model.new_mapping_refactore.request.auth.LoginRequest
import com.arakadds.arak.model.new_mapping_refactore.response.banners.transactions.TransactionsObject
import com.arakadds.arak.model.new_mapping_refactore.response.banners.transactions.UserTransactionsModel
import com.arakadds.arak.presentation.activities.ArakStore.CartActivity
import com.arakadds.arak.presentation.activities.ads.SelectAdsTypeActivity
import com.arakadds.arak.presentation.activities.authentication.LoginActivity
import com.arakadds.arak.presentation.activities.home.HomeActivity
import com.arakadds.arak.presentation.activities.services.ArakServicesActivity
import com.arakadds.arak.presentation.activities.settings.NotificationActivity
import com.arakadds.arak.presentation.activities.settings.SettingsActivity
import com.arakadds.arak.presentation.activities.payments.wallet.CashWithdrawActivity
import com.arakadds.arak.presentation.adapters.UserTransactionsAdapter
import com.arakadds.arak.presentation.dialogs.ApplicationDialogs
import com.arakadds.arak.presentation.dialogs.loader.ProgressDialogUtil.hideLoadingDialog
import com.arakadds.arak.presentation.dialogs.loader.ProgressDialogUtil.showLoadingDialog
import com.arakadds.arak.presentation.viewmodel.LoginViewModel
import com.arakadds.arak.presentation.viewmodel.UserTransactionsViewModel
import com.arakadds.arak.presentation.viewmodel.ValidationViewModel
import com.arakadds.arak.presentation.viewmodel.WalletViewModel
import com.arakadds.arak.utils.AppEnums
import com.arakadds.arak.utils.AppProperties
import com.arakadds.arak.utils.GlobalMethodsOldClass
import dagger.android.support.DaggerFragment
import io.paperdb.Paper
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.Calendar
import javax.inject.Inject

class WalletFragment : DaggerFragment(R.layout.fragment_wallet),
    ApplicationDialogs.AlertDialogCallbacks, UserTransactionsAdapter.UserTransactionsClickEvents {

    private lateinit var binding: FragmentWalletBinding
    private val preferenceHelper: IPreferenceHelper by lazy { PreferenceManager(requireActivity().applicationContext) }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val validationViewModel: ValidationViewModel by viewModels {
        viewModelFactory
    }
    private val walletViewModel: WalletViewModel by viewModels {
        viewModelFactory
    }
    private val userTransactionsViewModel: UserTransactionsViewModel by viewModels {
        viewModelFactory
    }

    private val loginViewModel: LoginViewModel by viewModels {
        viewModelFactory
    }
    var connectionLiveData: ConnectionLiveData? = null

    private lateinit var token: String
    private lateinit var activityResources: Resources
    private var language: String = "ar"
    private var userBalance = 0f
    lateinit var chargeBalanceAlertDialog: AlertDialog
    private lateinit var dialog: ProgressDialog
    private lateinit var dateFromSetListener: OnDateSetListener
    private lateinit var dateToSetListener: OnDateSetListener
    private lateinit var userTransactionsAdapter: UserTransactionsAdapter
    private var userTransactionsDetailsArrayList = ArrayList<TransactionsObject>()
    private var currentPage = 1
    private var lastPage: Int = 1
    private var dateFrom = ""
    private var dateTo = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        connectionLiveData = activity?.let { ConnectionLiveData(it) }
        binding = FragmentWalletBinding.bind(view)
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

        updateWalletFragView(language)
        initData()
        setupUserTransactionsAdapter()
        setListeners()
        getUserBalance()
        updateCartCounter()
        if (token == "non") return
        getUserTransactions(page = currentPage)
    }

    private fun initData() {
        token = preferenceHelper.getToken()

        binding.cartCounterTextViewId.setText(HomeActivity.cartNum)
        binding.notificationCounterTextViewId.setText(HomeActivity.NotificationNum)
    }

    private fun checkNetworkConnection() {
        connectionLiveData?.observe(viewLifecycleOwner) {
            //update ui
//            if (!isNetworkAvailable) {
//                Toast.makeText(activity, "connected", Toast.LENGTH_SHORT).show()
//            } else {
//                Toast.makeText(activity, "disconnected", Toast.LENGTH_SHORT).show()
//            }
        }


    }

    private fun updateWalletFragView(language: String) {
        val context = LocaleHelper.setLocale(activity, language)
        activityResources = context.resources
        binding.filterDateFromTextViewId.text =
            activityResources.getString(R.string.wallet_frag_From)
        binding.filterDateToTextViewId.text = activityResources.getString(R.string.wallet_frag_To)
        binding.walletFragMakeAdButtonId.text =
            activityResources.getString(R.string.wallet_frag_Make_An_Ad)
        binding.walletFragEarningTextViewId.text =
            activityResources.getString(R.string.wallet_frag_Earning)
        binding.walletFragActivityTextViewId.text =
            activityResources.getString(R.string.wallet_frag_ACTIVITIES)
        binding.walletFragWithdrawButtonId.text =
            activityResources.getString(R.string.wallet_frag_Withdraw)
        binding.walletFragUseCodeButtonId.text =
            activityResources.getString(R.string.wallet_frag_Enter_Coupon_Code)
        this.language = language
    }

    private fun setListeners() {

        binding.homeNotificationImageViewId.setOnClickListener {
            ActivityHelper.goToActivity(
                activity,
                NotificationActivity::class.java, false
            )
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

        binding.homeLogoImageViewId.setOnClickListener {
            ActivityHelper.goToActivity(
                activity,
                ArakServicesActivity::class.java, false
            )
        }
        binding.walletFragMakeAdButtonId.setOnClickListener {
            ActivityHelper.goToActivity(
                activity,
                SelectAdsTypeActivity::class.java, false
            )
        }
        binding.walletFragWithdrawButtonId.setOnClickListener {
            ActivityHelper.goToActivity(
                activity,
                CashWithdrawActivity::class.java, false
            )
        }
        binding.walletFragUseCodeButtonId.setOnClickListener { chargeBalanceDialog() }
        binding.walletFilterFromLinearLayoutId.setOnClickListener {
            /*val calendar = Calendar.getInstance()
            val year = calendar[Calendar.YEAR]
            val month = calendar[Calendar.MONTH]
            val day = calendar[Calendar.DAY_OF_MONTH]
            val datePickerDialog = DatePickerDialog(
                requireActivity(), android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                dateFromSetListener, year, month, day
            )
            //datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePickerDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            datePickerDialog.show()*/

            showDateFromPicker()

        }

        binding.walletFilterToLinearLayoutId.setOnClickListener {
            /*val calendar = Calendar.getInstance()
            val year = calendar[Calendar.YEAR]
            val month = calendar[Calendar.MONTH]
            val day = calendar[Calendar.DAY_OF_MONTH]
            val datePickerDialog = DatePickerDialog(
                requireActivity(), android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                dateFromSetListener, year, month, day
            )
            //datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePickerDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            datePickerDialog.show()*/

            showDateToPicker()
        }
        /* dateFromSetListener =
             OnDateSetListener { datePicker, year, month, dayOfMonth ->
                 var month = month
                 month += 1

                 val monthOfYear: String
                 var day: String? = null
                 monthOfYear = if (month < 10) {
                     "0$month"
                 } else {
                     "" + month
                 }
                 day = if (dayOfMonth < 10) {
                     "0$dayOfMonth"
                 } else {
                     "" + dayOfMonth
                 }
                 dateFrom = "$year-$monthOfYear-$dayOfMonth"

                 binding.filterDateFromTextViewId.text = dateFrom
                 userTransactionsDetailsArrayList = java.util.ArrayList()
                 if (token == "non") return@OnDateSetListener
                 currentPage = 1
                 if (dateFrom.trim().isNotEmpty() && dateTo.trim().isNotEmpty())
                     getUserTransactions(page = currentPage, dateFrom = dateFrom, dateTo = dateTo)
             }

         dateToSetListener =
             OnDateSetListener { datePicker, year, month, dayOfMonth ->
                 var month = month
                 month += 1

                 val monthOfYear: String
                 var day: String? = null
                 monthOfYear = if (month < 10) {
                     "0$month"
                 } else {
                     "" + month
                 }
                 day = if (dayOfMonth < 10) {
                     "0$dayOfMonth"
                 } else {
                     "" + dayOfMonth
                 }
                 dateTo = "$year-$monthOfYear-$dayOfMonth"

                 binding.filterDateToTextViewId.text = dateTo
                 userTransactionsDetailsArrayList = java.util.ArrayList()
                 if (token == "non") return@OnDateSetListener
                 currentPage = 1
                 if (dateFrom.trim().isNotEmpty() && dateTo.trim().isNotEmpty())
                     getUserTransactions(page = currentPage, dateFrom = dateFrom, dateTo = dateTo)
             }*/

    }


    // Function to show DatePickerDialog for "from" date
    fun showDateFromPicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireActivity(),
            OnDateSetListener { datePicker, year, month, dayOfMonth ->
                val formattedDate = formatDate(year, month, dayOfMonth)
                dateFrom = formattedDate
                binding.filterDateFromTextViewId.text = dateFrom
                currentPage = 1
                if (dateFrom.trim().isNotEmpty() && dateTo.trim().isNotEmpty())
                    getUserTransactions(page = currentPage, dateFrom = dateFrom, dateTo = dateTo)
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    // Function to show DatePickerDialog for "to" date
    fun showDateToPicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireActivity(),
            OnDateSetListener { datePicker, year, month, dayOfMonth ->
                val formattedDate = formatDate(year, month, dayOfMonth)
                dateTo = formattedDate
                binding.filterDateToTextViewId.text = dateTo
                currentPage = 1
                if (dateFrom.trim().isNotEmpty() && dateTo.trim().isNotEmpty())
                    getUserTransactions(page = currentPage, dateFrom = dateFrom, dateTo = dateTo)
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    // Helper function to format the date
    fun formatDate(year: Int, month: Int, dayOfMonth: Int): String {
        val monthOfYear = if (month + 1 < 10) {
            "0${month + 1}"
        } else {
            "${month + 1}"
        }
        val day = if (dayOfMonth < 10) {
            "0$dayOfMonth"
        } else {
            "$dayOfMonth"
        }
        return "$year-$monthOfYear-$day"
    }

    private fun chargeBalanceDialog() {
        chargeBalanceAlertDialog = AlertDialog.Builder(requireActivity()).create()
        chargeBalanceAlertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        val inflater = chargeBalanceAlertDialog.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_promo_code, null)
        val promoCodeEditText =
            dialogView.findViewById<EditText>(R.id.dialog_promo_code_code_editText_id)
        val useButton = dialogView.findViewById<Button>(R.id.dialog_promo_code_use_Button_id)
        val titleTextView = dialogView.findViewById<TextView>(R.id.dialog_promo_code_title_textView)
        val purchaseCouponTextView =
            dialogView.findViewById<TextView>(R.id.dialog_promo_code_purchase_coupon_textView_id)
        titleTextView.text = activityResources.getString(R.string.dialogs_Payment_Available_Coupons)
        purchaseCouponTextView.text =
            activityResources.getString(R.string.dialogs_Payment_purchase_coupon)
        useButton.text = activityResources.getString(R.string.dialogs_Payment_Use)
        purchaseCouponTextView.setOnClickListener { v: View? ->
            ActivityHelper.goToActivity(
                activity,
                ArakServicesActivity::class.java, false
            )
        }
        useButton.setOnClickListener { v: View? ->
            val code = promoCodeEditText.text.toString().trim { it <= ' ' }
            if (code == "") {
                Toast.makeText(
                    activity,
                    activityResources.getString(R.string.toast_Insert_requested_promo_code),
                    Toast.LENGTH_SHORT
                ).show();
                return@setOnClickListener
            }
            val promoCodeHashMap =
                HashMap<String, String>()
            promoCodeHashMap["code"] = code
            consumeCoupon(promoCodeHashMap)
            chargeBalanceAlertDialog.dismiss()
        }
        chargeBalanceAlertDialog.setView(dialogView)
        chargeBalanceAlertDialog.show()
    }

    private fun loginValidation() {
        val email = preferenceHelper.getUserEmail()
        val password = preferenceHelper.getPassword()
        if (email != null && password != null) {
            if (ActivityHelper.isValid(email)) {
                val loginRequest = LoginRequest(email, password)
                login(loginRequest)
            }
        } else {
            //userLogout()
        }
    }

    private fun setPreferencesValues(loginResponseBody: LoginResponseData) {
        preferenceHelper.setToken(loginResponseBody.loginResponseBody.token)
        loginResponseBody.loginResponseBody.userObject.role?.let { preferenceHelper.setUserRole(it) }
        loginResponseBody.loginResponseBody.userObject.balance?.toFloat()
            ?.let { preferenceHelper.setUserBalance(it) }
        preferenceHelper.setUserPhoneNumber(loginResponseBody.loginResponseBody.userObject.phoneNo)
        preferenceHelper.setUserFullName(loginResponseBody.loginResponseBody.userObject.fullname)
        preferenceHelper.setUserCountry(loginResponseBody.loginResponseBody.userObject.countryId.toString())
        preferenceHelper.setUserCity(loginResponseBody.loginResponseBody.userObject.cityId.toString())
        preferenceHelper.setKeyUserId(loginResponseBody.loginResponseBody.userObject.id)
        preferenceHelper.setUserHasStore(loginResponseBody.loginResponseBody.userObject.hasStore)
        preferenceHelper.setUserGender(loginResponseBody.loginResponseBody.userObject.gender)
        preferenceHelper.setUserImage(loginResponseBody.loginResponseBody.userObject.imgAvatar)
        preferenceHelper.setBirthOfDate(loginResponseBody.loginResponseBody.userObject.birthdate)
        preferenceHelper.setFCMToken(loginResponseBody.loginResponseBody.userObject.fcmToken)
        preferenceHelper.setHasWallet(loginResponseBody.loginResponseBody.userObject.hasWallet)
        preferenceHelper.setIsActive(loginResponseBody.loginResponseBody.userObject.isActive)
        preferenceHelper.setNotificationsEnabled(loginResponseBody.loginResponseBody.userObject.notificationsEnabled)
    }

    private fun consumeCoupon(hashMap: HashMap<String, String>) {
        showLoadingDialog(activityResources.getString(R.string.dialogs_Uploading_Sending), activity)
        walletViewModel.consumeCoupon("Bearer $token", language, hashMap)
        activity?.let {
            walletViewModel.baseResponseModel.observe(
                it,
                Observer(function = fun(baseResponse: BaseResponse?) {
                    baseResponse?.let {
                        if (baseResponse.statusCode == 201) {
                            Toast.makeText(
                                activity,
                                activityResources.getString(R.string.toast_charged_successfully),
                                Toast.LENGTH_SHORT
                            ).show();
                            getUserBalance()
                        } else {
                            activity?.let { it1 ->
                                ApplicationDialogs.openAlertDialog(
                                    it1,
                                    activityResources.getString(R.string.dialogs_error),
                                    baseResponse.message,
                                    false,
                                    AppEnums.DialogActionTypes.DISMISS,
                                    this
                                )
                            }
                        }
                    }
                })
            )
            hideLoadingDialog()
        }

        activity?.let {
            walletViewModel.throwableResponse.observe(
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
                })
            )
            hideLoadingDialog()
        }
    }

    private fun getUserTransactions(
        page: Int? = null,
        dateFrom: String? = null,
        dateTo: String? = null,
    ) {
        showLoadingDialog("message", activity)
        userTransactionsViewModel.getUserTransactions(
            "Bearer $token",
            language,
            page,
            dateFrom,
            dateTo
        )
        activity?.let {
            userTransactionsViewModel.userTransactionsModelModel.observe(
                it,
                Observer(function = fun(userTransactionsModel: UserTransactionsModel?) {
                    userTransactionsModel?.let {
                        if (userTransactionsModel.statusCode == 200) {
                            userTransactionsDetailsArrayList.clear()
                            userTransactionsDetailsArrayList.addAll(
                                userTransactionsModel.data.transactions
                            )
                            userTransactionsAdapter.notifyDataSetChanged()

                        } else {
                            activity?.let { it1 ->
                                ApplicationDialogs.openAlertDialog(
                                    it1,
                                    activityResources.getString(R.string.dialogs_error),
                                    userTransactionsModel.message,
                                    false,
                                    AppEnums.DialogActionTypes.DISMISS,
                                    this
                                )
                            }
                        }
                    }
                })
            )
            hideLoadingDialog()
        }

        activity?.let {
            userTransactionsViewModel.throwableResponse.observe(
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
                })
            )
            hideLoadingDialog()
        }
    }

    private fun setupUserTransactionsAdapter() {
        userTransactionsAdapter =
            UserTransactionsAdapter(userTransactionsDetailsArrayList, activity, this)
        val mLayoutManager2 =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.walletRecyclerViewId.layoutManager = mLayoutManager2
        binding.walletRecyclerViewId.isFocusable = false
        binding.walletRecyclerViewId.isNestedScrollingEnabled = false
        binding.walletRecyclerViewId.adapter = userTransactionsAdapter
    }

    private fun getUserBalance() {
        showLoadingDialog("message", activity)
        walletViewModel.getUserBalance("Bearer $token", language)
        activity?.let {
            walletViewModel.userBalanceModelModel.observe(
                it,
                Observer(function = fun(userBalanceModel: UserBalanceModel?) {
                    userBalanceModel?.let {
                        if (userBalanceModel.statusCode == 200) {
                            userBalance = userBalanceModel.userBalanceData.balance
                            preferenceHelper.setUserBalance(userBalance)
                            binding.walletPayWalletBalanceTextViewId.text = userBalanceModel.userBalanceData.balance
                                .toString() + " " + preferenceHelper.getCurrencySymbol()
                            activity?.viewModelStore?.clear()
                        } else {
                            activity?.let { it1 ->
                                ApplicationDialogs.openAlertDialog(
                                    it1,
                                    activityResources.getString(R.string.dialogs_error),
                                    userBalanceModel.message,
                                    false,
                                    AppEnums.DialogActionTypes.DISMISS,
                                    this
                                )
                            }
                        }
                    }
                })
            )
            hideLoadingDialog()
        }

        activity?.let {
            walletViewModel.throwableResponse.observe(
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

                })
            )
            hideLoadingDialog()
        }
    }

    private fun login(loginRequest: LoginRequest) {
        showLoadingDialog("message", activity)
        loginViewModel.userLogin(language, loginRequest)
        activity?.let {
            loginViewModel.loginResponseBodyModel.observe(
                it,
                Observer(function = fun(loginResponseBody: LoginResponseData?) {
                    loginResponseBody?.let {
                        if (loginResponseBody.statusCode == 201) {
                            setPreferencesValues(loginResponseBody)
                            val intent = Intent(activity, HomeActivity::class.java)
                            startActivity(intent)
                        } else {
                            activity?.let { it1 ->
                                ApplicationDialogs.openAlertDialog(
                                    it1,
                                    activityResources.getString(R.string.dialogs_error),
                                    loginResponseBody.message,
                                    false,
                                    AppEnums.DialogActionTypes.DISMISS,
                                    this
                                )
                            }
                            // hideView(binding.loginProgressBarId*//*progressBar*//*)

                        }
                    }
                })
            )
            hideLoadingDialog()
        }

        activity?.let {
            loginViewModel.throwableResponse.observe(
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

                })
            )
            hideLoadingDialog()
        }
    }

    /* private fun userLogout() {
         showLoadingDialog("message", activity)
         validationViewModel.logout(token)
         activity?.let {
             validationViewModel.baseResponseModel.observe(
                 it,
                 Observer(function = fun(baseResponse: BaseResponse?) {
                     baseResponse?.let {
                         if (baseResponse.statusCode == 200) {
                             setPreferences()
                             ActivityHelper.goToActivity(
                                 activity,
                                 LoginActivity::class.java, false
                             )
                             Toast.makeText(activity, "Please Login Again", Toast.LENGTH_SHORT)
                                 .show();
                         } else {
                             activity?.let { it1 ->
                                 ApplicationDialogs.openAlertDialog(
                                     it1,
                                     activityResources.getString(R.string.dialogs_error),
                                     baseResponse.description,
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
            validationViewModel.throwableResponse.observe(
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
    }*/

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

    fun onResponseFailed() {
        loginValidation()
    }

    override fun onClose() {
    }

    override fun onConfirm(actionType: Int) {
    }

    override fun onNextPageRequired() {
        if (currentPage < lastPage) {
            getUserTransactions(page = currentPage + 1)
            if (dateFrom.trim().isNotEmpty()) {
                getUserTransactions(
                    page = currentPage + 1,
                    dateFrom = dateFrom/* dateTo = dateTo,*/
                )
            } else {
                getUserTransactions(page = currentPage + 1)
            }
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