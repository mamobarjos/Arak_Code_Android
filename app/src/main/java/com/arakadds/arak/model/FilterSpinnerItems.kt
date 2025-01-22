package com.arakadds.arak.model


import android.os.Parcel
import android.os.Parcelable
import com.arakadds.arak.utils.AppEnums.LanguagesEnums.ARABIC

import com.arakadds.arak.utils.Constants

data class FilterSpinnerItems(
    var id: Int = 0,

    var categoryEnglish: String = "",

    var categoryArabic: String = "",

    var language: String = "",

    var isOpen: Boolean = false
){
    override fun toString(): String {
        return  if (language == ARABIC)
            categoryArabic
        else
            categoryEnglish
    }
}