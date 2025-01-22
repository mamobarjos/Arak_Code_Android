package com.arakadds.arak.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.BannersModel
import com.arakadds.arak.model.new_mapping_refactore.home.HomeAdsModel
import com.arakadds.arak.repository.DataRepository
import javax.inject.Inject

class HomeAdsViewModel @Inject constructor(private val dataRepository: DataRepository) :
    ViewModel() {

    var homeAdsModelModel = MutableLiveData<HomeAdsModel?>()
    var specialAdsModelModel = MutableLiveData<HomeAdsModel?>()
    var bannersModelModel = MutableLiveData<BannersModel?>()
    var throwableResponse = MutableLiveData<Throwable?>()

    fun getHomeAds(
        lang: String,
        token: String,
        isFeatured: Boolean? = null,
        adCategoryId: String? = null,
        textSearch: String? = null,
        page: Int,
    ) {
        dataRepository.getHomeAds(
            lang,
            token,
            isFeatured,
            adCategoryId,
            textSearch,
            page,
            object : DataRepository.ServiceResponse<HomeAdsModel> {
                override fun successResponse(
                    response: HomeAdsModel,
                    responseCode: Int
                ) {
                    if (isFeatured == true) {
                        specialAdsModelModel.value = response
                    } else {
                        homeAdsModelModel.value = response
                    }
                }

                override fun ErrorResponse(t: Throwable?) {
                    throwableResponse.value = t
                }
            })
    }

    fun getSpecialAds(
        lang: String,
        token: String,
        page: Int,
        adCategoryId: String? = null,
        textSearch: String? = null,
    ) {
        dataRepository.getHomeAds(
            lang,
            token,
            isFeatured = true,
            adCategoryId,
            textSearch,
            page,
            object : DataRepository.ServiceResponse<HomeAdsModel> {
                override fun successResponse(
                    response: HomeAdsModel,
                    responseCode: Int
                ) {
                    specialAdsModelModel.value = response
                }

                override fun ErrorResponse(t: Throwable?) {
                    throwableResponse.value = t
                }
            })
    }

    fun getBanners(
        lang: String,
        token: String,
        bannerType: String,
    ) {
        dataRepository.getBanners(
            lang,
            token,
            bannerType,
            object : DataRepository.ServiceResponse<BannersModel> {
                override fun successResponse(
                    response: BannersModel,
                    responseCode: Int
                ) {
                    bannersModelModel.value = response
                }

                override fun ErrorResponse(t: Throwable?) {
                    throwableResponse.value = t
                }
            }
        )
    }
}