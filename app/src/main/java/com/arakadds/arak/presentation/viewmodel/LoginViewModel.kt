package com.arakadds.arak.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.auth.LoginResponseData
import com.arakadds.arak.model.new_mapping_refactore.request.auth.LoginRequest
import com.arakadds.arak.model.new_mapping_refactore.request.auth.SocialLoginRequest
import com.arakadds.arak.model.new_mapping_refactore.response.banners.my_info.UserInformationModel
import com.arakadds.arak.repository.DataRepository
import javax.inject.Inject

class LoginViewModel @Inject constructor(private val dataRepository: DataRepository) : ViewModel() {

    //var loginModel = MutableLiveData<RegistrationResponseData>()
    //var socialLoginModel = MutableLiveData<RegistrationResponseData>()
    var loginResponseBodyModel = MutableLiveData<LoginResponseData>()
    var userInformationModelModel = MutableLiveData<UserInformationModel>()

    //var baseResponseModel = MutableLiveData<BaseResponse>()
    var throwableResponse = MutableLiveData<Throwable?>()

    fun userLogin(lang: String, loginRequest: LoginRequest) {
        dataRepository.userLogin(
            lang,
            loginRequest,
            object : DataRepository.ServiceResponse<LoginResponseData> {
                override fun successResponse(
                    response: LoginResponseData,
                    responseCode: Int
                ) {
                    loginResponseBodyModel.value = response
                }

                override fun ErrorResponse(t: Throwable?) {
                    throwableResponse.value = t
                }
            })
    }

    fun getUserInformation(
        token: String,
        lang: String
    ) {
        dataRepository.getUserInformation(
            token,
            lang,
            object : DataRepository.ServiceResponse<UserInformationModel> {
                override fun successResponse(
                    response: UserInformationModel,
                    responseCode: Int
                ) {
                    userInformationModelModel.value = response
                }

                override fun ErrorResponse(t: Throwable?) {
                    throwableResponse.value = t
                }
            })
    }

    /* fun socialRegisterLogin(socialLoginRequest: SocialLoginRequest) {
         dataRepository.socialRegisterLogin(
             socialLoginRequest,
             object : DataRepository.ServiceResponse<RegistrationResponseData> {
                 override fun successResponse(
                     response: RegistrationResponseData,
                     responseCode: Int
                 ) {
                     socialLoginModel.value = response
                 }

                 override fun ErrorResponse(t: Throwable?) {
                     throwableResponse.value = t
                 }
             })
     }*/

    /* fun updateFCMToken(token: String,) {
         dataRepository.updateFCMToken(
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