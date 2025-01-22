package com.arakadds.arak.common.preferaence

import android.content.Context
import android.content.SharedPreferences
import com.arakadds.arak.utils.AppEnums

open class PreferenceManager constructor(context: Context) : IPreferenceHelper {
    private val PREFS_NAME = "SharedPreference"
    private var preferences: SharedPreferences

    init {
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    override fun setToken(token: String) {
        preferences[TOKEN] = token
    }
    override fun getToken(): String {
        return preferences[TOKEN] ?: ""
    }
    override fun setUserName(userName: String) {
        preferences[USER_NAME] = userName
    }
    override fun getUserName(): String {
        return preferences[USER_NAME] ?: ""
    }
    override fun setPassword(password: String?) {
        preferences[PASSWORD_ID] = password
    }
    override fun getPassword(): String? {
        return preferences[PASSWORD_ID]
    }
    override fun setFirstLunch(IsEnable: Boolean) {
        preferences[FIRST_LUNCH] = IsEnable
    }
    override fun isFirstLunch(): Boolean {
        return preferences[FIRST_LUNCH] ?: false
    }
    override fun setRefreshToken(token: String) {
        preferences[REFRESH_TOKEN] = token
    }
    override fun getRefreshToken(): String {
        return preferences[REFRESH_TOKEN] ?: ""
    }
    override fun setKeyUserId(keyUserId: Int) {
        preferences[KEY_USER_ID] = keyUserId
    }
    override fun getKeyUserId(): Int {
        return preferences[KEY_USER_ID] ?: 1
    }
    override fun setUserPhoneNumber(userPhoneNumber: String?) {
        preferences[KEY_USER_PHONE_NUMBER] = userPhoneNumber
    }
    override fun getUserPhoneNumber(): String? {
        return preferences[KEY_USER_PHONE_NUMBER]
    }
    override fun setSocialToken(socialToken: String?) {
        preferences[SOCIAL_TOKEN_ID] = socialToken
    }
    override fun getSocialToken(): String? {
        return preferences[SOCIAL_TOKEN_ID]
    }
    override fun setUserEmail(userEmail: String?) {
        preferences[KEY_USER_EMAIL] = userEmail
    }
    override fun getUserEmail(): String? {
        return preferences[KEY_USER_EMAIL]
    }
    override fun setUserStatus(userStatus: Int) {
        preferences[KEY_USER_STATUS] = userStatus
    }
    override fun getUserStatus(): Int {
        return preferences[KEY_USER_STATUS] ?: 1
    }
    override fun setUserRole(userRole: String) {
        preferences[KEY_USER_ROLE] = userRole
    }
    override fun getUserRole(): String {
        return preferences[KEY_USER_ROLE] ?:""
    }
    override fun setUserBalance(userBalance: Float) {
        preferences[KEY_USER_BALANCE] = userBalance
    }
    override fun getUserBalance(): Float {
        return preferences[KEY_USER_BALANCE] ?: 1f
    }
    override fun setUserFullName(userFullName: String?) {
        preferences[KEY_USER_FULL_NAME] = userFullName
    }
    override fun getUserFullName(): String? {
        return preferences[KEY_USER_FULL_NAME]
    }
    override fun getUserCountry(): String? {
        return preferences[KEY_USER_COUNTRY]
    }
    override fun setUserCountry(userCountry: String?) {
        preferences[KEY_USER_COUNTRY] = userCountry
    }
    override fun getUserCity(): String? {
        return preferences[KEY_USER_CITY]
    }
    override fun setUserCity(userCity: String?) {
        preferences[KEY_USER_CITY] = userCity
    }
    override fun getUserGender(): String? {
        return preferences[KEY_USER_GENDER]
    }
    override fun setUserGender(userGender: String?) {
        preferences[KEY_USER_GENDER] = userGender
    }
    override fun getUserImage(): String? {
        return preferences[KEY_USER_IMAGE]
    }
    override fun setUserImage(userImage: String?) {
        preferences[KEY_USER_IMAGE] = userImage
    }
    override fun getCompanyName(): String? {
        return preferences[COMPANY_NAME]
    }
    override fun setCompanyName(companyName: String?) {
        preferences[COMPANY_NAME] = companyName
    }
    override fun setUserHasStore(hasStore: Boolean) {
        preferences[USER_HAVE_STORE] = hasStore
    }
    override fun isUserHasStore(): Boolean {
        return preferences[USER_HAVE_STORE] ?: false
    }
    override fun getLat(): String? {
        return preferences[KEY_LAT]
    }
    override fun setLat(lat: String?) {
        preferences[KEY_LAT] = lat
    }
    override fun getLon(): String? {
        return preferences[KEY_LON]
    }
    override fun setLon(lon: String?) {
        preferences[KEY_LON] = lon
    }
    override fun getSelectedLocationName(): String {
        return preferences[SELECTED_LOCATION_NAME] ?: ""
    }
    override fun setSelectedLocationName(selectedLocationName: String) {
        preferences[SELECTED_LOCATION_NAME] = selectedLocationName
    }
    override fun setNotificationStatus(notificationEnabledStatus: Boolean) {
        preferences[KEY_NOTIFICATION_STATUS] = notificationEnabledStatus
    }
    override fun getNotificationStatus(): Boolean {
        return preferences[KEY_NOTIFICATION_STATUS] ?: false
    }
    override fun setStoreId(StoreId: Int) {
        preferences[STORE_ID] = StoreId
    }
    override fun getStoreId(): Int {
        return preferences[STORE_ID] ?: 1
    }
    override fun setStoreEmail(storeEmail: String) {
        preferences[STORE_EMAIL_ID] = storeEmail
    }
    override fun getStoreEmail(): String {
        return preferences[STORE_EMAIL_ID] ?: ""

    }
    override fun setStoreFullName(storeFullName: String) {
        preferences[STORE_FULL_NAME_ID] = storeFullName
    }
    override fun getStoreFullName(): String {
        return preferences[STORE_FULL_NAME_ID] ?: ""

    }
    override fun setStoreImageAvatar(storeImageAvatar: String) {
        preferences[STORE_IMG_AVATAR_ID] = storeImageAvatar
    }
    override fun getStoreImageAvatar(): String {
        return preferences[STORE_IMG_AVATAR_ID] ?: ""

    }
    override fun setProductId(storeFullName: Int) {
        preferences[PRODUCT_ID] = storeFullName
    }
    override fun getProductId(): Int {
        return preferences[PRODUCT_ID] ?: 1

    }
    override fun setBirthOfDate(birthOfDate: String?) {
        preferences[BIRTH_DATE] = birthOfDate
    }
    override fun getBirthOfDate(): String? {
        return preferences[BIRTH_DATE]

    }
    override fun getFCMToken(): String? {
        return preferences[FCM_TOKEN]
    }
    override fun setFCMToken(userCity: String?) {
        preferences[FCM_TOKEN] = userCity
    }


    override fun setHasWallet(hasWallet: Boolean) {
        preferences[HAS_WALLET] = hasWallet
    }
    override fun isHasWallet(): Boolean {
        return preferences[HAS_WALLET] ?: false
    }

    override fun setCountryNameAr(countryNameAr: String) {
        preferences[COUNTRY_NAME_AR] = countryNameAr
    }

    override fun getCountryNameAr(): String {
        return preferences[COUNTRY_NAME_AR]?: ""
    }

    override fun setCountryNameEn(countryNameEn: String) {
        preferences[COUNTRY_NAME_EN] = countryNameEn
    }

    override fun getCountryNameEn(): String {
        return preferences[COUNTRY_NAME_EN]?: ""
    }

    override fun setCountryCode(countryCode: String) {
        preferences[COUNTRY_CODE] = countryCode

    }

    override fun getCountryCode(): String {
        return preferences[COUNTRY_CODE]?: ""
    }

    override fun setCurrencySymbol(currencySymbol: String) {
        preferences[CURRANCY_SYMBOL] = currencySymbol
    }

    override fun getCurrencySymbol(): String {
        return preferences[CURRANCY_SYMBOL]?: ""
    }

    override fun setCityNameAr(cityNameAr: String) {
        preferences[CITY_NAME_AR] = cityNameAr
    }

    override fun getCityNameAr(): String {
        return preferences[CITY_NAME_AR]?: ""
    }

    override fun setCityNameEn(cityNameEn: String) {
        preferences[CITY_NAME_EN] = cityNameEn
    }

    override fun getCityNameEn(): String {
        return preferences[CITY_NAME_EN]?: ""
    }

    override fun setIsActive(isActive: Boolean) {
        preferences[IS_ACTIVE] = isActive
    }
    override fun isActive(): Boolean {
        return preferences[IS_ACTIVE] ?: false
    }

    override fun setNotificationsEnabled(isNotificationsEnabled: Boolean) {
        preferences[NOTIFICATIONS_ENABLED] = isNotificationsEnabled
    }
    override fun isNotificationsEnabled(): Boolean {
        return preferences[NOTIFICATIONS_ENABLED] ?: false
    }

    override fun setBiometricEnabled(isBiometricEnabled: Boolean) {
        preferences[BIOMETRIC_ENABLED] = isBiometricEnabled
    }
    override fun isBiometricEnabled(): Boolean {
        return preferences[BIOMETRIC_ENABLED] ?: false
    }


    override fun clearPrefs() {
        preferences.edit().clear().apply()
    }

    override fun getLanguage(): String {
        return preferences[LANGUAGE_KEY] ?: AppEnums.LanguagesEnums.ENGLISH
    }
    override fun setLanguage(lang: String) {
        preferences[LANGUAGE_KEY] = lang
    }

    companion object {
        const val TOKEN = "TOKEN"
        const val REFRESH_TOKEN = "REFRESH_TOKEN"
        const val USER_NAME = "USER_NAME"
        const val PASSWORD_ID = "PASSWORD_ID"
        const val FIRST_LUNCH = "FIRST_LUNCH"
        const val LANGUAGE_KEY = "languageKey"
        const val KEY_USER_ID = "KEY_USER_ID"
        const val KEY_USER_PHONE_NUMBER = "KEY_USER_PHONE_NUMBER"
        const val SOCIAL_TOKEN_ID = "SOCIAL_TOKEN_ID"
        const val EMAIL_ID = "EMAIL_ID"
        const val KEY_USER_STATUS = "KEY_USER_STATUS"
        const val KEY_USER_ROLE = "KEY_USER_ROLE"
        const val KEY_USER_BALANCE = "KEY_USER_BALANCE"
        const val KEY_USER_FULL_NAME = "KEY_USER_FULL_NAME"
        const val KEY_USER_EMAIL = "KEY_USER_EMAIL"
        const val KEY_USER_COUNTRY = "KEY_USER_COUNTRY"
        const val KEY_USER_CITY = "KEY_USER_CITY"
        const val KEY_USER_GENDER = "KEY_USER_GENDER"
        const val KEY_USER_IMAGE = "KEY_USER_IMAGE"
        const val COMPANY_NAME = "COMPANY_NAME"
        const val USER_HAVE_STORE = "USER_HAVE_STORE"
        const val KEY_LAT = "KEY_LAT"
        const val KEY_LON = "KEY_LON"
        const val SELECTED_LOCATION_NAME = "SELECTED_LOCATION_NAME"
        const val KEY_NOTIFICATION_STATUS = "KEY_NOTIFICATION_STATUS"
        const val STORE_ID = "STORE_ID"
        const val STORE_EMAIL_ID = "STORE_EMAIL_ID"
        const val STORE_FULL_NAME_ID = "STORE_FULL_NAME_ID"
        const val STORE_IMG_AVATAR_ID = "STORE_IMG_AVATAR_ID"
        const val PRODUCT_ID = "PRODUCT_ID"
        const val BIRTH_DATE = "BIRTH_DATE"
        const val FCM_TOKEN = "FCM_TOKEN"
        const val HAS_WALLET = "HAS_WALLET"
        const val IS_ACTIVE = "IS_ACTIVE"
        const val NOTIFICATIONS_ENABLED = "NOTIFICATIONS_ENABLED"
        const val BIOMETRIC_ENABLED = "BIOMETRIC_ENABLED"

        const val COUNTRY_NAME_AR = "COUNTRY_NAME_AR"
        const val COUNTRY_NAME_EN = "COUNTRY_NAME_EN"
        const val COUNTRY_CODE = "COUNTRY_CODE"
        const val CURRANCY_SYMBOL = "CURRANCY_SYMBOL"
        const val CITY_NAME_AR = "CITY_NAME_AR"
        const val CITY_NAME_EN = "CITY_NAME_EN"


    }
}

/**
 * SharedPreferences extension function, to listen the edit() and apply() fun calls
 * on every SharedPreferences operation.
 */
private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
    val editor = this.edit()
    operation(editor)
    editor.apply()
}

/**
 * puts a key value pair in shared prefs if doesn't exists, otherwise updates value on given [key]
 */
private operator fun SharedPreferences.set(key: String, value: Any?) {
    when (value) {
        is String? -> edit { it.putString(key, value) }
        is Int -> edit { it.putInt(key, value) }
        is Boolean -> edit { it.putBoolean(key, value) }
        is Float -> edit { it.putFloat(key, value) }
        is Long -> edit { it.putLong(key, value) }
        else -> throw UnsupportedOperationException("Not yet implemented")
    }
}

/**
 * finds value on given key.
 * [T] is the type of value
 * @param defaultValue optional default value - will take null for strings, false for bool and -1 for numeric values if [defaultValue] is not specified
 */
private inline operator fun <reified T : Any> SharedPreferences.get(
    key: String,
    defaultValue: T? = null
): T? {
    return when (T::class) {
        String::class -> getString(key, defaultValue as? String) as T?
        Int::class -> getInt(key, defaultValue as? Int ?: -1) as T?
        Boolean::class -> getBoolean(key, defaultValue as? Boolean ?: false) as T?
        Float::class -> getFloat(key, defaultValue as? Float ?: -1f) as T?
        Long::class -> getLong(key, defaultValue as? Long ?: -1) as T?
        else -> throw UnsupportedOperationException("Not yet implemented")
    }
}