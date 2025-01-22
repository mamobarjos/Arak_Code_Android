package com.arakadds.arak.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.elected.ElectedPeopleModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.elected.SingleElectedPersonModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.governorates.GovernoratesModel
import com.arakadds.arak.repository.DataRepository
import javax.inject.Inject

class ElectionViewModel @Inject constructor(private val dataRepository: DataRepository) :
    ViewModel() {
    var electedPeopleModelModel = MutableLiveData<ElectedPeopleModel>()
    var singleElectedPersonModelModel = MutableLiveData<SingleElectedPersonModel>()
    var governoratesModelModel = MutableLiveData<GovernoratesModel>()
    var throwableResponse = MutableLiveData<Throwable?>()

    fun getElectedPeople(
        token: String,
        lang: String,
        governorateId: Int? = null
    ) {
        dataRepository.getElectedPeople(
            token,
            lang,
            governorateId,
            object : DataRepository.ServiceResponse<ElectedPeopleModel> {
                override fun successResponse(
                    response: ElectedPeopleModel,
                    responseCode: Int
                ) {
                    electedPeopleModelModel.value = response
                }

                override fun ErrorResponse(t: Throwable?) {
                    throwableResponse.value = t
                }
            })
    }

    fun getSingleElectedPerson(
        token: String,
        lang: String,
        id: Int
    ) {
        dataRepository.getSingleElectedPerson(
            token,
            lang,
            id,
            object : DataRepository.ServiceResponse<SingleElectedPersonModel> {
                override fun successResponse(
                    response: SingleElectedPersonModel,
                    responseCode: Int
                ) {
                    singleElectedPersonModelModel.value = response
                }

                override fun ErrorResponse(t: Throwable?) {
                    throwableResponse.value = t
                }
            })
    }

    fun getGovernorates(
        token: String,
        lang: String,
    ) {
        dataRepository.getGovernorates(
            token,
            lang,
            object : DataRepository.ServiceResponse<GovernoratesModel> {
                override fun successResponse(
                    response: GovernoratesModel,
                    responseCode: Int
                ) {
                    governoratesModelModel.value = response
                }

                override fun ErrorResponse(t: Throwable?) {
                    throwableResponse.value = t
                }
            })
    }
}