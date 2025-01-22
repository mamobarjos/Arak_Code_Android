package com.arakadds.arak.presentation.activities.authentication

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.arakadds.arak.R
import com.arakadds.arak.common.helper.ActivityHelper
import com.arakadds.arak.common.helper.LocaleHelper
import com.arakadds.arak.common.helper.connectivity_checker.ConnectionLiveData
import com.arakadds.arak.common.preferaence.IPreferenceHelper
import com.arakadds.arak.common.preferaence.PreferenceManager
import com.arakadds.arak.databinding.ActivityResetPasswordBinding
import com.arakadds.arak.model.new_mapping_refactore.BaseResponse
import com.arakadds.arak.model.new_mapping_refactore.request.auth.ForgetPasswordRequest
import com.arakadds.arak.presentation.activities.other.BaseActivity
import com.arakadds.arak.presentation.activities.other.NoInternetConnectionActivity
import com.arakadds.arak.presentation.activities.other.SuccessActivity
import com.arakadds.arak.presentation.dialogs.ApplicationDialogs
import com.arakadds.arak.presentation.dialogs.ApplicationDialogs.openAlertDialog
import com.arakadds.arak.presentation.viewmodel.ValidationViewModel
import com.arakadds.arak.utils.AppEnums.DialogActionTypes.DISMISS
import com.arakadds.arak.utils.AppProperties
import com.arakadds.arak.utils.Constants.IS_CHANGE_PASSWORD
import dagger.android.AndroidInjection
import io.paperdb.Paper
import javax.inject.Inject

class ResetPasswordActivity : BaseActivity(), ApplicationDialogs.AlertDialogCallbacks {

    private val preferenceHelper: IPreferenceHelper by lazy { PreferenceManager(applicationContext) }
    lateinit var binding: ActivityResetPasswordBinding
    private lateinit var activityContext: Context

    private lateinit var toolbar: Toolbar
    private var connectionLiveData: ConnectionLiveData? = null

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: ValidationViewModel by viewModels {
        viewModelFactory
    }

    private lateinit var pageTitle: TextView
    private lateinit var token: String
    private lateinit var activityResources: Resources
    private lateinit var language: String
    private var isChangePassword = false
    private var phoneNumber = ""
    private var email = ""
    private var otpCode = ""
    private var userId: Int = 0

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connectionLiveData = ConnectionLiveData(this)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AndroidInjection.inject(this)
        activityContext = this

        language = preferenceHelper.getLanguage()

        updateResetPasswordView(language)
        initToolbar()
        getIntents()
        setListeners()
    }

    private fun initToolbar() {
        toolbar = binding.appBarLayout.findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        pageTitle = binding.appBarLayout.findViewById(R.id.page_title)
        val backImageView: ImageView = binding.appBarLayout.findViewById(R.id.back_button)
        //shareImageView = binding.appBarLayout.findViewById(R.id.share_button)
        //favoriteImageView = binding.appBarLayout.findViewById(R.id.favorite_button)
        pageTitle.text = activityResources.getString(R.string.profile_frag_Change_Password)
        backImageView.setOnClickListener { finish() }
    }

    private fun getIntents() {
        token = preferenceHelper.getToken()
        userId = preferenceHelper.getKeyUserId()

        isChangePassword = intent.getBooleanExtra(IS_CHANGE_PASSWORD, false)

        if (isChangePassword) {
            binding.changePasswordCurrentPasswordEditTextId.visibility = View.VISIBLE
        } else {
            binding.changePasswordCurrentPasswordEditTextId.visibility = View.GONE
        }


        try {
            otpCode = intent.getStringExtra("otp_code")!!
            phoneNumber = intent.getStringExtra("phone_number")!!
            email = intent.getStringExtra("email")!!
            if (phoneNumber == null) {
                phoneNumber = ""
            }
            if (otpCode == null) {
                otpCode = ""
            }
            if (email == null) {
                email = ""
            }
        } catch (e: Exception) {
            e.printStackTrace()
            phoneNumber = ""
            otpCode = ""
        }

    }

    private fun updateResetPasswordView(language: String) {
        val context = LocaleHelper.setLocale(this@ResetPasswordActivity, language)
        activityResources = context.resources
        binding.changePasswordTitleTextViewId.text =
            activityResources.getString(R.string.rest_password_activity_Change_Your_Password)
        binding.changePasswordDescTextViewId.text =
            activityResources.getString(R.string.rest_password_activity_enter_new_password)
        if (isChangePassword) {
            binding.changePasswordCurrentPasswordEditTextId.visibility = View.VISIBLE
            binding.changePasswordCurrentPasswordEditTextId.hint =
                activityResources.getString(R.string.rest_password_activity_current_password)
            binding.changePasswordPasswordEditTextId.hint =
                activityResources.getString(R.string.rest_password_activity_new_password)
        } else {
            binding.changePasswordCurrentPasswordEditTextId.visibility = View.GONE
            binding.changePasswordPasswordEditTextId.hint =
                activityResources.getString(R.string.registration_activity_password)
        }
        binding.changePasswordConfirmPasswordEditTextId.hint =
            activityResources.getString(R.string.rest_password_activity_confirm_password)
        binding.changePasswordSubmitButtonId.text =
            activityResources.getString(R.string.rest_password_activity_Submit)
        this.language = language
    }

    fun setListeners() {
        binding.changePasswordSubmitButtonId.setOnClickListener(View.OnClickListener {
            if (binding.changePasswordPasswordEditTextId.text.toString().trim { it <= ' ' }
                    .isEmpty()) {
                binding.changePasswordPasswordEditTextId.error =
                    activityResources.getString(R.string.error_messages_insert_your_new_password)
                binding.changePasswordPasswordEditTextId.requestFocus()
                return@OnClickListener
            }
            if (binding.changePasswordConfirmPasswordEditTextId.text.toString()
                    .trim { it <= ' ' }.isEmpty()
            ) {
                binding.changePasswordConfirmPasswordEditTextId.error =
                    activityResources.getString(R.string.error_messages_insert_confirm_your_new_password)
                binding.changePasswordConfirmPasswordEditTextId.requestFocus()
                return@OnClickListener
            }
            val password: String =
                binding.changePasswordPasswordEditTextId.text.toString().trim { it <= ' ' }
            val confirmPassword: String =
                binding.changePasswordConfirmPasswordEditTextId.text.toString().trim { it <= ' ' }
            if (password != confirmPassword) {
                Toast.makeText(
                    activityContext,
                    activityResources.getString(R.string.toast_Passwords_not_match),
                    Toast.LENGTH_SHORT
                ).show();
                return@OnClickListener
            }
            if (isChangePassword) {
                if (binding.changePasswordCurrentPasswordEditTextId.text.toString()
                        .trim { it <= ' ' }.isEmpty()
                ) {
                    binding.changePasswordCurrentPasswordEditTextId.error =
                        activityResources.getString(R.string.error_messages_insert_your_current_password)
                    binding.changePasswordCurrentPasswordEditTextId.requestFocus()
                    return@OnClickListener
                }
                val oldPassword: String =
                    binding.changePasswordCurrentPasswordEditTextId.text.toString()
                val newPassword: String = binding.changePasswordPasswordEditTextId.text.toString()
                val confirmNewPassword: String =
                    binding.changePasswordConfirmPasswordEditTextId.text.toString()
                val hashMap = HashMap<String, String>()
                hashMap["old_password"] = oldPassword
                hashMap["new_password"] = newPassword
                //hashMap["confirmed_password"] = confirmNewPassword
                resetPassword(hashMap, newPassword)
            } else {
                val formattedPhoneNumber = when {
                    phoneNumber.startsWith("+962") -> phoneNumber // Already in the correct format
                    phoneNumber.startsWith("962") -> "+$phoneNumber" // Add the "+" if it's missing
                    phoneNumber.startsWith("0") -> phoneNumber.replaceFirst(
                        "0",
                        "+962"
                    ) // Replace "0" with "+962"
                    else -> throw IllegalArgumentException("Invalid phone number format")
                }
                forgetPassword(
                    ForgetPasswordRequest(
                        formattedPhoneNumber,
                        email,
                        otpCode,
                        password
                    )
                )
            }
        })
    }

    private fun forgetPassword(forgetPasswordRequest: ForgetPasswordRequest) {
        showLoadingDialog(this, "message")
        viewModel.forgetPassword(language, forgetPasswordRequest)
        viewModel.baseResponseModel.observe(
            this,
            Observer(function = fun(baseResponse: BaseResponse?) {
                baseResponse?.let {
                    if (baseResponse.statusCode == 201) {
                        ActivityHelper.goToActivity(
                            this@ResetPasswordActivity,
                            SuccessActivity::class.java, false
                        )
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
    }

    private fun resetPassword(hashMap: HashMap<String, String>, newPassword: String) {
        showLoadingDialog(this, "message")
        viewModel.resetPassword("Bearer $token", language, userId, hashMap)
        viewModel.baseResponseModel.observe(
            this,
            Observer(function = fun(baseResponse: BaseResponse?) {
                baseResponse?.let {
                    if (baseResponse.statusCode == 200) {
                        Toast.makeText(
                            activityContext,
                            activityResources.getString(R.string.toast_Password_updated_successfully),
                            Toast.LENGTH_SHORT
                        ).show();
                        preferenceHelper.setPassword(newPassword)
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