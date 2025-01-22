package com.arakadds.arak.presentation.activities.authentication

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.arakadds.arak.R
import com.arakadds.arak.common.helper.LocaleHelper
import com.arakadds.arak.common.helper.connectivity_checker.ConnectionLiveData
import com.arakadds.arak.common.preferaence.IPreferenceHelper
import com.arakadds.arak.common.preferaence.PreferenceManager
import com.arakadds.arak.databinding.ActivityChooseInterestsBinding
import com.arakadds.arak.model.new_mapping_refactore.BaseResponse
import com.arakadds.arak.model.new_mapping_refactore.store.StoreCategoriesModel
import com.arakadds.arak.presentation.activities.home.HomeActivity
import com.arakadds.arak.presentation.activities.other.BaseActivity
import com.arakadds.arak.presentation.activities.other.NoInternetConnectionActivity
import com.arakadds.arak.presentation.adapters.UserInterestsAdapter
import com.arakadds.arak.presentation.dialogs.ApplicationDialogs
import com.arakadds.arak.presentation.dialogs.loader.ProgressDialogUtil
import com.arakadds.arak.presentation.viewmodel.StoresViewModel
import com.arakadds.arak.presentation.viewmodel.UserProfileViewModel
import com.arakadds.arak.utils.AppEnums
import dagger.android.AndroidInjection
import javax.inject.Inject

class ChooseInterestsActivity : BaseActivity(), ApplicationDialogs.AlertDialogCallbacks {

    private val preferenceHelper: IPreferenceHelper by lazy { PreferenceManager(applicationContext) }
    lateinit var binding: ActivityChooseInterestsBinding
    private lateinit var activityContext: Context

    private lateinit var toolbar: Toolbar
    private var connectionLiveData: ConnectionLiveData? = null

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val userInterestsViewModel: UserProfileViewModel by viewModels {
        viewModelFactory
    }

    private val viewModelStores: StoresViewModel by viewModels {
        viewModelFactory
    }

    private lateinit var language: String
    private lateinit var token: String
    private lateinit var activityResources: Resources

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connectionLiveData = ConnectionLiveData(this)
        binding = ActivityChooseInterestsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AndroidInjection.inject(this)
        activityContext = this
        language = preferenceHelper.getLanguage()
        updateChooseInterestsView(language)
        checkNetworkConnection()
        initData()
        binding.recyclerInterests.layoutManager = GridLayoutManager(this, 3)
        getStoreCategories()
    }

    private fun updateChooseInterestsView(language: String) {
        val context = LocaleHelper.setLocale(activityContext, language)
        activityResources = context.resources

        binding.tvTitle.text = activityResources.getString(R.string.user_intrest_title)
        binding.btnContinue.text = activityResources.getString(R.string.dialogs_Continue)

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

    fun userInterests(arrayCategoryIds: ArrayList<Int>) {

        val param =HashMap<String,Any>()

        param["category_ids"]=arrayCategoryIds

        userInterestsViewModel.userInterests(
            "Bearer $token"  ,
            language,
            param
        )
        this?.let {
            userInterestsViewModel.baseResponseModel.observe(
                it,
                Observer(function = fun(baseResponse: BaseResponse?) {
                    baseResponse?.let {
                        if (baseResponse.statusCode == 201) {

                            startActivity(Intent(this, HomeActivity::class.java))

                        } else {
                            this.let { it1 ->
                                ApplicationDialogs.openAlertDialog(
                                    it1,
                                    this.resources.getString(R.string.dialogs_error),
                                    baseResponse.message,
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

        this.let {
            userInterestsViewModel.throwableResponse.observe(
                it,
                Observer(function = fun(throwable: Throwable?) {
                        throwable.let {
                            ApplicationDialogs.openAlertDialog(
                                this,
                                this.resources.getString(R.string.dialogs_error),
                                throwable?.message,
                                false,
                                AppEnums.DialogActionTypes.DISMISS,
                                this
                            )
                        }
                    hideLoadingDialog()
                })
            )
        }
    }


    private fun getStoreCategories() {
        //showLoadingDialog(this, "message")
        viewModelStores.getStoreCategories("Bearer $token", language)
        this.let {
            viewModelStores.storeCategoriesModelModel.observe(
                it,
                Observer(function = fun(storeCategoriesModel: StoreCategoriesModel?) {
                    storeCategoriesModel?.let {
                        if (storeCategoriesModel.statusCode == 200) {

                            binding.recyclerInterests.adapter= UserInterestsAdapter(activityContext, storeCategoriesModel.data.storeCategories,this)


                        } else {
                            this?.let { it1 ->
                                ApplicationDialogs.openAlertDialog(
                                    it1,
                                    resources?.getString(R.string.dialogs_error),
                                    storeCategoriesModel.message,
                                    false,
                                    AppEnums.DialogActionTypes.DISMISS,
                                    this
                                )
                            }
                            // hideView(binding.loginProgressBarId*//*progressBar*//*)
                        }
                    }
                    ProgressDialogUtil.hideLoadingDialog()
                })
            )
        }

        this.let {
            viewModelStores.throwableResponse.observe(
                it,
                Observer(function = fun(throwable: Throwable?) {
                    throwable?.let {
                        this?.let { it1 ->
                            ApplicationDialogs.openAlertDialog(
                                it1,
                                resources?.getString(R.string.dialogs_error),
                                throwable.message,
                                false,
                                AppEnums.DialogActionTypes.DISMISS,
                                this
                            )
                        }
                    }
                    ProgressDialogUtil.hideLoadingDialog()
                })
            )
        }
    }

    override fun onClose() {
    }

    override fun onConfirm(actionType: Int) {
    }

}