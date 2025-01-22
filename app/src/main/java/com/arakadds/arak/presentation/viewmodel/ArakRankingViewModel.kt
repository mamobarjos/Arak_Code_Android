package com.arakadds.arak.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.ranking.UsersRankingModel
import com.arakadds.arak.repository.DataRepository
import javax.inject.Inject

class ArakRankingViewModel @Inject constructor(private val dataRepository: DataRepository) :
    ViewModel() {

    var usersRankingModelModel = MutableLiveData<UsersRankingModel>()
    var throwableResponse = MutableLiveData<Throwable?>()
    fun getArakRanking(
        token: String,
        lang: String,
        page: Int,
    ) {
        dataRepository.getArakRanking(
            token,
            lang,
            page,
            object : DataRepository.ServiceResponse<UsersRankingModel> {
                override fun successResponse(
                    response: UsersRankingModel,
                    responseCode: Int
                ) {
                    usersRankingModelModel.value = response
                }

                override fun ErrorResponse(t: Throwable?) {
                    throwableResponse.value = t
                }
            })
    }
}