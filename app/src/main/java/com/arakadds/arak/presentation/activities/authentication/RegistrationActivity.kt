package com.arakadds.arak.presentation.activities.authentication

import android.app.DatePickerDialog
import android.content.Context
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.arakadds.arak.R
import com.arakadds.arak.common.helper.ActivityHelper
import com.arakadds.arak.common.helper.FontHelper
import com.arakadds.arak.common.helper.LocaleHelper
import com.arakadds.arak.common.helper.MethodHelper
import com.arakadds.arak.common.helper.MethodHelper.stringToDate
import com.arakadds.arak.common.helper.connectivity_checker.ConnectionLiveData
import com.arakadds.arak.common.preferaence.IPreferenceHelper
import com.arakadds.arak.common.preferaence.PreferenceManager
import com.arakadds.arak.databinding.ActivityRegistrationBinding
import com.arakadds.arak.model.new_mapping_refactore.BaseResponse
import com.arakadds.arak.model.new_mapping_refactore.request.auth.OTPRequest
import com.arakadds.arak.model.new_mapping_refactore.request.auth.RegistrationBodyRequest
import com.arakadds.arak.model.new_mapping_refactore.response.banners.auth.RegistrationModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.cities.CitiesModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.cities.CityResponseData
import com.arakadds.arak.model.new_mapping_refactore.response.banners.countries.CountriesModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.countries.CountriesResponseData
import com.arakadds.arak.presentation.activities.home.HomeActivity
import com.arakadds.arak.presentation.activities.other.BaseActivity
import com.arakadds.arak.presentation.activities.other.NoInternetConnectionActivity
import com.arakadds.arak.presentation.activities.settings.AboutInformationActivity
import com.arakadds.arak.presentation.dialogs.ApplicationDialogs
import com.arakadds.arak.presentation.dialogs.ApplicationDialogs.openAlertDialog
import com.arakadds.arak.presentation.viewmodel.CreateAccountViewModel
import com.arakadds.arak.presentation.viewmodel.LoginViewModel
import com.arakadds.arak.presentation.viewmodel.ValidationViewModel
import com.arakadds.arak.utils.AppEnums
import com.arakadds.arak.utils.AppEnums.DialogActionTypes.DISMISS
import com.arakadds.arak.utils.AppEnums.GenderTypes.FEMALE
import com.arakadds.arak.utils.AppEnums.GenderTypes.MALE
import com.arakadds.arak.utils.Constants.IS_REGISTRATION
import com.arakadds.arak.utils.Constants.USER_REGISTRATION_INFO
import com.facebook.CallbackManager
import com.facebook.login.LoginBehavior
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import dagger.android.AndroidInjection
import java.util.Calendar
import javax.inject.Inject

class RegistrationActivity : BaseActivity(), ApplicationDialogs.AlertDialogCallbacks {

    private val preferenceHelper: IPreferenceHelper by lazy { PreferenceManager(applicationContext) }
    lateinit var binding: ActivityRegistrationBinding
    private lateinit var activityContext: Context

    private lateinit var toolbar: Toolbar
    private var connectionLiveData: ConnectionLiveData? = null

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val validationViewModel: ValidationViewModel by viewModels {
        viewModelFactory
    }
    private val createAccountViewModel: CreateAccountViewModel by viewModels {
        viewModelFactory
    }
    private val loginViewModel: LoginViewModel by viewModels {
        viewModelFactory
    }
    private lateinit var token: String
    private lateinit var activityResources: Resources
    private lateinit var language: String
    var mGoogleSignInClient: GoogleSignInClient? = null
    var callbackManager: CallbackManager? = null
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    private var cityId = 0
    private var countryId = 0
    private var countryCode = ""
    private var isEmailRequired = false
    private var socialToken: String? = null
    private var userEmail: String? = null
    private var birthDate: String? = null
    private var selectedGender: String? = null

    //private var email: String? = null
    private var name: String? = null

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connectionLiveData = ConnectionLiveData(this)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AndroidInjection.inject(this)
        activityContext = this

        language = preferenceHelper.getLanguage()

        LoginManager.getInstance().loginBehavior = LoginBehavior.WEB_VIEW_ONLY
        callbackManager = CallbackManager.Factory.create()
        initUi()
        initData()
        updateRegistrationView(language)
        setupUi()
        setListeners()
        getCountries()
    }

    private fun initData() {
        token = preferenceHelper.getToken()
    }

    private fun setupUi() {
        val spannableString =
            SpannableString(resources.getString(R.string.registration_activity_desc))
        val fcsGray = activityResources.getColor(R.color.gray_100).let { ForegroundColorSpan(it) }
        val fcsOrange =
            activityResources.getColor(R.color.orange_dark).let { ForegroundColorSpan(it) }
        if (language == "en") {
            spannableString.setSpan(fcsGray, 0, 35, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableString.setSpan(fcsOrange, 35, 40, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableString.setSpan(fcsGray, 40, 44, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableString.setSpan(fcsOrange, 44, 50, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableString.setSpan(fcsGray, 50, 51, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        } else if (language == "ar") {
            spannableString.setSpan(fcsGray, 0, 25, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableString.setSpan(fcsOrange, 25, 34, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableString.setSpan(fcsGray, 34, 35, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableString.setSpan(fcsOrange, 35, 41, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableString.setSpan(fcsGray, 41, 42, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        binding.registrationDescTextViewId.text = spannableString

        try {
            if (socialToken != null) {
                //binding.registrationBusinessNameEditeTextId.visibility = View.GONE
                binding.registrationPasswordEditeTextId.visibility = View.GONE
                binding.registrationConfirmPasswordEditeTextId.visibility = View.GONE
                //binding.registrationPasswordTextInputLayoutId.visibility = View.GONE
                //binding.registrationConfirmPasswordTextInputLayoutId.visibility = View.GONE
                binding.registrationGendarSpinnerId.visibility = View.GONE
                binding.registrationCountrySpinnerId.visibility = View.GONE
                binding.registrationCitySpinnerId.visibility = View.GONE
                binding.registrationViewId.visibility = View.GONE
                binding.userRegistrationGmailLoginLinearLayoutId.visibility = View.GONE
                binding.userRegistrationFacebookRegistrationButtonId.visibility = View.GONE
                binding.haveAccountTextViewId.visibility = View.GONE
                binding.registrationOrTextViewId.visibility = View.GONE
                if (name != null && name != "") {
                    binding.registrationFullNameEditeTextId.setText(name)
                }
                /*if (email != null) {
                    binding.registrationEmailEditeTextId.setText(email)
                }*/
            }
        } catch (e: Exception) {
            e.printStackTrace()
            socialToken = ""
        }
    }

    private fun initUi() {

    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun updateRegistrationView(lang: String) {
        val context = LocaleHelper.setLocale(this@RegistrationActivity, lang)
        activityResources = context.resources
        binding.registrationTitleTextViewId.text =
            activityResources.getString(R.string.registration_activity_title)
        binding.signupRegistrationButtonId.text =
            activityResources.getString(R.string.registration_activity_Sign_up)
        binding.registrationFullNameEditeTextId.hint =
            activityResources.getString(R.string.registration_activity_full_name)
        // binding.registrationBusinessNameEditeTextId.hint = activityResources.getString(R.string.registration_activity_Business_Name)
        //binding.registrationEmailEditeTextId.hint = activityResources.getString(R.string.registration_activity_email)
        binding.registrationPasswordEditeTextId.hint =
            activityResources.getString(R.string.registration_activity_password)
        binding.registrationConfirmPasswordEditeTextId.hint =
            activityResources.getString(R.string.registration_activity_Confirm_password)

        binding.registrationDateOfBirthTextView.hint =
            activityResources.getString(R.string.registration_activity_date_of_birth)
        //phoneNumberEditText.setHint(resources.getString(R.string.registration_activity_phone_number));
        binding.registrationOrTextViewId.text =
            activityResources.getString(R.string.registration_activity_or)
        binding.userRegistrationFacebookLoginTextViewId.text =
            activityResources.getString(R.string.registration_activity_Login_Facebook)
        binding.userRegistrationGmailLoginTextViewId.text =
            activityResources.getString(R.string.registration_activity_Login_gmail)

        binding.registrationEmailEditeTextId.hint =
            activityResources.getString(R.string.login_activity_email)

        binding.haveAccountTextViewId.text =
            activityResources.getString(R.string.registration_activity_Have_account)
        if (lang == "en") {
            binding.haveAccountTextViewId.text = FontHelper.setMultiColorsToText(
                this@RegistrationActivity,
                activityResources.getString(R.string.registration_activity_Have_account),
                1,
                16,
                23
            )
            binding.termsConditionsTextViewId.text = FontHelper.setMultiColorsToText(
                this@RegistrationActivity,
                activityResources.getString(R.string.registration_activity_Terms),
                0,
                31,
                70
            )
        } else if (lang == "ar") {
            binding.haveAccountTextViewId.text = FontHelper.setMultiColorsToText(
                this@RegistrationActivity,
                activityResources.getString(R.string.registration_activity_Have_account),
                1,
                15,
                28
            )
            binding.termsConditionsTextViewId.text = FontHelper.setMultiColorsToText(
                this@RegistrationActivity,
                activityResources.getString(R.string.registration_activity_Terms),
                0,
                25,
                69
            )
        }
        language = lang
        initSpinners(language)
    }

    private fun initSpinners(language: String) {
        if (language == "en") {
            loadSpinnerIdTypes(binding.registrationGendarSpinnerId, R.array.gender_array_en)
        } else if (language == "ar") {
            loadSpinnerIdTypes(binding.registrationGendarSpinnerId, R.array.gender_array_ar)
        } else {
            loadSpinnerIdTypes(binding.registrationGendarSpinnerId, R.array.gender_array_en)
        }
    }

    private fun loadSpinnerIdTypes(spinner: Spinner, genderArray: Int) {

        // Set up the ArrayAdapter with the array
        val adapter = ArrayAdapter.createFromResource(
            this,
            genderArray,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // Handle the selection
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (position) {
                    1 -> {
                        // Option "Male" selected
                        selectedGender = MALE

                    }

                    2 -> {
                        // Option "Female" selected
                        selectedGender = FEMALE
                    }

                    else -> {
                        // Do nothing for "Gender" option
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // No action needed
            }
        }
    }

    fun setListeners() {
        binding.signupRegistrationButtonId.setOnClickListener(View.OnClickListener {
            binding.signupRegistrationButtonId.isEnabled = false
            val fullName: String =
                binding.registrationFullNameEditeTextId.text.toString().trim { it <= ' ' }
            val phoneNumber: String = binding.forgetPasswordCodeTextViewId.text.toString() +
                    binding.registrationPhoneNumberEditeTextId.text.toString().trim { it <= ' ' }

            // Add this line to get email value
            userEmail = binding.registrationEmailEditeTextId.text.toString().trim()





            /*if (socialToken != "" && socialToken != null) {
                if (fullName.isNotEmpty() && phoneNumber.isNotEmpty()) {
                    val hashMap = HashMap<String, Any>()
                    hashMap["fullname"] = fullName
                    hashMap["phone_no"] = "+962$phoneNumber"
                    hashMap["social_token"] = socialToken!!

                    createNewUser(hashMap)

                } else {
                    if (fullName == "") {
                        binding.registrationFullNameEditeTextId.error =
                            activityResources.getString(R.string.toast_Insert_full_name)
                        binding.registrationFullNameEditeTextId.requestFocus()
                    }
                    if (phoneNumber == "") {
                        binding.registrationPhoneNumberEditeTextId.error =
                            activityResources.getString(
                                R.string.toast_Insert_phone_number
                            )
                        binding.registrationPhoneNumberEditeTextId.requestFocus()
                    }
                    binding.signupRegistrationButtonId.isEnabled = true
                    binding.registrationProgressBarId.visibility = View.GONE
                    return@OnClickListener
                }
            } else {*/
            val password: String =
                binding.registrationPasswordEditeTextId.text.toString().trim { it <= ' ' }
            val confirmPassword: String =
                binding.registrationConfirmPasswordEditeTextId.text.toString()
                    .trim { it <= ' ' }
            val city: String = binding.registrationCitySpinnerId.selectedItem.toString()
            val country: String = binding.registrationCountrySpinnerId.selectedItem.toString()
            val gender: String = binding.registrationGendarSpinnerId.selectedItem.toString()
            if (!phoneNumber.trim { it <= ' ' }.isEmpty()
                && !password.trim { it <= ' ' }.isEmpty()
                && !confirmPassword.trim { it <= ' ' }.isEmpty()
                && !phoneNumber.trim { it <= ' ' }.isEmpty()
                && !city.isEmpty()
                && !gender.isEmpty()
            ) {
                if (password.trim { it <= ' ' }.length < 6) {
                    binding.registrationPasswordEditeTextId.error =
                        activityResources.getString(R.string.toast_password_length)
                    binding.registrationPasswordEditeTextId.requestFocus()
                    binding.signupRegistrationButtonId.isEnabled = true
                    return@OnClickListener
                }
                if (confirmPassword.trim { it <= ' ' }.length < 6) {
                    binding.registrationConfirmPasswordEditeTextId.error =
                        activityResources.getString(
                            R.string.toast_password_length
                        )
                    binding.registrationConfirmPasswordEditeTextId.requestFocus()
                    binding.signupRegistrationButtonId.isEnabled = true
                    return@OnClickListener
                }

                if (password != confirmPassword) {
                    binding.registrationPasswordEditeTextId.error =
                        activityResources.getString(R.string.toast_Passwords_not_match)
                    binding.registrationPasswordEditeTextId.requestFocus()
                    binding.registrationConfirmPasswordEditeTextId.error =
                        activityResources.getString(
                            R.string.toast_Passwords_not_match
                        )
                    binding.registrationConfirmPasswordEditeTextId.requestFocus()
                    binding.signupRegistrationButtonId.isEnabled = true
                    return@OnClickListener
                }

                if (isEmailRequired) {
                    if (userEmail == null || userEmail!!.isEmpty()) {
                        binding.registrationEmailEditeTextId.error =
                            activityResources.getString(R.string.toast_Insert_email)
                        binding.registrationEmailEditeTextId.requestFocus()
                        binding.signupRegistrationButtonId.isEnabled = true
                        return@OnClickListener
                    }
                }

                if (isEmailRequired) {
                    // Validate email format
                    if (!isValidEmail(userEmail!!)) {
                        binding.registrationEmailEditeTextId.error =
                            activityResources.getString(R.string.toast_please_Insert_valid_email_address)
                        binding.registrationEmailEditeTextId.requestFocus()
                        binding.signupRegistrationButtonId.isEnabled = true
                        return@OnClickListener
                    }
                }

                if (selectedGender == null) {
                    Toast.makeText(
                        activityContext,
                        activityResources.getString(R.string.toast_Select_Your_Gender),
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.registrationGendarSpinnerId.requestFocus()
                    binding.signupRegistrationButtonId.isEnabled = true
                    return@OnClickListener
                }

                if (password.equals(confirmPassword)) {
                    val registrationBodyRequest = RegistrationBodyRequest()
                    registrationBodyRequest.fullname = fullName
                    registrationBodyRequest.phoneNo = phoneNumber
                    registrationBodyRequest.password = password
                    registrationBodyRequest.birthdate =
                        birthDate?.let { it1 -> stringToDate(it1, "yyyy-mm-dd") }
                    registrationBodyRequest.gender = selectedGender
                    registrationBodyRequest.cityId = cityId
                    registrationBodyRequest.countryId = countryId
                    registrationBodyRequest.userEmail = userEmail
                    sendOTP(registrationBodyRequest)
                } else {
                    binding.registrationPasswordEditeTextId.error =
                        activityResources.getString(R.string.toast_Passwords_not_match)
                    binding.registrationPasswordEditeTextId.requestFocus()
                    binding.registrationConfirmPasswordEditeTextId.error =
                        activityResources.getString(
                            R.string.toast_Passwords_not_match
                        )
                    binding.registrationConfirmPasswordEditeTextId.requestFocus()
                    binding.signupRegistrationButtonId.isEnabled = true
                }

            } else {
                if (phoneNumber.trim { it <= ' ' }.isEmpty()) {
                    binding.registrationPhoneNumberEditeTextId.error =
                        activityResources.getString(
                            R.string.toast_Insert_phone_number
                        )
                    binding.registrationPhoneNumberEditeTextId.requestFocus()
                }
                if (password.trim { it <= ' ' }.isEmpty()) {
                    binding.registrationPasswordEditeTextId.error =
                        activityResources.getString(R.string.toast_Insert_password)
                    binding.registrationPasswordEditeTextId.requestFocus()
                }
                if (password.trim { it <= ' ' }.isEmpty()) {
                    binding.registrationPasswordEditeTextId.error =
                        activityResources.getString(R.string.toast_Insert_password)
                    binding.registrationPasswordEditeTextId.requestFocus()
                }
                /*if (email.trim { it <= ' ' }.isEmpty()) {
                    binding.registrationEmailEditeTextId.error =
                        activityResources.getString(R.string.toast_Insert_email)
                    binding.registrationEmailEditeTextId.requestFocus()
                }*/
                if (fullName.trim { it <= ' ' }.isEmpty()) {
                    binding.registrationFullNameEditeTextId.error =
                        activityResources.getString(R.string.toast_Insert_full_name)
                    binding.registrationFullNameEditeTextId.requestFocus()
                }
                Toast.makeText(
                    activityContext,
                    activityResources.getString(R.string.toast_fill_all_required_fields),
                    Toast.LENGTH_SHORT
                ).show();
                binding.signupRegistrationButtonId.isEnabled = true
            }
            //}
        })
        binding.haveAccountTextViewId.setOnClickListener { finish() }
        binding.termsConditionsTextViewId.setOnClickListener {
            val intent = Intent(this@RegistrationActivity, AboutInformationActivity::class.java)
            intent.putExtra("info_label", "terms")
            intent.putExtra("page_title", "Terms and Conditions")
            startActivity(intent)
        }
        binding.userRegistrationGmailLoginLinearLayoutId.setOnClickListener { /*gmailLogin()*/ }
        binding.userRegistrationFacebookRegistrationButtonId.setOnClickListener { /*facebookLogin()*/ }

        binding.registrationDateOfBirthTextView.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar[Calendar.YEAR]
            val month = calendar[Calendar.MONTH]
            val day = calendar[Calendar.DAY_OF_MONTH]
            val datePickerDialog = DatePickerDialog(
                activityContext, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                dateSetListener, year, month, day
            )
            //datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePickerDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            datePickerDialog.show()
        }
        dateSetListener =
            OnDateSetListener { datePicker, year, month, dayOfMonth ->
                var month = month
                month += 1
                val date: String
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
                date = "$year-$monthOfYear-$day"

                birthDate = date
                binding.registrationDateOfBirthTextView.text = birthDate
            }
    }
    /*private fun gmailLogin() {
        binding.registrationProgressBarId.visibility = View.VISIBLE
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
            var socialLoginRequest =
                SocialLoginRequest(account?.id, phoneNumber,countryId,cityId,birthDate, gender,null)

            socialRegisterLogin(account, socialLoginRequest)

            account.id?.let { preferenceHelper.setSocialToken(it) }

        } catch (e: Exception) {
            e.printStackTrace()
        }
        val signInIntent = mGoogleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }*/

    /*  private fun facebookLogin() {
          //List<String> permissionNeeds = Arrays.asList("user_photos", "email", "user_birthday", "public_profile");
          binding.registrationProgressBarId.visibility = View.VISIBLE
          LoginManager.getInstance()
              .logInWithReadPermissions(
                  this,
                  mutableListOf("public_profile", "email", "user_birthday", "user_friends")
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
                                  //email = `object`.getString("email")
                                  //Log.v("Email = ", " $email")
                                  //Toast.makeText(getApplicationContext(), "Name " + name + ", email " + email, Toast.LENGTH_LONG).show();
                              } catch (e: JSONException) {
                                  e.printStackTrace()
                              }
                          }
                          val parameters = Bundle()
                          parameters.putString("fields", "id,name,email,gender, birthday")
                          request.parameters = parameters
                          request.executeAsync()

                          //Log.d(TAG, "onSuccess() returned: getCurrentProfile: get id: " + Profile.getCurrentProfile().getId());

                          preferenceHelper.setSocialToken(loginResult.accessToken.userId)

                          var socialLoginRequest = SocialLoginRequest(
                              null,
                              name,
                              phoneNumber = null,
                              loginResult.accessToken.userId
                          )

                          socialRegisterLogin(null, socialLoginRequest)

                          binding.registrationProgressBarId.visibility = View.GONE
                      } catch (e: Exception) {
                          e.printStackTrace()
                          binding.registrationProgressBarId.visibility = View.GONE
                          Toast.makeText(
                              activityContext,
                              activityResources.getString(R.string.toast_please_try_again_later),
                              Toast.LENGTH_SHORT
                          ).show();
                      }
                  }

                  override fun onCancel() {
                      binding.registrationProgressBarId.visibility = View.GONE
                  }

                  override fun onError(error: FacebookException) {
                      binding.registrationProgressBarId.visibility = View.GONE
                  }
              })
      }
  */
    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager!!.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            binding.registrationProgressBarId.visibility = View.GONE
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            // Signed in successfully, show authenticated UI.

            var socialLoginRequest = SocialLoginRequest(null, name, phoneNumber = null, account.id)

            socialRegisterLogin(account, socialLoginRequest)

            account.id?.let { preferenceHelper.setSocialToken(it) }

        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            e.printStackTrace()
            //updateUI(null);
        }
    }*/

    private fun initCountrySpinner(countriesResponseDataArrayList: ArrayList<CountriesResponseData>) {
        if (countriesResponseDataArrayList.size > 0) {
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item, countriesResponseDataArrayList
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.registrationCountrySpinnerId.adapter = adapter
            binding.registrationCountrySpinnerId.onItemSelectedListener = object :
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
                    isEmailRequired = countriesResponseData.isEmailRequired
                    binding.forgetPasswordCodeTextViewId.text = countryCode
                    if (isEmailRequired) {
                        binding.registrationEmailEditeTextId.visibility = View.VISIBLE
                    } else {
                        binding.registrationEmailEditeTextId.visibility = View.GONE
                        userEmail = null
                    }
                    getCities(countryId)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
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
                        openAlertDialog(
                            this,
                            activityResources?.getString(R.string.dialogs_error),
                            citiesModel.message,
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

    private fun initCitiesSpinner(citiesArrayList: ArrayList<CityResponseData>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, citiesArrayList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.registrationCitySpinnerId.adapter = adapter
        binding.registrationCitySpinnerId.onItemSelectedListener = object :
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
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun sendOTP(registrationBodyRequest: RegistrationBodyRequest) {
        showLoadingDialog(this, "message")

        // Create OTP request with both phone and email
        val otpRequest = OTPRequest(
            phoneNumber = registrationBodyRequest.phoneNo ?: "",
            email = registrationBodyRequest.userEmail.toString()
        )

        validationViewModel.sendOTP(language, otpRequest)

        validationViewModel.baseResponseModel.observe(
            this,
            Observer(function = fun(baseResponse: BaseResponse?) {
                baseResponse?.let {
                    if (baseResponse.statusCode == 201) {
                        val intent = Intent(
                            this@RegistrationActivity,
                            CodeVerificationActivity::class.java
                        )
                        intent.putExtra(USER_REGISTRATION_INFO, registrationBodyRequest)
                        intent.putExtra(IS_REGISTRATION, "1")
                        startActivity(intent)
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

                    }
                }
                hideLoadingDialog()
            })
        )

        validationViewModel.throwableResponse.observe(
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

    /*  private fun socialRegisterLogin(
          account: GoogleSignInAccount?,
          socialLoginRequest: SocialLoginRequest
      ) {
          showLoadingDialog(this, "message")
          MethodHelper.disableView(binding.signupRegistrationButtonId)

          loginViewModel.socialRegisterLogin(socialLoginRequest)
          loginViewModel.loginModel.observe(
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
                              //intent.putExtra("email", email)
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
                            MethodHelper.enableView(binding.signupRegistrationButtonId)
                        }
                    }


                }
                hideLoadingDialog()
            })
        )

        loginViewModel.throwableResponse.observe(
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

    private fun createNewUser(registrationBodyRequest: RegistrationBodyRequest) {
        showLoadingDialog(this, "message")
        MethodHelper.disableView(binding.signupRegistrationButtonId)
        createAccountViewModel.createAccount(language, registrationBodyRequest)
        createAccountViewModel.registrationModelModel.observe(
            this,
            Observer(function = fun(registrationModel: RegistrationModel?) {
                registrationModel?.let {
                    if (registrationModel.statusCode == 201) {
                        setPreferencesValues(registrationModel)
                        ActivityHelper.goToActivity(this, HomeActivity::class.java, false)
                    } else {
                        openAlertDialog(
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
                MethodHelper.enableView(binding.signupRegistrationButtonId)
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
                        AppEnums.DialogActionTypes.DISMISS,
                        this
                    )
                }
                hideLoadingDialog()
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


    override fun onClose() {
    }

    override fun onConfirm(actionType: Int) {

    }
}