package com.arakadds.arak.common.preferaence

interface IPreferenceHelper {
    fun setToken(token: String)
    fun getToken(): String
    fun setRefreshToken(refreshToken: String)
    fun getRefreshToken(): String
    fun setUserName(token: String)
    fun getUserName(): String
    fun setPassword(token: String?)
    fun getPassword(): String?
    fun setFirstLunch(IsEnable: Boolean)
    fun isFirstLunch(): Boolean
    fun setKeyUserId(keyUserId: Int)
    fun getKeyUserId(): Int
    fun setUserPhoneNumber(userPhoneNumber: String?)
    fun getUserPhoneNumber(): String?
    fun setSocialToken(socialToken: String?)
    fun getSocialToken(): String?
    fun setUserEmail(userEmail: String?)
    fun getUserEmail(): String?
    fun setUserStatus(userStatus: Int)
    fun getUserStatus(): Int
    fun setUserRole(userRole: String)
    fun getUserRole(): String
    fun setUserBalance(userBalance: Float)
    fun getUserBalance(): Float
    fun setUserFullName(userFullName: String?)
    fun getUserFullName(): String?
    fun setUserCountry(userCountry: String?)
    fun getUserCountry(): String?
    fun setUserCity(userCity: String?)
    fun getUserCity(): String?
    fun setUserGender(userGender: String?)
    fun getUserGender(): String?
    fun setUserImage(userImage: String?)
    fun getUserImage(): String?
    fun setCompanyName(companyName: String?)
    fun getCompanyName(): String?
    fun setUserHasStore(hasStore: Boolean)
    fun isUserHasStore(): Boolean
    fun setLat(lat: String?)
    fun getLat(): String?
    fun setLon(lon: String?)
    fun getLon(): String?
    fun setSelectedLocationName(selectedLocationName: String)
    fun getSelectedLocationName(): String
    fun setNotificationStatus(notificationEnabledStatus: Boolean)
    fun getNotificationStatus(): Boolean
    fun setBiometricEnabled(biometricEnabledStatus: Boolean)
    fun isBiometricEnabled(): Boolean
    fun setStoreId(StoreId: Int)
    fun getStoreId(): Int
    fun setStoreEmail(storeEmail: String)
    fun getStoreEmail(): String
    fun setStoreFullName(storeFullName: String)
    fun getStoreFullName(): String
    fun setStoreImageAvatar(storeImageAvatar: String)
    fun getStoreImageAvatar(): String
    fun setProductId(storeFullName: Int)
    fun getProductId(): Int
    fun clearPrefs()
    fun getLanguage(): String
    fun setLanguage(lang: String)
    fun setBirthOfDate(birthOfDate: String?)
    fun getBirthOfDate(): String?

    fun setFCMToken(storeFullName: String?)
    fun getFCMToken(): String?

    fun setHasWallet(hasWallet: Boolean)
    fun isHasWallet(): Boolean

    fun setCountryNameAr(countryNameAr: String)
    fun getCountryNameAr(): String

    fun setCountryNameEn(countryNameEn: String)
    fun getCountryNameEn(): String

    fun setCountryCode(countryCode: String)
    fun getCountryCode(): String

    fun setCurrencySymbol(currencySymbol: String)
    fun getCurrencySymbol(): String

    fun setCityNameAr(cityNameAr: String)
    fun getCityNameAr(): String

    fun setCityNameEn(cityNameEn: String)
    fun getCityNameEn(): String

    fun setIsActive(isActive: Boolean)
    fun isActive(): Boolean
    fun setNotificationsEnabled(isNotificationsEnabled: Boolean)
    fun isNotificationsEnabled(): Boolean
}