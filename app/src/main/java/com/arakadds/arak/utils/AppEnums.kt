package com.arakadds.arak.utils

object AppEnums {

    object LanguagesEnums {
        const val ARABIC = "ar"
        const val ENGLISH = "en"
    }

    object FirebaseEnums {
        const val STORE_PROFILE_IMAGE = "Profile"
    }

    object DialogActionTypes {
        const val DISMISS = 1
        const val CLOSE = 2
        const val DELETE = 3
        const val OPEN_ACTIVITY = 4
        const val Enable_Biometric = 5
    }

    object GenderEnum {
        const val Male = 1
        const val Female = 2
    }

    object AdsTypeCategories {
        const val IMAGE = 1
        const val VIDEO = 2
        const val WEBSITE = 3
        const val STORES = 4
    }

    object AdsStatus {
        const val PENDING = "PENDING"
        const val APPROVED = "APPROVED"
        const val COMPLETED = "COMPLETED"
        const val DECLINED = "DECLINED"
        const val EXPIRED = "EXPIRED"
    }

    object BannerType {
        const val HOMEPAGE = "HOMEPAGE"
        const val STORE = "STORE"
        const val ELECTION = "ELECTION"
    }
    object TransactionType {
        const val INCOME = "INCOME"
        const val OUTCOME = "OUTCOME"
    }
    object IntentsFlags {
        const val PERSON_ID = "PERSON_ID"
    }
    object SocialMediaPlatforms {
        const val WHATS_UP = 1
        const val FACEBOOK = 2
        const val INSTAGRAM = 3
        const val YOUTUBE = 4
        const val WEBSITE = 5
    }

    object GenderTypes {
        const val MALE = "MALE"
        const val FEMALE = "FEMALE"
    }

    object PaymentEnums {
        const val WALLET_INSUFFICIENT = "WALLET_INSUFFICIENT"
        const val CARD = "CARD"
        const val WALLET = "WALLET"
    }


    /*enum AdPackageType {
  NORMAL
  CUSTOM
}

enum DiscountType {
  PERCENTAGE
  CURRENCY
}

enum Role {
  ADMIN
  USER
  CUSTOMER
}



enum AdStatus {
  PENDING
  APPROVED
  COMPLETED
  DECLINED
  EXPIRED
}

enum BannerType {
  HOMEPAGE
  STORE
  ELECTION
}

enum BannerStatus {
  PENDING
  APPROVED
  COMPLETED
  DECLINED
  EXPIRED
}

enum StoreStatus {
  PENDING
  APPROVED
  DECLINED
}

enum WithdrawRequestStatus {
  PENDING
  APPROVED
  DECLINED
}

enum NotificationType {
  BROADCAST
  USER
}

*/
}