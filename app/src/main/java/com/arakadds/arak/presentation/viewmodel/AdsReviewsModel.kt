package com.arakadds.arak.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arakadds.arak.model.new_mapping_refactore.BaseResponse
import com.arakadds.arak.model.new_mapping_refactore.request.AddAdReviewRequest
import com.arakadds.arak.model.new_mapping_refactore.response.banners.ads.AdReviewsModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.ads.ad_reviews.CreateAdReviewModel
import com.arakadds.arak.repository.DataRepository
import javax.inject.Inject

class AdsReviewsModel @Inject constructor(private val dataRepository: DataRepository) :
    ViewModel() {

    var adReviewsModelModel = MutableLiveData<AdReviewsModel>()
    var createAdReviewModelModel = MutableLiveData<CreateAdReviewModel>()
    var baseResponseModel = MutableLiveData<BaseResponse>()
    var throwableResponse = MutableLiveData<Throwable?>()

    fun getAdReviews(
        token: String,
        lang:String,
        adId: String
    ) {
        dataRepository.getAdReviews(
            token,
            lang,
            adId,
            object : DataRepository.ServiceResponse<AdReviewsModel> {
                override fun successResponse(
                    response: AdReviewsModel,
                    responseCode: Int
                ) {
                    adReviewsModelModel.value = response
                }

                override fun ErrorResponse(t: Throwable?) {
                    throwableResponse.value = t
                }
            })
    }
    fun createAdReview(
        token: String,
        lang:String,
        addAdReviewRequest: AddAdReviewRequest
    ) {
        dataRepository.createAdReview(
            token,
            lang,
            addAdReviewRequest,
            object : DataRepository.ServiceResponse<CreateAdReviewModel> {
                override fun successResponse(
                    response: CreateAdReviewModel,
                    responseCode: Int
                ) {
                    createAdReviewModelModel.value = response
                }

                override fun ErrorResponse(t: Throwable?) {
                    throwableResponse.value = t
                }
            })
    }
    fun deleteAdReview(
        token: String,
        lang:String,
        adReviewId: Int
    ) {
        dataRepository.deleteAdReview(
            token,
            lang,
            adReviewId,
            object : DataRepository.ServiceResponse<BaseResponse> {
                override fun successResponse(
                    response: BaseResponse,
                    responseCode: Int
                ) {
                    baseResponseModel.value = response
                }

                override fun ErrorResponse(t: Throwable?) {
                    throwableResponse.value = t
                }
            })
    }


}