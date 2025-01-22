package com.arakadds.arak.di.components

import android.app.Application
import com.arakadds.arak.MainApplication
import com.arakadds.arak.di.modules.AppModule
import com.arakadds.arak.di.modules.FragmentBuildersModule
import com.arakadds.arak.di.modules.MainActivityModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        AppModule::class,
        MainActivityModule::class,
        FragmentBuildersModule::class,
    ]
)
interface AppComponent : AndroidInjector<MainApplication> {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    override fun inject(mainApplication: MainApplication)
}