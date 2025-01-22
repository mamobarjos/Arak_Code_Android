package com.arakadds.arak.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arakadds.arak.model.new_mapping_refactore.BaseResponse
import com.arakadds.arak.model.new_mapping_refactore.response.banners.ads.AdResponseData
import com.arakadds.arak.repository.DataRepository
import javax.inject.Inject

class AdDetailsViewModel @Inject constructor(private val dataRepository: DataRepository) :
    ViewModel() {

    var adDataModel = MutableLiveData<AdResponseData>()
    var baseResponseModel = MutableLiveData<BaseResponse>()
    var throwableResponse = MutableLiveData<Throwable?>()

    fun getSingleAdDetails(
        lang: String,
        token: String,
        id: String
    ) {
        dataRepository.getSingleAdDetails(
            lang,
            token,
            id,
            object : DataRepository.ServiceResponse<AdResponseData> {
                override fun successResponse(
                    response: AdResponseData,
                    responseCode: Int
                ) {
                    adDataModel.value = response
                }

                override fun ErrorResponse(t: Throwable?) {
                    throwableResponse.value = t
                }
            })
    }

     fun deleteAd(
         token: String,
         lang: String,
         id: Int
    ) {
        dataRepository.deleteAd(
            token,
            lang,
            id,
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