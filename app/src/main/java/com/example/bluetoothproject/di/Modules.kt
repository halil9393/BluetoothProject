package com.example.bluetoothproject.di

import com.example.bluetoothproject.service.MockApi
import com.example.bluetoothproject.viewmodels.DetailFragmentViewModel
import com.example.bluetoothproject.viewmodels.HomeFragmentViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.BuildConfig
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//               DB icin Hata Veriyor
//val appModule = module {
//    fun provideDataBase(application: Application): BluetoothHistoryDB {
//        return Room.databaseBuilder(application, BluetoothHistoryDB::class.java, "bluetoothdevices_database")
//            .fallbackToDestructiveMigration()
//            .build()
//    }
//
//    fun provideDao(dataBase: BluetoothHistoryDB): DevicesDao {
//        return dataBase.devicesDao()
//    }
//    single { provideDataBase(androidApplication()) }
//    single { provideDao(get()) }
//
//}

val viewModelModules = module {
    viewModel { HomeFragmentViewModel() }

    viewModel { DetailFragmentViewModel(get()) }

}

val networkModule = module {
    factory { provideRetrofit() }
    single { provideNetworkApi(get()) }
}

fun provideNetworkApi(retrofit: Retrofit): MockApi =
    retrofit.create(MockApi::class.java)

fun provideRetrofit(): Retrofit {

    val okhttp3client = OkHttpClient.Builder()
    val logging = HttpLoggingInterceptor()
    logging.level = HttpLoggingInterceptor.Level.BODY
    if(BuildConfig.DEBUG){
        okhttp3client.addInterceptor(logging)
    }

    return Retrofit.Builder()
        .baseUrl(MockApi.BASE_URL)
        .client(okhttp3client.build())
        .addConverterFactory(GsonConverterFactory.create())
        .build()


}