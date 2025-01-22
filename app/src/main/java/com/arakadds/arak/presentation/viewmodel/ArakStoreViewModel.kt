package com.arakadds.arak.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arakadds.arak.model.ArakStoreMakeOrder
import com.arakadds.arak.model.CustomPackage
import com.arakadds.arak.model.ProductVariant
import com.arakadds.arak.model.arakStoreModel.StoreCategory
import com.arakadds.arak.model.arakStoreModel.StoreProduct
import com.arakadds.arak.model.new_mapping_refactore.BaseResponse
import com.arakadds.arak.repository.DataRepository
import javax.inject.Inject

class ArakStoreViewModel @Inject constructor(private val dataRepository: DataRepository) :
    ViewModel()  {

    var baseResponseModel = MutableLiveData<StoreCategory>()
    var baseResponseProductModel = MutableLiveData<StoreProduct>()
    var baseResponseProductVariantModel = MutableLiveData<ProductVariant>()
    var baseResponseCustomPackageModel = MutableLiveData<CustomPackage>()
    var baseResponseBaseResponseModel = MutableLiveData<BaseResponse>()
    var baseResponseOrderModel = MutableLiveData<ArakStoreMakeOrder>()

    var throwableResponse = MutableLiveData<Throwable?>()

    fun getArakStoreCategory(
        token: String,
        lang: String,
    ) {
        dataRepository.getArakStoreCategory(
            token,
            lang,
            object : DataRepository.ServiceResponse<StoreCategory> {
                override fun successResponse(
                    response: StoreCategory,
                    responseCode: Int
                ) {
                    baseResponseModel.postValue(response)
                }

                override fun ErrorResponse(t: Throwable?) {
                    throwableResponse.value = t
                }
            }
        )
    }


    fun getArakStoreProduct(
        token: String,
        lang: String,
        category: Int?=null,
        page: Int?=null,
        perPage: Int?=null,
    ) {
        dataRepository.getArakStoreProduct(
            token,
            lang,
            category,
            page,
            perPage,
            object : DataRepository.ServiceResponse<StoreProduct> {
                override fun successResponse(
                    response: StoreProduct,
                    responseCode: Int
                ) {
                    baseResponseProductModel.value = response
                }

                override fun ErrorResponse(t: Throwable?) {
                    throwableResponse.value = t
                }
            })
    }


    fun getArakStoreProductVariant(
        token: String,
        lang: String,
        productID:String
    ) {
        dataRepository.getArakStoreProductVariant(
            token,
            lang,
            productID,
            object : DataRepository.ServiceResponse<ProductVariant> {
                override fun successResponse(
                    response: ProductVariant,
                    responseCode: Int
                ) {
                    baseResponseProductVariantModel.value = response
                }

                override fun ErrorResponse(t: Throwable?) {
                    throwableResponse.value = t
                }
            })
    }


    fun makeOrderArakStore(
        token: String,
        lang: String,
        orderHashMap: HashMap<String, Any>
    ) {
        dataRepository.makeOrderArakStore(
            token,
            lang,
            orderHashMap,
            object : DataRepository.ServiceResponse<ArakStoreMakeOrder> {
                override fun successResponse(
                    response: ArakStoreMakeOrder,
                    responseCode: Int
                ) {
                    baseResponseOrderModel.value = response
                }

                override fun ErrorResponse(t: Throwable?) {
                    throwableResponse.value = t
                }
            })
    }




    fun CustomPackageData(
        token: String,
        lang: String,
    ) {
        dataRepository.CustomPackageData(
            token,
            lang,
            object : DataRepository.ServiceResponse<CustomPackage> {
                override fun successResponse(
                    response: CustomPackage,
                    responseCode: Int
                ) {
                    baseResponseCustomPackageModel.value = response
                }

                override fun ErrorResponse(t: Throwable?) {
                    throwableResponse.value = t
                }
            })
    }


    /*fun CheckoutCustomPackageData(
        token: String,
        lang: String,
        params: HashMap<String, Any>,
    ) {
        dataRepository.CheckoutCustomPackageData(
            token,
            lang,
            params,
            object : DataRepository.ServiceResponse<CustomPackage> {
                override fun successResponse(
                    response: CustomPackage,
                    responseCode: Int
                ) {
                    baseResponseCustomPackageModel.value = response
                }

                override fun ErrorResponse(t: Throwable?) {
                    throwableResponse.value = t
                }
            })
    }*/


    fun userInterests(
        token: String,
        lang: String,
        params: HashMap<String, Any>,
    ) {
        dataRepository.userInterests(
            token,
            lang,
            params,
            object : DataRepository.ServiceResponse<BaseResponse> {
                override fun successResponse(
                    response: BaseResponse,
                    responseCode: Int
                ) {
                    baseResponseBaseResponseModel.value = response
                }

                override fun ErrorResponse(t: Throwable?) {
                    throwableResponse.value = t
                }
            })
    }




}