package com.arakadds.arak.di.modules


import com.arakadds.arak.presentation.activities.ArakStore.CartActivity
import com.arakadds.arak.presentation.activities.ArakStore.ProductDetailsToCartActivity
import com.arakadds.arak.presentation.activities.ArakStore.ShippingAddressActivity
import com.arakadds.arak.presentation.activities.ads.AdDetailUserViewActivity
import com.arakadds.arak.presentation.activities.ads.AdsStoryActivity
import com.arakadds.arak.presentation.activities.ads.CategoryPackagesActivity
import com.arakadds.arak.presentation.activities.ads.CreateNewAdActivity
import com.arakadds.arak.presentation.activities.ads.CustomPackage.CustomPackageActivity
import com.arakadds.arak.presentation.activities.ads.DisplayFullScreenImageActivity
import com.arakadds.arak.presentation.activities.ads.MyAdDetailsActivity
import com.arakadds.arak.presentation.activities.ads.MyAdsActivity
import com.arakadds.arak.presentation.activities.ads.NewAdSummeryActivity
import com.arakadds.arak.presentation.activities.ads.NormalAdsActivity
import com.arakadds.arak.presentation.activities.ads.SearchActivity
import com.arakadds.arak.presentation.activities.ads.SelectAdCategoryActivity
import com.arakadds.arak.presentation.activities.ads.SelectAdsTypeActivity
import com.arakadds.arak.presentation.activities.ads.SpecialAdsActivity
import com.arakadds.arak.presentation.activities.ads.VideoActivity
import com.arakadds.arak.presentation.activities.authentication.ChooseInterestsActivity
import com.arakadds.arak.presentation.activities.authentication.CodeVerificationActivity
import com.arakadds.arak.presentation.activities.authentication.ForgetPasswordActivity
import com.arakadds.arak.presentation.activities.authentication.LoginActivity
import com.arakadds.arak.presentation.activities.authentication.RegistrationActivity
import com.arakadds.arak.presentation.activities.authentication.ResetPasswordActivity
import com.arakadds.arak.presentation.activities.election.ElectionDetailsActivity
import com.arakadds.arak.presentation.activities.home.HomeActivity
import com.arakadds.arak.presentation.activities.map.MapActivity
import com.arakadds.arak.presentation.activities.other.BaseActivity
import com.arakadds.arak.presentation.activities.other.ReviewsActivity
import com.arakadds.arak.presentation.activities.other.SplashScreenActivity
import com.arakadds.arak.presentation.activities.other.SuccessActivity
import com.arakadds.arak.presentation.activities.other.WebViewActivity
import com.arakadds.arak.presentation.activities.payments.CliqPaymentActivity
import com.arakadds.arak.presentation.activities.payments.PaymentOptionsActivity
import com.arakadds.arak.presentation.activities.payments.PaymentWebActivity
import com.arakadds.arak.presentation.activities.profile.HistoryActivity
import com.arakadds.arak.presentation.activities.profile.MyDetailsActivity
import com.arakadds.arak.presentation.activities.profile.MyFavoritesActivity
import com.arakadds.arak.presentation.activities.profile.MyProductsActivity
import com.arakadds.arak.presentation.activities.services.ArakServicesActivity
import com.arakadds.arak.presentation.activities.services.RankingActivity
import com.arakadds.arak.presentation.activities.settings.AboutInformationActivity
import com.arakadds.arak.presentation.activities.settings.NotificationActivity
import com.arakadds.arak.presentation.activities.settings.SettingsActivity
import com.arakadds.arak.presentation.activities.stores.CreateProductActivity
import com.arakadds.arak.presentation.activities.stores.CreateStoreActivity
import com.arakadds.arak.presentation.activities.stores.ProductDetailsActivity
import com.arakadds.arak.presentation.activities.stores.StoreProductsActivity
import com.arakadds.arak.presentation.activities.stores.StoreProfileActivity
import com.arakadds.arak.presentation.activities.payments.wallet.CashWithdrawActivity
import com.arakadds.arak.presentation.activities.payments.wallet.WalletPaymentActivity
import com.arakadds.arak.presentation.activities.services.ServicesActivity
import com.arakadds.arak.presentation.activities.settings.FeedBackActivity
import com.arakadds.arak.presentation.activities.stores.MainStoreProfileActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainActivityModule {

    @ContributesAndroidInjector(modules = [FragmentBuildersModule::class])
    abstract fun contributeMainActivity(): HomeActivity

    @ContributesAndroidInjector
    abstract fun contributeAdsStoryActivity(): AdsStoryActivity?

    @ContributesAndroidInjector
    abstract fun contributeBaseActivity(): BaseActivity?

    @ContributesAndroidInjector
    abstract fun contributeAdDetailUserViewActivity(): AdDetailUserViewActivity?

    @ContributesAndroidInjector
    abstract fun contributeCategoryPackagesActivity(): CategoryPackagesActivity?

    @ContributesAndroidInjector
    abstract fun contributeCreateNewAdActivity(): CreateNewAdActivity?

    @ContributesAndroidInjector
    abstract fun contributeDisplayFullScreenImageActivity(): DisplayFullScreenImageActivity?

    @ContributesAndroidInjector
    abstract fun contributeMyAdsActivity(): MyAdsActivity?

    @ContributesAndroidInjector
    abstract fun contributeNewAdSummeryActivity(): NewAdSummeryActivity?

    @ContributesAndroidInjector
    abstract fun contributeSearchActivity(): SearchActivity?

    @ContributesAndroidInjector
    abstract fun contributeSelectAdsTypeActivity(): SelectAdsTypeActivity?

    @ContributesAndroidInjector
    abstract fun contributeSelectAdCategoryActivity(): SelectAdCategoryActivity?

    @ContributesAndroidInjector
    abstract fun contributeSpecialAdsActivity(): SpecialAdsActivity?

    @ContributesAndroidInjector
    abstract fun contributeVideoActivity(): VideoActivity?

    @ContributesAndroidInjector
    abstract fun contributeCodeVerificationActivity(): CodeVerificationActivity?

    @ContributesAndroidInjector
    abstract fun contributeForgetPasswordActivity(): ForgetPasswordActivity?

    @ContributesAndroidInjector
    abstract fun contributeLoginActivity(): LoginActivity?

    @ContributesAndroidInjector
    abstract fun contributeRegistrationActivity(): RegistrationActivity?

    @ContributesAndroidInjector
    abstract fun contributeResetPasswordActivity(): ResetPasswordActivity?

    @ContributesAndroidInjector
    abstract fun contributeMapActivity(): MapActivity?

    @ContributesAndroidInjector
    abstract fun contributeSplashScreenActivity(): SplashScreenActivity?

    @ContributesAndroidInjector
    abstract fun contributeSuccessActivity(): SuccessActivity?

    @ContributesAndroidInjector
    abstract fun contributeWebViewActivity(): WebViewActivity?

    @ContributesAndroidInjector
    abstract fun contributePaymentOptionsActivity(): PaymentOptionsActivity?

    @ContributesAndroidInjector
    abstract fun contributePaymentWebActivity(): PaymentWebActivity?

    @ContributesAndroidInjector
    abstract fun contributeHistoryActivity(): HistoryActivity?

    @ContributesAndroidInjector
    abstract fun contributeMyDetailsActivity(): MyDetailsActivity?

    @ContributesAndroidInjector
    abstract fun contributeMyFavoritesActivity(): MyFavoritesActivity?

    @ContributesAndroidInjector
    abstract fun contributeMyProductsActivity(): MyProductsActivity?

    @ContributesAndroidInjector
    abstract fun contributeArakServicesActivity(): ArakServicesActivity?

    @ContributesAndroidInjector
    abstract fun contributeRankingActivity(): RankingActivity?

    @ContributesAndroidInjector
    abstract fun contributeAboutInformationActivity(): AboutInformationActivity?

    @ContributesAndroidInjector
    abstract fun contributeFeedBackActivity(): FeedBackActivity?

    @ContributesAndroidInjector
    abstract fun contributeNotificationActivity(): NotificationActivity?

    @ContributesAndroidInjector
    abstract fun contributeSettingsActivity(): SettingsActivity?

    @ContributesAndroidInjector
    abstract fun contributeCreateProductActivity(): CreateProductActivity?

    @ContributesAndroidInjector
    abstract fun contributeCreateStoreActivity(): CreateStoreActivity?

    @ContributesAndroidInjector
    abstract fun contributeProductDetailsActivity(): ProductDetailsActivity?

    @ContributesAndroidInjector
    abstract fun contributeStoreProductsActivity(): StoreProductsActivity?

    @ContributesAndroidInjector
    abstract fun contributeStoreProfileActivity(): StoreProfileActivity?

    @ContributesAndroidInjector
    abstract fun contributeMainStoreProfileActivity(): MainStoreProfileActivity?

    @ContributesAndroidInjector
    abstract fun contributeCashWithdrawActivity(): CashWithdrawActivity?

    @ContributesAndroidInjector
    abstract fun contributeWalletPaymentActivity(): WalletPaymentActivity?

    @ContributesAndroidInjector
    abstract fun contributeNormalAdsActivity(): NormalAdsActivity?

    @ContributesAndroidInjector
    abstract fun contributeCliqPaymentActivity(): CliqPaymentActivity?

    @ContributesAndroidInjector
    abstract fun contributeServicesActivity(): ServicesActivity?

    @ContributesAndroidInjector
    abstract fun contributeElectionDetailsActivity(): ElectionDetailsActivity?

    @ContributesAndroidInjector
    abstract fun contributeMyAdDetailsActivity(): MyAdDetailsActivity?

    @ContributesAndroidInjector
    abstract fun contributeReviewsActivity(): ReviewsActivity?

    @ContributesAndroidInjector
    abstract fun contributeProductDetailToCartActivity(): ProductDetailsToCartActivity?

    @ContributesAndroidInjector
    abstract fun contributeCartActivity(): CartActivity?

    @ContributesAndroidInjector
    abstract fun contributeShippingAddressActivity(): ShippingAddressActivity?

    @ContributesAndroidInjector
    abstract fun contributeCustomPackageActivity(): CustomPackageActivity?


    @ContributesAndroidInjector
    abstract fun contributeChooseInterestsActivity(): ChooseInterestsActivity?



  /*  @ContributesAndroidInjector(modules = [FragmentBuildersModule::class])
    abstract fun contributeTutorialsActivity(): TutorialActivity*/

    /*@ContributesAndroidInjector()
    abstract fun contributeMainActivity(): MainActivity*/

    /*@ContributesAndroidInjector
    abstract fun contributeHomeFragment(): HomeFragment*/
}