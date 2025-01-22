package com.arakadds.arak.presentation.activities.ads

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arakadds.arak.R
import com.arakadds.arak.common.helper.LocaleHelper
import com.arakadds.arak.common.helper.connectivity_checker.ConnectionLiveData
import com.arakadds.arak.common.preferaence.IPreferenceHelper
import com.arakadds.arak.common.preferaence.PreferenceManager
import com.arakadds.arak.databinding.ActivityCategoryPackagesBinding
import com.arakadds.arak.model.new_mapping_refactore.response.banners.packages.PackagesDetails
import com.arakadds.arak.presentation.activities.other.BaseActivity
import com.arakadds.arak.presentation.activities.other.NoInternetConnectionActivity
import com.arakadds.arak.presentation.adapters.PackagesAdapter
import com.arakadds.arak.presentation.dialogs.ApplicationDialogs
import com.arakadds.arak.presentation.dialogs.ApplicationDialogs.openAlertDialog
import com.arakadds.arak.presentation.viewmodel.CategoryPackagesViewModel
import com.arakadds.arak.utils.AppEnums.AdsTypeCategories.IMAGE
import com.arakadds.arak.utils.AppEnums.AdsTypeCategories.STORES
import com.arakadds.arak.utils.AppEnums.AdsTypeCategories.VIDEO
import com.arakadds.arak.utils.AppEnums.AdsTypeCategories.WEBSITE
import com.arakadds.arak.utils.AppEnums.DialogActionTypes.DISMISS
import com.arakadds.arak.utils.AppProperties
import com.arakadds.arak.utils.Constants.CATEGORY_ID
import com.arakadds.arak.utils.GlobalMethodsOldClass
import dagger.android.AndroidInjection
import io.paperdb.Paper
import javax.inject.Inject

class CategoryPackagesActivity : BaseActivity(), ApplicationDialogs.AlertDialogCallbacks {

    private val preferenceHelper: IPreferenceHelper by lazy { PreferenceManager(applicationContext) }
    lateinit var binding: ActivityCategoryPackagesBinding
    private lateinit var activityContext: Context

    private lateinit var toolbar: Toolbar
    private lateinit var pageTitle: TextView
    private var connectionLiveData: ConnectionLiveData? = null

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: CategoryPackagesViewModel by viewModels {
        viewModelFactory
    }
    private lateinit var token: String
    lateinit var packagesRecyclerView: RecyclerView
    private lateinit var selectedAdTypeId: String
    private lateinit var language: String
    private lateinit var resources: Resources

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connectionLiveData = ConnectionLiveData(this)
        binding = ActivityCategoryPackagesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AndroidInjection.inject(this)
        activityContext = this

        language = preferenceHelper.getLanguage()

        updateCategoryPackagesView(language)
        checkNetworkConnection()
        initData()
        initToolbar()
        setupRecyclerView()
        getPackagesList()
    }

    private fun initData() {
        token = preferenceHelper.getToken()
        selectedAdTypeId = intent.getStringExtra(CATEGORY_ID).toString()
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

        //pageTitle.text = resources.getText(R.string.notifications_activity_title_label)
        when (selectedAdTypeId.toInt()) {
            IMAGE-> {
                pageTitle.text = resources.getString(R.string.Category_Packages_activity_Image_Ads)
            }
            VIDEO -> {
                pageTitle.text = resources.getString(R.string.Category_Packages_activity_Video_Ads)
            }
            WEBSITE -> {
                pageTitle.text = resources.getString(R.string.Category_Packages_activity_Website_Ads)
            }
            STORES -> {
                pageTitle.text = resources.getString(R.string.Category_Packages_activity_Stores)
            }
        }

        backImageView.setOnClickListener { finish() }
    }

    private fun updateCategoryPackagesView(language: String) {
        val context = LocaleHelper.setLocale(this@CategoryPackagesActivity, language)
        resources = context.resources

        binding.selectedCategorySpecialServiceTextViewId.text = resources.getString(R.string.Category_Packages_activity_Special_Service)

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

    private fun setupRecyclerView() {
        packagesRecyclerView = findViewById(R.id.packeges_RecyclerView_id)
        val glm = GridLayoutManager(this@CategoryPackagesActivity, 2)
        packagesRecyclerView.layoutManager = glm
    }

    private fun getPackagesList() {
        showLoadingDialog(this, "message")
        viewModel.getCategoryPackages(language,"Bearer $token", selectedAdTypeId )
        viewModel.packagesDetailsModel.observe(
            this,
            Observer(function = fun(packagesDetails: PackagesDetails?) {
                packagesDetails?.let {
                    if (packagesDetails.statusCode == 200) {
                        val packagesAdapter = PackagesAdapter(
                            packagesDetails.packagesData.adPackagesArrayList,
                            this@CategoryPackagesActivity,
                            selectedAdTypeId,
                            preferenceHelper,
                            resources
                        )
                        packagesRecyclerView.adapter = packagesAdapter

                    } else {
                        openAlertDialog(
                            this,
                            resources?.getString(R.string.dialogs_error),
                            packagesDetails.message,
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

    override fun onClose() {
    }

    override fun onConfirm(actionType: Int) {

    }
}