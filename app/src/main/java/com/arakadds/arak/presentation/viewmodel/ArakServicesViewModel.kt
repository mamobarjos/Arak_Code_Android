package com.arakadds.arak.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arakadds.arak.model.new_mapping_refactore.BaseResponse
import com.arakadds.arak.model.new_mapping_refactore.request.RequestArakServiceRequestBody
import com.arakadds.arak.model.new_mapping_refactore.response.banners.arak_services.ArakServicesModel
import com.arakadds.arak.repository.DataRepository
import javax.inject.Inject

class ArakServicesViewModel @Inject constructor(private val dataRepository: DataRepository) :
    ViewModel() {

    var arakServicesModelModel = MutableLiveData<ArakServicesModel>()
    var baseResponseModel = MutableLiveData<BaseResponse>()
    var throwableResponse = MutableLiveData<Throwable?>()

    fun getArakServices(
        token: String,
        lang: String,
        page: Int? = null
    ) {
        dataRepository.getArakServices(
            token,
            lang,
            page,
            object : DataRepository.ServiceResponse<ArakServicesModel> {
                override fun successResponse(
                    response: ArakServicesModel,
                    responseCode: Int
                ) {
                    arakServicesModelModel.value = response
                }

                override fun ErrorResponse(t: Throwable?) {
                    throwableResponse.value = t
                }
            })
    }

    fun requestArakService(
        token: String,
        lang: String,
        requestArakServiceRequestBody: RequestArakServiceRequestBody,
    ) {
        dataRepository.requestArakService(
            token,
            lang,
            requestArakServiceRequestBody,
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