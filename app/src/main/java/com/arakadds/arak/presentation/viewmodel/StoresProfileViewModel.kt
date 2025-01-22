package com.arakadds.arak.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arakadds.arak.model.new_mapping_refactore.BaseResponse
import com.arakadds.arak.model.new_mapping_refactore.request.AddStoreReviewRequest
import com.arakadds.arak.model.new_mapping_refactore.store.SingleStoreModel
import com.arakadds.arak.model.new_mapping_refactore.store.reviews.CreateReviewModel
import com.arakadds.arak.repository.DataRepository
import javax.inject.Inject
class StoresProfileViewModel @Inject constructor(private val dataRepository: DataRepository) :
    ViewModel() {

    var singleStoreModel = MutableLiveData<SingleStoreModel>()
    var createReviewModelModel = MutableLiveData<CreateReviewModel>()
    var baseResponseModel = MutableLiveData<BaseResponse>()
    var throwableResponse = MutableLiveData<Throwable?>()

    fun getSingleStore(
        token: String,
        lang: String,
        id: Int,
    ) {
        dataRepository.getSingleStore(
            token,
            lang,
            id,
            object : DataRepository.ServiceResponse<SingleStoreModel> {
                override fun successResponse(
                    response: SingleStoreModel,
                    responseCode: Int
                ) {
                    singleStoreModel.value = response
                }

                override fun ErrorResponse(t: Throwable?) {
                    throwableResponse.value = t
                }
            })
    }

    fun getMyStore(
        token: String,
        lang: String,
    ) {
        dataRepository.getMyStore(
            token,
            lang,
            object : DataRepository.ServiceResponse<SingleStoreModel> {
                override fun successResponse(
                    response: SingleStoreModel,
                    responseCode: Int
                ) {
                    singleStoreModel.value = response
                }

                override fun ErrorResponse(t: Throwable?) {
                    throwableResponse.value = t
                }
            })
    }
    fun addStoreReview(
        token: String,
        lang: String,
        addStoreReviewRequest: AddStoreReviewRequest,
    ) {
        dataRepository.addStoreReview(
            token,
            lang,
            addStoreReviewRequest,
            object : DataRepository.ServiceResponse<CreateReviewModel> {
                override fun successResponse(
                    response: CreateReviewModel,
                    responseCode: Int
                ) {
                    createReviewModelModel.value = response
                }

                override fun ErrorResponse(t: Throwable?) {
                    throwableResponse.value = t
                }
            })
    }

    /*fun getMyStore(
        token: String
    ) {
        dataRepository.getMyStore(
            token,
            object : DataRepository.ServiceResponse<SingleStoreModel> {
                override fun successResponse(
                    response: SingleStoreModel,
                    responseCode: Int
                ) {
                    singleStoreModel.value = response
                }

                override fun ErrorResponse(t: Throwable?) {
                    throwableResponse.value = t
                }
            })
    }*/

    fun deleteStoreReview(
        token: String,
        lang: String,
        id: Int,
    ) {
        dataRepository.deleteStoreReview(
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