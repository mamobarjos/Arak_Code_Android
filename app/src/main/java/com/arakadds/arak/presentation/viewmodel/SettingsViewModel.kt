package com.arakadds.arak.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.other.AboutModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.other.FeedbackModel
import com.arakadds.arak.repository.DataRepository
import javax.inject.Inject

class SettingsViewModel @Inject constructor(private val dataRepository: DataRepository) :
    ViewModel() {

    var feedbackModelModel = MutableLiveData<FeedbackModel>()
    var aboutModelModel = MutableLiveData<AboutModel>()
    var throwableResponse = MutableLiveData<Throwable?>()

    fun sendUserFeedback(
        lang: String,
        token: String,
        userFeedbackHashMap: HashMap<String, String>
    ) {
        dataRepository.sendUserFeedback(
            lang,
            token,
            userFeedbackHashMap,
            object : DataRepository.ServiceResponse<FeedbackModel> {
                override fun successResponse(
                    response: FeedbackModel,
                    responseCode: Int
                ) {
                    feedbackModelModel.value = response
                }

                override fun ErrorResponse(t: Throwable?) {
                    throwableResponse.value = t
                }
            })
    }

    fun getAboutInformation(
        token: String,
        lang: String
    ) {
        dataRepository.getAboutInformation(
            token,
            lang,
            object : DataRepository.ServiceResponse<AboutModel> {
                override fun successResponse(
                    response: AboutModel,
                    responseCode: Int
                ) {
                    aboutModelModel.value = response
                }

                override fun ErrorResponse(t: Throwable?) {
                    throwableResponse.value = t
                }
            })
    }
}