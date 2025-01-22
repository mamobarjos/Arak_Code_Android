package com.arakadds.arak.di.modules


import com.arakadds.arak.network.NetWorkApi
import com.arakadds.arak.repository.DataRepository
import dagger.Module
import dagger.Provides

import javax.inject.Singleton

@Module
object DataModule {

    /*@Singleton
    @Provides
    fun providesDataRepository(netWorkAp: NetWorkApi): DataRepository =
        DataRepository(netWorkAp)

        {*/

    @Provides
    @Singleton
    fun provideDataRepository(netWorkApi: NetWorkApi): DataRepository =
        DataRepository(netWorkApi)

}