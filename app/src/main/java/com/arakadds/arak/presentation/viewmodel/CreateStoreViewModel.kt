package com.arakadds.arak.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arakadds.arak.model.new_mapping_refactore.request.store.CreateStoreRequestBody
import com.arakadds.arak.model.new_mapping_refactore.store.CreateStoreModel
import com.arakadds.arak.repository.DataRepository
import javax.inject.Inject
class CreateStoreViewModel @Inject constructor(private val dataRepository: DataRepository) :
    ViewModel() {

    var createStoreModelModel = MutableLiveData<CreateStoreModel>()
    var updateStoreModelModel = MutableLiveData<CreateStoreModel>()
    var throwableResponse = MutableLiveData<Throwable?>()

    fun createStore(
        token: String,
        lang: String,
        createStoreRequestBody: CreateStoreRequestBody,
    ) {
        dataRepository.createStore(
            token,
            lang,
            createStoreRequestBody,
            object : DataRepository.ServiceResponse<CreateStoreModel> {
                override fun successResponse(
                    response: CreateStoreModel,
                    responseCode: Int
                ) {
                    createStoreModelModel.value = response
                }

                override fun ErrorResponse(t: Throwable?) {
                    throwableResponse.value = t
                }
            })
    }

    fun updateStore(
        token: String,
        id: String,
        createStoreRequestBody: CreateStoreRequestBody,
    ) {
        dataRepository.updateStore(
            token,
            id,
            createStoreRequestBody,
            object : DataRepository.ServiceResponse<CreateStoreModel> {
                override fun successResponse(
                    response: CreateStoreModel,
                    responseCode: Int
                ) {
                    updateStoreModelModel.value = response
                }

                override fun ErrorResponse(t: Throwable?) {
                    throwableResponse.value = t
                }
            })
    }



}