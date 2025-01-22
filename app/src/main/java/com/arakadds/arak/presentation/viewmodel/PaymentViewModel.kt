package com.arakadds.arak.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arakadds.arak.model.new_mapping_refactore.BaseResponse
import com.arakadds.arak.model.new_mapping_refactore.request.ad.AdRequest
import com.arakadds.arak.model.new_mapping_refactore.request.payment.CreateCliqPaymentRequest
import com.arakadds.arak.model.new_mapping_refactore.response.banners.create_ad.CreateNewAdModel
import com.arakadds.arak.repository.DataRepository
import javax.inject.Inject

class PaymentViewModel @Inject constructor(private val dataRepository: DataRepository) : ViewModel() {

    //var createNewAdResponseModel = MutableLiveData<CreateNewAdResponse>()
    var baseResponseModel = MutableLiveData<BaseResponse>()
    var throwableResponse = MutableLiveData<Throwable?>()

    /*fun requestPaymentMethod(
        token: String,
        requestPaymentMethodHashMap: HashMap<String, String>,
    ) {
        dataRepository.requestPaymentMethod(
            token,
            requestPaymentMethodHashMap,
            object : DataRepository.ServiceResponse<CreateNewAdResponse> {
                override fun successResponse(
                    response: CreateNewAdResponse,
                    responseCode: Int
                ) {
                    createNewAdResponseModel.value = response
                }

                override fun ErrorResponse(t: Throwable?) {
                    throwableResponse.value = t
                }
            })
    }*/

    fun createCliqPayment(
        token: String,
        lang: String,
        createCliqPaymentRequest: CreateCliqPaymentRequest,
    ) {
        dataRepository.createCliqPayment(
            token,
            lang,
            createCliqPaymentRequest,
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