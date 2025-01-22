package com.arakadds.arak.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.ad_category.AdsCategoriesModel
import com.arakadds.arak.repository.DataRepository
import javax.inject.Inject

class AdsTypeViewModel @Inject constructor(private val dataRepository: DataRepository) :
    ViewModel() {

    var AdsCategoriesModelModel = MutableLiveData<AdsCategoriesModel>()
    var throwableResponse = MutableLiveData<Throwable?>()

    fun getAdsType(
        token: String,
        lang: String
    ) {
        dataRepository.getAdsType(
            token,
            lang,
            object : DataRepository.ServiceResponse<AdsCategoriesModel> {
                override fun successResponse(
                    response: AdsCategoriesModel,
                    responseCode: Int
                ) {
                    AdsCategoriesModelModel.value = response
                }

                override fun ErrorResponse(t: Throwable?) {
                    throwableResponse.value = t
                }
            })
    }


}