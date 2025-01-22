package com.arakadds.arak.model.new_mapping_refactore.response.banners.wallets

import com.google.gson.annotations.SerializedName

data class DigitalWalletsData(
    @SerializedName("digitalWallets") val digitalWallets: ArrayList<DigitalWallet>,
)
