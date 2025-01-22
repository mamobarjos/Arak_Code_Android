package com.arakadds.arak.presentation.activities.election


import android.Manifest.permission.CALL_PHONE
import android.R.attr.phoneNumber
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.arakadds.arak.R
import com.arakadds.arak.common.helper.LocaleHelper
import com.arakadds.arak.common.helper.connectivity_checker.ConnectionLiveData
import com.arakadds.arak.common.preferaence.IPreferenceHelper
import com.arakadds.arak.common.preferaence.PreferenceManager
import com.arakadds.arak.databinding.ActivityEllectionDetailsBinding
import com.arakadds.arak.model.new_mapping_refactore.response.banners.elected.ElectedPerson
import com.arakadds.arak.model.new_mapping_refactore.response.banners.elected.SingleElectedPersonModel
import com.arakadds.arak.presentation.activities.other.BaseActivity
import com.arakadds.arak.presentation.activities.other.NoInternetConnectionActivity
import com.arakadds.arak.presentation.dialogs.ApplicationDialogs
import com.arakadds.arak.presentation.dialogs.ApplicationDialogs.openAlertDialog
import com.arakadds.arak.presentation.viewmodel.ElectionViewModel
import com.arakadds.arak.utils.AppEnums.DialogActionTypes.DISMISS
import com.arakadds.arak.utils.AppEnums.IntentsFlags.PERSON_ID
import com.arakadds.arak.utils.AppEnums.LanguagesEnums.ARABIC
import com.arakadds.arak.utils.AppProperties
import com.arakadds.arak.utils.Constants.MY_PERMISSIONS_REQUEST_CALL_PHONE
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import dagger.android.AndroidInjection
import io.paperdb.Paper
import javax.inject.Inject
class ElectionDetailsActivity : BaseActivity(), ApplicationDialogs.AlertDialogCallbacks {

    private val preferenceHelper: IPreferenceHelper by lazy { PreferenceManager(applicationContext) }
    lateinit var binding: ActivityEllectionDetailsBinding
    private lateinit var activityContext: Context

    private lateinit var toolbar: Toolbar
    private var connectionLiveData: ConnectionLiveData? = null

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val electionViewModel: ElectionViewModel by viewModels {
        viewModelFactory
    }
    private lateinit var token: String
    private lateinit var language: String
    private lateinit var personId: String
    private lateinit var activityResources: Resources
    private var email: String? = null
    private var phoneNumber : String? = null

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connectionLiveData = ConnectionLiveData(this)
        binding = ActivityEllectionDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AndroidInjection.inject(this)
        activityContext = this

        language = preferenceHelper.getLanguage()

        updateElectionDetailsView(language)
        initData()
        getPersonElectionData(personId.toInt());
        setListeners();
    }

    private fun initData() {
        token = preferenceHelper.getToken()
        personId = intent.getStringExtra(PERSON_ID).toString()

    }

    private fun updateElectionDetailsView(language: String) {
        val context = LocaleHelper.setLocale(this@ElectionDetailsActivity, language)
        activityResources = context.resources
        binding.ellectionGovernorateTitleTextViewId.text =
            resources.getString(R.string.election_details_activity_governorate)
        binding.ellectionDistrictTitleTextViewId.text =
            resources.getString(R.string.election_details_activity_electoral_district)
        binding.ellectionClusterTitleTextViewId.text =
            resources.getString(R.string.election_details_activity_bloc_membership)
        this.language = language
    }

    fun setListeners() {
        binding.ellectionBackIconImageViewId.setOnClickListener { finish() }

        binding.ellectionPhoneCallImageViewId.setOnClickListener {
            if (phoneNumber != null) {
                val number = "tel:" + phoneNumber
                val intent = Intent(Intent.ACTION_CALL)
                intent.data = Uri.parse(number)
                // Here, thisActivity is the current activity
                if (ContextCompat.checkSelfPermission(
                        this@ElectionDetailsActivity,
                       CALL_PHONE
                    )
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this@ElectionDetailsActivity,
                        arrayOf<String>(CALL_PHONE),
                        MY_PERMISSIONS_REQUEST_CALL_PHONE
                    )

                    // MY_PERMISSIONS_REQUEST_CALL_PHONE is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                } else {
                    //You already have permission
                    try {
                        startActivity(intent)
                    } catch (e: SecurityException) {
                        e.printStackTrace()
                    }
                }
            }
        }

        binding.ellectionEmailImageViewId.setOnClickListener {
            email?.let { sendEmail(it) }
        }
    }

    private fun sendEmail(email: String) {
        val emailIntent = Intent(Intent.ACTION_SENDTO)
        emailIntent.data = Uri.parse("mailto:" + this.email)
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject")
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Body")
        if (emailIntent.resolveActivity(packageManager) != null) {
            startActivity(emailIntent)
        }
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

    private fun setViewsData(electedPerson: ElectedPerson) {
        val options: RequestOptions = RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.image_holder_virtical)
            .error(R.drawable.image_holder_virtical)

        Glide.with(this@ElectionDetailsActivity).load(electedPerson.coverImg)
            .apply(options).into(binding.ellectionCoverImageViewId)

        Glide.with(this@ElectionDetailsActivity).load(electedPerson.img)
            .apply(options).into(binding.ellectionPrfileImageViewId)

        binding.ellectionNameTextViewId.text = electedPerson.name
        /*if (language == ARABIC) {
            districtTextView.setText(electedPerson.governorate?.nameAr)
        }else{
            districtTextView.setText(electedPerson.governorate?.name)
        }*/

        email = electedPerson.email
        phoneNumber = electedPerson.phoneNo
        binding.ellectionDescriptionTextViewId.text = electedPerson.description
        if (language == ARABIC) {
            binding.ellectionGovernorateTextViewId.text = electedPerson.governorate?.nameAr
            //districtCityTextView.setText(electedPerson.getDistrictData().getNameAr())
            binding.ellectionClusterTextViewId.text = electedPerson.cluster
        } else {
            binding.ellectionGovernorateTextViewId.text = electedPerson.governorate?.name
            //districtCityTextView.setText(electedPerson.getDistrictData().getName())
            binding.ellectionClusterTextViewId.text = electedPerson.cluster
        }
    }

    private fun getPersonElectionData(
        page: Int
    ) {
        showLoadingDialog(this, "message")
        electionViewModel.getSingleElectedPerson(token, language, page)
        electionViewModel.singleElectedPersonModelModel.observe(
            this,
            Observer(function = fun(singleElectedPersonModel: SingleElectedPersonModel?) {
                singleElectedPersonModel?.let {
                    if (singleElectedPersonModel.statusCode == 200) {
                        setViewsData(singleElectedPersonModel.electedPerson);
                    } else {
                        openAlertDialog(
                            this,
                            activityResources.getString(R.string.dialogs_error),
                            singleElectedPersonModel.message,
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

        electionViewModel.throwableResponse.observe(
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

    override fun onClose() {
    }

    override fun onConfirm(actionType: Int) {

    }
}