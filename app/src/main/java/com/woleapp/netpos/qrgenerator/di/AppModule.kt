package com.woleapp.netpos.qrgenerator.di

import android.content.Context
import com.google.gson.Gson
import com.woleapp.netpos.qrgenerator.BuildConfig
import com.woleapp.netpos.qrgenerator.db.AppDatabase
import com.woleapp.netpos.qrgenerator.db.QrDao
import com.woleapp.netpos.qrgenerator.network.CheckoutService
import com.woleapp.netpos.qrgenerator.network.MerchantService
import com.woleapp.netpos.qrgenerator.network.QRService
import com.woleapp.netpos.qrgenerator.network.TransactionService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.reactivex.disposables.CompositeDisposable
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    @Named("baseUrl")
    fun providesBaseUrl(): String = BuildConfig.STRING_TALLY_BASE_URL


    @Provides
    @Singleton
    @Named("transactionBaseUrl")
    fun providesTransactionBaseUrl(): String = BuildConfig.STRING_TRANSACTION_BASE_URL

    @Provides
    @Singleton
    @Named("merchantBaseUrl")
    fun providesMerchantBaseUrl(): String = BuildConfig.STRING_MERCHANT_BASE_URL


    @Provides
    @Singleton
    @Named("checkoutBaseUrl")
    fun providesCheckoutBaseUrl(): String = BuildConfig.STRING_CHECKOUT_BASE_URL


    @Provides
    @Singleton
    @Named("loggingInterceptor")
    fun providesLoggingInterceptor(): Interceptor = HttpLoggingInterceptor().apply {
        setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    @Provides
    @Singleton
    @Named("headerInterceptor")
    fun providesHeaderInterceptor(): Interceptor =
        BasicAuthInterceptor(BuildConfig.STRING_AUTH_USER_NAME, BuildConfig.STRING_AUTH_PASSWORD)

    @Provides
    @Singleton
    @Named("defaultOkHttp")
    fun providesOKHTTPClient(
        @Named("loggingInterceptor") loggingInterceptor: Interceptor,
        @Named("headerInterceptor") headerInterceptor: Interceptor
    ): OkHttpClient =
        OkHttpClient().newBuilder()
            .connectTimeout(70, TimeUnit.SECONDS)
            .readTimeout(70, TimeUnit.SECONDS)
            .writeTimeout(70, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor(headerInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()

    @Provides
    @Singleton
    @Named("checkOutOkHttp")
    fun providesOKHTTPClientForCheckOut(
        @Named("loggingInterceptor") loggingInterceptor: Interceptor,
    ): OkHttpClient =
        OkHttpClient().newBuilder()
            .connectTimeout(70, TimeUnit.SECONDS)
            .readTimeout(70, TimeUnit.SECONDS)
            .writeTimeout(70, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor(loggingInterceptor)
            .build()

    @Provides
    @Singleton
    @Named("defaultRetrofit")
    fun providesRetrofit(
        @Named("defaultOkHttp") okhttp: OkHttpClient,
        @Named("baseUrl") baseUrl: String
    ): Retrofit =
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .baseUrl(baseUrl)
            .client(okhttp)
            .build()

    @Provides
    @Singleton
    @Named("defaultTransactionRetrofit")
    fun providesTransactionRetrofit(
        @Named("defaultOkHttp") okhttp: OkHttpClient,
        @Named("transactionBaseUrl") baseUrl: String
    ): Retrofit =
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .baseUrl(baseUrl)
            .client(okhttp)
            .build()

    @Provides
    @Singleton
    @Named("defaultMerchantRetrofit")
    fun providesMerchantRetrofit(
        @Named("defaultOkHttp") okhttp: OkHttpClient,
        @Named("merchantBaseUrl") baseUrl: String
    ): Retrofit =
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .baseUrl(baseUrl)
            .client(okhttp)
            .build()

    @Provides
    @Singleton
    @Named("defaultCheckoutRetrofit")
    fun providesCheckoutRetrofit(
        @Named("checkOutOkHttp") okhttp: OkHttpClient,
        @Named("checkoutBaseUrl") baseUrl: String
    ): Retrofit =
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .baseUrl(baseUrl)
            .client(okhttp)
            .build()

    @Provides
    @Singleton
    fun providesContactlessRegService(
        @Named("defaultRetrofit") retrofit: Retrofit
    ): QRService = retrofit.create(QRService::class.java)

    @Provides
    @Singleton
    fun providesTransactionService(
        @Named("defaultTransactionRetrofit") retrofit: Retrofit
    ): TransactionService = retrofit.create(TransactionService::class.java)

    @Provides
    @Singleton
    fun providesMerchantService(
        @Named("defaultMerchantRetrofit") retrofit: Retrofit
    ): MerchantService = retrofit.create(MerchantService::class.java)

    @Provides
    @Singleton
    fun providesCheckoutService(
        @Named("defaultCheckoutRetrofit") retrofit: Retrofit
    ): CheckoutService = retrofit.create(CheckoutService::class.java)

    @Provides
    @Singleton
    fun providesGson(): Gson = Gson()

    @Provides
    @Singleton
    fun providesAppDatabase(
        @ApplicationContext context: Context) : AppDatabase =
        AppDatabase.getDatabaseInstance(context)

    @Provides
    @Singleton
    fun providesQrDao(
        appDatabase: AppDatabase): QrDao {
        return appDatabase.getQrDao()
    }

    @Provides
    @Singleton
    fun providesCompositeDisposable(): CompositeDisposable =
        CompositeDisposable()
}