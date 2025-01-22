package com.arakadds.arak.repository

import android.util.Log
import com.arakadds.arak.model.ArakStoreMakeOrder
import com.arakadds.arak.model.CustomPackage
import com.arakadds.arak.model.ProductVariant
import com.arakadds.arak.model.arakStoreModel.StoreCategory
import com.arakadds.arak.model.arakStoreModel.StoreProduct
import com.arakadds.arak.model.new_mapping_refactore.home.HomeAdsModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.auth.LoginResponseData
import com.arakadds.arak.model.new_mapping_refactore.store.StoreProductsDataModel
import com.arakadds.arak.model.new_mapping_refactore.BaseResponse
import com.arakadds.arak.model.new_mapping_refactore.request.AddAdReviewRequest
import com.arakadds.arak.model.new_mapping_refactore.request.AddStoreProductReviewRequest
import com.arakadds.arak.model.new_mapping_refactore.request.AddStoreReviewRequest
import com.arakadds.arak.model.new_mapping_refactore.request.RequestArakServiceRequestBody
import com.arakadds.arak.model.new_mapping_refactore.request.ad.AdRequest
import com.arakadds.arak.model.new_mapping_refactore.request.ad.CreateStoreAdRequest
import com.arakadds.arak.model.new_mapping_refactore.request.auth.ForgetPasswordRequest
import com.arakadds.arak.model.new_mapping_refactore.request.auth.OTPRequest
import com.arakadds.arak.model.new_mapping_refactore.request.auth.RegistrationBodyRequest
import com.arakadds.arak.model.new_mapping_refactore.response.banners.BannersModel
import com.arakadds.arak.model.new_mapping_refactore.request.create_store_product.CreateProductRequestBody
import com.arakadds.arak.model.new_mapping_refactore.response.banners.ad_category.AdsCategoriesModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.ads.AdResponseData
import com.arakadds.arak.model.new_mapping_refactore.response.banners.auth.user_balance.UserBalanceModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.cities.CitiesModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.countries.CountriesModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.create_ad.CreateNewAdModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.edit_user_info.EditUserInformation
import com.arakadds.arak.model.new_mapping_refactore.response.banners.packages.PackagesDetails
import com.arakadds.arak.model.new_mapping_refactore.store.SingleProductModel
import com.arakadds.arak.model.new_mapping_refactore.request.auth.LoginRequest
import com.arakadds.arak.model.new_mapping_refactore.request.payment.CreateCliqPaymentRequest
import com.arakadds.arak.model.new_mapping_refactore.request.store.CreateStoreRequestBody
import com.arakadds.arak.model.new_mapping_refactore.response.banners.ads.AdReviewsModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.ads.ad_reviews.CreateAdReviewModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.arak_services.ArakServicesModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.auth.RegistrationModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.elected.ElectedPeopleModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.elected.SingleElectedPersonModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.governorates.GovernoratesModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.my_info.UserInformationModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.notification.NotificationsModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.other.AboutModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.other.FeedbackModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.ranking.UsersRankingModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.transactions.UserTransactionsModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.wallets.DigitalWalletsModel
import com.arakadds.arak.model.new_mapping_refactore.store.CreateStoreModel
import com.arakadds.arak.model.new_mapping_refactore.store.SingleStoreModel
import com.arakadds.arak.model.new_mapping_refactore.store.StoreCategoriesModel
import com.arakadds.arak.model.new_mapping_refactore.store.StoreProductsModel
import com.arakadds.arak.model.new_mapping_refactore.store.StoresListModel
import com.arakadds.arak.model.new_mapping_refactore.store.products.RelatedStoreProductsModel
import com.arakadds.arak.model.new_mapping_refactore.store.products.my_products.MyStoreProductsModel
import com.arakadds.arak.model.new_mapping_refactore.store.reviews.CreateReviewModel
import com.arakadds.arak.network.NetWorkApi
import com.arakadds.arak.utils.Constants.API_KEY
import com.arakadds.arak.utils.Constants.buildVersion
import com.arakadds.arak.utils.Constants.cashValue
import com.arakadds.arak.utils.Constants.userAgent
import com.google.gson.Gson
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import javax.inject.Inject

class DataRepository @Inject constructor(private val netWorkApi: NetWorkApi) {

    private val TAG = "DataRepository"

    fun createAccount(
        lang: String, registrationBodyRequest: RegistrationBodyRequest,
        serviceResponse: ServiceResponse<RegistrationModel>
    ) {
        netWorkApi.createAccount(cashValue,API_KEY, lang, buildVersion, userAgent, registrationBodyRequest)
            .enqueue(object : retrofit2.Callback<RegistrationModel> {
                override fun onResponse(
                    call: Call<RegistrationModel>,
                    response: Response<RegistrationModel>
                ) {
                    var registrationModel: RegistrationModel? = null
                    if (response.code() == 201) {
                        registrationModel = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            registrationModel =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    RegistrationModel::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    registrationModel?.let {
                        serviceResponse.successResponse(
                            it,
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<RegistrationModel>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }

    fun getCountries(
        lang: String,
        serviceResponse: ServiceResponse<CountriesModel>
    ) {
        netWorkApi.getCountries(cashValue,API_KEY, lang, buildVersion, userAgent)
            .enqueue(object : retrofit2.Callback<CountriesModel> {
                override fun onResponse(
                    call: Call<CountriesModel>,
                    response: Response<CountriesModel>
                ) {
                    var countriesModel: CountriesModel? = null
                    if (response.code() == 200) {
                        countriesModel = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            countriesModel =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    CountriesModel::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    countriesModel?.let {
                        serviceResponse.successResponse(
                            it,
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<CountriesModel>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }

    fun getCities(
        countryId: Int,
        lang: String,
        serviceResponse: ServiceResponse<CitiesModel>
    ) {
        netWorkApi.getCities(cashValue,API_KEY, lang, buildVersion, userAgent, countryId)
            .enqueue(object : retrofit2.Callback<CitiesModel> {
                override fun onResponse(
                    call: Call<CitiesModel>,
                    response: Response<CitiesModel>
                ) {
                    var citiesModel: CitiesModel? = null
                    if (response.code() == 200) {
                        citiesModel = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            citiesModel =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    CitiesModel::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    citiesModel?.let {
                        serviceResponse.successResponse(
                            it,
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<CitiesModel>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }

    fun userLogin(
        lang: String,
        loginRequest: LoginRequest,
        serviceResponse: ServiceResponse<LoginResponseData>
    ) {
        netWorkApi.userLogin(cashValue,API_KEY, lang, buildVersion, userAgent, loginRequest)
            .enqueue(object : retrofit2.Callback<LoginResponseData> {
                override fun onResponse(
                    call: Call<LoginResponseData>,
                    response: Response<LoginResponseData>
                ) {

                    var loginModel: LoginResponseData? = null
                    if (response.code() == 201) {
                        loginModel = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            loginModel =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    LoginResponseData::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    loginModel?.let { serviceResponse.successResponse(it, response.code()) }
                }

                override fun onFailure(call: Call<LoginResponseData>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }

    /*fun socialRegisterLogin(
        socialLoginRequest: SocialLoginRequest,
        serviceResponse: ServiceResponse<RegistrationResponseData>
    ) {
        netWorkApi.socialRegisterLogin(API_KEY, socialLoginRequest)
            .enqueue(object : retrofit2.Callback<RegistrationResponseData> {
                override fun onResponse(
                    call: Call<RegistrationResponseData>,
                    response: Response<RegistrationResponseData>
                ) {

                    var registrationResponseData: RegistrationResponseData? = null
                    if (response.code() == 200) {
                        registrationResponseData = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            registrationResponseData =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    RegistrationResponseData::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    registrationResponseData?.let {
                        serviceResponse.successResponse(
                            it,
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<RegistrationResponseData>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }*/

    /*fun logout(
        token: String,
        serviceResponse: ServiceResponse<BaseResponse>
    ) {
        netWorkApi.logout(API_KEY, token)
            .enqueue(object : retrofit2.Callback<BaseResponse> {
                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {

                    var baseResponse: BaseResponse? = null
                    if (response.code() == 200) {
                        baseResponse = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            baseResponse =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    BaseResponse::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    baseResponse?.let { serviceResponse.successResponse(it, response.code()) }
                }

                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }*/

    /* fun passwordRecoverRequest(
         lang: String,
         passwordRecoverRequest: PasswordRecoverRequest,
         serviceResponse: ServiceResponse<BaseResponse>
     ) {
         netWorkApi.passwordRecoverRequest(API_KEY, lang, buildVersion, userAgent, passwordRecoverRequest)
             .enqueue(object : retrofit2.Callback<BaseResponse> {
                 override fun onResponse(
                     call: Call<BaseResponse>,
                     response: Response<BaseResponse>
                 ) {

                     var baseResponse: BaseResponse? = null
                     if (response.code() == 200) {
                         baseResponse = response.body()
                     } else {
                         try {
                             val gson = Gson()
                             val jsonObject = JSONObject(response.errorBody()!!.string())
                             baseResponse =
                                 gson.fromJson(
                                     jsonObject.toString(),
                                     BaseResponse::class.java
                                 )
                         } catch (e: Exception) {
                             Log.d(TAG, "onResponse: Exception: " + e.message)
                         }
                     }
                     baseResponse?.let { serviceResponse.successResponse(it, response.code()) }
                 }

                 override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                     serviceResponse.ErrorResponse(t)
                 }
             })
     }*/

    fun getHomeAds(
        lang: String,
        token: String,
        isFeatured: Boolean? = null,
        adCategoryId: String? = null,
        textSearch: String? = null,
        page: Int,
        serviceResponse: ServiceResponse<HomeAdsModel>
    ) {
        netWorkApi.getHomeAds(
            cashValue, API_KEY,
            lang,
            buildVersion,
            userAgent,
            token,
            isFeatured,
            adCategoryId,
            textSearch,
            page
        )
            .enqueue(object : retrofit2.Callback<HomeAdsModel> {
                override fun onResponse(
                    call: Call<HomeAdsModel>,
                    response: Response<HomeAdsModel>
                ) {

                    var homeAdsModel: HomeAdsModel? = null
                    if (response.code() == 200) {
                        homeAdsModel = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            homeAdsModel =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    HomeAdsModel::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    homeAdsModel?.let {
                        serviceResponse.successResponse(
                            it,
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<HomeAdsModel>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }

    fun getBanners(
        lang: String,
        token: String,
        bannerType: String,
        serviceResponse: ServiceResponse<BannersModel>
    ) {
        netWorkApi.getBanners(cashValue,API_KEY, lang, buildVersion, userAgent,token, bannerType)
            .enqueue(object : retrofit2.Callback<BannersModel> {
                override fun onResponse(
                    call: Call<BannersModel>,
                    response: Response<BannersModel>
                ) {

                    var bannersModel: BannersModel? = null
                    if (response.code() == 200) {
                        bannersModel = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            bannersModel =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    BannersModel::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    bannersModel?.let {
                        serviceResponse.successResponse(
                            it,
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<BannersModel>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }

    fun getCategoryPackages(
        lang: String,
        token: String,
        id: String,
        serviceResponse: ServiceResponse<PackagesDetails>
    ) {
        netWorkApi.getCategoryPackages(cashValue, API_KEY, lang, buildVersion, userAgent, token, id)
            .enqueue(object : retrofit2.Callback<PackagesDetails> {
                override fun onResponse(
                    call: Call<PackagesDetails>,
                    response: Response<PackagesDetails>
                ) {

                    var packagesDetails: PackagesDetails? = null
                    if (response.code() == 200) {
                        packagesDetails = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            packagesDetails =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    PackagesDetails::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    packagesDetails?.let { serviceResponse.successResponse(it, response.code()) }
                }

                override fun onFailure(call: Call<PackagesDetails>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }

    fun createNewAd(
        lang: String,
        token: String,
        adRequest: AdRequest,
        serviceResponse: ServiceResponse<CreateNewAdModel>
    ) {
        netWorkApi.createNewAd(cashValue, API_KEY, lang, buildVersion, userAgent, token, adRequest)
            .enqueue(object : retrofit2.Callback<CreateNewAdModel> {
                override fun onResponse(
                    call: Call<CreateNewAdModel>,
                    response: Response<CreateNewAdModel>
                ) {

                    var createNewAdModel: CreateNewAdModel? = null
                    if (response.code() == 201) {
                        createNewAdModel = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            createNewAdModel =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    CreateNewAdModel::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    createNewAdModel?.let {
                        serviceResponse.successResponse(
                            it,
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<CreateNewAdModel>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }

    fun getSingleAdDetails(
        lang: String,
        token: String,
        id: String,
        serviceResponse: ServiceResponse<AdResponseData>
    ) {
        netWorkApi.getSingleAdDetails(cashValue, API_KEY, lang, buildVersion, userAgent, token, id)
            .enqueue(object : retrofit2.Callback<AdResponseData> {
                override fun onResponse(
                    call: Call<AdResponseData>,
                    response: Response<AdResponseData>
                ) {

                    var adData: AdResponseData? = null
                    if (response.code() == 200) {
                        adData = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            adData =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    AdResponseData::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    adData?.let { serviceResponse.successResponse(it, response.code()) }
                }

                override fun onFailure(call: Call<AdResponseData>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }

    fun getUserTransactions(
        token: String,
        lang: String,
        page: Int? = null,
        dateFrom: String? = null,
        dateTo: String? = null,
        serviceResponse: ServiceResponse<UserTransactionsModel>
    ) {
        netWorkApi.getUserTransactions(
            cashValue, API_KEY,
            lang,
            buildVersion,
            userAgent,
            token,
            page,
            dateFrom,
            dateTo
        )
            .enqueue(object : retrofit2.Callback<UserTransactionsModel> {
                override fun onResponse(
                    call: Call<UserTransactionsModel>,
                    response: Response<UserTransactionsModel>
                ) {

                    var userTransactionsModel: UserTransactionsModel? = null
                    if (response.code() == 200) {
                        userTransactionsModel = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            userTransactionsModel =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    UserTransactionsModel::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    userTransactionsModel?.let {
                        serviceResponse.successResponse(
                            it,
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<UserTransactionsModel>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }

    fun getUserInformation(
        token: String,
        lang: String,
        serviceResponse: ServiceResponse<UserInformationModel>
    ) {
        netWorkApi.getUserInformation(cashValue, API_KEY, lang, buildVersion, userAgent, token)
            .enqueue(object : retrofit2.Callback<UserInformationModel> {
                override fun onResponse(
                    call: Call<UserInformationModel>,
                    response: Response<UserInformationModel>
                ) {

                    var userInformationModel: UserInformationModel? = null
                    if (response.code() == 200) {
                        userInformationModel = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            userInformationModel =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    UserInformationModel::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    userInformationModel?.let {
                        serviceResponse.successResponse(
                            it,
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<UserInformationModel>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }

    fun getAdsType(
        token: String,
        lang: String,
        serviceResponse: ServiceResponse<AdsCategoriesModel>
    ) {
        netWorkApi.getAdsType(
            cashValue, API_KEY,
            lang,
            buildVersion,
            userAgent,
            token
        )
            .enqueue(object : retrofit2.Callback<AdsCategoriesModel> {
                override fun onResponse(
                    call: Call<AdsCategoriesModel>,
                    response: Response<AdsCategoriesModel>
                ) {

                    var adsCategoriesModel: AdsCategoriesModel? = null
                    if (response.code() == 200) {
                        adsCategoriesModel = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            adsCategoriesModel =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    AdsCategoriesModel::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    adsCategoriesModel?.let { serviceResponse.successResponse(it, response.code()) }
                }

                override fun onFailure(call: Call<AdsCategoriesModel>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }

    fun sendUserFeedback(
        lang: String,
        token: String,
        userFeedbackHashMap: HashMap<String, String>,
        serviceResponse: ServiceResponse<FeedbackModel>
    ) {
        netWorkApi.sendUserFeedback(
            cashValue, API_KEY,
            lang,
            buildVersion,
            userAgent,
            token,
            userFeedbackHashMap
        )
            .enqueue(object : retrofit2.Callback<FeedbackModel> {
                override fun onResponse(
                    call: Call<FeedbackModel>,
                    response: Response<FeedbackModel>
                ) {
                    var feedbackModel: FeedbackModel? = null
                    if (response.code() == 201) {
                        feedbackModel = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            feedbackModel =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    FeedbackModel::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    feedbackModel?.let {
                        serviceResponse.successResponse(
                            it,
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<FeedbackModel>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }

    /*fun makeTransaction(
        token: String,
        makeTransactionHashMap: HashMap<String, String>,
        serviceResponse: ServiceResponse<MakeTransactionResponse>
    ) {
        netWorkApi.makeTransaction(cashValue, API_KEY, token, makeTransactionHashMap)
            .enqueue(object : retrofit2.Callback<MakeTransactionResponse> {
                override fun onResponse(
                    call: Call<MakeTransactionResponse>,
                    response: Response<MakeTransactionResponse>
                ) {
                    var makeTransactionResponse: MakeTransactionResponse? = null
                    if (response.code() == 200) {
                        makeTransactionResponse = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            makeTransactionResponse =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    MakeTransactionResponse::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    makeTransactionResponse?.let {
                        serviceResponse.successResponse(
                            it,
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<MakeTransactionResponse>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }
*/
    fun getArakServices(
        token: String,
        lang: String,
        page: Int? = null,
        serviceResponse: ServiceResponse<ArakServicesModel>
    ) {
        netWorkApi.getArakServices(cashValue, API_KEY, lang, buildVersion, userAgent, token, page)
            .enqueue(object : retrofit2.Callback<ArakServicesModel> {
                override fun onResponse(
                    call: Call<ArakServicesModel>,
                    response: Response<ArakServicesModel>
                ) {
                    var arakServicesModel: ArakServicesModel? = null
                    if (response.code() == 200) {
                        arakServicesModel = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            arakServicesModel =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    ArakServicesModel::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    arakServicesModel?.let {
                        serviceResponse.successResponse(
                            it,
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<ArakServicesModel>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }

    fun requestWithDraw(
        token: String,
        lang: String,
        makeTransactionHashMap: HashMap<String, String>,
        serviceResponse: ServiceResponse<BaseResponse>
    ) {
        netWorkApi.requestWithDraw(
            cashValue, API_KEY,
            lang,
            buildVersion,
            userAgent,
            token,
            makeTransactionHashMap
        )
            .enqueue(object : retrofit2.Callback<BaseResponse> {
                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    var baseResponse: BaseResponse? = null
                    if (response.code() == 201) {
                        baseResponse = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            baseResponse =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    BaseResponse::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    baseResponse?.let {
                        serviceResponse.successResponse(
                            it,
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }

    fun getDigitalWallets(
        lang: String,
        token: String,
        serviceResponse: ServiceResponse<DigitalWalletsModel>
    ) {
        netWorkApi.getDigitalWallets(cashValue, API_KEY, lang, buildVersion, userAgent, token)
            .enqueue(object : retrofit2.Callback<DigitalWalletsModel> {
                override fun onResponse(
                    call: Call<DigitalWalletsModel>,
                    response: Response<DigitalWalletsModel>
                ) {
                    var digitalWalletsModel: DigitalWalletsModel? = null
                    if (response.code() == 200) {
                        digitalWalletsModel = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            digitalWalletsModel =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    DigitalWalletsModel::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    digitalWalletsModel?.let {
                        serviceResponse.successResponse(
                            it,
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<DigitalWalletsModel>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }

    fun getUserBalance(
        token: String,
        lang: String,
        serviceResponse: ServiceResponse<UserBalanceModel>
    ) {
        netWorkApi.getUserBalance(cashValue, API_KEY, lang, buildVersion, userAgent, token)
            .enqueue(object : retrofit2.Callback<UserBalanceModel> {
                override fun onResponse(
                    call: Call<UserBalanceModel>,
                    response: Response<UserBalanceModel>
                ) {
                    var userBalanceModel: UserBalanceModel? = null
                    if (response.code() == 200) {
                        userBalanceModel = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            userBalanceModel =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    UserBalanceModel::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    userBalanceModel?.let {
                        serviceResponse.successResponse(
                            it,
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<UserBalanceModel>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }

    fun getArakRanking(
        token: String,
        lang: String,
        page: Int,
        serviceResponse: ServiceResponse<UsersRankingModel>
    ) {
        netWorkApi.getArakRanking(cashValue, API_KEY, lang, buildVersion, userAgent, token, page)
            .enqueue(object : retrofit2.Callback<UsersRankingModel> {
                override fun onResponse(
                    call: Call<UsersRankingModel>,
                    response: Response<UsersRankingModel>
                ) {
                    var usersRankingModel: UsersRankingModel? = null
                    if (response.code() == 200) {
                        usersRankingModel = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            usersRankingModel =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    UsersRankingModel::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    usersRankingModel?.let {
                        serviceResponse.successResponse(
                            it,
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<UsersRankingModel>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }

    fun getAdReviews(
        token: String,
        lang: String,
        adId: String,
        serviceResponse: ServiceResponse<AdReviewsModel>
    ) {
        netWorkApi.getAdReviews(cashValue, API_KEY, lang, buildVersion, userAgent, token, adId)
            .enqueue(object : retrofit2.Callback<AdReviewsModel> {
                override fun onResponse(
                    call: Call<AdReviewsModel>,
                    response: Response<AdReviewsModel>
                ) {
                    var adReviewsModel: AdReviewsModel? = null
                    if (response.code() == 200) {
                        adReviewsModel = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            adReviewsModel =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    AdReviewsModel::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    adReviewsModel?.let {
                        serviceResponse.successResponse(
                            it,
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<AdReviewsModel>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }

    fun createAdReview(
        token: String,
        lang: String,
        addAdReviewRequest: AddAdReviewRequest,
        serviceResponse: ServiceResponse<CreateAdReviewModel>
    ) {
        netWorkApi.createAdReview(cashValue, API_KEY, lang, buildVersion, userAgent, token, addAdReviewRequest)
            .enqueue(object : retrofit2.Callback<CreateAdReviewModel> {
                override fun onResponse(
                    call: Call<CreateAdReviewModel>,
                    response: Response<CreateAdReviewModel>
                ) {
                    var createAdReviewModel: CreateAdReviewModel? = null
                    if (response.code() == 201) {
                        createAdReviewModel = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            createAdReviewModel =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    CreateAdReviewModel::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    createAdReviewModel?.let {
                        serviceResponse.successResponse(
                            it,
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<CreateAdReviewModel>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }

    fun deleteAdReview(
        token: String,
        lang: String,
        adReviewId: Int,
        serviceResponse: ServiceResponse<BaseResponse>
    ) {
        netWorkApi.deleteAdReview(cashValue, API_KEY, lang, buildVersion, userAgent, token, adReviewId)
            .enqueue(object : retrofit2.Callback<BaseResponse> {
                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    var baseResponse: BaseResponse? = null
                    if (response.code() == 200) {
                        baseResponse = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            baseResponse =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    BaseResponse::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    baseResponse?.let {
                        serviceResponse.successResponse(
                            it,
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }

    fun editUserInformation(
        token: String,
        lang: String,
        userId: Int,
        editUserInformationHashMap: HashMap<String, String>,
        serviceResponse: ServiceResponse<EditUserInformation>
    ) {
        netWorkApi.editUserInformation(
            cashValue, API_KEY,
            lang,
            buildVersion,
            userAgent,
            token,
            userId,
            editUserInformationHashMap
        )
            .enqueue(object : retrofit2.Callback<EditUserInformation> {
                override fun onResponse(
                    call: Call<EditUserInformation>,
                    response: Response<EditUserInformation>
                ) {
                    var editUserInformation: EditUserInformation? = null
                    if (response.code() == 200) {
                        editUserInformation = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            editUserInformation =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    EditUserInformation::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    editUserInformation?.let {
                        serviceResponse.successResponse(
                            it,
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<EditUserInformation>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }

    fun editUserImage(
        token: String,
        lang: String,
        userId: Int,
        editUserImageHashMap: HashMap<String, String>,
        serviceResponse: ServiceResponse<EditUserInformation>
    ) {
        netWorkApi.editUserImage(
            cashValue, API_KEY,
            lang,
            buildVersion,
            userAgent,
            token,
            userId,
            editUserImageHashMap
        )
            .enqueue(object : retrofit2.Callback<EditUserInformation> {
                override fun onResponse(
                    call: Call<EditUserInformation>,
                    response: Response<EditUserInformation>
                ) {
                    var editUserInformation: EditUserInformation? = null
                    if (response.code() == 200) {
                        editUserInformation = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            editUserInformation =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    EditUserInformation::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    editUserInformation?.let {
                        serviceResponse.successResponse(
                            it,
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<EditUserInformation>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }


    fun sendOTP(
        lang: String,
        otpRequest: OTPRequest,
        serviceResponse: ServiceResponse<BaseResponse>
    ) {
        netWorkApi.sendOTP(cashValue, API_KEY, lang, buildVersion, userAgent, otpRequest)
            .enqueue(object : retrofit2.Callback<BaseResponse> {
                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    var baseResponse: BaseResponse? = null
                    if (response.code() == 201) {
                        baseResponse = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            baseResponse =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    BaseResponse::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    baseResponse?.let {
                        serviceResponse.successResponse(
                            it,
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }

    fun forgetPassword(
        lang: String,
        forgetPasswordRequest: ForgetPasswordRequest,
        serviceResponse: ServiceResponse<BaseResponse>
    ) {
        netWorkApi.forgetPassword(cashValue, API_KEY, lang, buildVersion, userAgent, forgetPasswordRequest)
            .enqueue(object : retrofit2.Callback<BaseResponse> {
                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    var baseResponse: BaseResponse? = null
                    if (response.code() == 201) {
                        baseResponse = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            baseResponse =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    BaseResponse::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    baseResponse?.let {
                        serviceResponse.successResponse(
                            it,
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }

    /*fun requestPaymentMethod(
        token: String,
        lang: String,
        requestPaymentMethodHashMap: HashMap<String, String>,
        serviceResponse: ServiceResponse<CreateNewAdResponse>
    ) {
        netWorkApi.requestPaymentMethod(cashValue, API_KEY, lang, buildVersion, userAgent, token, requestPaymentMethodHashMap)
            .enqueue(object : retrofit2.Callback<CreateNewAdResponse> {
                override fun onResponse(
                    call: Call<CreateNewAdResponse>,
                    response: Response<CreateNewAdResponse>
                ) {
                    var createNewAdResponse: CreateNewAdResponse? = null
                    if (response.code() == 200) {
                        createNewAdResponse = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            createNewAdResponse =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    CreateNewAdResponse::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    createNewAdResponse?.let {
                        serviceResponse.successResponse(
                            it,
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<CreateNewAdResponse>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }*/

    fun favoriteAdAction(
        token: String,
        lang: String,
        adId: String,
        serviceResponse: ServiceResponse<BaseResponse>
    ) {
        netWorkApi.favoriteAdAction(cashValue, API_KEY, lang, buildVersion, userAgent, token, adId)
            .enqueue(object : retrofit2.Callback<BaseResponse> {
                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    var baseResponse: BaseResponse? = null
                    if (response.code() == 201) {
                        baseResponse = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            baseResponse =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    BaseResponse::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    baseResponse?.let {
                        serviceResponse.successResponse(
                            it,
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }

    /*fun search(
        lang: String,
        token: String,
        url: String,
        serviceResponse: ServiceResponse<HomeAdsModel>
    ) {
        netWorkApi.search(cashValue, API_KEY, lang, token, url)
            .enqueue(object : retrofit2.Callback<HomeAdsModel> {
                override fun onResponse(
                    call: Call<HomeAdsModel>,
                    response: Response<HomeAdsModel>
                ) {
                    var homeAdsModel: HomeAdsModel? = null
                    if (response.code() == 200) {
                        homeAdsModel = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            homeAdsModel =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    HomeAdsModel::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    homeAdsModel?.let {
                        serviceResponse.successResponse(
                            it,
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<HomeAdsModel>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }*/

    fun requestArakService(
        token: String,
        lang: String,
        requestArakServiceRequestBody: RequestArakServiceRequestBody,
        serviceResponse: ServiceResponse<BaseResponse>
    ) {
        netWorkApi.requestArakService(
            cashValue, API_KEY,
            lang,
            buildVersion,
            userAgent,
            token,
            requestArakServiceRequestBody
        )
            .enqueue(object : retrofit2.Callback<BaseResponse> {
                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    var baseResponse: BaseResponse? = null
                    if (response.code() == 201) {
                        baseResponse = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            baseResponse =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    BaseResponse::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    baseResponse?.let {
                        serviceResponse.successResponse(
                            it,
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }

    fun getMyAds(
        token: String,
        lang: String,
        dateFrom: String? = null,
        dateTo: String? = null,
        page: Int? = null,
        serviceResponse: ServiceResponse<HomeAdsModel>
    ) {
        netWorkApi.getMyAds(cashValue, API_KEY, lang, buildVersion, userAgent, token, dateFrom, dateTo, page)
            .enqueue(object : retrofit2.Callback<HomeAdsModel> {
                override fun onResponse(
                    call: Call<HomeAdsModel>,
                    response: Response<HomeAdsModel>
                ) {
                    var homeAdsModel: HomeAdsModel? = null
                    if (response.code() == 200) {
                        homeAdsModel = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            homeAdsModel =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    HomeAdsModel::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    homeAdsModel?.let {
                        serviceResponse.successResponse(
                            it,
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<HomeAdsModel>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }

    fun getUserHistory(
        token: String,
        lang: String,
        page: Int,
        serviceResponse: ServiceResponse<HomeAdsModel>
    ) {
        netWorkApi.getUserHistory(
            cashValue, API_KEY,
            lang,
            buildVersion,
            userAgent,
            token,
            page
        )
            .enqueue(object : retrofit2.Callback<HomeAdsModel> {
                override fun onResponse(
                    call: Call<HomeAdsModel>,
                    response: Response<HomeAdsModel>
                ) {
                    var homeAdsModel: HomeAdsModel? = null
                    if (response.code() == 200) {
                        homeAdsModel = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            homeAdsModel =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    HomeAdsModel::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    homeAdsModel?.let {
                        serviceResponse.successResponse(
                            it,
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<HomeAdsModel>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }

    fun getUserFavorites(
        token: String,
        lang: String,
        page: Int,
        serviceResponse: ServiceResponse<HomeAdsModel>
    ) {
        netWorkApi.getUserFavorites(
            cashValue, API_KEY,
            lang,
            buildVersion,
            userAgent,
            token,
            page
        )
            .enqueue(object : retrofit2.Callback<HomeAdsModel> {
                override fun onResponse(
                    call: Call<HomeAdsModel>,
                    response: Response<HomeAdsModel>
                ) {
                    var homeAdsModel: HomeAdsModel? = null
                    if (response.code() == 200) {
                        homeAdsModel = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            homeAdsModel =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    HomeAdsModel::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    homeAdsModel?.let {
                        serviceResponse.successResponse(
                            it,
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<HomeAdsModel>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }

    fun resetPhoneNumber(
        token: String,
        lang: String,
        userId: Int,
        resetPhoneNumberHashMap: HashMap<String, String?>,
        serviceResponse: ServiceResponse<BaseResponse>
    ) {
        netWorkApi.resetPhoneNumber(
            cashValue, API_KEY,
            lang,
            buildVersion,
            userAgent,
            token,
            userId,
            resetPhoneNumberHashMap
        )
            .enqueue(object : retrofit2.Callback<BaseResponse> {
                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    var baseResponse: BaseResponse? = null
                    if (response.code() == 200) {
                        baseResponse = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            baseResponse =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    BaseResponse::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    baseResponse?.let {
                        serviceResponse.successResponse(
                            it,
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }

    fun resetPassword(
        token: String,
        lang: String,
        userId: Int,
        resetPasswordHashMap: HashMap<String, String>,
        serviceResponse: ServiceResponse<BaseResponse>
    ) {
        netWorkApi.resetPassword(
            cashValue, API_KEY,
            lang,
            buildVersion,
            userAgent,
            token,
            userId,
            resetPasswordHashMap
        )
            .enqueue(object : retrofit2.Callback<BaseResponse> {
                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    var baseResponse: BaseResponse? = null
                    if (response.code() == 200) {
                        baseResponse = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            baseResponse =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    BaseResponse::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    baseResponse?.let {
                        serviceResponse.successResponse(
                            it,
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }

    fun consumeCoupon(
        token: String,
        lang: String,
        consumeCouponHashMap: HashMap<String, String>,
        serviceResponse: ServiceResponse<BaseResponse>
    ) {
        netWorkApi.consumeCoupon(

            cashValue, API_KEY,
            lang,
            buildVersion,
            userAgent,
            token,
            consumeCouponHashMap
        )
            .enqueue(object : retrofit2.Callback<BaseResponse> {
                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    var baseResponse: BaseResponse? = null
                    if (response.code() == 201) {
                        baseResponse = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            baseResponse =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    BaseResponse::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    baseResponse?.let {
                        serviceResponse.successResponse(
                            it,
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }

    /* fun getUserFilteredTransaction(
         token: String,
         url: String,
         serviceResponse: ServiceResponse<UserTransactionsResponse>
     ) {
         netWorkApi.getUserFilteredTransaction(cashValue, API_KEY, token, url)
             .enqueue(object : retrofit2.Callback<UserTransactionsResponse> {
                 override fun onResponse(
                     call: Call<UserTransactionsResponse>,
                     response: Response<UserTransactionsResponse>
                 ) {
                     var userTransactionsResponse: UserTransactionsResponse? = null
                     if (response.code() == 200) {
                         userTransactionsResponse = response.body()
                     } else {
                         try {
                             val gson = Gson()
                             val jsonObject = JSONObject(response.errorBody()!!.string())
                             userTransactionsResponse =
                                 gson.fromJson(
                                     jsonObject.toString(),
                                     UserTransactionsResponse::class.java
                                 )
                         } catch (e: Exception) {
                             Log.d(TAG, "onResponse: Exception: " + e.message)
                         }
                     }
                     userTransactionsResponse?.let {
                         serviceResponse.successResponse(
                             it,
                             response.code()
                         )
                     }
                 }

                 override fun onFailure(call: Call<UserTransactionsResponse>, t: Throwable) {
                     serviceResponse.ErrorResponse(t)
                 }
             })
     }*/

    /* fun getMyFilteredAds(
         token: String,
         url: String,
         serviceResponse: ServiceResponse<HomeAdsModel>
     ) {
         netWorkApi.getMyFilteredAds(cashValue, API_KEY, token, url)
             .enqueue(object : retrofit2.Callback<HomeAdsModel> {
                 override fun onResponse(
                     call: Call<HomeAdsModel>,
                     response: Response<HomeAdsModel>
                 ) {
                     var homeAdsModel: HomeAdsModel? = null
                     if (response.code() == 200) {
                         homeAdsModel = response.body()
                     } else {
                         try {
                             val gson = Gson()
                             val jsonObject = JSONObject(response.errorBody()!!.string())
                             homeAdsModel =
                                 gson.fromJson(
                                     jsonObject.toString(),
                                     HomeAdsModel::class.java
                                 )
                         } catch (e: Exception) {
                             Log.d(TAG, "onResponse: Exception: " + e.message)
                         }
                     }
                     homeAdsModel?.let {
                         serviceResponse.successResponse(
                             it,
                             response.code()
                         )
                     }
                 }

                 override fun onFailure(call: Call<HomeAdsModel>, t: Throwable) {
                     serviceResponse.ErrorResponse(t)
                 }
             })
     }*/

    fun getNotifications(
        token: String,
        lang: String,
        page: Int? = null,
        serviceResponse: ServiceResponse<NotificationsModel>
    ) {
        netWorkApi.getNotifications(cashValue, API_KEY, lang, buildVersion, userAgent, token, page)
            .enqueue(object : retrofit2.Callback<NotificationsModel> {
                override fun onResponse(
                    call: Call<NotificationsModel>,
                    response: Response<NotificationsModel>
                ) {
                    var notificationsModel: NotificationsModel? = null
                    if (response.code() == 200) {
                        notificationsModel = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            notificationsModel =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    NotificationsModel::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    notificationsModel?.let {
                        serviceResponse.successResponse(
                            it,
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<NotificationsModel>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }

    fun getAboutInformation(
        token: String,
        lang: String,
        serviceResponse: ServiceResponse<AboutModel>
    ) {
        netWorkApi.getAboutInformation(cashValue, API_KEY, lang, buildVersion, userAgent, token)
            .enqueue(object : retrofit2.Callback<AboutModel> {
                override fun onResponse(
                    call: Call<AboutModel>,
                    response: Response<AboutModel>
                ) {
                    var aboutModel: AboutModel? = null
                    if (response.code() == 200) {
                        aboutModel = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            aboutModel =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    AboutModel::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    aboutModel?.let {
                        serviceResponse.successResponse(
                            it,
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<AboutModel>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }

    /*fun changeNotificationsStatus(
        token: String,
        serviceResponse: ServiceResponse<NotificationsStatusResponse>
    ) {
        netWorkApi.changeNotificationsStatus(cashValue, API_KEY, token)
            .enqueue(object : retrofit2.Callback<NotificationsStatusResponse> {
                override fun onResponse(
                    call: Call<NotificationsStatusResponse>,
                    response: Response<NotificationsStatusResponse>
                ) {
                    var notificationsStatusResponse: NotificationsStatusResponse? = null
                    if (response.code() == 200) {
                        notificationsStatusResponse = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            notificationsStatusResponse =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    NotificationsStatusResponse::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    notificationsStatusResponse?.let {
                        serviceResponse.successResponse(
                            it,
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<NotificationsStatusResponse>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }*/


    /* fun getNotificationsStatus(
         token: String,
         serviceResponse: ServiceResponse<NotificationsStatusResponse>
     ) {
         netWorkApi.getNotificationsStatus(cashValue, API_KEY, token)
             .enqueue(object : retrofit2.Callback<NotificationsStatusResponse> {
                 override fun onResponse(
                     call: Call<NotificationsStatusResponse>,
                     response: Response<NotificationsStatusResponse>
                 ) {
                     var notificationsStatusResponse: NotificationsStatusResponse? = null
                     if (response.code() == 200) {
                         notificationsStatusResponse = response.body()
                     } else {
                         try {
                             val gson = Gson()
                             val jsonObject = JSONObject(response.errorBody()!!.string())
                             notificationsStatusResponse =
                                 gson.fromJson(
                                     jsonObject.toString(),
                                     NotificationsStatusResponse::class.java
                                 )
                         } catch (e: Exception) {
                             Log.d(TAG, "onResponse: Exception: " + e.message)
                         }
                     }
                     notificationsStatusResponse?.let {
                         serviceResponse.successResponse(
                             it,
                             response.code()
                         )
                     }
                 }

                 override fun onFailure(call: Call<NotificationsStatusResponse>, t: Throwable) {
                     serviceResponse.ErrorResponse(t)
                 }
             })
     }*/

    /*fun updateFCMToken(
        token: String,
        serviceResponse: ServiceResponse<BaseResponse>
    ) {
        netWorkApi.updateFCMToken(cashValue, API_KEY, token)
            .enqueue(object : retrofit2.Callback<BaseResponse> {
                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    var baseResponse: BaseResponse? = null
                    if (response.code() == 200) {
                        baseResponse = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            baseResponse =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    BaseResponse::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    baseResponse?.let {
                        serviceResponse.successResponse(
                            it,
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }*/

    /*fun getCustomPackagesDropdownLists(
        token: String,
        serviceResponse: ServiceResponse<CustomPackageData>
    ) {
        netWorkApi.getCustomPackagesDropdownLists(cashValue, API_KEY, token)
            .enqueue(object : retrofit2.Callback<CustomPackageData> {
                override fun onResponse(
                    call: Call<CustomPackageData>,
                    response: Response<CustomPackageData>
                ) {
                    var customPackageData: CustomPackageData? = null
                    if (response.code() == 200) {
                        customPackageData = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            customPackageData =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    CustomPackageData::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    customPackageData?.let {
                        serviceResponse.successResponse(
                            it,
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<CustomPackageData>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }*/

    /*fun createCustomPackage(
        token: String,
        createCustomPackageHashMap: HashMap<String?, String?>,
        serviceResponse: ServiceResponse<CreateCustomPackageData>
    ) {
        netWorkApi.createCustomPackage(cashValue, API_KEY, token, createCustomPackageHashMap)
            .enqueue(object : retrofit2.Callback<CreateCustomPackageData> {
                override fun onResponse(
                    call: Call<CreateCustomPackageData>,
                    response: Response<CreateCustomPackageData>
                ) {
                    var createCustomPackageData: CreateCustomPackageData? = null
                    if (response.code() == 200) {
                        createCustomPackageData = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            createCustomPackageData =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    CreateCustomPackageData::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    createCustomPackageData?.let {
                        serviceResponse.successResponse(
                            it,
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<CreateCustomPackageData>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }*/

    fun getStoresList(
        lang: String,
        token: String,
        page: Int,
        random: Boolean? = null,
        name: String? = null,
        phoneNumber: String? = null,
        isFeatured: Boolean? = null,
        storeCategoryId: Int? = null,
        hasVisa: Boolean? = null,
        hasMastercard: Boolean? = null,
        hasPaypal: Boolean? = null,
        hasCash: Boolean? = null,
        serviceResponse: ServiceResponse<StoresListModel>
    ) {
        netWorkApi.getStoresList(
            cashValue, API_KEY,
            lang,
            buildVersion,
            userAgent,
            token,
            page,
            random,
            name,
            phoneNumber,
            isFeatured,
            storeCategoryId,
            hasVisa,
            hasMastercard,
            hasPaypal,
            hasCash
        )
            .enqueue(object : retrofit2.Callback<StoresListModel> {
                override fun onResponse(
                    call: Call<StoresListModel>,
                    response: Response<StoresListModel>
                ) {
                    var storesListModel: StoresListModel? = null
                    if (response.code() == 200) {
                        storesListModel = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            storesListModel =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    StoresListModel::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    storesListModel?.let {
                        serviceResponse.successResponse(
                            it,
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<StoresListModel>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }

    /*fun filterStoresByCategory(
        token: String,
        url: String,
        serviceResponse: ServiceResponse<StoreFilterModel>
    ) {
        netWorkApi.filterStoresByCategory(cashValue, API_KEY, token, url)
            .enqueue(object : retrofit2.Callback<StoreFilterModel> {
                override fun onResponse(
                    call: Call<StoreFilterModel>,
                    response: Response<StoreFilterModel>
                ) {
                    var storeFilterModel: StoreFilterModel? = null
                    if (response.code() == 200) {
                        storeFilterModel = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            storeFilterModel =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    StoreFilterModel::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    storeFilterModel?.let {
                        serviceResponse.successResponse(
                            it,
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<StoreFilterModel>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }*/

    /* fun searchStores(
         token: String,
         url: String,
         serviceResponse: ServiceResponse<StoreFilterModel>
     ) {
         netWorkApi.searchStores(cashValue, API_KEY, token, url)
             .enqueue(object : retrofit2.Callback<StoreFilterModel> {
                 override fun onResponse(
                     call: Call<StoreFilterModel>,
                     response: Response<StoreFilterModel>
                 ) {
                     var storeFilterModel: StoreFilterModel? = null
                     if (response.code() == 200) {
                         storeFilterModel = response.body()
                     } else {
                         try {
                             val gson = Gson()
                             val jsonObject = JSONObject(response.errorBody()!!.string())
                             storeFilterModel =
                                 gson.fromJson(
                                     jsonObject.toString(),
                                     StoreFilterModel::class.java
                                 )
                         } catch (e: Exception) {
                             Log.d(TAG, "onResponse: Exception: " + e.message)
                         }
                     }
                     storeFilterModel?.let {
                         serviceResponse.successResponse(
                             it,
                             response.code()
                         )
                     }
                 }

                 override fun onFailure(call: Call<StoreFilterModel>, t: Throwable) {
                     serviceResponse.ErrorResponse(t)
                 }
             })
     }*/

    fun getSingleStore(
        token: String,
        lang: String,
        id: Int,
        serviceResponse: ServiceResponse<SingleStoreModel>
    ) {
        netWorkApi.getSingleStore(cashValue, API_KEY, lang, buildVersion, userAgent, token, id)
            .enqueue(object : retrofit2.Callback<SingleStoreModel> {
                override fun onResponse(
                    call: Call<SingleStoreModel>,
                    response: Response<SingleStoreModel>
                ) {
                    var singleStoreModel: SingleStoreModel? = null
                    if (response.code() == 200) {
                        singleStoreModel = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            singleStoreModel =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    SingleStoreModel::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    singleStoreModel?.let {
                        serviceResponse.successResponse(
                            it,
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<SingleStoreModel>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }

    fun getStoreProducts(
        token: String,
        lang: String,
        page: Int,
        random: Boolean? = null,
        storeId: Int? = null,
        storeCategoryId: Int? = null,
        serviceResponse: ServiceResponse<StoreProductsModel>
    ) {
        netWorkApi.getStoreProducts(
            cashValue, API_KEY,
            lang,
            buildVersion,
            userAgent,
            token,
            page,
            random,
            storeId,
            storeCategoryId
        )
            .enqueue(object : retrofit2.Callback<StoreProductsModel> {
                override fun onResponse(
                    call: Call<StoreProductsModel>,
                    response: Response<StoreProductsModel>
                ) {
                    var storeProductsModel: StoreProductsModel? = null
                    if (response.code() == 200) {
                        storeProductsModel = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            storeProductsModel =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    StoreProductsModel::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    storeProductsModel?.let {
                        serviceResponse.successResponse(
                            it,
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<StoreProductsModel>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }

    fun getMyStoreProducts(
        token: String,
        lang: String,
        page: Int,
        serviceResponse: ServiceResponse<MyStoreProductsModel>
    ) {
        netWorkApi.getMyStoreProducts(cashValue, API_KEY, lang, buildVersion, userAgent, token, page)
            .enqueue(object : retrofit2.Callback<MyStoreProductsModel> {
                override fun onResponse(
                    call: Call<MyStoreProductsModel>,
                    response: Response<MyStoreProductsModel>
                ) {
                    var myStoreProductsModel: MyStoreProductsModel? = null
                    if (response.code() == 200) {
                        myStoreProductsModel = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            myStoreProductsModel =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    MyStoreProductsModel::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    myStoreProductsModel?.let {
                        serviceResponse.successResponse(
                            it,
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<MyStoreProductsModel>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }

    fun deleteProduct(
        token: String,
        lang: String,
        id: Int,
        serviceResponse: ServiceResponse<BaseResponse>
    ) {
        netWorkApi.deleteProduct(cashValue, API_KEY, lang, buildVersion, userAgent, token, id)
            .enqueue(object : retrofit2.Callback<BaseResponse> {
                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    var baseResponse: BaseResponse? = null
                    if (response.code() == 200) {
                        baseResponse = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            baseResponse =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    BaseResponse::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    baseResponse?.let {
                        serviceResponse.successResponse(
                            it,
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }

    fun createStore(
        token: String,
        lang: String,
        createStoreRequestBody: CreateStoreRequestBody,
        serviceResponse: ServiceResponse<CreateStoreModel>
    ) {
        netWorkApi.createStore(
            cashValue, API_KEY,
            lang,
            buildVersion,
            userAgent,
            token,
            createStoreRequestBody
        )
            .enqueue(object : retrofit2.Callback<CreateStoreModel> {
                override fun onResponse(
                    call: Call<CreateStoreModel>,
                    response: Response<CreateStoreModel>
                ) {
                    var createStoreModel: CreateStoreModel? = null
                    if (response.code() == 201) {
                        createStoreModel = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            createStoreModel =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    CreateStoreModel::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    createStoreModel?.let {
                        serviceResponse.successResponse(
                            it,
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<CreateStoreModel>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }

    fun addStoreReview(
        token: String,
        lang: String,
        addStoreReviewRequest: AddStoreReviewRequest,
        serviceResponse: ServiceResponse<CreateReviewModel>
    ) {
        netWorkApi.addStoreReview(
            cashValue, API_KEY,
            lang,
            buildVersion,
            userAgent,
            token,
            addStoreReviewRequest
        )
            .enqueue(object : retrofit2.Callback<CreateReviewModel> {
                override fun onResponse(
                    call: Call<CreateReviewModel>,
                    response: Response<CreateReviewModel>
                ) {
                    var createReviewModel: CreateReviewModel? = null
                    if (response.code() == 201) {
                        createReviewModel = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            createReviewModel =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    CreateReviewModel::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    createReviewModel?.let {
                        serviceResponse.successResponse(
                            it,
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<CreateReviewModel>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }

    fun getStoreProduct(
        token: String,
        lang: String,
        isRandomProducts: Boolean? = null,
        serviceResponse: ServiceResponse<StoreProductsDataModel>
    ) {
        netWorkApi.getStoreProduct(cashValue, API_KEY, lang, buildVersion, userAgent,token, isRandomProducts)
            .enqueue(object : retrofit2.Callback<StoreProductsDataModel> {
                override fun onResponse(
                    call: Call<StoreProductsDataModel>,
                    response: Response<StoreProductsDataModel>
                ) {
                    var storeProductsDataModel: StoreProductsDataModel? = null
                    if (response.code() == 200) {
                        storeProductsDataModel = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            storeProductsDataModel =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    StoreProductsDataModel::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    storeProductsDataModel?.let {
                        serviceResponse.successResponse(
                            it,
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<StoreProductsDataModel>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }

    fun getSingleProduct(
        token: String,
        lang: String,
        id: String,
        serviceResponse: ServiceResponse<SingleProductModel>
    ) {
        netWorkApi.getSingleProduct(cashValue, API_KEY, lang, buildVersion, userAgent, token, id)
            .enqueue(object : retrofit2.Callback<SingleProductModel> {
                override fun onResponse(
                    call: Call<SingleProductModel>,
                    response: Response<SingleProductModel>
                ) {
                    var singleProductModel: SingleProductModel? = null
                    if (response.code() == 200) {
                        singleProductModel = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            singleProductModel =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    SingleProductModel::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    singleProductModel?.let {
                        serviceResponse.successResponse(
                            it,
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<SingleProductModel>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }

    fun getStoreCategories(
        token: String,
        lang: String,
        serviceResponse: ServiceResponse<StoreCategoriesModel>
    ) {
        netWorkApi.getStoreCategories(cashValue, API_KEY, lang, buildVersion, userAgent, token)
            .enqueue(object : retrofit2.Callback<StoreCategoriesModel> {
                override fun onResponse(
                    call: Call<StoreCategoriesModel>,
                    response: Response<StoreCategoriesModel>
                ) {
                    var storeCategoriesModel: StoreCategoriesModel? = null
                    if (response.code() == 200) {
                        storeCategoriesModel = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            storeCategoriesModel =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    StoreCategoriesModel::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    storeCategoriesModel?.let {
                        serviceResponse.successResponse(
                            it,
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<StoreCategoriesModel>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }

    fun deleteAd(
        token: String,
        lang: String,
        id: Int,
        serviceResponse: ServiceResponse<BaseResponse>
    ) {
        netWorkApi.deleteAd(cashValue, API_KEY, lang, buildVersion, userAgent, token, id)
            .enqueue(object : retrofit2.Callback<BaseResponse> {
                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    var baseResponse: BaseResponse? = null
                    if (response.code() == 200) {
                        baseResponse = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            baseResponse =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    BaseResponse::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    baseResponse?.let {
                        serviceResponse.successResponse(
                            it,
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }

    /*fun getMyStore(
        token: String,
        serviceResponse: ServiceResponse<SingleStoreModel>
    ) {
        netWorkApi.getMyStore(cashValue, API_KEY, token)
            .enqueue(object : retrofit2.Callback<SingleStoreModel> {
                override fun onResponse(
                    call: Call<SingleStoreModel>,
                    response: Response<SingleStoreModel>
                ) {
                    var singleStoreModel: SingleStoreModel? = null
                    if (response.code() == 200) {
                        singleStoreModel = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            singleStoreModel =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    SingleStoreModel::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    singleStoreModel?.let {
                        serviceResponse.successResponse(
                            it,
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<SingleStoreModel>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }*/

    fun createProduct(
        token: String,
        lang: String,
        createProductRequestBody: CreateProductRequestBody,
        serviceResponse: ServiceResponse<SingleProductModel>
    ) {
        netWorkApi.createStoreProduct(
            cashValue, API_KEY,
            lang,
            buildVersion,
            userAgent,
            token,
            createProductRequestBody
        )
            .enqueue(object : retrofit2.Callback<SingleProductModel> {
                override fun onResponse(
                    call: Call<SingleProductModel>,
                    response: Response<SingleProductModel>
                ) {
                    var singleProductModel: SingleProductModel? = null
                    if (response.code() == 201) {
                        singleProductModel = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            singleProductModel =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    SingleProductModel::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    singleProductModel?.let {
                        serviceResponse.successResponse(
                            it,
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<SingleProductModel>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }

    fun updateProduct(
        token: String,
        lang: String,
        id: Int,
        createProductRequestBody: CreateProductRequestBody,
        serviceResponse: ServiceResponse<BaseResponse>
    ) {
        netWorkApi.updateProduct(
            cashValue, API_KEY,
            lang,
            buildVersion,
            userAgent,
            token,
            id,
            createProductRequestBody
        )
            .enqueue(object : retrofit2.Callback<BaseResponse> {
                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    var baseResponse: BaseResponse? = null
                    if (response.code() == 200) {
                        baseResponse = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            baseResponse =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    BaseResponse::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    baseResponse?.let {
                        serviceResponse.successResponse(
                            it,
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }

    /*fun updateProduct(
        token: String,
        id: String,
        updateProductHashMap: HashMap<String, Any>,
        serviceResponse: ServiceResponse<BaseResponse>
    ) {
        netWorkApi.updateProduct(cashValue, API_KEY, token, id, updateProductHashMap)
            .enqueue(object : retrofit2.Callback<BaseResponse> {
                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    var baseResponse: BaseResponse? = null
                    if (response.code() == 200) {
                        baseResponse = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            baseResponse =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    BaseResponse::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    baseResponse?.let {
                        serviceResponse.successResponse(
                            it,
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }*/

    fun deleteStoreReview(
        token: String,
        lang: String,
        id: Int,
        serviceResponse: ServiceResponse<BaseResponse>
    ) {
        netWorkApi.deleteStoreReview(cashValue, API_KEY, lang, buildVersion, userAgent, token, id)
            .enqueue(object : retrofit2.Callback<BaseResponse> {
                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    var baseResponse: BaseResponse? = null
                    if (response.code() == 200) {
                        baseResponse = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            baseResponse =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    BaseResponse::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    baseResponse?.let {
                        serviceResponse.successResponse(
                            it,
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }

    fun addProductReview(
        token: String,
        lang: String,
        addStoreProductReviewRequest: AddStoreProductReviewRequest,
        serviceResponse: ServiceResponse<CreateReviewModel>
    ) {
        netWorkApi.addProductReview(
            cashValue, API_KEY,
            lang,
            buildVersion,
            userAgent,
            token,
            addStoreProductReviewRequest
        )
            .enqueue(object : retrofit2.Callback<CreateReviewModel> {
                override fun onResponse(
                    call: Call<CreateReviewModel>,
                    response: Response<CreateReviewModel>
                ) {
                    var createReviewModel: CreateReviewModel? = null
                    if (response.code() == 201) {
                        createReviewModel = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            createReviewModel =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    CreateReviewModel::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    createReviewModel?.let {
                        serviceResponse.successResponse(
                            it,
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<CreateReviewModel>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }


    fun getRelatedStoreProducts(
        token: String,
        lang: String,
        id: String,
        serviceResponse: ServiceResponse<RelatedStoreProductsModel>
    ) {
        netWorkApi.getRelatedStoreProducts(cashValue, API_KEY, lang, buildVersion, userAgent, token, id)
            .enqueue(object : retrofit2.Callback<RelatedStoreProductsModel> {
                override fun onResponse(
                    call: Call<RelatedStoreProductsModel>,
                    response: Response<RelatedStoreProductsModel>
                ) {
                    var relatedStoreProductsModel: RelatedStoreProductsModel? = null
                    if (response.code() == 200) {
                        relatedStoreProductsModel = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            relatedStoreProductsModel =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    RelatedStoreProductsModel::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    relatedStoreProductsModel?.let {
                        serviceResponse.successResponse(
                            it,
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<RelatedStoreProductsModel>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }

    /*fun getMyProducts(
        token: String,
        url: String,
        serviceResponse: ServiceResponse<StoreProductsModel>
    ) {
        netWorkApi.getMyProducts(cashValue, API_KEY, token, url)
            .enqueue(object : retrofit2.Callback<StoreProductsModel> {
                override fun onResponse(
                    call: Call<StoreProductsModel>,
                    response: Response<StoreProductsModel>
                ) {
                    var storeProductsModel: StoreProductsModel? = null
                    if (response.code() == 200) {
                        storeProductsModel = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            storeProductsModel =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    StoreProductsModel::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    storeProductsModel?.let {
                        serviceResponse.successResponse(
                            it,
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<StoreProductsModel>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }*/

    /*fun deleteProduct(
        token: String,
        id: String,
        serviceResponse: ServiceResponse<BaseResponse>
    ) {
        netWorkApi.deleteProduct(cashValue, API_KEY, token, id)
            .enqueue(object : retrofit2.Callback<BaseResponse> {
                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    var baseResponse: BaseResponse? = null
                    if (response.code() == 200) {
                        baseResponse = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            baseResponse =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    BaseResponse::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    baseResponse?.let {
                        serviceResponse.successResponse(
                            it,
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }*/

    fun deleteProductReview(
        token: String,
        lang: String,
        id: Int,
        serviceResponse: ServiceResponse<BaseResponse>
    ) {
        netWorkApi.deleteProductReview(cashValue, API_KEY, lang, buildVersion, userAgent, token, id)
            .enqueue(object : retrofit2.Callback<BaseResponse> {
                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    var baseResponse: BaseResponse? = null
                    if (response.code() == 200) {
                        baseResponse = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            baseResponse =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    BaseResponse::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    baseResponse?.let {
                        serviceResponse.successResponse(
                            it,
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }

    fun getMyStore(
        token: String,
        lang: String,
        serviceResponse: ServiceResponse<SingleStoreModel>
    ) {
        netWorkApi.getMyStore(cashValue, API_KEY, lang, buildVersion, userAgent, token)
            .enqueue(object : retrofit2.Callback<SingleStoreModel> {
                override fun onResponse(
                    call: Call<SingleStoreModel>,
                    response: Response<SingleStoreModel>
                ) {
                    var singleStoreModel: SingleStoreModel? = null
                    if (response.code() == 200) {
                        singleStoreModel = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            singleStoreModel =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    SingleStoreModel::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    singleStoreModel?.let {
                        serviceResponse.successResponse(
                            it,
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<SingleStoreModel>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }

    fun getElectedPeople(
        token: String,
        lang: String,
        governorateId: Int? = null,
        serviceResponse: ServiceResponse<ElectedPeopleModel>
    ) {
        netWorkApi.getElectedPeople(cashValue, API_KEY, lang, buildVersion, userAgent, token, governorateId)
            .enqueue(object : retrofit2.Callback<ElectedPeopleModel> {
                override fun onResponse(
                    call: Call<ElectedPeopleModel>,
                    response: Response<ElectedPeopleModel>
                ) {
                    var electedPeopleModel: ElectedPeopleModel? = null
                    if (response.code() == 200) {
                        electedPeopleModel = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            electedPeopleModel =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    ElectedPeopleModel::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    electedPeopleModel?.let {
                        serviceResponse.successResponse(
                            it,
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<ElectedPeopleModel>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }

    fun getSingleElectedPerson(
        token: String,
        lang: String,
        id: Int,
        serviceResponse: ServiceResponse<SingleElectedPersonModel>
    ) {
        netWorkApi.getSingleElectedPerson(cashValue, API_KEY, lang, buildVersion, userAgent, token, id)
            .enqueue(object : retrofit2.Callback<SingleElectedPersonModel> {
                override fun onResponse(
                    call: Call<SingleElectedPersonModel>,
                    response: Response<SingleElectedPersonModel>
                ) {
                    var singleElectedPersonModel: SingleElectedPersonModel? = null
                    if (response.code() == 200) {
                        singleElectedPersonModel = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            singleElectedPersonModel =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    SingleElectedPersonModel::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    singleElectedPersonModel?.let {
                        serviceResponse.successResponse(
                            it,
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<SingleElectedPersonModel>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }

    fun getGovernorates(
        token: String,
        lang: String,
        serviceResponse: ServiceResponse<GovernoratesModel>
    ) {
        netWorkApi.getGovernorates(cashValue, API_KEY, lang, buildVersion, userAgent, token)
            .enqueue(object : retrofit2.Callback<GovernoratesModel> {
                override fun onResponse(
                    call: Call<GovernoratesModel>,
                    response: Response<GovernoratesModel>
                ) {
                    var governoratesModel: GovernoratesModel? = null
                    if (response.code() == 200) {
                        governoratesModel = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            governoratesModel =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    GovernoratesModel::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    governoratesModel?.let {
                        serviceResponse.successResponse(
                            it,
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<GovernoratesModel>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }

    fun createCliqPayment(
        token: String,
        lang: String,
        createCliqPaymentRequest: CreateCliqPaymentRequest,
        serviceResponse: ServiceResponse<BaseResponse>
    ) {
        netWorkApi.createCliqPayment(
            cashValue, API_KEY,
            lang,
            buildVersion,
            userAgent,
            token,
            createCliqPaymentRequest
        )
            .enqueue(object : retrofit2.Callback<BaseResponse> {
                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    var baseResponse: BaseResponse? = null
                    if (response.code() == 201) {
                        baseResponse = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            baseResponse =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    BaseResponse::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    baseResponse?.let {
                        serviceResponse.successResponse(
                            it,
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }


    fun getArakStoreCategory(
        token: String,
        lang: String,
        serviceResponse: DataRepository.ServiceResponse<StoreCategory>
    ) {
        netWorkApi.getArakStoreCategory(cashValue, API_KEY, lang, buildVersion, userAgent, token)
            .enqueue(object : retrofit2.Callback<StoreCategory> {
                override fun onResponse(
                    call: Call<StoreCategory>,
                    response: Response<StoreCategory>
                ) {
                    handleResponse(response, StoreCategory::class.java, serviceResponse)
                }

                override fun onFailure(call: Call<StoreCategory>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }

    fun getArakStoreProduct(
        token: String,
        lang: String,
        category: Int? = null,
        page: Int? = null,
        perPage: Int? = null,
        serviceResponse: DataRepository.ServiceResponse<StoreProduct>
    ) {
        val call =
            netWorkApi.getArakStoreProduct(
                cashValue, API_KEY,
                lang,
                buildVersion,
                userAgent,
                token,
                category,
                page,
                perPage
            )


        call.enqueue(object : retrofit2.Callback<StoreProduct> {
            override fun onResponse(
                call: Call<StoreProduct>,
                response: Response<StoreProduct>
            ) {
                handleResponse(response, StoreProduct::class.java, serviceResponse)
            }

            override fun onFailure(call: Call<StoreProduct>, t: Throwable) {
                serviceResponse.ErrorResponse(t)
            }
        })
    }


    fun getArakStoreProductVariant(
        token: String,
        lang: String,
        productID: String,
        serviceResponse: DataRepository.ServiceResponse<ProductVariant>
    ) {
        val call = netWorkApi.getArakStoreProductVariant(
            cashValue, API_KEY,
            lang,
            buildVersion,
            userAgent,
            token,
            productID
        )


        call.enqueue(object : retrofit2.Callback<ProductVariant> {
            override fun onResponse(
                call: Call<ProductVariant>,
                response: Response<ProductVariant>
            ) {
                handleResponse(response, ProductVariant::class.java, serviceResponse)
            }

            override fun onFailure(call: Call<ProductVariant>, t: Throwable) {
                serviceResponse.ErrorResponse(t)
            }
        })
    }


    fun makeOrderArakStore(
        token: String,
        lang: String,
        orderHashMap: HashMap<String, Any>,
        serviceResponse: DataRepository.ServiceResponse<ArakStoreMakeOrder>
    ) {
        val call = netWorkApi.makeOrderArakStore(
            cashValue, API_KEY,
            lang,
            buildVersion,
            userAgent,
            token,
            orderHashMap
        )


        call.enqueue(object : retrofit2.Callback<ArakStoreMakeOrder> {
            override fun onResponse(
                call: Call<ArakStoreMakeOrder>,
                response: Response<ArakStoreMakeOrder>
            ) {
                handleResponse(response, ArakStoreMakeOrder::class.java, serviceResponse)
            }

            override fun onFailure(call: Call<ArakStoreMakeOrder>, t: Throwable) {
                serviceResponse.ErrorResponse(t)
            }
        })
    }


    fun CustomPackageData(
        token: String,
        lang: String,
        serviceResponse: DataRepository.ServiceResponse<CustomPackage>
    ) {
        val call = netWorkApi.CustomPackageData(cashValue, API_KEY, lang, buildVersion, userAgent, token)


        call.enqueue(object : retrofit2.Callback<CustomPackage> {
            override fun onResponse(
                call: Call<CustomPackage>,
                response: Response<CustomPackage>
            ) {
                handleResponse(response, CustomPackage::class.java, serviceResponse)
            }

            override fun onFailure(call: Call<CustomPackage>, t: Throwable) {
                serviceResponse.ErrorResponse(t)
            }
        })
    }

    /*fun CheckoutCustomPackageData(
        token: String,
        lang: String,
        params: HashMap<String, Any>,
        serviceResponse: DataRepository.ServiceResponse<CustomPackage>
    ) {
        val call = netWorkApi.CheckoutCustomPackageData(cashValue, API_KEY, lang, buildVersion, userAgent, token,params)


        call.enqueue(object : retrofit2.Callback<CustomPackage> {
            override fun onResponse(
                call: Call<CustomPackage>,
                response: Response<CustomPackage>
            ) {
                handleResponse(response, CustomPackage::class.java, serviceResponse)
            }

            override fun onFailure(call: Call<CustomPackage>, t: Throwable) {
                serviceResponse.ErrorResponse(t)
            }
        })
    }*/

    fun userInterests(
        token: String,
        lang: String,
        params: HashMap<String, Any>,
        serviceResponse: ServiceResponse<BaseResponse>
    ) {
        netWorkApi.setUserInterests(cashValue,API_KEY, lang, buildVersion, userAgent, token, params)
            .enqueue(object : retrofit2.Callback<BaseResponse> {
                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    var baseResponse: BaseResponse? = null
                    if (response.code() == 201) {
                        baseResponse = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            baseResponse =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    BaseResponse::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    baseResponse?.let {
                        serviceResponse.successResponse(
                            it,
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }

    fun createStoreAd(
        lang: String,
        token: String,
        createStoreAdRequest: CreateStoreAdRequest,
        serviceResponse: ServiceResponse<CreateNewAdModel>
    ) {
        netWorkApi.createStoreAd(
            cashValue, API_KEY,
            lang,
            buildVersion,
            userAgent,
            token,
            createStoreAdRequest
        )
            .enqueue(object : retrofit2.Callback<CreateNewAdModel> {
                override fun onResponse(
                    call: Call<CreateNewAdModel>,
                    response: Response<CreateNewAdModel>
                ) {
                    var createNewAdModel: CreateNewAdModel? = null
                    if (response.code() == 201) {
                        createNewAdModel = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            createNewAdModel =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    CreateNewAdModel::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    createNewAdModel?.let {
                        serviceResponse.successResponse(
                            it,
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<CreateNewAdModel>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }


    private fun <T> handleResponse(
        response: Response<T>,
        clazz: Class<T>,
        serviceResponse: DataRepository.ServiceResponse<T>
    ) {
        var baseResponse: T? = null
        if (response.code() == 200 || response.code() == 201 || response.code() == 202) {
            baseResponse = response.body()
        } else {
            response.errorBody()?.let {
                try {
                    val gson = Gson()
                    val jsonObject = JSONObject(it.string())
                    baseResponse = gson.fromJson(jsonObject.toString(), clazz)
                } catch (e: Exception) {
                    Log.d(TAG, "onResponse: Exception: ${e.message}")
                }
            }
        }
        baseResponse?.let {
            serviceResponse.successResponse(it, response.code())
        }
    }


    fun updateStore(
        token: String,
        id: String,
        createStoreRequestBody: CreateStoreRequestBody,
        serviceResponse: ServiceResponse<CreateStoreModel>
    ) {
        netWorkApi.updateStore(cashValue, API_KEY, token, id, createStoreRequestBody)
            .enqueue(object : retrofit2.Callback<CreateStoreModel> {
                override fun onResponse(
                    call: Call<CreateStoreModel>,
                    response: Response<CreateStoreModel>
                ) {
                    var singleStoreModel: CreateStoreModel? = null
                    if (response.code() == 200) {
                        singleStoreModel = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            singleStoreModel =
                                gson.fromJson(
                                    jsonObject.toString(),
                                    CreateStoreModel::class.java
                                )
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    singleStoreModel?.let {
                        serviceResponse.successResponse(
                            it,
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<CreateStoreModel>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
    }

    /* fun getProductsByCategory(
         token: String,
         url: String,
         serviceResponse: ServiceResponse<StoreProductsModel>
     ) {
         netWorkApi.getProductsByCategory(cashValue, API_KEY, token, url)
             .enqueue(object : retrofit2.Callback<StoreProductsModel> {
                 override fun onResponse(
                     call: Call<StoreProductsModel>,
                     response: Response<StoreProductsModel>
                 ) {
                     var storeProductsModel: StoreProductsModel? = null
                     if (response.code() == 200) {
                         storeProductsModel = response.body()
                     } else {
                         try {
                             val gson = Gson()
                             val jsonObject = JSONObject(response.errorBody()!!.string())
                             storeProductsModel =
                                 gson.fromJson(
                                     jsonObject.toString(),
                                     StoreProductsModel::class.java
                                 )
                         } catch (e: Exception) {
                             Log.d(TAG, "onResponse: Exception: " + e.message)
                         }
                     }
                     storeProductsModel?.let {
                         serviceResponse.successResponse(
                             it,
                             response.code()
                         )
                     }
                 }

                 override fun onFailure(call: Call<StoreProductsModel>, t: Throwable) {
                     serviceResponse.ErrorResponse(t)
                 }
             })
     }*/


    /*
        fun refreshToken(
            refreshToken: String,
            serviceResponse: ServiceResponse<LoginModel>
        ) {
            netWorkApi.refreshToken(refreshToken).enqueue(object : retrofit2.Callback<LoginModel> {
                override fun onResponse(call: Call<LoginModel>, response: Response<LoginModel>) {

                    var loginModel: LoginModel? = null
                    if (response.code() == 200) {
                        loginModel = response.body()
                    } else {
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            loginModel =
                                gson.fromJson(jsonObject.toString(), LoginModel::class.java)
                        } catch (e: Exception) {
                            Log.d(TAG, "onResponse: Exception: " + e.message)
                        }
                    }
                    loginModel?.let { serviceResponse.successResponse(it, response.code()) }
                }

                override fun onFailure(call: Call<LoginModel>, t: Throwable) {
                    serviceResponse.ErrorResponse(t)
                }
            })
        }*/

    interface ServiceResponse<T> {
        fun successResponse(response: T, responseCode: Int)
        fun ErrorResponse(t: Throwable?)
    }
}