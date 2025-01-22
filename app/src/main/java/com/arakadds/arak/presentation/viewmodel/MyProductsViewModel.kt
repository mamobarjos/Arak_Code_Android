package com.arakadds.arak.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arakadds.arak.repository.DataRepository
import javax.inject.Inject
class MyProductsViewModel @Inject constructor(private val dataRepository: DataRepository) :
    ViewModel() {

    //var storeProductsModelModel = MutableLiveData<StoreProductsModel>()
    var throwableResponse = MutableLiveData<Throwable?>()

    /*fun getMyProducts(
        token: String,
        url: String
    ) {
        dataRepository.getMyProducts(
            token,
            url,
            object : DataRepository.ServiceResponse<StoreProductsModel> {
                override fun successResponse(
                    response: StoreProductsModel,
                    responseCode: Int
                ) {
                    storeProductsModelModel.value = response
                }

                override fun ErrorResponse(t: Throwable?) {
                    throwableResponse.value = t
                }
            })
    }*/


}