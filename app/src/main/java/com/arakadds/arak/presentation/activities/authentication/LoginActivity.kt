package com.arakadds.arak.presentation.activities.authentication

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.arakadds.arak.R
import com.arakadds.arak.common.helper.ActivityHelper
import com.arakadds.arak.common.helper.LocaleHelper
import com.arakadds.arak.common.helper.MethodHelper
import com.arakadds.arak.common.helper.connectivity_checker.ConnectionLiveData
import com.arakadds.arak.common.preferaence.IPreferenceHelper
import com.arakadds.arak.common.preferaence.PreferenceManager
import com.arakadds.arak.databinding.ActivityLoginBinding
import com.arakadds.arak.model.new_mapping_refactore.request.auth.LoginRequest
import com.arakadds.arak.model.new_mapping_refactore.response.banners.auth.LoginResponseData
import com.arakadds.arak.model.new_mapping_refactore.response.banners.auth.RegistrationModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.countries.CountriesModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.countries.CountriesResponseData
import com.arakadds.arak.model.new_mapping_refactore.response.banners.my_info.UserInformationModel
import com.arakadds.arak.presentation.activities.home.HomeActivity
import com.arakadds.arak.presentation.activities.other.BaseActivity
import com.arakadds.arak.presentation.activities.other.NoInternetConnectionActivity
import com.arakadds.arak.presentation.dialogs.ApplicationDialogs
import com.arakadds.arak.presentation.dialogs.ApplicationDialogs.openAlertDialog
import com.arakadds.arak.presentation.viewmodel.CreateAccountViewModel
import com.arakadds.arak.presentation.viewmodel.LoginViewModel
import com.arakadds.arak.utils.AppEnums.DialogActionTypes.DISMISS
import com.arakadds.arak.utils.Constants
import com.arakadds.arak.utils.Constants.RC_SIGN_IN
import com.arakadds.arak.utils.Utilities
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.GraphResponse
import com.facebook.Profile
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import dagger.android.AndroidInjection
import org.json.JSONException
import org.json.JSONObject
import java.util.concurrent.Executor
import javax.inject.Inject


class LoginActivity : BaseActivity(), ApplicationDialogs.AlertDialogCallbacks {

    private val preferenceHelper: IPreferenceHelper by lazy { PreferenceManager(applicationContext) }
    lateinit var binding: ActivityLoginBinding
    private lateinit var activityContext: Context

    private lateinit var toolbar: Toolbar
    var connectionLiveData: ConnectionLiveData? = null

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: LoginViewModel by viewModels {
        viewModelFactory
    }
    private val createAccountViewModel: CreateAccountViewModel by viewModels {
        viewModelFactory
    }
    private var token: String? = null
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    var language: String = "ar"
    private lateinit var activityResources: Resources
    var mGoogleSignInClient: GoogleSignInClient? = null
    private var email: String? = null
    private var name: String? = null
    private lateinit var selectedCountryCode: String
    var callbackManager: CallbackManager? = null

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connectionLiveData = ConnectionLiveData(this)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AndroidInjection.inject(this)
        activityContext = this

        callbackManager = CallbackManager.Factory.create()
        language = preferenceHelper.getLanguage()

        updateLoginView(language)

        //initToolbar()
        checkNetworkConnection()
        getCountries()
        initData()

        if (token!!.isNotEmpty() && preferenceHelper.isBiometricEnabled()) {
            checkBiometric()
        } else {
            binding.authenticatefingerprintImageView.visibility = View.GONE
        }
        setListeners()
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

    private fun updateLoginView(language: String) {
        val context = LocaleHelper.setLocale(this@LoginActivity, language)
        activityResources = context.resources

        binding.welcomeTextViewId.text =
            activityResources?.getString(R.string.login_activity_welcome)
        binding.welcomeDescTextViewId.text =
            activityResources?.getString(R.string.login_activity_thousands_registered_members)
        binding.orTextViewId.text = activityResources?.getString(R.string.registration_activity_or)
        binding.loginWithFacebook.text =
            activityResources?.getString(R.string.login_activity_Login_Facebook)
        binding.loginWithGmail.text =
            activityResources?.getString(R.string.login_activity_Login_Google)
        binding.forgetPasswordTextViewId.text =
            activityResources?.getString(R.string.login_activity_Forget_password)
        binding.phoneNumberEditTextId.hint = "xxxxxxxxx"
        binding.passwordEditTextId.hint =
            activityResources?.getString(R.string.login_activity_password)
        binding.loginButtonId.text = activityResources?.getString(R.string.login_activity_Login)
        binding.loginGest.text = activityResources?.getString(R.string.login_activity_Login_guest)
        if (language == "en") {
            val text = "Don't have an account? Sign up"
            val spannableString = SpannableString(text)
            val fcsBlue =
                activityResources.getColor(R.color.dark_blue)?.let { ForegroundColorSpan(it) }
            val fcsOrange =
                activityResources.getColor(R.color.orange)?.let { ForegroundColorSpan(it) }
            spannableString.setSpan(fcsBlue, 1, 22, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableString.setSpan(fcsOrange, 22, 30, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            binding.createNewAccountTextViewId.text = spannableString
        } else if (language == "ar") {
            val text = "لا تمتلك حساب؟ حساب جديد"
            val spannableString = SpannableString(text)
            val fcsBlue =
                activityResources.getColor(R.color.dark_blue)?.let { ForegroundColorSpan(it) }
            val fcsOrange =
                activityResources.getColor(R.color.orange)?.let { ForegroundColorSpan(it) }
            spannableString.setSpan(fcsBlue, 1, 14, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableString.setSpan(fcsOrange, 14, 24, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            binding.createNewAccountTextViewId.text = spannableString
        }
        // registerNewAccountTextView.setText(resources.getString(R.string.login_activity_Sign_up));
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

    private fun login(loginRequest: LoginRequest) {
        showLoadingDialog(this, "message")
        MethodHelper.disableView(binding.loginButtonId)
        viewModel.userLogin(language, loginRequest)
        viewModel.loginResponseBodyModel.observe(
            this,
            Observer(function = fun(loginResponseBody: LoginResponseData?) {
                loginResponseBody?.let {
                    if (loginResponseBody.statusCode == 201) {

                        setPreferencesValues(loginResponseBody)

                        preferenceHelper.setUserPhoneNumber(loginRequest.phoneNumber)
                        preferenceHelper.setPassword(loginRequest.password)
                        getUserInformation(loginResponseBody.loginResponseBody.token)
                        /*preferenceHelper.setToken(loginResponseBody.loginResponseResult.accessToken)
                        preferenceHelper.setUserName(loginRequest.userNameOrEmailAddress)
                        preferenceHelper.setPassword(loginRequest.password)
                        preferenceHelper.setRefreshToken(loginResponseBody.loginResponseResult.refreshToken)*/

                    } else {
                        openAlertDialog(
                            this,
                            activityResources?.getString(R.string.dialogs_error),
                            loginResponseBody.message,
                            false,
                            DISMISS,
                            this
                        )
                        // hideView(binding.loginProgressBarId*//*progressBar*//*)
                        MethodHelper.enableView(binding.loginButtonId)
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
                MethodHelper.enableView(binding.loginButtonId)
            })
        )
    }

    private fun getUserInformation(token: String) {
        showLoadingDialog(this, "message")
        MethodHelper.disableView(binding.loginButtonId)
        viewModel.getUserInformation("Bearer $token", language)
        viewModel.userInformationModelModel.observe(
            this,
            Observer(function = fun(userInformationModel: UserInformationModel?) {
                userInformationModel?.let {
                    if (userInformationModel.statusCode == 200) {

                        setPreferencesValues(userInformationModel)

                        /*preferenceHelper.setToken(loginResponseBody.loginResponseResult.accessToken)
                        preferenceHelper.setUserName(loginRequest.userNameOrEmailAddress)
                        preferenceHelper.setPassword(loginRequest.password)
                        preferenceHelper.setRefreshToken(loginResponseBody.loginResponseResult.refreshToken)*/
                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    } else {
                        openAlertDialog(
                            this,
                            activityResources?.getString(R.string.dialogs_error),
                            userInformationModel.message,
                            false,
                            DISMISS,
                            this
                        )
                        // hideView(binding.loginProgressBarId*//*progressBar*//*)
                        MethodHelper.enableView(binding.loginButtonId)
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
                MethodHelper.enableView(binding.loginButtonId)
            })
        )
    }

    private fun setPreferencesValues(registrationModel: RegistrationModel) {
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
    }

    private fun setPreferencesValues(loginResponseBody: LoginResponseData) {
        preferenceHelper.setToken(loginResponseBody.loginResponseBody.token)
        loginResponseBody.loginResponseBody.userObject.role?.let { preferenceHelper.setUserRole(it) }
        preferenceHelper.setUserBalance(loginResponseBody.loginResponseBody.userObject.balance!!.toFloat())
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


    /*
    *
    *  private fun socialRegisterLogin(
         account: GoogleSignInAccount?,
         socialLoginRequest: SocialLoginRequest
     ) {
         showLoadingDialog(this, "message")
         MethodHelper.disableView(binding.loginButtonId)

         viewModel.socialRegisterLogin(socialLoginRequest)
         viewModel.loginModel.observe(
             this,
             Observer(function = fun(registrationResponseData: RegistrationResponseData?) {
                 registrationResponseData?.let {

                     if (registrationResponseData == null) {
                         val intent = Intent(this, RegistrationActivity::class.java)
                         if (account != null) {
                             intent.putExtra("email", account.email)
                             intent.putExtra("name", account.displayName)
                         } else {
                             //ActivityHelper.goToActivity(LoginActivity.this, UserRegistrationActivity.class, false, "social_token", socialId);
                             intent.putExtra("email", email)
                             intent.putExtra("name", name)
                         }
                         intent.putExtra("social_token", socialToken)
                         startActivity(intent)
                     } else {

                         if (registrationResponseData.statusCode == 200) {
                             GlobalMethods.setPreferencesValues(registrationResponseData)
                             ActivityHelper.goToActivity(
                                 this,
                                 HomeActivity::class.java,
                                 false
                             )
                         } else {
                             openAlertDialog(
                                 this,
                                 resources?.getString(R.string.dialogs_error),
                                 registrationResponseData.description,
                                 false,
                                 DISMISS,
                                 this
                             )
                             // hideView(binding.loginProgressBarId*//*progressBar*//*)
                            MethodHelper.enableView(binding.loginButtonId)
                        }


                    }
                    hideLoadingDialog()
                }


                viewModel.throwableResponse.observe(
                    this,
                    Observer(function = fun(throwable: Throwable?) {
                        throwable?.let {
                            openAlertDialog(
                                this,
                                resources?.getString(R.string.dialogs_error),
                                throwable.message,
                                false,
                                DISMISS,
                                this
                            )
                        }
                        hideLoadingDialog()
                        MethodHelper.enableView(binding.loginButtonId)
                    })
                )
            }
    }
   * */

    /*   private fun socialRegisterLogin(
           account: GoogleSignInAccount?,
           socialLoginRequest: SocialLoginRequest
       ) {
           showLoadingDialog(this, "message")
           MethodHelper.disableView(binding.loginButtonId)

           viewModel.socialRegisterLogin(socialLoginRequest)
           viewModel.socialLoginModel.observe(
               this,
               Observer(function = fun(registrationResponseData: RegistrationResponseData?) {
                   registrationResponseData?.let {


                       if (registrationResponseData == null) {
                           val intent = Intent(this, RegistrationActivity::class.java)
                           if (account != null) {
                               intent.putExtra("email", account.email)
                               intent.putExtra("name", account.displayName)
                           } else {
                               //ActivityHelper.goToActivity(LoginActivity.this, UserRegistrationActivity.class, false, "social_token", socialId);
                               intent.putExtra("email", email)
                               intent.putExtra("name", name)
                           }
                           intent.putExtra("social_token", socialLoginRequest.gmailToken)
                           startActivity(intent)
                       } else {
                           if (registrationResponseData.statusCode == 200) {
                               setPreferencesValues(registrationResponseData)
                               ActivityHelper.goToActivity(
                                   this,
                                   HomeActivity::class.java,
                                   false
                               )
                           } else {
                               openAlertDialog(
                                   this,
                                   activityResources?.getString(R.string.dialogs_error),
                                   registrationResponseData.message,
                                   false,
                                   DISMISS,
                                   this
                               )
                               // hideView(binding.loginProgressBarId*//*progressBar*//*)
                        }
                    }
                }
                MethodHelper.enableView(binding.loginButtonId)
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
            })
        )
    }*/

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



                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
    }
    private fun setListeners() {

        binding.loginButtonId.setOnClickListener {
            binding.loginButtonId.isEnabled = false

            val phoneNumber: String =
                formatPhoneNumber(selectedCountryCode, binding.phoneNumberEditTextId.text.toString().trim())
            val password = binding.passwordEditTextId.text.toString()

            if (phoneNumber.trim().isEmpty()) {
                binding.phoneNumberEditTextId.requestFocus()
                binding.phoneNumberEditTextId.error = getString(R.string.toast_Insert_email)
                binding.loginButtonId.isEnabled = true
                return@setOnClickListener
            }

            if (password.trim().isEmpty()) {
                binding.passwordEditTextId.requestFocus()
                binding.passwordEditTextId.error = getString(R.string.toast_Insert_password)

                binding.loginButtonId.isEnabled = true
                return@setOnClickListener
            }

            if (ActivityHelper.isValid(phoneNumber)) {
                binding.phoneNumberEditTextId.requestFocus()
                binding.phoneNumberEditTextId.error =
                    getString(R.string.toast_please_Insert_valid_email_address)
                binding.loginButtonId.isEnabled = true
            }
            /*loginButton.isEnabled = false
            loginButton.isClickable = false*/
            //showHide(binding.loginProgressBarId*//*progressBar*//*)
            val loginRequest = LoginRequest(phoneNumber, password)
            login(loginRequest)
            //startActivity(Intent(this,HomeActivity::class.java))
        }

        binding.forgetPasswordTextViewId.setOnClickListener {
            val intent = Intent(
                this,
                ForgetPasswordActivity::class.java
            )
            intent.putExtra(Constants.IS_FORGET_PASSWORD, true)
            startActivity(intent)
        }

        binding.gmailLoginLinearLayoutId.setOnClickListener { gmailLogin() }

        binding.facebookLoginLinearLayoutId.setOnClickListener { facebookLogin() }

        binding.guestLoginLinearLayoutId.setOnClickListener(View.OnClickListener { v: View? ->
            startActivity(Intent(this, HomeActivity::class.java))
        })

        binding.createNewAccountTextViewId.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
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

    private fun gmailLogin() {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        val account = GoogleSignIn.getLastSignedInAccount(this)
        try {
            if (account != null) {

                account.id?.let { preferenceHelper.setSocialToken(it) }
            }

            //   var socialLoginRequest = SocialLoginRequest(email, name, phoneNumber = null, account!!.id)

            // socialRegisterLogin(account, socialLoginRequest)

        } catch (e: Exception) {
            e.printStackTrace()
        }
        val signInIntent: Intent? = this.mGoogleSignInClient?.signInIntent
        if (signInIntent != null) {
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    private fun facebookLogin() {

        //List<String> permissionNeeds = Arrays.asList("user_photos", "email", "user_birthday", "public_profile");
        LoginManager.getInstance()
            .logInWithReadPermissions(
                this,
                mutableListOf(
                    "public_profile",
                    "email",
                    "user_birthday",
                    "user_friends"
                )
            )
        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    try {
                        val accessToken = loginResult.accessToken
                        val profile = Profile.getCurrentProfile()

                        // Facebook Email address
                        val request = GraphRequest.newMeRequest(
                            loginResult.accessToken
                        ) { `object`: JSONObject, response: GraphResponse ->
                            Log.v("LoginActivity Response ", response.toString())
                            try {
                                name = `object`.getString("name")
                                email = `object`.getString("email")
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }
                        val parameters = Bundle()
                        parameters.putString("fields", "id,name,email,gender, birthday")
                        request.parameters = parameters
                        request.executeAsync()

                        preferenceHelper.setSocialToken(loginResult.accessToken.userId)

                        /* var socialLoginRequest = SocialLoginRequest(
                             email,
                             name,
                             phoneNumber = null,
                             loginResult.accessToken.userId
                         )

                         socialRegisterLogin(null, socialLoginRequest)*/

                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                        Toast.makeText(
                            activityContext,
                            activityResources!!.getString(R.string.toast_please_try_again_later),
                            Toast.LENGTH_SHORT
                        ).show();
                    }
                }

                override fun onCancel() {
                }

                override fun onError(error: FacebookException) {
                }
            })
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager?.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            // Signed in successfully, show authenticated UI.

            // var socialLoginRequest = SocialLoginRequest(email, name, phoneNumber = null, account.id)

            //socialRegisterLogin(account, socialLoginRequest)
            account.id?.let { preferenceHelper.setSocialToken(it) }

        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            e.printStackTrace()
            //updateUI(null);
        }
    }

    private fun setPrompt() {
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    Utilities.showSnackBar(
                        Constants.AUTHENTICATION_ERROR + " " + errString,
                        activityContext as LoginActivity
                    )
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)
                    Utilities.showSnackBar(
                        Constants.AUTHENTICATION_SUCCEEDED,
                        activityContext as LoginActivity
                    )
                    startActivity(Intent(activityContext, HomeActivity::class.java))
                    //binding.textViewAuthResult.visibility = View.VISIBLE
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Utilities.showSnackBar(
                        Constants.AUTHENTICATION_FAILED,
                        activityContext as LoginActivity
                    )
                }
            })
    }

    private fun checkBiometric() {

        if (Utilities.deviceHasPasswordPinLock(this)) {
            //binding.DeviceHasPINPasswordLock.text = Contestants.TRUE
        } else {
            //binding.DeviceHasPINPasswordLock.text = Contestants.FALSE
        }

        executor = ContextCompat.getMainExecutor(activityContext)

        setPrompt()

        if (Utilities.isBiometricHardWareAvailable(activityContext)) {
            // binding.DeviceHasBiometricFeatures.text = Contestants.AVAILABLE
            //binding.DeviceHasFingerPrint.text = Contestants.TRUE

            //Enable the button if the device has biometric hardware available
            //binding.authenticatefingerprintbutton.isEnabled = true

            initBiometricPrompt(
                Constants.BIOMETRIC_AUTHENTICATION,
                Constants.BIOMETRIC_AUTHENTICATION_SUBTITLE,
                Constants.BIOMETRIC_AUTHENTICATION_DESCRIPTION,
                false
            )
        } else {

            //Fallback, use device password/pin
            if (Utilities.deviceHasPasswordPinLock(activityContext)) {
                initBiometricPrompt(
                    Constants.PASSWORD_PIN_AUTHENTICATION,
                    Constants.PASSWORD_PIN_AUTHENTICATION_SUBTITLE,
                    Constants.PASSWORD_PIN_AUTHENTICATION_DESCRIPTION,
                    true
                )
            } else {
                openAlertDialog(
                    this,
                    activityResources?.getString(R.string.dialogs_error),
                    "لا يمتلك بصمة اصبع",
                    false,
                    DISMISS,
                    this
                )
            }
        }
    }

    private fun initBiometricPrompt(
        title: String,
        subtitle: String,
        description: String,
        setDeviceCred: Boolean
    ) {
        if (setDeviceCred) {
            /*For API level > 30
              Newer API setAllowedAuthenticators is used*/
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val authFlag = DEVICE_CREDENTIAL or BIOMETRIC_STRONG
                promptInfo = BiometricPrompt.PromptInfo.Builder()
                    .setTitle(title)
                    .setSubtitle(subtitle)
                    .setDescription(description)
                    .setAllowedAuthenticators(authFlag)
                    .build()
            } else {
                /*SetDeviceCredentials method deprecation is ignored here
                  as this block is for API level<30*/
                @Suppress("DEPRECATION")
                promptInfo = BiometricPrompt.PromptInfo.Builder()
                    .setTitle(title)
                    .setSubtitle(subtitle)
                    .setDescription(description)
                    .setDeviceCredentialAllowed(true)
                    .build()
            }
        } else {
            promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle(title)
                .setSubtitle(subtitle)
                .setDescription(description)
                .setNegativeButtonText(Constants.CANCEL)
                .build()
        }

        binding.authenticatefingerprintImageView.setOnClickListener {
            biometricPrompt.authenticate(promptInfo)
        }
    }

    override fun onClose() {

    }

    override fun onConfirm(actionType: Int) {

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
