package com.arakadds.arak.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arakadds.arak.model.new_mapping_refactore.BaseResponse
import com.arakadds.arak.model.new_mapping_refactore.response.banners.auth.user_balance.UserBalanceModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.wallets.DigitalWalletsModel
import com.arakadds.arak.repository.DataRepository
import javax.inject.Inject

class WalletViewModel @Inject constructor(private val dataRepository: DataRepository) :
    ViewModel() {

    var baseResponseModel = MutableLiveData<BaseResponse>()
    var userBalanceModelModel = MutableLiveData<UserBalanceModel>()

    //var userTransactionsResponseModel = MutableLiveData<UserTransactionsResponse>()
    var digitalWalletsModelModel = MutableLiveData<DigitalWalletsModel>()
    var throwableResponse = MutableLiveData<Throwable?>()

    fun requestWithDraw(
        token: String,
        lang: String,
        makeTransactionHashMap: HashMap<String, String>,
    ) {
        dataRepository.requestWithDraw(
            token,
            lang,
            makeTransactionHashMap,
            object : DataRepository.ServiceResponse<BaseResponse> {
                override fun successResponse(
                    response: BaseResponse,
                    responseCode: Int
                ) {
                    baseResponseModel.value = response
                }

                override fun ErrorResponse(t: Throwable?) {
                    throwableResponse.value = t
                }
            })
    }

    fun getUserBalance(
        token: String,
        lang: String
    ) {
        dataRepository.getUserBalance(
            token,
            lang,
            object : DataRepository.ServiceResponse<UserBalanceModel> {
                override fun successResponse(
                    response: UserBalanceModel,
                    responseCode: Int
                ) {
                    userBalanceModelModel.value = response
                }

                override fun ErrorResponse(t: Throwable?) {
                    throwableResponse.value = t
                }
            })
    }


    fun getDigitalWallets(
        lang: String,
        token: String
    ) {
        dataRepository.getDigitalWallets(
            lang,
            token,
            object : DataRepository.ServiceResponse<DigitalWalletsModel> {
                override fun successResponse(
                    response: DigitalWalletsModel,
                    responseCode: Int
                ) {
                    digitalWalletsModelModel.value = response
                }

                override fun ErrorResponse(t: Throwable?) {
                    throwableResponse.value = t
                }
            })
    }

    fun consumeCoupon(
        token: String,
        lang: String,
        consumeCouponHashMap: HashMap<String, String>,
    ) {
        dataRepository.consumeCoupon(
            token,
            lang,
            consumeCouponHashMap,
            object : DataRepository.ServiceResponse<BaseResponse> {
                override fun successResponse(
                    response: BaseResponse,
                    responseCode: Int
                ) {
                    baseResponseModel.value = response
                }

                override fun ErrorResponse(t: Throwable?) {
                    throwableResponse.value = t
                }
            })
    }



    /*fun getUserFilteredTransaction(
        token: String,
        url: String,
    ) {
        dataRepository.getUserFilteredTransaction(
            token,
            url,
            object : DataRepository.ServiceResponse<UserTransactionsResponse> {
                override fun successResponse(
                    response: UserTransactionsResponse,
                    responseCode: Int
                ) {
                    userTransactionsResponseModel.value = response
                }

                override fun ErrorResponse(t: Throwable?) {
                    throwableResponse.value = t
                }
            })
    }*/
}