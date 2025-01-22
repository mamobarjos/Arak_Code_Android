package com.arakadds.arak.di.modules

import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(
    includes = [
        ViewModelModule::class,
        NetworkModule::class,


    ]
)
class AppModule/*{

    @Provides
    @Singleton
    fun provideDataRepository(repository: DataRepository?): DataRepository? {
        return repository
    }
}*/