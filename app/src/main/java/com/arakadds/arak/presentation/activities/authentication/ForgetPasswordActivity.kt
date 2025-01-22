package com.arakadds.arak.presentation.activities.authentication

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.arakadds.arak.R
import com.arakadds.arak.common.helper.ActivityHelper
import com.arakadds.arak.common.helper.LocaleHelper
import com.arakadds.arak.common.helper.MethodHelper
import com.arakadds.arak.common.helper.connectivity_checker.ConnectionLiveData
import com.arakadds.arak.common.preferaence.IPreferenceHelper
import com.arakadds.arak.common.preferaence.PreferenceManager
import com.arakadds.arak.databinding.ActivityForgetPasswordBinding
import com.arakadds.arak.model.new_mapping_refactore.BaseResponse
import com.arakadds.arak.model.new_mapping_refactore.request.auth.OTPRequest
import com.arakadds.arak.model.new_mapping_refactore.request.auth.LoginRequest
import com.arakadds.arak.model.new_mapping_refactore.response.banners.countries.CountriesModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.countries.CountriesResponseData
import com.arakadds.arak.presentation.activities.other.BaseActivity
import com.arakadds.arak.presentation.activities.other.NoInternetConnectionActivity
import com.arakadds.arak.presentation.activities.profile.MyDetailsActivity
import com.arakadds.arak.presentation.dialogs.ApplicationDialogs
import com.arakadds.arak.presentation.dialogs.ApplicationDialogs.openAlertDialog
import com.arakadds.arak.presentation.viewmodel.CreateAccountViewModel
import com.arakadds.arak.presentation.viewmodel.UserProfileViewModel
import com.arakadds.arak.presentation.viewmodel.ValidationViewModel
import com.arakadds.arak.utils.AppEnums.DialogActionTypes.DISMISS
import com.arakadds.arak.utils.Constants.IS_FORGET_PASSWORD
import com.arakadds.arak.utils.Constants.IS_RESET_PHONE_NUMBER
import com.arakadds.arak.utils.Constants.OTP_CODE
import dagger.android.AndroidInjection
import javax.inject.Inject

class ForgetPasswordActivity : BaseActivity(), ApplicationDialogs.AlertDialogCallbacks {

    private val preferenceHelper: IPreferenceHelper by lazy { PreferenceManager(applicationContext) }
    lateinit var binding: ActivityForgetPasswordBinding
    private lateinit var activityContext: Context

    private lateinit var toolbar: Toolbar
    var connectionLiveData: ConnectionLiveData? = null

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: ValidationViewModel by viewModels {
        viewModelFactory
    }

    private val userProfileViewModel: UserProfileViewModel by viewModels {
        viewModelFactory
    }
    private val createAccountViewModel: CreateAccountViewModel by viewModels {
        viewModelFactory
    }
    private lateinit var token: String
    private lateinit var language: String
    private var isChangePhoneNumber = false
    private var otpCode = "0"
    private var isForgetPassword = false
    private var userId = 0
    private lateinit var activityResources: Resources
    private lateinit var dialog: ProgressDialog
    private lateinit var selectedCountryCode: String
    private var isEmailRequired = false

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connectionLiveData = ConnectionLiveData(this)
        binding = ActivityForgetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AndroidInjection.inject(this)
        activityContext = this

        language = preferenceHelper.getLanguage()

        //initToolbar()
        checkNetworkConnection()
        updateMyDetailsView(language)
        getCountries()
        getIntents()
        initUi()
        setListeners()

        // Add this line after setting up the country code picker
        //updateEmailVisibility(binding.countryCode.selectedCountryCode)
        //CodeVerificationActivity
    }

    private fun getIntents() {
        token = preferenceHelper.getToken()
        userId = preferenceHelper.getKeyUserId()
        isForgetPassword = intent.getBooleanExtra(IS_FORGET_PASSWORD, false)

        isChangePhoneNumber = intent.getBooleanExtra(IS_RESET_PHONE_NUMBER, false)

        try {
            otpCode = intent.getStringExtra(OTP_CODE).toString()

            if (otpCode == null) {
                otpCode = "0"
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            otpCode = "0"
        }
    }

    private fun updateMyDetailsView(language: String) {
        val context = LocaleHelper.setLocale(this@ForgetPasswordActivity, language)
        activityResources = context.resources
        if (isChangePhoneNumber) {
            binding.forgetPasswordTitleTextViewId.text =
                activityResources.getString(R.string.change_Phone_Number_activity_Reset_Phone_Number)
            binding.enterEmailTitleTextViewId.text =
                activityResources.getString(R.string.change_Phone_Number_activity_Please_enter_Current_Phone_Number)
            binding.forgetPasswordPhoneNumberEditTextId.hint =
                activityResources.getString(R.string.change_Phone_Number_activity_Current_Phone_Number)

            val phoneNumber = preferenceHelper.getUserPhoneNumber()

            binding.forgetPasswordPhoneNumberEditTextId.setText(phoneNumber?.substring(4))
            binding.forgetPasswordPhoneNumberEditTextId.isEnabled = false
        } else if (otpCode != "0") {
            binding.forgetPasswordTitleTextViewId.text =
                activityResources.getString(R.string.change_Phone_Number_activity_Reset_Phone_Number)
            binding.enterEmailTitleTextViewId.text =
                activityResources.getString(R.string.change_Phone_Number_activity_Please_enter_New_Phone_Number)
            binding.forgetPasswordPhoneNumberEditTextId.hint =
                activityResources.getString(R.string.change_Phone_Number_activity_New_Phone_Number)
        } else {
            binding.forgetPasswordTitleTextViewId.text =
                activityResources.getString(R.string.forget_password_activity_Forget_Your_Password)
            binding.enterEmailTitleTextViewId.text =
                activityResources.getString(R.string.forget_password_activity_Reset_enter_email)
            binding.forgetPasswordPhoneNumberEditTextId.hint =
                activityResources.getString(R.string.registration_activity_phone_number)
        }
        binding.backTitleTextViewId.text =
            activityResources.getString(R.string.forget_password_activity_Back)
        binding.forgetPasswordNextButtonId.text =
            activityResources.getString(R.string.forget_password_activity_Next)
        this.language = language
    }

    private fun initUi() {
        dialog = ProgressDialog(this)
        dialog.setMessage(activityResources.getString(R.string.dialogs_Uploading_Sending))
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
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
                        openAlertDialog(
                            this,
                            activityResources?.getString(R.string.dialogs_error),
                            countriesModel.message,
                            false,
                            DISMISS,
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
                    openAlertDialog(
                        this,
                        activityResources?.getString(R.string.dialogs_error),
                        throwable.message,
                        false,
                        DISMISS,
                        this
                    )
                }
                hideLoadingDialog()
            })
        )
    }

    private fun initCountrySpinner(countriesResponseDataArrayList: ArrayList<CountriesResponseData>) {
        if (countriesResponseDataArrayList.size > 0) {
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item, countriesResponseDataArrayList
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.countryCodeId.adapter = adapter
            binding.countryCodeId.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    val countriesResponseData = parent.selectedItem as CountriesResponseData
                    selectedCountryCode = countriesResponseData.countryCode
                    isEmailRequired = countriesResponseData.isEmailRequired
                    updateEmailVisibility(selectedCountryCode)

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        // Use regex to validate email
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // Add this function to check and update email visibility
    private fun updateEmailVisibility(countryCode: String) {
        if (isEmailRequired) {
            binding.forgetPasswordEmailEditTextId.visibility = View.VISIBLE
        } else {
            binding.forgetPasswordEmailEditTextId.visibility = View.GONE
            binding.forgetPasswordEmailEditTextId.text?.clear() // Clear email when hiding
        }
    }

    private fun formatPhoneNumber(countryCode: String, phoneNumber: String): String {
        if (phoneNumber.isEmpty()) {
            return ""
        }

        // Remove leading and trailing whitespaces and any internal spaces
        var validPhone = phoneNumber.trim().replace("\\s+".toRegex(), "")

        // Ensure countryCode starts with a "+"
        val formattedCountryCode = if (countryCode.startsWith("+")) {
            countryCode
        } else {
            "+$countryCode"
        }

        // Remove any existing country code from the phone number
        if (validPhone.startsWith(formattedCountryCode)) {
            validPhone = validPhone.substring(formattedCountryCode.length)
        }

        // Handle leading zero if present
        if (validPhone.startsWith("0")) {
            validPhone = validPhone.substring(1)
        }

        // Prepend the correct country code
        return "$formattedCountryCode$validPhone"
    }

    private fun setListeners() {
        binding.forgetPasswordLinearLayoutId.setOnClickListener(View.OnClickListener {
            ActivityHelper.goToActivity(
                this,
                LoginActivity::class.java, false
            )
        })

        binding.forgetPasswordNextButtonId.setOnClickListener {

            if (isEmailRequired) {
                if (binding.forgetPasswordEmailEditTextId.text.isEmpty()) {
                    binding.forgetPasswordEmailEditTextId.error =
                        activityResources.getString(R.string.toast_Insert_email)
                    binding.forgetPasswordEmailEditTextId.requestFocus()
                    return@setOnClickListener
                }

                if (!isValidEmail(binding.forgetPasswordEmailEditTextId.text.toString())) {
                    binding.forgetPasswordEmailEditTextId.error =
                        activityResources.getString(R.string.toast_please_Insert_valid_email_address)
                    binding.forgetPasswordEmailEditTextId.requestFocus()
                    return@setOnClickListener
                }
            }

            val email: String = binding.forgetPasswordEmailEditTextId.text.toString()
            val phoneNumber: String = formatPhoneNumber(
                selectedCountryCode,
                binding.forgetPasswordPhoneNumberEditTextId.text.toString().trim()
            )
            if (phoneNumber.isNotEmpty()) {
                if (isForgetPassword) {
                    sendOTP(
                        OTPRequest(
                            phoneNumber,
                            email
                        )
                    )
                } else {
                    val newPhoneNumber: String = formatPhoneNumber(selectedCountryCode, binding.forgetPasswordPhoneNumberEditTextId.text.toString().trim())
                    resetPhoneNumber(newPhoneNumber, email)
                }
            }
        }
    }

    private fun sendOTP(otpRequest: OTPRequest) {
        showLoadingDialog(this, "message")
        MethodHelper.disableView(binding.forgetPasswordNextButtonId)
        viewModel.sendOTP(language, otpRequest)
        viewModel.baseResponseModel.observe(
            this,
            Observer(function = fun(baseResponse: BaseResponse?) {
                baseResponse?.let {
                    if (baseResponse.statusCode == 201) {
                        val intent = Intent(
                            this,
                            CodeVerificationActivity::class.java
                        )
                        intent.putExtra("phone_number", otpRequest.phoneNumber)
                        intent.putExtra("email", otpRequest.email)
                        if (isChangePhoneNumber) {
                            intent.putExtra(IS_RESET_PHONE_NUMBER, true)
                        }
                        startActivity(intent)
                        finish()
                    } else {
                        openAlertDialog(
                            this,
                            activityResources?.getString(R.string.dialogs_error),
                            baseResponse.message,
                            false,
                            DISMISS,
                            this
                        )
                        // hideView(binding.loginProgressBarId*//*progressBar*//*)
                        MethodHelper.enableView(binding.forgetPasswordNextButtonId)
                    }
                }
                hideLoadingDialog()
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
                hideLoadingDialog()
                MethodHelper.enableView(binding.forgetPasswordNextButtonId)
            })
        )
    }

    private fun resetPhoneNumber(newPhoneNumber: String, email: String?) {

        val phoneNumber = preferenceHelper.getUserPhoneNumber()

        val hashMap = HashMap<String, String?>()
        //hashMap["phone_no"] = phoneNumber
        hashMap["phone_no"] = newPhoneNumber
        hashMap["email"] = email.toString()
        hashMap["otp_code"] = otpCode
        //hashMap["new_phone_no"] = "+962$newPhoneNumber"

        showLoadingDialog(this, "message")
        MethodHelper.disableView(binding.forgetPasswordNextButtonId)
        userProfileViewModel.resetPhoneNumber(token, language, userId, hashMap)
        userProfileViewModel.baseResponseModel.observe(
            this,
            Observer(function = fun(baseResponse: BaseResponse?) {
                baseResponse?.let {
                    if (baseResponse.statusCode == 201) {
                        Toast.makeText(
                            activityContext,
                            activityResources.getString(R.string.toast_Phone_Number_updated_successfully),
                            Toast.LENGTH_SHORT
                        ).show();

                       preferenceHelper.setUserPhoneNumber(formatPhoneNumber(selectedCountryCode, binding.forgetPasswordPhoneNumberEditTextId.text.toString().trim()))

                        ActivityHelper.goToActivity(
                            this,
                            MyDetailsActivity::class.java, false
                        )
                        finish()
                    } else {
                        openAlertDialog(
                            this,
                            activityResources.getString(R.string.dialogs_error),
                            baseResponse.message,
                            false,
                            DISMISS,
                            this
                        )
                        // hideView(binding.loginProgressBarId*//*progressBar*//*)
                        MethodHelper.enableView(binding.forgetPasswordNextButtonId)
                    }
                }
                hideLoadingDialog()
            })
        )

        userProfileViewModel.throwableResponse.observe(
            this,
            Observer(function = fun(throwable: Throwable?) {
                throwable?.let {
                    openAlertDialog(
                        this,
                        activityResources.getString(R.string.dialogs_error),
                        throwable.message,
                        false,
                        DISMISS,
                        this
                    )
                }
                hideLoadingDialog()
                MethodHelper.enableView(binding.forgetPasswordNextButtonId)
            })
        )
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

    private fun login(loginRequest: LoginRequest) {

    }


    override fun onClose() {

    }

    override fun onConfirm(actionType: Int) {

    }
}