package com.arakadds.arak.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arakadds.arak.model.new_mapping_refactore.home.HomeAdsModel
import com.arakadds.arak.repository.DataRepository
import javax.inject.Inject

class UserAdsViewModel @Inject constructor(private val dataRepository: DataRepository) :
    ViewModel() {

    var homeAdsModelModel = MutableLiveData<HomeAdsModel>()
    var throwableResponse = MutableLiveData<Throwable?>()

    fun getMyAds(
        token: String,
        lang: String,
        dateFrom: String? = null,
        dateTo: String? = null,
        page: Int? = null,
    ) {
        dataRepository.getMyAds(
            token,
            lang,
            dateFrom,
            dateTo,
            page,
            object : DataRepository.ServiceResponse<HomeAdsModel> {
                override fun successResponse(
                    response: HomeAdsModel,
                    responseCode: Int
                ) {
                    homeAdsModelModel.value = response
                }

                override fun ErrorResponse(t: Throwable?) {
                    throwableResponse.value = t
                }
            })
    }

    fun getUserHistory(
        token: String,
        lang: String,
        page: Int
    ) {
        dataRepository.getUserHistory(
            token,
            lang,
            page,
            object : DataRepository.ServiceResponse<HomeAdsModel> {
                override fun successResponse(
                    response: HomeAdsModel,
                    responseCode: Int
                ) {
                    homeAdsModelModel.value = response
                }

                override fun ErrorResponse(t: Throwable?) {
                    throwableResponse.value = t
                }
            })
    }

    fun getUserFavorites(
        token: String,
        lang: String,
        page: Int,
    ) {
        dataRepository.getUserFavorites(
            token,
            lang,
            page,
            object : DataRepository.ServiceResponse<HomeAdsModel> {
                override fun successResponse(
                    response: HomeAdsModel,
                    responseCode: Int
                ) {
                    homeAdsModelModel.value = response
                }

                override fun ErrorResponse(t: Throwable?) {
                    throwableResponse.value = t
                }
            })
    }

    /*fun getMyFilteredAds(
        token: String,
        url: String,
    ) {
        dataRepository.getMyFilteredAds(
            token,
            url,
            object : DataRepository.ServiceResponse<HomeAdsModel> {
                override fun successResponse(
                    response: HomeAdsModel,
                    responseCode: Int
                ) {
                    homeAdsModelModel.value = response
                }

                override fun ErrorResponse(t: Throwable?) {
                    throwableResponse.value = t
                }
            })
    }*/
}