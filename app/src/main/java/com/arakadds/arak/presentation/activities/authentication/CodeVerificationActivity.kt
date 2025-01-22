package com.arakadds.arak.presentation.activities.authentication

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
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
import com.arakadds.arak.databinding.ActivityCodeVerificationBinding
import com.arakadds.arak.model.new_mapping_refactore.request.auth.RegistrationBodyRequest
import com.arakadds.arak.model.new_mapping_refactore.response.banners.auth.RegistrationModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.my_info.UserInformationModel
import com.arakadds.arak.presentation.activities.home.HomeActivity
import com.arakadds.arak.presentation.activities.other.BaseActivity
import com.arakadds.arak.presentation.activities.other.NoInternetConnectionActivity
import com.arakadds.arak.presentation.dialogs.ApplicationDialogs
import com.arakadds.arak.presentation.viewmodel.CreateAccountViewModel
import com.arakadds.arak.presentation.viewmodel.LoginViewModel
import com.arakadds.arak.utils.AppEnums
import com.arakadds.arak.utils.AppProperties
import com.arakadds.arak.utils.Constants.IS_REGISTRATION
import com.arakadds.arak.utils.Constants.IS_RESET_PHONE_NUMBER
import com.arakadds.arak.utils.Constants.USER_REGISTRATION_INFO
import dagger.android.AndroidInjection
import io.paperdb.Paper
import javax.inject.Inject

class CodeVerificationActivity : BaseActivity(), ApplicationDialogs.AlertDialogCallbacks {

    private val preferenceHelper: IPreferenceHelper by lazy { PreferenceManager(applicationContext) }
    lateinit var binding: ActivityCodeVerificationBinding
    private lateinit var activityContext: Context

    private lateinit var toolbar: Toolbar
    var connectionLiveData: ConnectionLiveData? = null

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: CreateAccountViewModel by viewModels {
        viewModelFactory
    }
    private val loginViewModel: LoginViewModel by viewModels {
        viewModelFactory
    }
    private var token: String? = null

    var language: String = "ar"
    private lateinit var activityResources: Resources
    private var isChangePhoneNumber = false
    private var phoneNumber = "0"
    private var email = ""
    private var isRegistration = "0"
    var registrationBodyRequest = RegistrationBodyRequest()
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connectionLiveData = ConnectionLiveData(this)
        binding = ActivityCodeVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AndroidInjection.inject(this)
        activityContext = this

        language = preferenceHelper.getLanguage()

        updateCodeVerificationView(language)

        //initToolbar()

        checkNetworkConnection()
        initData()
        setActionType()
        setListeners()
    }

    private fun setActionType() {
        /*try {
                   socialToken = getIntent().getStringExtra(SOCIAL_TOKEN);
                   if (socialToken==null){
                       socialToken="";
                   }
               } catch (Exception e) {
                   e.printStackTrace();
                   socialToken="";
               }*/

        isChangePhoneNumber=intent.getBooleanExtra(IS_RESET_PHONE_NUMBER,false)

        try {
            isRegistration = intent.getStringExtra(IS_REGISTRATION)!!
            if (isRegistration == null) {
                isRegistration = "0"
            }

            registrationBodyRequest = intent.getParcelableExtra<RegistrationBodyRequest>(USER_REGISTRATION_INFO)!!
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            isRegistration = "0"
        }

        try {
            phoneNumber = intent.getStringExtra("phone_number")!!
            email = intent.getStringExtra("email")!!
            if (phoneNumber == null) {
                phoneNumber = "0"
            }
            if (email == null) {
                email = ""
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            phoneNumber = "0"
            email = ""
        }
    }

    /*private fun initToolbar() {
        toolbar = binding.appBarLayout.findViewById(R.id.toolbar_id)

        setSupportActionBar(toolbar)
        val mTitle: TextView =
            binding.appBarLayout.findViewById<TextView>(R.id.toolbar_title_TextView_id)
        val backImageView: ImageView =
            binding.appBarLayout.findViewById<ImageView>(R.id.toolbar_category_icon_ImageView_id)
        mTitle.text = resources.getText(R.string.login_activity_login_label)
        backImageView.setOnClickListener { finish() }
    }*/
    private fun updateCodeVerificationView(language: String) {
        val context = LocaleHelper.setLocale(this@CodeVerificationActivity, language)
        activityResources = context.resources

        binding.codeVerificationTitleTextViewId.text =
            activityResources?.getString(R.string.Code_Verification_activity_Almost_There)
        binding.codeVerificationDescTextViewId.text =
            activityResources?.getString(R.string.Code_Verification_activity_Please_enter_ottp_number)
        binding.codeVerificationCounterTextViewId.text =
            activityResources?.getString(R.string.Code_Verification_activity_second_left)
        binding.codeVerificationResendCodeTextViewId.text =
            activityResources?.getString(R.string.Code_Verification_activity_Resend_Code)
        binding.codeVerificationNextButtonId.text =
            activityResources?.getString(R.string.forget_password_activity_Next)
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

    private fun initData() {
        token = preferenceHelper.getToken()
    }

    private fun handelOTPCode() {
        if (binding.codeVerificationDescEditTextId.text?.length == 4) {
            if (isChangePhoneNumber) {
                val intent =
                    Intent(this@CodeVerificationActivity, ForgetPasswordActivity::class.java)
                intent.putExtra("otp_code", binding.codeVerificationDescEditTextId.text?.toString())
                startActivity(intent)
            } else if (isRegistration == "1") {
                val otpCode: String = binding.codeVerificationDescEditTextId.text.toString()
                registrationBodyRequest.otpCode = otpCode

                //}
                createNewUser(registrationBodyRequest)
            } else {
                val intent =
                    Intent(this@CodeVerificationActivity, ResetPasswordActivity::class.java)
                intent.putExtra("otp_code", binding.codeVerificationDescEditTextId.text.toString())
                intent.putExtra("phone_number", phoneNumber)
                intent.putExtra("email", email)
                startActivity(intent)
            }
        } else {
            Toast.makeText(
                activityContext, activityResources.getString(
                    R.string.toast_please_enter_OTP_Code
                ), Toast.LENGTH_SHORT
            ).show();

        }
    }

    private fun setListeners() {
        binding.codeVerificationNextButtonId.setOnClickListener {
            handelOTPCode()
        }
    }

    private fun createNewUser(registrationBodyRequest: RegistrationBodyRequest) {
        showLoadingDialog(this, "message")
        MethodHelper.disableView(binding.codeVerificationNextButtonId)
        viewModel.createAccount(language, registrationBodyRequest)
        viewModel.registrationModelModel.observe(
            this,
            Observer(function = fun(registrationModel: RegistrationModel?) {
                registrationModel?.let {
                    if (registrationModel.statusCode == 201) {
                        preferenceHelper.setToken(registrationModel.data.accessToken)
                        preferenceHelper.setUserRole(registrationModel.data.role)
                        preferenceHelper.setUserBalance(registrationModel.data.balance.toFloat())
                        preferenceHelper.setUserPhoneNumber(registrationModel.data.phoneNo)
                        preferenceHelper.setUserFullName(registrationModel.data.fullname)
                        preferenceHelper.setUserCountry(registrationModel.data.countryId.toString())
                        preferenceHelper.setUserCity(registrationModel.data.cityId.toString())
                        preferenceHelper.setKeyUserId(registrationModel.data.id)
                        preferenceHelper.setUserHasStore(registrationModel.data.hasStore)
                        preferenceHelper.setUserGender(registrationModel.data.gender)
                        preferenceHelper.setUserImage(registrationModel.data.imgAvatar)
                        preferenceHelper.setBirthOfDate(registrationModel.data.birthdate)
                        preferenceHelper.setFCMToken(registrationModel.data.fcmToken)
                        preferenceHelper.setHasWallet(registrationModel.data.hasWallet)
                        preferenceHelper.setIsActive(registrationModel.data.isActive)
                        preferenceHelper.setNotificationsEnabled(registrationModel.data.notificationsEnabled)
                        getUserInformation(registrationModel.data.accessToken)

                    } else {
                        ApplicationDialogs.openAlertDialog(
                            this,
                            activityResources?.getString(R.string.dialogs_error),
                            registrationModel.message,
                            false,
                            AppEnums.DialogActionTypes.DISMISS,
                            this
                        )

                        // hideView(binding.loginProgressBarId*//*progressBar*//*)

                    }
                }
                MethodHelper.enableView(binding.codeVerificationNextButtonId)
                hideLoadingDialog()
            })
        )

        viewModel.throwableResponse.observe(
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

    private fun getUserInformation(token: String) {
        showLoadingDialog(this, "message")
        loginViewModel.getUserInformation("Bearer $token", language)
        loginViewModel.userInformationModelModel.observe(
            this,
            Observer(function = fun(userInformationModel: UserInformationModel?) {
                userInformationModel?.let {
                    if (userInformationModel.statusCode == 200) {

                        setPreferencesValues(userInformationModel)

                        /*preferenceHelper.setToken(loginResponseBody.loginResponseResult.accessToken)
                        preferenceHelper.setUserName(loginRequest.userNameOrEmailAddress)
                        preferenceHelper.setPassword(loginRequest.password)
                        preferenceHelper.setRefreshToken(loginResponseBody.loginResponseResult.refreshToken)*/
                        ActivityHelper.goToActivity(this, ChooseInterestsActivity::class.java, false)
                        finish()
                    } else {
                        ApplicationDialogs.openAlertDialog(
                            this,
                            activityResources?.getString(R.string.dialogs_error),
                            userInformationModel.message,
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

        viewModel.throwableResponse.observe(
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
    private fun setPreferencesValues(userInformationModel: UserInformationModel) {
        preferenceHelper.setUserHasStore(userInformationModel.data.hasStore)
        preferenceHelper.setHasWallet(userInformationModel.data.hasWallet)
        preferenceHelper.setCountryNameAr(userInformationModel.data.country.nameAr)
        preferenceHelper.setCountryNameEn(userInformationModel.data.country.nameEn)
        preferenceHelper.setCountryCode(userInformationModel.data.country.countryCode)
        preferenceHelper.setCurrencySymbol(userInformationModel.data.country.currency.symbol)
        preferenceHelper.setCityNameAr(userInformationModel.data.city.nameAr)
        preferenceHelper.setCityNameEn(userInformationModel.data.city.nameEn)
    }
    override fun onClose() {
    }

    override fun onConfirm(actionType: Int) {

    }
}