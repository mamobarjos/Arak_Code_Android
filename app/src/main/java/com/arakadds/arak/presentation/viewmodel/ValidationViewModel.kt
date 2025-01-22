package com.arakadds.arak.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arakadds.arak.model.new_mapping_refactore.BaseResponse
import com.arakadds.arak.model.new_mapping_refactore.request.auth.ForgetPasswordRequest
import com.arakadds.arak.model.new_mapping_refactore.request.auth.OTPRequest
import com.arakadds.arak.repository.DataRepository
import javax.inject.Inject

class ValidationViewModel @Inject constructor(private val dataRepository: DataRepository) :
    ViewModel() {

    var baseResponseModel = MutableLiveData<BaseResponse>()
    var throwableResponse = MutableLiveData<Throwable?>()

    /*fun logout(token: String) {
        dataRepository.logout(
            token,
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
    }*/

    fun sendOTP(
        lang: String,
        otpRequest: OTPRequest,
    ) {
        dataRepository.sendOTP(
            lang,
            otpRequest,
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

    fun forgetPassword(
        lang: String,
        forgetPasswordRequest: ForgetPasswordRequest,
    ) {
        dataRepository.forgetPassword(
            lang,
            forgetPasswordRequest,
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

    fun resetPassword(
        token: String,
        lang: String,
        userId:Int,
        resetPasswordHashMap: HashMap<String, String>,
    ) {
        dataRepository.resetPassword(
            token,
            lang,
            userId,
            resetPasswordHashMap,
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

    /*fun getRefreshToken(refreshToken: String) {
        dataRepository.refreshToken(
            refreshToken,
            object : DataRepository.ServiceResponse<LoginModel> {
                override fun successResponse(
                    response: LoginModel,
                    responseCode: Int
                ) {
                    loginModel.value = response
                }

                override fun ErrorResponse(t: Throwable?) {
                    throwableResponse.value = t
                }
            })
    }*/
}