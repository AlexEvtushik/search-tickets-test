package ua.searchtickets.app.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import ua.searchtickets.BuildConfig
import ua.searchtickets.data.datasources.SearchTicketsWebApi

val networkModule = module {
    single<Gson> { GsonBuilder().create() }
    single<SearchTicketsWebApi> {
        Retrofit.Builder()
            .baseUrl(BuildConfig.baseUrl)
            .client(OkHttpClient.Builder().build())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
            .addConverterFactory(GsonConverterFactory.create(get()))
            .build()
            .create(SearchTicketsWebApi::class.java)
    }
}
