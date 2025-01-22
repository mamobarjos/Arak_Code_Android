package com.arakadds.arak.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arakadds.arak.model.new_mapping_refactore.BaseResponse
import com.arakadds.arak.model.new_mapping_refactore.response.banners.edit_user_info.EditUserInformation
import com.arakadds.arak.repository.DataRepository
import javax.inject.Inject

class UserProfileViewModel @Inject constructor(private val dataRepository: DataRepository) :
    ViewModel() {

    var editUserInformationModel = MutableLiveData<EditUserInformation>()
    var baseResponseModel = MutableLiveData<BaseResponse>()
    var throwableResponse = MutableLiveData<Throwable?>()

    fun editUserInformation(
        token: String,
        lang: String,
        userId: Int,
        editUserInformationHashMap: HashMap<String, String>,
    ) {
        dataRepository.editUserInformation(
            token,
            lang,
            userId,
            editUserInformationHashMap,
            object : DataRepository.ServiceResponse<EditUserInformation> {
                override fun successResponse(
                    response: EditUserInformation,
                    responseCode: Int
                ) {
                    editUserInformationModel.value = response
                }

                override fun ErrorResponse(t: Throwable?) {
                    throwableResponse.value = t
                }
            })
    }

    fun editUserImage(
        token: String,
        lang: String,
        userId: Int,
        editUserImageHashMap: HashMap<String, String>,
    ) {
        dataRepository.editUserImage(
            token,
            lang,
            userId,
            editUserImageHashMap,
                object : DataRepository.ServiceResponse<EditUserInformation> {
                override fun successResponse(
                    response: EditUserInformation,
                    responseCode: Int
                ) {
                    editUserInformationModel.value = response
                }

                override fun ErrorResponse(t: Throwable?) {
                    throwableResponse.value = t
                }
            })
    }

    fun resetPhoneNumber(
        token: String,
        lang: String,
        userId: Int,
        resetPhoneNumberHashMap: HashMap<String, String?>,
    ) {
        dataRepository.resetPhoneNumber(
            token,
            lang,
            userId,
            resetPhoneNumberHashMap,
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



    fun userInterests(
        token: String,
        lang: String,
        idsHashMap: HashMap<String, Any>,
    ) {
        dataRepository.userInterests(
            token,
            lang,
            idsHashMap,
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