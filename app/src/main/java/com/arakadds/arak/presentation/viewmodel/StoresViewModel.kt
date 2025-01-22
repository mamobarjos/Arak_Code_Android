package com.arakadds.arak.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arakadds.arak.model.new_mapping_refactore.store.StoreCategoriesModel
import com.arakadds.arak.model.new_mapping_refactore.store.StoresListModel
import com.arakadds.arak.repository.DataRepository
import javax.inject.Inject

class StoresViewModel @Inject constructor(private val dataRepository: DataRepository) :
    ViewModel() {

    var storesListModelModel = MutableLiveData<StoresListModel>()
    //var storeFilterModelModel = MutableLiveData<StoreFilterModel>()
    var storeCategoriesModelModel = MutableLiveData<StoreCategoriesModel>()
    var throwableResponse = MutableLiveData<Throwable?>()

    fun getStoresList(
        lang: String,
        token: String,
        page: Int,
        random: Boolean? = null,
        name: String? = null,
        phoneNumber: String? = null,
        isFeatured: Boolean? = null,
        storeCategoryId: Int? = null,
        hasVisa: Boolean? = null,
        hasMastercard: Boolean? = null,
        hasPaypal: Boolean? = null,
        hasCash: Boolean? = null,
    ) {
        dataRepository.getStoresList(
            lang,
            token,
            page,
            random,
            name,
            phoneNumber,
            isFeatured,
            storeCategoryId,
            hasVisa,
            hasMastercard,
            hasPaypal,
            hasCash,
            object : DataRepository.ServiceResponse<StoresListModel> {
                override fun successResponse(
                    response: StoresListModel,
                    responseCode: Int
                ) {
                    storesListModelModel.value = response
                }

                override fun ErrorResponse(t: Throwable?) {
                    throwableResponse.value = t
                }
            })
    }

    /*fun filterStoresByCategory(
        token: String,
        url: String,
    ) {
        dataRepository.filterStoresByCategory(
            token,
            url,
            object : DataRepository.ServiceResponse<StoreFilterModel> {
                override fun successResponse(
                    response: StoreFilterModel,
                    responseCode: Int
                ) {
                    storeFilterModelModel.value = response
                }

                override fun ErrorResponse(t: Throwable?) {
                    throwableResponse.value = t
                }
            })
    }*/

   /* fun searchStores(
        token: String,
        url: String,
    ) {
        dataRepository.searchStores(
            token,
            url,
            object : DataRepository.ServiceResponse<StoreFilterModel> {
                override fun successResponse(
                    response: StoreFilterModel,
                    responseCode: Int
                ) {
                    storeFilterModelModel.value = response
                }

                override fun ErrorResponse(t: Throwable?) {
                    throwableResponse.value = t
                }
            })
    }*/

    fun getStoreCategories(
        token: String,
        lang: String,
    ) {
        dataRepository.getStoreCategories(
            token,
            lang,
            object : DataRepository.ServiceResponse<StoreCategoriesModel> {
                override fun successResponse(
                    response: StoreCategoriesModel,
                    responseCode: Int
                ) {
                    storeCategoriesModelModel.value = response
                }

                override fun ErrorResponse(t: Throwable?) {
                    throwableResponse.value = t
                }
            })
    }


}