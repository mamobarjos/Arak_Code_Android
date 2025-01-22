package com.arakadds.arak.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.notification.NotificationsModel
import com.arakadds.arak.repository.DataRepository
import javax.inject.Inject

class NotificationsViewModel @Inject constructor(private val dataRepository: DataRepository) :
    ViewModel() {

    var notificationsModelModel = MutableLiveData<NotificationsModel>()
    //var notificationsStatusResponseModel = MutableLiveData<NotificationsStatusResponse>()
    //var baseResponseModel = MutableLiveData<BaseResponse>()
    var throwableResponse = MutableLiveData<Throwable?>()

    fun getNotifications(
        token: String,
        lang: String,
        page: Int
    ) {
        dataRepository.getNotifications(
            token,
            lang,
            page,
            object : DataRepository.ServiceResponse<NotificationsModel> {
                override fun successResponse(
                    response: NotificationsModel,
                    responseCode: Int
                ) {
                    notificationsModelModel.value = response
                }

                override fun ErrorResponse(t: Throwable?) {
                    throwableResponse.value = t
                }
            })
    }

   /* fun changeNotificationsStatus(
        token: String,
    ) {
        dataRepository.changeNotificationsStatus(
            token,
            object : DataRepository.ServiceResponse<NotificationsStatusResponse> {
                override fun successResponse(
                    response: NotificationsStatusResponse,
                    responseCode: Int
                ) {
                    notificationsStatusResponseModel.value = response
                }

                override fun ErrorResponse(t: Throwable?) {
                    throwableResponse.value = t
                }
            })
    }*/

   /* fun getNotificationsStatus(
        token: String,
    ) {
        dataRepository.getNotificationsStatus(
            token,
            object : DataRepository.ServiceResponse<NotificationsStatusResponse> {
                override fun successResponse(
                    response: NotificationsStatusResponse,
                    responseCode: Int
                ) {
                    notificationsStatusResponseModel.value = response
                }

                override fun ErrorResponse(t: Throwable?) {
                    throwableResponse.value = t
                }
            })
    }*/
}