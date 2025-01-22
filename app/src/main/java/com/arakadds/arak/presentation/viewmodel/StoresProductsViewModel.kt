package com.arakadds.arak.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arakadds.arak.model.new_mapping_refactore.store.StoreProductsDataModel
import com.arakadds.arak.model.new_mapping_refactore.BaseResponse
import com.arakadds.arak.model.new_mapping_refactore.request.AddStoreProductReviewRequest
import com.arakadds.arak.model.new_mapping_refactore.request.AddStoreReviewRequest
import com.arakadds.arak.model.new_mapping_refactore.request.create_store_product.CreateProductRequestBody
import com.arakadds.arak.model.new_mapping_refactore.store.SingleProductModel
import com.arakadds.arak.model.new_mapping_refactore.store.StoreProductsModel
import com.arakadds.arak.model.new_mapping_refactore.store.products.RelatedStoreProductsModel
import com.arakadds.arak.model.new_mapping_refactore.store.products.my_products.MyStoreProductsModel
import com.arakadds.arak.model.new_mapping_refactore.store.reviews.CreateReviewModel
import com.arakadds.arak.repository.DataRepository
import javax.inject.Inject

class StoresProductsViewModel @Inject constructor(private val dataRepository: DataRepository) :
    ViewModel() {

    var storeProductsModelModel = MutableLiveData<StoreProductsModel>()
    var relatedStoreProductsModelModel = MutableLiveData<RelatedStoreProductsModel>()
    var myStoreProductsModelModel = MutableLiveData<MyStoreProductsModel>()
    var singleProductModelModel = MutableLiveData<SingleProductModel>()
    var storeProductsDataModelModel = MutableLiveData<StoreProductsDataModel>()
    var baseResponseModel = MutableLiveData<BaseResponse>()
    var createReviewModelModel = MutableLiveData<CreateReviewModel>()
    var throwableResponse = MutableLiveData<Throwable?>()

    fun getStoreProducts(
        token: String,
        lang: String,
        page: Int,
        random: Boolean? = null,
        storeId: Int? = null,
        storeCategoryId: Int? = null,
    ) {
        dataRepository.getStoreProducts(
            token,
            lang,
            page,
            random,
            storeId,
            storeCategoryId,
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
    }

    fun getRelatedStoreProducts(
        token: String,
        lang: String,
        id: String,
    ) {
        dataRepository.getRelatedStoreProducts(
            token,
            lang,
            id,
            object : DataRepository.ServiceResponse<RelatedStoreProductsModel> {
                override fun successResponse(
                    response: RelatedStoreProductsModel,
                    responseCode: Int
                ) {
                    relatedStoreProductsModelModel.value = response
                }

                override fun ErrorResponse(t: Throwable?) {
                    throwableResponse.value = t
                }
            })
    }

    fun getMyStoreProducts(
        token: String,
        lang: String,
        page: Int,
    ) {
        dataRepository.getMyStoreProducts(
            token,
            lang,
            page,
            object : DataRepository.ServiceResponse<MyStoreProductsModel> {
                override fun successResponse(
                    response: MyStoreProductsModel,
                    responseCode: Int
                ) {
                    myStoreProductsModelModel.value = response
                }

                override fun ErrorResponse(t: Throwable?) {
                    throwableResponse.value = t
                }
            })
    }

    //new app part
    fun getStoreProduct(
        token: String,
        lang: String,
        isRandomProducts: Boolean? = null,
    ) {
        dataRepository.getStoreProduct(
            token,
            lang,
            isRandomProducts,
            object : DataRepository.ServiceResponse<StoreProductsDataModel> {
                override fun successResponse(
                    response: StoreProductsDataModel,
                    responseCode: Int
                ) {
                    storeProductsDataModelModel.value = response
                }

                override fun ErrorResponse(t: Throwable?) {
                    throwableResponse.value = t
                }
            })
    }

    fun getSingleProduct(
        token: String,
        lang: String,
        id: String,
    ) {
        dataRepository.getSingleProduct(
            token,
            lang,
            id,
            object : DataRepository.ServiceResponse<SingleProductModel> {
                override fun successResponse(
                    response: SingleProductModel,
                    responseCode: Int
                ) {
                    singleProductModelModel.value = response
                }

                override fun ErrorResponse(t: Throwable?) {
                    throwableResponse.value = t
                }
            })
    }

    fun createProduct(
        token: String,
        lang: String,
        createProductRequestBody: CreateProductRequestBody
    ) {
        dataRepository.createProduct(
            token,
            lang,
            createProductRequestBody,
            object : DataRepository.ServiceResponse<SingleProductModel> {
                override fun successResponse(
                    response: SingleProductModel,
                    responseCode: Int
                ) {
                    singleProductModelModel.value = response
                }

                override fun ErrorResponse(t: Throwable?) {
                    throwableResponse.value = t
                }
            })
    }

    fun updateProduct(
        token: String,
        lang: String,
        id: Int,
        createProductRequestBody: CreateProductRequestBody,
    ) {
        dataRepository.updateProduct(
            token,
            lang,
            id,
            createProductRequestBody,
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

    /*fun updateProduct(
        token: String,
        id: String,
        updateProductHashMap: HashMap<String, Any>
    ) {
        dataRepository.updateProduct(
            token,
            id,
            updateProductHashMap,
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
    }*/

    fun addProductReview(
        token: String,
        lang: String,
        addStoreProductReviewRequest: AddStoreProductReviewRequest
    ) {
        dataRepository.addProductReview(
            token,
            lang,
            addStoreProductReviewRequest,
            object : DataRepository.ServiceResponse<CreateReviewModel> {
                override fun successResponse(
                    response: CreateReviewModel,
                    responseCode: Int
                ) {
                    createReviewModelModel.value = response
                }

                override fun ErrorResponse(t: Throwable?) {
                    throwableResponse.value = t
                }
            })
    }

    fun deleteProduct(
        token: String,
        lang: String,
        id: Int,
    ) {
        dataRepository.deleteProduct(
            token,
            lang,
            id,
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

    fun deleteProductReview(
        token: String,
        lang: String,
        id: Int,
    ) {
        dataRepository.deleteProductReview(
            token,
            lang,
            id,
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

    /*fun getProductsByCategory(
        token: String,
        url: String
    ) {
        dataRepository.getProductsByCategory(
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