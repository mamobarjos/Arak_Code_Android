package com.arakadds.arak.di.modules

import com.arakadds.arak.presentation.activities.home.fragments.HomeFragment
import com.arakadds.arak.presentation.activities.home.fragments.ProfileFragment
import com.arakadds.arak.presentation.activities.home.fragments.ArakStoreFragment
import com.arakadds.arak.presentation.activities.home.fragments.StoreFragment
import com.arakadds.arak.presentation.activities.home.fragments.WalletFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeHomeFragment(): HomeFragment

    @ContributesAndroidInjector
    abstract fun contributeProfileFragment(): ProfileFragment

    @ContributesAndroidInjector
    abstract fun contributeServicesFragment(): ArakStoreFragment

    @ContributesAndroidInjector
    abstract fun contributeStoreFragment(): StoreFragment

    @ContributesAndroidInjector
    abstract fun contributeWalletFragment(): WalletFragment
}