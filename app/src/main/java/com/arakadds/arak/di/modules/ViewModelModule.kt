package com.arakadds.arak.di.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.arakadds.arak.presentation.viewmodel.AdDetailsViewModel
import com.arakadds.arak.presentation.viewmodel.AdsReviewsModel
import com.arakadds.arak.presentation.viewmodel.AdsTypeViewModel
import com.arakadds.arak.presentation.viewmodel.ArakRankingViewModel
import com.arakadds.arak.presentation.viewmodel.ArakServicesViewModel
import com.arakadds.arak.presentation.viewmodel.ArakStoreViewModel
import com.arakadds.arak.presentation.viewmodel.CategoryPackagesViewModel
import com.arakadds.arak.presentation.viewmodel.CreateAccountViewModel
import com.arakadds.arak.presentation.viewmodel.CreateNewAdViewModel
import com.arakadds.arak.presentation.viewmodel.CreateStoreViewModel
import com.arakadds.arak.presentation.viewmodel.ElectionViewModel
import com.arakadds.arak.presentation.viewmodel.FavoriteAdViewModel
import com.arakadds.arak.presentation.viewmodel.HomeAdsViewModel
import com.arakadds.arak.presentation.viewmodel.LoginViewModel
import com.arakadds.arak.presentation.viewmodel.MyProductsViewModel
import com.arakadds.arak.presentation.viewmodel.NotificationsViewModel
import com.arakadds.arak.presentation.viewmodel.PaymentViewModel
import com.arakadds.arak.presentation.viewmodel.SettingsViewModel
import com.arakadds.arak.presentation.viewmodel.StoresProductsViewModel
import com.arakadds.arak.presentation.viewmodel.StoresProfileViewModel
import com.arakadds.arak.presentation.viewmodel.StoresViewModel
import com.arakadds.arak.presentation.viewmodel.UserAdsViewModel
import com.arakadds.arak.presentation.viewmodel.UserProfileViewModel
import com.arakadds.arak.presentation.viewmodel.UserTransactionsViewModel
import com.arakadds.arak.presentation.viewmodel.ValidationViewModel
import com.arakadds.arak.presentation.viewmodel.ViewModelFactory
import com.arakadds.arak.presentation.viewmodel.WalletViewModel
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import kotlin.reflect.KClass


@Suppress("unused")
@Module

abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    abstract fun bindLoginViewModel(loginViewModel: LoginViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CreateAccountViewModel::class)
    abstract fun bindCreateAccountViewModel(createAccountViewModel: CreateAccountViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AdDetailsViewModel::class)
    abstract fun bindAdDetailsViewModel(adDetailsViewModel: AdDetailsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AdsTypeViewModel::class)
    abstract fun bindAdsTypeViewModel(adsTypeViewModel: AdsTypeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ArakRankingViewModel::class)
    abstract fun bindArakRankingViewModel(arakRankingViewModel: ArakRankingViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ArakServicesViewModel::class)
    abstract fun bindArakServicesViewModel(arakServicesViewModel: ArakServicesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CategoryPackagesViewModel::class)
    abstract fun bindCategoryPackagesViewModel(categoryPackagesViewModel: CategoryPackagesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CreateNewAdViewModel::class)
    abstract fun bindCreateNewAdViewModel(createNewAdViewModel: CreateNewAdViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CreateStoreViewModel::class)
    abstract fun bindCreateStoreViewModel(createStoreViewModel: CreateStoreViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FavoriteAdViewModel::class)
    abstract fun bindFavoriteAdViewModel(favoriteAdViewModel: FavoriteAdViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MyProductsViewModel::class)
    abstract fun bindMyProductsViewModel(MyProductsViewModel: MyProductsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PaymentViewModel::class)
    abstract fun bindPaymentViewModel(paymentViewModel: PaymentViewModel): ViewModel

    /*@Binds
    @IntoMap
    @ViewModelKey(SearchViewModel::class)
    abstract fun bindSearchViewModel(searchViewModel: SearchViewModel): ViewModel*/

    @Binds
    @IntoMap
    @ViewModelKey(NotificationsViewModel::class)
    abstract fun bindNotificationsViewModel(notificationsViewModel: NotificationsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SettingsViewModel::class)
    abstract fun bindSettingsViewModel(settingsViewModel: SettingsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(StoresProductsViewModel::class)
    abstract fun bindStoresProductsViewModel(storesProductsViewModel: StoresProductsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(StoresProfileViewModel::class)
    abstract fun bindStoresProfileViewModel(storesProfileViewModel: StoresProfileViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(StoresViewModel::class)
    abstract fun bindStoresViewModel(storesViewModel: StoresViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(UserAdsViewModel::class)
    abstract fun bindUserAdsViewModel(userAdsViewModel: UserAdsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HomeAdsViewModel::class)
    abstract fun bindHomeAdsViewModel(homeAdsViewModel: HomeAdsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(UserProfileViewModel::class)
    abstract fun bindUserProfileViewModel(userProfileViewModel: UserProfileViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(UserTransactionsViewModel::class)
    abstract fun bindUserTransactionsViewModel(userTransactionsViewModel: UserTransactionsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ValidationViewModel::class)
    abstract fun bindValidationViewModel(validationViewModel: ValidationViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(WalletViewModel::class)
    abstract fun bindWalletViewModel(walletViewModel: WalletViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ElectionViewModel::class)
    abstract fun bindElectionViewModel(electionViewModel: ElectionViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ArakStoreViewModel::class)
    abstract fun bindArakStoreViewModel(arakStoreViewModel: ArakStoreViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AdsReviewsModel::class)
    abstract fun bindAdsReviewsModel(adsReviewsModel: AdsReviewsModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}

@MustBeDocumented
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)
