package com.vonage.android.di

import com.vonage.android.util.coroutines.CoroutinePoller
import com.vonage.android.util.coroutines.CoroutinePollerFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier

@Module
@InstallIn(SingletonComponent::class)
object CoroutinesModule {

    @IODispatcher
    @Provides
    fun provideIODispatcher(): CoroutineDispatcher = Dispatchers.IO

    @MainDispatcher
    @Provides
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    @DefaultDispatcher
    @Provides
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @Provides
    fun provideCoroutinePollerFactory(
        @IODispatcher dispatcher: CoroutineDispatcher,
    ): CoroutinePollerFactory = CoroutinePollerFactory { fetchData ->
        CoroutinePoller(dispatcher, fetchData)
    }

}

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class IODispatcher

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class MainDispatcher

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class DefaultDispatcher