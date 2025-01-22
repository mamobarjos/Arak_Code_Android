package com.arakadds.arak.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.packages.PackagesDetails
import com.arakadds.arak.repository.DataRepository
import javax.inject.Inject

class CategoryPackagesViewModel @Inject constructor(private val dataRepository: DataRepository) :
    ViewModel() {

    var packagesDetailsModel = MutableLiveData<PackagesDetails>()
    //var customPackageDataModel = MutableLiveData<CustomPackageData>()
    //var createCustomPackageDataModel = MutableLiveData<CreateCustomPackageData>()
    var throwableResponse = MutableLiveData<Throwable?>()

    fun getCategoryPackages(
        lang: String,
        token: String,
        id: String
    ) {
        dataRepository.getCategoryPackages(
            lang,
            token,
            id,
            object : DataRepository.ServiceResponse<PackagesDetails> {
                override fun successResponse(
                    response: PackagesDetails,
                    responseCode: Int
                ) {
                    packagesDetailsModel.value = response
                }

                override fun ErrorResponse(t: Throwable?) {
                    throwableResponse.value = t
                }
            })
    }

    /*fun getCustomPackagesDropdownLists(
        token: String,
    ) {
        dataRepository.getCustomPackagesDropdownLists(
            token,
            object : DataRepository.ServiceResponse<CustomPackageData> {
                override fun successResponse(
                    response: CustomPackageData,
                    responseCode: Int
                ) {
                    customPackageDataModel.value = response
                }

                override fun ErrorResponse(t: Throwable?) {
                    throwableResponse.value = t
                }
            })
    }*/

    /*fun createCustomPackage(
        token: String,
        createCustomPackageHashMap: HashMap<String?, String?>
    ) {
        dataRepository.createCustomPackage(
            token,
            createCustomPackageHashMap,
            object : DataRepository.ServiceResponse<CreateCustomPackageData> {
                override fun successResponse(
                    response: CreateCustomPackageData,
                    responseCode: Int
                ) {
                    createCustomPackageDataModel.value = response
                }

                override fun ErrorResponse(t: Throwable?) {
                    throwableResponse.value = t
                }
            })
    }*/
}