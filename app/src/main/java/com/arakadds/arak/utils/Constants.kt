package com.arakadds.arak.utils

object Constants {

    val BASE_URL = "https://arakads.live/api/v2/"

    //val API_KEY = "yzjPf7Ng7ccQwJeBjVa6Uj3hWM8PcMyz"
    // val BASE_URL = "https://arak-be.solutionslap.com/api/v2/"
    val API_KEY = "w0QEzdIHjitCUB902JGf6q2xgyGKoP9A"

    const val GOOGLE_SERVER = "8.8.8.8" //GOOGLE SERVER
    const val GOOGLE_PORT = 53 //GOOGLE PORT


    //header request Constants
    const val userAgent = "android"
    const val buildVersion = "1.4.2"
    const val cashValue = "no-cache"

    //Language Constants
    const val ENGLISH = "en"
    const val ARABIC = "ar"

    val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA)
    val CAMERA_REQUEST_CODE = 1
    val GALLERY_REQUEST_CODE = 2
    val BARCODE_REQUEST_CODE = 3
    const val REQUEST_CODE_PERMISSIONS = 10

    const val TAG = "MMS_APP"
    const val CATEGORY_ID = "category_id"
    const val CHECK_OUT_URL_ID = "Checkout Url"
    const val PAYMENT_TYPE = "payment type web"
    const val selectedAdObject = "selectedAdObject"
    const val AD_VEDIO = "AD_VEDIO"
    const val IS_HOME = "isHomeAd"
    const val PAYMENT_TYPE_ID = "payment type"
    const val AD_PACKAGE_ID = "AD_PACKAGE_ID"
    const val PACKAGE_TIME_LONG_ID = "package time long"
    const val ADS_STATISTICS = "ADS_STATISTICS"
    const val NEW_AD_DATA = "new_ad_data"
    const val OTP_CODE = "otp_code"
    const val IS_FORGET_PASSWORD = "IS_FORGET_PASSWORD"
    const val USER_REGISTRATION_INFO = "user_registration_info"
    const val IS_REGISTRATION = "is_registration"
    const val SOCIAL_TOKEN = "social_token"
    const val IS_CHANGE_PASSWORD = "isChangePassword"
    const val IS_RESET_PHONE_NUMBER = "IS_RESET_PHONE_NUMBER"
    const val PRODUCT_FILES_INFORMATION = "product Files Information"
    const val POSITION = "position"
    const val UPLOADED_MEDIA_ARRAY_LIST = "uploaded media array list"
    const val AD_CATEGORY_ID = "Ad_category_id"
    const val IS_STORE_SEARCH = "IS_STORE_SEARCH"
    const val SELECTED_STORE_ID = "SELECTED_STORE_ID"
    const val SELECTED_CATEGORY_ID = "SELECTED_CATEGORY_ID"
    const val SELECTED_PRODUCT_ID = "SELECTED_PRODUCT_ID"
    const val SELECTED_PRODUCT_OBJECT_ID = "SELECTED_PRODUCT_OBJECT_ID"
    const val IS_EDIT_ID = "IS_EDIT_ID"
    const val STORE_ID = "STORE_ID"
    const val IS_NORMAL_AD = "IS_NORMAL_AD"
    const val REVIEWS = "REVIEWS"
    const val SELECTED_STORE_NAME = "SELECTED_STORE_NAME"
    const val CREATE_STORE_DATA_ID = "CREATE_STORE_DATA_ID"
    const val STORE_OBJECT_ID = "STORE_OBJECT_ID"
    const val RESULT_LOAD_IMG = 123
    const val RESULT_LOAD_VIDEO = 124
    const val AD_ID = "ad_id"
    const val AD_NAME = "AD_NAME"
    const val ERROR_DIALOG_REQUEST = 9001
    const val LOCATION_PERMISSION_REQUEST_CODE = 1234
    const val SELECT_IMAGE_PERMISSIONS_CODE = 1000
    const val PICK_IMAGE_GALLERY = 1001
    const val PICK_IMAGE_CAMERA = 1002
    const val DEFAULT_ZOOM = 18f
    const val MY_PERMISSIONS_REQUEST_CALL_PHONE = 8001
    const val ONESIGNAL_APP_ID = "b232d8d4-177e-40ba-92b6-9911344d0c2a"
    const val RC_SIGN_IN = 8050

    //biometric
    const val AUTHENTICATION_FAILED = "Authentication failed"
    const val AUTHENTICATION_SUCCEEDED = "Authentication succeeded"
    const val AUTHENTICATION_ERROR = "Authentication error"
    const val BIOMETRIC_AUTHENTICATION = "Biometric Authentication"
    const val BIOMETRIC_AUTHENTICATION_SUBTITLE = "Use your fingerprint to authenticate"
    const val BIOMETRIC_AUTHENTICATION_DESCRIPTION =
        "This app uses your makes use of device biometrics (user fingerprint) to authenticate the dialog."
    const val AUTHENTICATE_OTHER = "Authenticate using Device Password/PIN"
    const val AUTHENTICATE_FINGERPRINT = "Authenticate using Fingerprint"
    const val AVAILABLE = "Available"
    const val UNAVAILABLE = "Unavailable"
    const val TRUE = "True"
    const val FALSE = "False"
    const val CANCEL = "Cancel"
    const val PASSWORD_PIN_AUTHENTICATION = "Password/PIN Authentication"
    const val PASSWORD_PIN_AUTHENTICATION_SUBTITLE = "Authenticate using Device Password/PIN"
    const val PASSWORD_PIN_AUTHENTICATION_DESCRIPTION =
        "This app uses your makes use of device password/pin to authenticate the dialog."

    fun getBaseUrl(): String {
        return BASE_URL
    }

}