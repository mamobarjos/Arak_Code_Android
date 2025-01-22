package com.arakadds.arak.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.transactions.UserTransactionsModel
import com.arakadds.arak.repository.DataRepository
import javax.inject.Inject

class UserTransactionsViewModel @Inject constructor(private val dataRepository: DataRepository) :
    ViewModel() {

    var userTransactionsModelModel = MutableLiveData<UserTransactionsModel>()
    var throwableResponse = MutableLiveData<Throwable?>()

    fun getUserTransactions(
        token: String,
        lang: String,
        page: Int? = null,
        dateFrom: String? = null,
        dateTo: String? = null,
    ) {
        dataRepository.getUserTransactions(
            token,
            lang,
            page,
            dateFrom,
            dateTo,
            object : DataRepository.ServiceResponse<UserTransactionsModel> {
                override fun successResponse(
                    response: UserTransactionsModel,
                    responseCode: Int
                ) {
                    userTransactionsModelModel.value = response
                }

                override fun ErrorResponse(t: Throwable?) {
                    throwableResponse.value = t
                }
            })
    }
}