package com.arakadds.arak.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arakadds.arak.model.new_mapping_refactore.request.auth.RegistrationBodyRequest
import com.arakadds.arak.model.new_mapping_refactore.response.banners.auth.RegistrationModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.cities.CitiesModel
import com.arakadds.arak.model.new_mapping_refactore.response.banners.countries.CountriesModel
import com.arakadds.arak.repository.DataRepository
import javax.inject.Inject

class CreateAccountViewModel @Inject constructor(private val dataRepository: DataRepository) :
    ViewModel() {

    var registrationModelModel = MutableLiveData<RegistrationModel>()
    var countriesModel = MutableLiveData<CountriesModel>()
    var citiesModel = MutableLiveData<CitiesModel>()


    /*var userProfileModel = MutableLiveData<UserProfileModel>()
    var countriesModel = MutableLiveData<CountriesModel>()
    var baseResponse = MutableLiveData<BaseResponse>()*/
    var throwableResponse = MutableLiveData<Throwable?>()

    fun createAccount(lang: String, registrationBodyRequest: RegistrationBodyRequest) {
        dataRepository.createAccount(
            lang,
            registrationBodyRequest,
            object : DataRepository.ServiceResponse<RegistrationModel> {
                override fun successResponse(
                    response: RegistrationModel,
                    responseCode: Int
                ) {
                    registrationModelModel.value = response
                }

                override fun ErrorResponse(t: Throwable?) {
                    throwableResponse.value = t
                }
            })
    }

    fun getCountries(lang: String) {
        dataRepository.getCountries(
            lang,
            object : DataRepository.ServiceResponse<CountriesModel> {
                override fun successResponse(
                    response: CountriesModel,
                    responseCode: Int
                ) {
                    countriesModel.value = response
                }

                override fun ErrorResponse(t: Throwable?) {
                    throwableResponse.value = t
                }
            })
    }

    fun getCities(
        countryId: Int,
        lang: String
    ) {
        dataRepository.getCities(
            countryId,
            lang,
            object : DataRepository.ServiceResponse<CitiesModel> {
                override fun successResponse(
                    response: CitiesModel,
                    responseCode: Int
                ) {
                    citiesModel.value = response
                }

                override fun ErrorResponse(t: Throwable?) {
                    throwableResponse.value = t
                }
            })
    }

    /* fun getUserProfile(token: String) {
         dataRepository.getUserProfile(
             token,
             object : DataRepository.ServiceResponse<UserProfileModel> {
                 override fun successResponse(response: UserProfileModel, responseCode: Int) {
                     userProfileModel.value = response
                 }

                 override fun ErrorResponse(t: Throwable?) {
                     throwableResponse.value = t
                 }
             })
     }

     fun updateUserProfile(token: String, updateUserProfileRequest: UpdateUserProfileRequest) {
         dataRepository.updateUserProfile(
             token,
             updateUserProfileRequest,
             object : DataRepository.ServiceResponse<BaseResponse> {
                 override fun successResponse(response: BaseResponse, responseCode: Int) {
                     baseResponse.value = response
                 }

                 override fun ErrorResponse(t: Throwable?) {
                     throwableResponse.value = t
                 }
             })
     }

     */
}