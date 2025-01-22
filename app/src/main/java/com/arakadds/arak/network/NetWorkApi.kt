package com.arakadds.arak.network

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
import com.arakadds.arak.model.new_mapping_refactore.request.store.CreateStoreRequestBody
import com.arakadds.arak.model.new_mapping_refactore.response.banners.ad_category.AdsCategoriesModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.ads.AdResponseData
import com.arakadds.arak.model.new_mapping_refactore.response.banners.ads.AdReviewsModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.ads.RelatedAdsModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.ads.ad_reviews.CreateAdReviewModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.arak_services.ArakServicesModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.auth.user_balance.UserBalanceModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.cities.CitiesModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.countries.CountriesModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.create_ad.CreateNewAdModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.edit_user_info.EditUserInformation
import com.arakadds.arak.model.new_mapping_refactore.response.banners.notification.NotificationsModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.other.AboutModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.other.FeedbackModel
import com.arakadds.arak.model.new_mapping_refactore.store.SingleStoreModel
import com.arakadds.arak.model.new_mapping_refactore.store.StoreCategoriesModel
import com.arakadds.arak.model.new_mapping_refactore.store.reviews.CreateReviewModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.wallets.DigitalWalletsModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.withdraw.MyWithdrawRequestsModel
import com.arakadds.arak.model.new_mapping_refactore.store.CreateStoreModel
import com.arakadds.arak.model.new_mapping_refactore.store.SingleProductModel
import com.arakadds.arak.model.new_mapping_refactore.store.StoreProductsModel
import com.arakadds.arak.model.new_mapping_refactore.store.StoresListModel
import com.arakadds.arak.model.new_mapping_refactore.store.products.RelatedStoreProductsModel
import com.arakadds.arak.model.new_mapping_refactore.request.auth.LoginRequest
import com.arakadds.arak.model.new_mapping_refactore.request.payment.CreateCliqPaymentRequest
import com.arakadds.arak.model.new_mapping_refactore.response.banners.auth.RegistrationModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.elected.ElectedPeopleModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.elected.SingleElectedPersonModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.governorates.GovernoratesModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.my_info.UserInformationModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.packages.PackagesDetails
import com.arakadds.arak.model.new_mapping_refactore.response.banners.ranking.UsersRankingModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.transactions.UserTransactionsModel
import com.arakadds.arak.model.new_mapping_refactore.store.products.my_products.MyStoreProductsModel
import retrofit2.Call
import retrofit2.http.*

interface NetWorkApi {

    @POST("auth/login")
    fun userLogin(
        @Header("Cache-Control") cachingStatus: String = "no-cache",
        @Header("x-api-key") apiKey: String,
        @Header("Accept-Language") language: String,
        @Header("X-Build-Version") buildVersion: String,
        @Header("User-Agent") userAgent: String,
        @Body loginRequest: LoginRequest
    ): Call<LoginResponseData>

    @POST("users")
    fun createAccount(
        @Header("Cache-Control") cachingStatus: String = "no-cache",
        @Header("x-api-key") apiKey: String,
        @Header("Accept-Language") language: String,
        @Header("X-Build-Version") buildVersion: String,
        @Header("User-Agent") userAgent: String,
        @Body registrationBodyRequest: RegistrationBodyRequest
    ): Call<RegistrationModel>

    @GET("ads")
    fun getHomeAds(
        @Header("Cache-Control") cachingStatus: String = "no-cache",
        @Header("x-api-key") apiKey: String,
        @Header("Accept-Language") language: String,
        @Header("X-Build-Version") buildVersion: String,
        @Header("User-Agent") userAgent: String,
        @Header("Authorization") token: String,
        @Query("is_featured") isFeatured: Boolean? = null,
        @Query("ad_category_id") adCategoryId: String? = null,
        @Query("title") textSearch: String? = null,
        @Query("page") page: Int
    ): Call<HomeAdsModel>

    @GET("ads/{id}")
    fun getSingleAdDetails(
        @Header("Cache-Control") cachingStatus: String = "no-cache",
        @Header("x-api-key") apiKey: String,
        @Header("Accept-Language") language: String,
        @Header("X-Build-Version") buildVersion: String,
        @Header("User-Agent") userAgent: String,
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Call<AdResponseData>

    @GET("ads/{ad_id}/related")
    fun getRelatedAds(
        @Header("Cache-Control") cachingStatus: String = "no-cache",
        @Header("x-api-key") apiKey: String,
        @Header("Accept-Language") language: String,
        @Header("X-Build-Version") buildVersion: String,
        @Header("User-Agent") userAgent: String,
        @Header("Authorization") token: String,
        @Path("ad_id") id: String
    ): Call<RelatedAdsModel>

    @GET("banners")
    fun getBanners(
        @Header("Cache-Control") cachingStatus: String = "no-cache",
        @Header("x-api-key") apiKey: String,
        @Header("Accept-Language") language: String,
        @Header("X-Build-Version") buildVersion: String,
        @Header("User-Agent") userAgent: String,
        @Header("Authorization") token: String,
        @Query("type") bannerType: String
    ): Call<BannersModel>

    @GET("ad-packages")
    fun getCategoryPackages(
        @Header("Cache-Control") cachingStatus: String = "no-cache",
        @Header("x-api-key") apiKey: String,
        @Header("Accept-Language") language: String,
        @Header("X-Build-Version") buildVersion: String,
        @Header("User-Agent") userAgent: String,
        @Header("Authorization") token: String,
        @Query("ad_category_id") adCategoryId: String
    ): Call<PackagesDetails>

    @GET("ads/user/my-ads")
    fun getMyAds(
        @Header("Cache-Control") cachingStatus: String = "no-cache",
        @Header("x-api-key") apiKey: String,
        @Header("Accept-Language") language: String,
        @Header("X-Build-Version") buildVersion: String,
        @Header("User-Agent") userAgent: String,
        @Header("Authorization") token: String,
        @Query("date_from") dateFrom: String? = null,
        @Query("date_to") dateTo: String? = null,
        @Query("page") page: Int? = null,
    ): Call<HomeAdsModel>

    /* @GET
     fun getMyFilteredAds(
         @Header("x-api-key") apiKey: String,
         @Header("Authorization") authorization: String,
         @Url url: String
     ): Call<HomeAdsModel>*/

    @GET("ads/user/history")
    fun getUserHistory(
        @Header("Cache-Control") cachingStatus: String = "no-cache",
        @Header("x-api-key") apiKey: String,
        @Header("Accept-Language") language: String,
        @Header("X-Build-Version") buildVersion: String,
        @Header("User-Agent") userAgent: String,
        @Header("Authorization") token: String,
        @Query("page") page: Int? = null,
    ): Call<HomeAdsModel>

    @GET("ads/user/favorites")
    fun getUserFavorites(
        @Header("Cache-Control") cachingStatus: String = "no-cache",
        @Header("x-api-key") apiKey: String,
        @Header("Accept-Language") language: String,
        @Header("X-Build-Version") buildVersion: String,
        @Header("User-Agent") userAgent: String,
        @Header("Authorization") token: String,
        @Query("page") page: Int? = null,
    ): Call<HomeAdsModel>

    @POST("ads/{ad_id}/favorite")
    fun favoriteAdAction(
        @Header("Cache-Control") cachingStatus: String = "no-cache",
        @Header("x-api-key") apiKey: String,
        @Header("Accept-Language") language: String,
        @Header("X-Build-Version") buildVersion: String,
        @Header("User-Agent") userAgent: String,
        @Header("Authorization") token: String,
        @Path("ad_id") adId: String
    ): Call<BaseResponse>

    @GET("ad-categories")
    fun getAdsType(
        @Header("Cache-Control") cachingStatus: String = "no-cache",
        @Header("x-api-key") apiKey: String,
        @Header("Accept-Language") language: String,
        @Header("X-Build-Version") buildVersion: String,
        @Header("User-Agent") userAgent: String,
        @Header("Authorization") token: String,
    ): Call<AdsCategoriesModel>


    @GET("countries")
    fun getCountries(
        @Header("Cache-Control") cachingStatus: String = "no-cache",
        @Header("x-api-key") apiKey: String,
        @Header("Accept-Language") language: String,
        @Header("X-Build-Version") buildVersion: String,
        @Header("User-Agent") userAgent: String,
    ): Call<CountriesModel>

    @GET("cities")
    fun getCities(
        @Header("Cache-Control") cachingStatus: String = "no-cache",
        @Header("x-api-key") apiKey: String,
        @Header("Accept-Language") language: String,
        @Header("X-Build-Version") buildVersion: String,
        @Header("User-Agent") userAgent: String,
        @Query("country_id") countryId: Int
    ): Call<CitiesModel>

    @POST("ads")
    fun createNewAd(
        @Header("Cache-Control") cachingStatus: String = "no-cache",
        @Header("x-api-key") apiKey: String,
        @Header("Accept-Language") language: String,
        @Header("X-Build-Version") buildVersion: String,
        @Header("User-Agent") userAgent: String,
        @Header("Authorization") token: String,
        @Body adRequest: AdRequest
    ): Call<CreateNewAdModel>

    //not forget
    @PATCH("users/{user_id}/password")
    @FormUrlEncoded
    fun resetPassword(
        @Header("Cache-Control") cachingStatus: String = "no-cache",
        @Header("x-api-key") apiKey: String,
        @Header("Accept-Language") language: String,
        @Header("X-Build-Version") buildVersion: String,
        @Header("User-Agent") userAgent: String,
        @Header("Authorization") token: String,
        @Path("user_id") id: Int,
        @FieldMap resetPasswordHashMap: HashMap<String, String>
    ): Call<BaseResponse>

    @POST("users/forget-password")
    fun forgetPassword(
        @Header("Cache-Control") cachingStatus: String = "no-cache",
        @Header("x-api-key") apiKey: String,
        @Header("Accept-Language") language: String,
        @Header("X-Build-Version") buildVersion: String,
        @Header("User-Agent") userAgent: String,
        @Body forgetPasswordRequest: ForgetPasswordRequest
    ): Call<BaseResponse>

    @PATCH("users/{user_id}/phone")
    @FormUrlEncoded
    fun resetPhoneNumber(
        @Header("Cache-Control") cachingStatus: String = "no-cache",
        @Header("x-api-key") apiKey: String,
        @Header("Accept-Language") language: String,
        @Header("X-Build-Version") buildVersion: String,
        @Header("User-Agent") userAgent: String,
        @Header("Authorization") token: String,
        @Path("user_id") id: Int,
        @FieldMap resetPhoneNumberHashMap: HashMap<String, String?>
    ): Call<BaseResponse>

    @POST("otp/send")
    fun sendOTP(
        @Header("Cache-Control") cachingStatus: String = "no-cache",
        @Header("x-api-key") apiKey: String,
        @Header("Accept-Language") language: String,
        @Header("X-Build-Version") buildVersion: String,
        @Header("User-Agent") userAgent: String,
        @Body otpRequest: OTPRequest
    ): Call<BaseResponse>

    @PATCH("users/{user_id}/img")
    @FormUrlEncoded
    fun editUserImage(
        @Header("Cache-Control") cachingStatus: String = "no-cache",
        @Header("x-api-key") apiKey: String,
        @Header("Accept-Language") language: String,
        @Header("X-Build-Version") buildVersion: String,
        @Header("User-Agent") userAgent: String,
        @Header("Authorization") token: String,
        @Path("user_id") id: Int,
        @FieldMap editUserImageHashMap: HashMap<String, String>
    ): Call<EditUserInformation>

    @PATCH("users/{user_id}/info")
    @FormUrlEncoded
    fun editUserInformation(
        @Header("Cache-Control") cachingStatus: String = "no-cache",
        @Header("x-api-key") apiKey: String,
        @Header("Accept-Language") language: String,
        @Header("X-Build-Version") buildVersion: String,
        @Header("User-Agent") userAgent: String,
        @Header("Authorization") token: String,
        @Path("user_id") id: Int,
        @FieldMap editUserInformationHashMap: HashMap<String, String>
    ): Call<EditUserInformation>

    @GET("users/profile/balance")
    fun getUserBalance(
        @Header("Cache-Control") cachingStatus: String = "no-cache",
        @Header("x-api-key") apiKey: String,
        @Header("Accept-Language") language: String,
        @Header("X-Build-Version") buildVersion: String,
        @Header("User-Agent") userAgent: String,
        @Header("Authorization") token: String,
    ): Call<UserBalanceModel>

    @GET("users/stats/top-ranked-by-balance")
    fun getArakRanking(
        @Header("Cache-Control") cachingStatus: String = "no-cache",
        @Header("x-api-key") apiKey: String,
        @Header("Accept-Language") language: String,
        @Header("X-Build-Version") buildVersion: String,
        @Header("User-Agent") userAgent: String,
        @Header("Authorization") token: String,
        @Query("page") page: Int,
    ): Call<UsersRankingModel>

    @GET("ad-reviews")
    fun getAdReviews(
        @Header("Cache-Control") cachingStatus: String = "no-cache",
        @Header("x-api-key") apiKey: String,
        @Header("Accept-Language") language: String,
        @Header("X-Build-Version") buildVersion: String,
        @Header("User-Agent") userAgent: String,
        @Header("Authorization") token: String,
        @Query("ad_id") adId: String
    ): Call<AdReviewsModel>

    @POST("ad-reviews")
    fun createAdReview(
        @Header("Cache-Control") cachingStatus: String = "no-cache",
        @Header("x-api-key") apiKey: String,
        @Header("Accept-Language") language: String,
        @Header("X-Build-Version") buildVersion: String,
        @Header("User-Agent") userAgent: String,
        @Header("Authorization") token: String,
        @Body addAdReviewRequest: AddAdReviewRequest
    ): Call<CreateAdReviewModel>

    @DELETE("ad-reviews/{ad_review_id}")
    fun deleteAdReview(
        @Header("Cache-Control") cachingStatus: String = "no-cache",
        @Header("x-api-key") apiKey: String,
        @Header("Accept-Language") language: String,
        @Header("X-Build-Version") buildVersion: String,
        @Header("User-Agent") userAgent: String,
        @Header("Authorization") token: String,
        @Path("ad_review_id") adReviewId: Int,
    ): Call<BaseResponse>

    @GET("content/1")
    fun getAboutInformation(
        @Header("Cache-Control") cachingStatus: String = "no-cache",
        @Header("x-api-key") apiKey: String,
        @Header("Accept-Language") language: String,
        @Header("X-Build-Version") buildVersion: String,
        @Header("User-Agent") userAgent: String,
        @Header("Authorization") token: String,
    ): Call<AboutModel>

    @POST("feedbacks")
    @FormUrlEncoded
    fun sendUserFeedback(
        @Header("Cache-Control") cachingStatus: String = "no-cache",
        @Header("x-api-key") apiKey: String,
        @Header("Accept-Language") language: String,
        @Header("X-Build-Version") buildVersion: String,
        @Header("User-Agent") userAgent: String,
        @Header("Authorization") token: String,
        @FieldMap userFeedbackHashMap: HashMap<String, String>
    ): Call<FeedbackModel>

    @GET("services")
    fun getArakServices(
        @Header("Cache-Control") cachingStatus: String = "no-cache",
        @Header("x-api-key") apiKey: String,
        @Header("Accept-Language") language: String,
        @Header("X-Build-Version") buildVersion: String,
        @Header("User-Agent") userAgent: String,
        @Header("Authorization") token: String,
        @Query("page") page: Int? = null,
    ): Call<ArakServicesModel>

    @POST("requested-services")
    fun requestArakService(
        @Header("Cache-Control") cachingStatus: String = "no-cache",
        @Header("x-api-key") apiKey: String,
        @Header("Accept-Language") language: String,
        @Header("X-Build-Version") buildVersion: String,
        @Header("User-Agent") userAgent: String,
        @Header("Authorization") token: String,
        @Body requestArakServiceRequestBody: RequestArakServiceRequestBody
    ): Call<BaseResponse>

    @POST("withdraw-requests")
    @FormUrlEncoded
    fun requestWithDraw(
        @Header("Cache-Control") cachingStatus: String = "no-cache",
        @Header("x-api-key") apiKey: String,
        @Header("Accept-Language") language: String,
        @Header("X-Build-Version") buildVersion: String,
        @Header("User-Agent") userAgent: String,
        @Header("Authorization") token: String,
        @FieldMap makeTransactionHashMap: HashMap<String, String>
    ): Call<BaseResponse>

    @GET("withdraw-requests")
    fun getMyWithdrawRequests(
        @Header("Cache-Control") cachingStatus: String = "no-cache",
        @Header("x-api-key") apiKey: String,
        @Header("Accept-Language") language: String,
        @Header("X-Build-Version") buildVersion: String,
        @Header("User-Agent") userAgent: String,
        @Header("Authorization") token: String,
        @Query("page") page: Int? = null,
    ): Call<MyWithdrawRequestsModel>

    @GET("digital-wallets")
    fun getDigitalWallets(
        @Header("Cache-Control") cachingStatus: String = "no-cache",
        @Header("x-api-key") apiKey: String,
        @Header("Accept-Language") language: String,
        @Header("X-Build-Version") buildVersion: String,
        @Header("User-Agent") userAgent: String,
        @Header("Authorization") token: String,
    ): Call<DigitalWalletsModel>

    @POST("coupons/consume")
    @FormUrlEncoded
    fun consumeCoupon(
        @Header("Cache-Control") cachingStatus: String = "no-cache",
        @Header("x-api-key") apiKey: String,
        @Header("Accept-Language") language: String,
        @Header("X-Build-Version") buildVersion: String,
        @Header("User-Agent") userAgent: String,
        @Header("Authorization") token: String,
        @FieldMap consumeCouponHashMap: HashMap<String, String>
    ): Call<BaseResponse>

    @GET("notifications")
    fun getNotifications(
        @Header("Cache-Control") cachingStatus: String = "no-cache",
        @Header("x-api-key") apiKey: String,
        @Header("Accept-Language") language: String,
        @Header("X-Build-Version") buildVersion: String,
        @Header("User-Agent") userAgent: String,
        @Header("Authorization") token: String,
        @Query("page") page: Int? = null,
    ): Call<NotificationsModel>

    @GET("transactions")
    fun getUserTransactions(
        @Header("Cache-Control") cachingStatus: String = "no-cache",
        @Header("x-api-key") apiKey: String,
        @Header("Accept-Language") language: String,
        @Header("X-Build-Version") buildVersion: String,
        @Header("User-Agent") userAgent: String,
        @Header("Authorization") token: String,
        @Query("page") page: Int? = null,
        @Query("date_from") dateFrom: String? = null,
        @Query("date_to") dateTo: String? = null,
    ): Call<UserTransactionsModel>

    @GET("users/profile/info")
    fun getUserInformation(
        @Header("Cache-Control") cachingStatus: String = "no-cache",
        @Header("x-api-key") apiKey: String,
        @Header("Accept-Language") language: String,
        @Header("X-Build-Version") buildVersion: String,
        @Header("User-Agent") userAgent: String,
        @Header("Authorization") token: String,
    ): Call<UserInformationModel>


    @PATCH("ads/{id}")
    fun modifyAd(
        @Header("Cache-Control") cachingStatus: String = "no-cache",
        @Header("x-api-key") apiKey: String,
        @Header("Accept-Language") language: String,
        @Header("X-Build-Version") buildVersion: String,
        @Header("User-Agent") userAgent: String,
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Call<CreateNewAdModel>

    @DELETE("ads/{id}")
    fun deleteAd(
        @Header("Cache-Control") cachingStatus: String = "no-cache",
        @Header("x-api-key") apiKey: String,
        @Header("Accept-Language") language: String,
        @Header("X-Build-Version") buildVersion: String,
        @Header("User-Agent") userAgent: String,
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Call<BaseResponse>

    @GET("store-categories")
    fun getStoreCategories(
        @Header("Cache-Control") cachingStatus: String = "no-cache",
        @Header("x-api-key") apiKey: String,
        @Header("Accept-Language") language: String,
        @Header("X-Build-Version") buildVersion: String,
        @Header("User-Agent") userAgent: String,
        @Header("Authorization") token: String,
    ): Call<StoreCategoriesModel>

    @GET("store-products")
    fun getStoreProduct(
        @Header("Cache-Control") cachingStatus: String = "no-cache",
        @Header("x-api-key") apiKey: String,
        @Header("Accept-Language") language: String,
        @Header("X-Build-Version") buildVersion: String,
        @Header("User-Agent") userAgent: String,
        @Header("Authorization") token: String,
        @Query("random") random: Boolean? = null
    ): Call<StoreProductsDataModel>

    @GET("store-products/{id}")
    fun getSingleProduct(
        @Header("Cache-Control") cachingStatus: String = "no-cache",
        @Header("x-api-key") apiKey: String,
        @Header("Accept-Language") language: String,
        @Header("X-Build-Version") buildVersion: String,
        @Header("User-Agent") userAgent: String,
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Call<SingleProductModel>

    @POST("store-products")
    fun createStoreProduct(
        @Header("Cache-Control") cachingStatus: String = "no-cache",
        @Header("x-api-key") apiKey: String,
        @Header("Accept-Language") language: String,
        @Header("X-Build-Version") buildVersion: String,
        @Header("User-Agent") userAgent: String,
        @Header("Authorization") token: String,
        @Body createProductRequestBody: CreateProductRequestBody
    ): Call<SingleProductModel>

    @PATCH("store-products/{id}")
    fun updateProduct(
        @Header("Cache-Control") cachingStatus: String = "no-cache",
        @Header("x-api-key") apiKey: String,
        @Header("Accept-Language") language: String,
        @Header("X-Build-Version") buildVersion: String,
        @Header("User-Agent") userAgent: String,
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body createProductRequestBody: CreateProductRequestBody
    ): Call<BaseResponse>

    @GET("stores")
    fun getStoresList(
        @Header("Cache-Control") cachingStatus: String = "no-cache",
        @Header("x-api-key") apiKey: String,
        @Header("Accept-Language") language: String,
        @Header("X-Build-Version") buildVersion: String,
        @Header("User-Agent") userAgent: String,
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("is_random") random: Boolean? = null,
        @Query("name") name: String? = null,
        @Query("phone_no") phoneNumber: String? = null,
        @Query("is_featured") isFeatured: Boolean? = null,
        @Query("store_category_id") storeCategoryId: Int? = null,
        @Query("has_visa") hasVisa: Boolean? = null,
        @Query("has_mastercard") hasMastercard: Boolean? = null,
        @Query("has_paypal") hasPaypal: Boolean? = null,
        @Query("has_cash") hasCash: Boolean? = null,
    ): Call<StoresListModel>

    @POST("stores")
    fun createStore(
        @Header("Cache-Control") cachingStatus: String = "no-cache",
        @Header("x-api-key") apiKey: String,
        @Header("Accept-Language") language: String,
        @Header("X-Build-Version") buildVersion: String,
        @Header("User-Agent") userAgent: String,
        @Header("Authorization") token: String,
        @Body createStoreRequestBody: CreateStoreRequestBody
    ): Call<CreateStoreModel>

    @GET("stores/{id}")
    fun getSingleStore(
        @Header("Cache-Control") cachingStatus: String = "no-cache",
        @Header("x-api-key") apiKey: String,
        @Header("Accept-Language") language: String,
        @Header("X-Build-Version") buildVersion: String,
        @Header("User-Agent") userAgent: String,
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Call<SingleStoreModel>

    @POST("store-reviews")
    fun addStoreReview(
        @Header("Cache-Control") cachingStatus: String = "no-cache",
        @Header("x-api-key") apiKey: String,
        @Header("Accept-Language") language: String,
        @Header("X-Build-Version") buildVersion: String,
        @Header("User-Agent") userAgent: String,
        @Header("Authorization") token: String,
        @Body addStoreReviewRequest: AddStoreReviewRequest
    ): Call<CreateReviewModel>

    @DELETE("store-reviews/{id}")
    fun deleteStoreReview(
        @Header("Cache-Control") cachingStatus: String = "no-cache",
        @Header("x-api-key") apiKey: String,
        @Header("Accept-Language") language: String,
        @Header("X-Build-Version") buildVersion: String,
        @Header("User-Agent") userAgent: String,
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Call<BaseResponse>

    @GET("store-products")
    fun getStoreProducts(
        @Header("Cache-Control") cachingStatus: String = "no-cache",
        @Header("x-api-key") apiKey: String,
        @Header("Accept-Language") language: String,
        @Header("X-Build-Version") buildVersion: String,
        @Header("User-Agent") userAgent: String,
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("random") random: Boolean? = null,
        @Query("store_id") storeId: Int? = null,
        @Query("store_category_id") storeCategoryId: Int? = null,
    ): Call<StoreProductsModel>

    @GET("store-products/user/my-products")
    fun getMyStoreProducts(
        @Header("Cache-Control") cachingStatus: String = "no-cache",
        @Header("x-api-key") apiKey: String,
        @Header("Accept-Language") language: String,
        @Header("X-Build-Version") buildVersion: String,
        @Header("User-Agent") userAgent: String,
        @Header("Authorization") token: String,
        @Query("page") page: Int,
    ): Call<MyStoreProductsModel>

    @DELETE("store-products/{id}")
    fun deleteProduct(
        @Header("Cache-Control") cachingStatus: String = "no-cache",
        @Header("x-api-key") apiKey: String,
        @Header("Accept-Language") language: String,
        @Header("X-Build-Version") buildVersion: String,
        @Header("User-Agent") userAgent: String,
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Call<BaseResponse>


    @POST("store-product-reviews")
    fun addProductReview(
        @Header("Cache-Control") cachingStatus: String = "no-cache",
        @Header("x-api-key") apiKey: String,
        @Header("Accept-Language") language: String,
        @Header("X-Build-Version") buildVersion: String,
        @Header("User-Agent") userAgent: String,
        @Header("Authorization") token: String,
        @Body addStoreProductReviewRequest: AddStoreProductReviewRequest
    ): Call<CreateReviewModel>

    @GET("store-products/{id}/related")
    fun getRelatedStoreProducts(
        @Header("Cache-Control") cachingStatus: String = "no-cache",
        @Header("x-api-key") apiKey: String,
        @Header("Accept-Language") language: String,
        @Header("X-Build-Version") buildVersion: String,
        @Header("User-Agent") userAgent: String,
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Call<RelatedStoreProductsModel>

    @DELETE("store-product-reviews/{id}")
    fun deleteProductReview(
        @Header("Cache-Control") cachingStatus: String = "no-cache",
        @Header("x-api-key") apiKey: String,
        @Header("Accept-Language") language: String,
        @Header("X-Build-Version") buildVersion: String,
        @Header("User-Agent") userAgent: String,
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Call<BaseResponse>


    @GET("stores/user/my-store")
    fun getMyStore(
        @Header("Cache-Control") cachingStatus: String = "no-cache",
        @Header("x-api-key") apiKey: String,
        @Header("Accept-Language") language: String,
        @Header("X-Build-Version") buildVersion: String,
        @Header("User-Agent") userAgent: String,
        @Header("Authorization") token: String,
    ): Call<SingleStoreModel>

    @GET("elected-people")
    fun getElectedPeople(
        @Header("Cache-Control") cachingStatus: String = "no-cache",
        @Header("x-api-key") apiKey: String,
        @Header("Accept-Language") language: String,
        @Header("X-Build-Version") buildVersion: String,
        @Header("User-Agent") userAgent: String,
        @Header("Authorization") token: String,
        @Query("governorate_id") governorateId: Int? = null,
    ): Call<ElectedPeopleModel>

    @GET("elected-people/{id}")
    fun getSingleElectedPerson(
        @Header("Cache-Control") cachingStatus: String = "no-cache",
        @Header("x-api-key") apiKey: String,
        @Header("Accept-Language") language: String,
        @Header("X-Build-Version") buildVersion: String,
        @Header("User-Agent") userAgent: String,
        @Header("Authorization") token: String,
        @Path("id") id: Int,
    ): Call<SingleElectedPersonModel>

    @GET("governorates")
    fun getGovernorates(
        @Header("Cache-Control") cachingStatus: String = "no-cache",
        @Header("x-api-key") apiKey: String,
        @Header("Accept-Language") language: String,
        @Header("X-Build-Version") buildVersion: String,
        @Header("User-Agent") userAgent: String,
        @Header("Authorization") token: String,
    ): Call<GovernoratesModel>

    @POST("cliq-payments")
    fun createCliqPayment(
        @Header("Cache-Control") cachingStatus: String = "no-cache",
        @Header("x-api-key") apiKey: String,
        @Header("Accept-Language") language: String,
        @Header("X-Build-Version") buildVersion: String,
        @Header("User-Agent") userAgent: String,
        @Header("Authorization") token: String,
        @Body createCliqPaymentRequest: CreateCliqPaymentRequest
    ): Call<BaseResponse>

    @GET("stores/arak-store/categories")
    fun getArakStoreCategory(
        @Header("Cache-Control") cachingStatus: String = "no-cache",
        @Header("x-api-key") apiKey: String,
        @Header("Accept-Language") language: String,
        @Header("X-Build-Version") buildVersion: String,
        @Header("User-Agent") userAgent: String,
        @Header("Authorization") token: String,
        @Query("page") page: Int? = null,
    ): Call<StoreCategory>

    @GET("stores/arak-store/products")
    fun getArakStoreProduct(
        @Header("Cache-Control") cachingStatus: String = "no-cache",
        @Header("x-api-key") apiKey: String,
        @Header("Accept-Language") language: String,
        @Header("X-Build-Version") buildVersion: String,
        @Header("User-Agent") userAgent: String,
        @Header("Authorization") token: String,
        @Query("category") category: Int? = null,
        @Query("page") page: Int? = null,
        @Query("per_page") perPage: Int? = null,
    ): Call<StoreProduct>

    @GET("stores/arak-store/products/variations")
    fun getArakStoreProductVariant(
        @Header("Cache-Control") cachingStatus: String = "no-cache",
        @Header("x-api-key") apiKey: String,
        @Header("Accept-Language") language: String,
        @Header("X-Build-Version") buildVersion: String,
        @Header("User-Agent") userAgent: String,
        @Header("Authorization") token: String,
        @Query("product_id") productID: String
    ): Call<ProductVariant>

    @POST("stores/arak-store/order")
    @FormUrlEncoded
    fun makeOrderArakStore(
        @Header("Cache-Control") cachingStatus: String = "no-cache",
        @Header("x-api-key") apiKey: String,
        @Header("Accept-Language") language: String,
        @Header("X-Build-Version") buildVersion: String,
        @Header("User-Agent") userAgent: String,
        @Header("Authorization") token: String,
        @Body orderHashMap: HashMap<String, Any>
    ): Call<ArakStoreMakeOrder>

    @GET("content/custom/data")
    fun CustomPackageData(
        @Header("Cache-Control") cachingStatus: String = "no-cache",
        @Header("x-api-key") apiKey: String,
        @Header("Accept-Language") language: String,
        @Header("X-Build-Version") buildVersion: String,
        @Header("User-Agent") userAgent: String,
        @Header("Authorization") token: String,
    ): Call<CustomPackage>

    /* @POST("ad-packages/custom")
     fun CheckoutCustomPackageData(
         @Header("x-api-key") apiKey: String,
         @Header("Accept-Language") language: String,
         @Header("X-Build-Version") buildVersion: String,
         @Header("User-Agent") userAgent: String,
         @Header("Authorization") token: String,
         @Body params: HashMap<String, Any>
     ): Call<CustomPackage>*/

    @POST("users/update-preferences")
    @FormUrlEncoded
    fun setUserInterests(
        @Header("Cache-Control") cachingStatus: String = "no-cache",
        @Header("x-api-key") apiKey: String,
        @Header("Accept-Language") language: String,
        @Header("X-Build-Version") buildVersion: String,
        @Header("User-Agent") userAgent: String,
        @Header("Authorization") token: String,
        @FieldMap params: HashMap<String, Any>
    ): Call<BaseResponse>

    @POST("ads/custom/store-ads")
    fun createStoreAd(
        @Header("Cache-Control") cachingStatus: String = "no-cache",
        @Header("x-api-key") apiKey: String,
        @Header("Accept-Language") language: String,
        @Header("X-Build-Version") buildVersion: String,
        @Header("User-Agent") userAgent: String,
        @Header("Authorization") token: String,
        @Body createStoreAdRequest: CreateStoreAdRequest
    ): Call<CreateNewAdModel>

    @PATCH("stores/{id}")
    fun updateStore(
        @Header("Cache-Control") cachingStatus: String = "no-cache",
        @Header("x-api-key") apiKey: String,
        @Header("Authorization") authorization: String,
        @Path("id") id: String,
        @Body createStoreRequestBody: CreateStoreRequestBody
    ): Call<CreateStoreModel>


    //======end of new endpoints implemintaion======================================================================================================================================


    //======================================================
    // missed endpoints--------------------------------------
    /*@GET
    fun getProductsByCategory(
        @Header("x-api-key") apiKey: String,
        @Header("Authorization") authorization: String,
        @Url url: String
    ): Call<StoreProductsModel>

    @POST("stores/update-store/{id}")
    @FormUrlEncoded
    fun updateStore(
        @Header("x-api-key") apiKey: String,
        @Header("Authorization") authorization: String,
        @Path("id") id: String,
        @FieldMap updateStoreHashMap: HashMap<String, Any>
    ): Call<SingleStoreModel>

    @DELETE("store-products/delete-product/{id}")
    fun deleteProduct(
        @Header("x-api-key") apiKey: String,
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Call<BaseResponse>



    @GET
    fun getMyProducts(
        @Header("x-api-key") apiKey: String,
        @Header("Authorization") token: String,
        @Url url: String
    ): Call<StoreProductsModel>





    //waiting for client action
    @POST("transaction/pay")
    @FormUrlEncoded
    fun requestPaymentMethod(
        @Header("x-api-key") apiKey: String,
        @Header("Authorization") authorization: String,
        @FieldMap requestPaymentMethodHashMap: HashMap<String, String>
    ): Call<CreateNewAdResponse>

    @GET("cust-pkgs/cust-pkgs-lists")
    fun getCustomPackagesDropdownLists(
        @Header("x-api-key") apiKey: String,
        @Header("Authorization") authorization: String
    ): Call<CustomPackageData>
    @POST("package/create-custom-package")
    @FormUrlEncoded
    fun createCustomPackage(
        @Header("x-api-key") apiKey: String,
        @Header("Authorization") authorization: String,
        @FieldMap CreateCustomPackageHashMap: HashMap<String?, String?>
    ): Call<CreateCustomPackageData>
    */


    //======================================================
    //included with other endpoints --------------------
    /*   @GET
       fun filterStoresByCategory(
           @Header("x-api-key") apiKey: String,
           @Header("Authorization") authorization: String,
           @Url url: String
       ): Call<StoreFilterModel>*/

    /*    @GET
        fun searchStores(
            @Header("x-api-key") apiKey: String,
            @Header("Authorization") authorization: String,
            @Url url: String
        ): Call<StoreFilterModel>*/


    //====================================================
    // not needed any more--------------------------------

    /*@POST("auth/google")
    fun socialRegisterLogin(
        @Header("x-api-key") apiKey: String,
        @Body socialLoginRequest: SocialLoginRequest
    ): Call<RegistrationResponseData>*/

    /*@POST("auth/logout")
    fun logout(
        @Header("x-api-key") apiKey: String,
        @Header("Authorization") token: String *//*, @Header("Accept-Language") String language*//*
    ): Call<BaseResponse>*/

    /* @POST("transaction/make-transaction")
     @FormUrlEncoded
     fun makeTransaction(
         @Header("x-api-key") apiKey: String,
         @Header("Authorization") authorization: String,
         @FieldMap makeTransactionHashMap: HashMap<String, String>
     ): Call<MakeTransactionResponse>
    */

    /* @GET
     fun search(
         @Header("x-api-key") apiKey: String?,
         @Header("Accept-Language") language: String?,
         @Header("Authorization") token: String?,
         @Url url: String
     ): Call<HomeAdsModel>*/

    /*  @GET
      fun getUserFilteredTransaction(
          @Header("x-api-key") apiKey: String,
          @Header("Authorization") authorization: String,
          @Url url: String
      ): Call<UserTransactionsResponse>*/

    /*    @POST("notifications/toggle-notifications-status")
        fun changeNotificationsStatus(
            @Header("x-api-key") apiKey: String?,
            @Header("Authorization") authorization: String
        ): Call<NotificationsStatusResponse>

        @GET("notifications/get-notifications-status")
        fun getNotificationsStatus(
            @Header("x-api-key") apiKey: String,
            @Header("Authorization") authorization: String
        ): Call<NotificationsStatusResponse>

        @POST("notifications/update-token")
        fun updateFCMToken(
            @Header("x-api-key") apiKey: String,
            @Header("Authorization") authorization: String
        ): Call<BaseResponse>*/

}