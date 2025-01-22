package com.arakadds.arak.presentation.activities.services

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.arakadds.arak.R
import com.arakadds.arak.common.helper.LocaleHelper
import com.arakadds.arak.common.helper.connectivity_checker.ConnectionLiveData
import com.arakadds.arak.common.preferaence.IPreferenceHelper
import com.arakadds.arak.common.preferaence.PreferenceManager
import com.arakadds.arak.databinding.ActivityArakServicesBinding
import com.arakadds.arak.model.new_mapping_refactore.BaseResponse
import com.arakadds.arak.model.new_mapping_refactore.request.RequestArakServiceRequestBody
import com.arakadds.arak.model.new_mapping_refactore.response.banners.arak_services.ArakServicesModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.arak_services.ServiceDetails
import com.arakadds.arak.presentation.activities.other.BaseActivity
import com.arakadds.arak.presentation.activities.other.NoInternetConnectionActivity
import com.arakadds.arak.presentation.adapters.ArakServicesAdapter
import com.arakadds.arak.presentation.dialogs.ApplicationDialogs
import com.arakadds.arak.presentation.dialogs.ApplicationDialogs.openAlertDialog
import com.arakadds.arak.presentation.viewmodel.ArakServicesViewModel
import com.arakadds.arak.utils.AppEnums.DialogActionTypes.DISMISS
import com.arakadds.arak.utils.GlobalMethodsOldClass
import dagger.android.AndroidInjection
import javax.inject.Inject

class ArakServicesActivity : BaseActivity(), ArakServicesAdapter.RequestArakServiceCallBack,
    ApplicationDialogs.AlertDialogCallbacks {

    private val preferenceHelper: IPreferenceHelper by lazy { PreferenceManager(applicationContext) }
    lateinit var binding: ActivityArakServicesBinding
    private lateinit var activityContext: Context

    private lateinit var toolbar: Toolbar
    private lateinit var pageTitle: TextView
    private var connectionLiveData: ConnectionLiveData? = null

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: ArakServicesViewModel by viewModels {
        viewModelFactory
    }
    private lateinit var token: String
    private lateinit var alertDialog: AlertDialog
    private var serviceId: String? = null
    private var arakServicesAdapter: ArakServicesAdapter? = null
    private lateinit var language: String
    private var resources: Resources? = null
    private val currentPageDataArrayList = ArrayList<ServiceDetails>()
    private var isFirstClick = true

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connectionLiveData = ConnectionLiveData(this)
        binding = ActivityArakServicesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AndroidInjection.inject(this)
        activityContext = this

        language = preferenceHelper.getLanguage()

        updateAboutInformationView(language)
        checkNetworkConnection()
        initToolbar()
        initData()
        setUpArakServicesAdapter()
        getArakServices()
    }

    private fun initToolbar() {
        toolbar = binding.appBarLayout.findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        pageTitle = binding.appBarLayout.findViewById(R.id.page_title)
        val backImageView: ImageView = binding.appBarLayout.findViewById(R.id.back_button)
        //shareImageView = binding.appBarLayout.findViewById(R.id.share_button)
        //favoriteImageView = binding.appBarLayout.findViewById(R.id.favorite_button)
        pageTitle.text = resources?.getString(R.string.service_frag_Arak_Service)
        backImageView.setOnClickListener { finish() }
    }

    private fun initData() {
        token = preferenceHelper.getToken()
        if (token == "non" || token.trim().isEmpty()) {
            GlobalMethodsOldClass.askGuestLogin(resources, activityContext)
            return
        }
    }

    /*private fun initToolbar() {
        toolbar = binding.appBarLayout.findViewById(R.id.toolbar_id)

        setSupportActionBar(toolbar)
        val mTitle: TextView =
            binding.appBarLayout.findViewById(R.id.toolbar_title_TextView_id)
        val backImageView: ImageView =
            binding.appBarLayout.findViewById(R.id.toolbar_category_icon_ImageView_id)

        mTitle.text = resources?.getString(R.string.service_frag_Arak_Service)
        backImageView.setOnClickListener { finish() }
    }*/

    private fun updateAboutInformationView(language: String) {
        val context = LocaleHelper.setLocale(this@ArakServicesActivity, language)
        resources = context.resources

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

    private fun getArakServices() {
        showLoadingDialog(this, "message")
        viewModel.getArakServices("Bearer $token", language)
        viewModel.arakServicesModelModel.observe(
            this,
            Observer(function = fun(arakServicesModel: ArakServicesModel?) {
                arakServicesModel?.let {
                    if (arakServicesModel.statusCode == 200) {
                        currentPageDataArrayList.addAll(
                            arakServicesModel.servicesData.services
                        )
                        arakServicesAdapter?.notifyDataSetChanged()
                    } else {
                        openAlertDialog(
                            this,
                            resources?.getString(R.string.dialogs_error),
                            arakServicesModel.message,
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
                        resources?.getString(R.string.dialogs_error),
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

    private fun setUpArakServicesAdapter() {
        arakServicesAdapter = ArakServicesAdapter(
            currentPageDataArrayList,
            this@ArakServicesActivity,
            language,
            resources,
            this
        )
        val gridLayoutManager = GridLayoutManager(this@ArakServicesActivity, 1)
        binding.arakServicesRecyclerViewId.layoutManager = gridLayoutManager
        binding.arakServicesRecyclerViewId.isFocusable = false
        binding.arakServicesRecyclerViewId.isNestedScrollingEnabled = false
        binding.arakServicesRecyclerViewId.adapter = arakServicesAdapter
    }

    private fun requestArakService(requestArakServiceRequestBody: RequestArakServiceRequestBody) {
        showLoadingDialog(this, "message")
        viewModel.requestArakService("Bearer $token", language, requestArakServiceRequestBody)
        viewModel.baseResponseModel.observe(
            this,
            Observer(function = fun(baseResponse: BaseResponse?) {
                baseResponse?.let {
                    if (baseResponse.statusCode == 201) {
                        Toast.makeText(
                            activityContext,
                            resources!!.getString(R.string.toast_Thank_you),
                            Toast.LENGTH_SHORT
                        ).show();
                        alertDialog.dismiss()
                    } else {
                        openAlertDialog(
                            this,
                            resources?.getString(R.string.dialogs_error),
                            baseResponse.message,
                            false,
                            DISMISS,
                            this
                        )
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
                        resources?.getString(R.string.dialogs_error),
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

    private fun requestServiceFormDialog() {
        alertDialog = AlertDialog.Builder(this).create()
        alertDialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val inflater: LayoutInflater = alertDialog.getLayoutInflater()
        val dialogView = inflater.inflate(R.layout.dialog_request_service, null)
        val titleTextView =
            dialogView.findViewById<TextView>(R.id.request_service_title_textView_id)
        val descTextView = dialogView.findViewById<TextView>(R.id.request_service_desc_textView_id)
        val fullNameLabelTextView =
            dialogView.findViewById<TextView>(R.id.request_service_full_name_label_textView_id)
        val phoneNumberLabelTextView =
            dialogView.findViewById<TextView>(R.id.request_service_phone_number_label_textView_id)
        val emailLabelTextView =
            dialogView.findViewById<TextView>(R.id.request_service_email_label_textView_id)
        val fullNameBodyEditText =
            dialogView.findViewById<EditText>(R.id.request_service_full_name_body_editText_id)
        val phoneNumberBodyEditText =
            dialogView.findViewById<EditText>(R.id.request_service_phone_number_body_editText_id)
        val emailBodyEditText =
            dialogView.findViewById<EditText>(R.id.request_service_email_body_textView_id)
        val sendButton =
            dialogView.findViewById<Button>(R.id.request_service_send_request_Button_id)
        val phoneNumberLinearLayout =
            dialogView.findViewById<LinearLayout>(R.id.withdraw_amount_linearLayout_id)

        val fullName = preferenceHelper.getUserFullName()
        val email = preferenceHelper.getUserEmail()
        val phoneNumber = preferenceHelper.getUserPhoneNumber()
        if (fullName != "non") {
            fullNameBodyEditText.setText(fullName)
        }
        if (email != null) {
            emailBodyEditText.setText(email)
        }

        phoneNumberBodyEditText.setText(phoneNumber?.substring(4))

        titleTextView.text = resources!!.getString(R.string.dialogs_advisors_intro_title)
        descTextView.text = resources!!.getString(R.string.dialogs_advisors_intro)
        fullNameLabelTextView.text = resources!!.getString(R.string.registration_activity_full_name)
        phoneNumberLabelTextView.text =
            resources!!.getString(R.string.registration_activity_phone_number)
        emailLabelTextView.text = resources!!.getString(R.string.registration_activity_email)
        fullNameBodyEditText.hint = resources!!.getString(R.string.dialogs_Enter_Full_Name)
        phoneNumberBodyEditText.hint = resources!!.getString(R.string.dialogs_Enter_Phone_Number)
        emailBodyEditText.hint = resources!!.getString(R.string.dialogs_Enter_email)
        sendButton.text = resources!!.getString(R.string.dialogs_Continue)
        sendButton.setOnClickListener { v: View? ->
            if (isFirstClick) {
                isFirstClick = false
                titleTextView.text = resources!!.getString(R.string.dialogs_Contact_Info)
                descTextView.text = resources!!.getString(R.string.dialogs_advisors_contact_you)
                sendButton.text = resources!!.getString(R.string.dialogs_Send)
                fullNameLabelTextView.visibility = View.VISIBLE
                phoneNumberLabelTextView.visibility = View.VISIBLE
                emailLabelTextView.visibility = View.VISIBLE
                fullNameBodyEditText.visibility = View.VISIBLE
                phoneNumberLinearLayout.visibility = View.VISIBLE
                emailBodyEditText.visibility = View.VISIBLE
            } else {
                val name = fullNameBodyEditText.text.toString()
                val email1 = emailBodyEditText.text.toString()
                val phoneNumber1 = phoneNumberBodyEditText.text.toString()
                if (name != "" && email1 != "" && phoneNumber1 != "") {
                    requestArakService(RequestArakServiceRequestBody(name,email1,phoneNumber1,serviceId?.toInt()))
                } else {
                    if (email1.trim { it <= ' ' } == "") {
                        emailBodyEditText.error = resources!!.getString(R.string.toast_Insert_email)
                        emailBodyEditText.requestFocus()
                    }
                    if (phoneNumber1.trim { it <= ' ' } == "") {
                        phoneNumberBodyEditText.error =
                            resources!!.getString(R.string.toast_Insert_phone_number)
                        phoneNumberBodyEditText.requestFocus()
                    }
                    if (name.trim { it <= ' ' } == "") {
                        fullNameBodyEditText.error =
                            resources!!.getString(R.string.toast_Insert_full_name)
                        fullNameBodyEditText.requestFocus()
                    }
                }
            }
        }
        alertDialog.setView(dialogView)
        alertDialog.show()
    }


    override fun onClose() {
    }

    override fun onConfirm(actionType: Int) {

    }

    override fun onArakServiceSelected(serviceDetails: ServiceDetails?, position: Int) {
        if (token != "non") {
            serviceId = currentPageDataArrayList[position].id.toString()
            isFirstClick = true
            requestServiceFormDialog()
        } else {
            GlobalMethodsOldClass.askGuestLogin(resources, this)
        }

    }
}