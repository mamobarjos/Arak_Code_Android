package com.arakadds.arak.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arakadds.arak.model.new_mapping_refactore.BaseResponse
import com.arakadds.arak.repository.DataRepository
import javax.inject.Inject

class FavoriteAdViewModel @Inject constructor(private val dataRepository: DataRepository) :
    ViewModel() {

    var baseResponseModel = MutableLiveData<BaseResponse>()
    var throwableResponse = MutableLiveData<Throwable?>()

    fun favoriteAdAction(
        token: String,
        lang: String,
        adId: String,
    ) {
        dataRepository.favoriteAdAction(
            token,
            lang,
            adId,
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