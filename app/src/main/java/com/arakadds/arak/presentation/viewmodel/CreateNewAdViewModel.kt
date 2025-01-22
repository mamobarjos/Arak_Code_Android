package com.arakadds.arak.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arakadds.arak.model.new_mapping_refactore.BaseResponse
import com.arakadds.arak.model.new_mapping_refactore.request.ad.AdRequest
import com.arakadds.arak.model.new_mapping_refactore.request.ad.CreateStoreAdRequest
import com.arakadds.arak.model.new_mapping_refactore.response.banners.create_ad.CreateNewAdModel
import com.arakadds.arak.repository.DataRepository
import javax.inject.Inject

class CreateNewAdViewModel @Inject constructor(private val dataRepository: DataRepository) :
    ViewModel() {

    var createNewAdModelModel = MutableLiveData<CreateNewAdModel>()
    var throwableResponse = MutableLiveData<Throwable?>()

    fun createNewAd(
        lang: String,
        token: String,
        adRequest : AdRequest,
    ) {
        dataRepository.createNewAd(
            lang,
            token,
            adRequest,
            object : DataRepository.ServiceResponse<CreateNewAdModel> {
                override fun successResponse(
                    response: CreateNewAdModel,
                    responseCode: Int
                ) {
                    createNewAdModelModel.value = response
                }

                override fun ErrorResponse(t: Throwable?) {
                    throwableResponse.value = t
                }
            })
    }

    fun createStoreAd(
        lang: String,
        token: String,
        createStoreAdRequest: CreateStoreAdRequest,
    ) {
        dataRepository.createStoreAd(
            lang,
            token,
            createStoreAdRequest,
            object : DataRepository.ServiceResponse<CreateNewAdModel> {
                override fun successResponse(
                    response: CreateNewAdModel,
                    responseCode: Int
                ) {
                    createNewAdModelModel.value = response
                }

                override fun ErrorResponse(t: Throwable?) {
                    throwableResponse.value = t
                }
            })
    }


}