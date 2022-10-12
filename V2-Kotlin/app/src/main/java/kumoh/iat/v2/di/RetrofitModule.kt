package kumoh.iat.v2.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kumoh.iat.v2.BuildConfig
import kumoh.iat.v2.data.IATService
import kumoh.iat.v2.data.response.question.*
import kumoh.iat.v2.util.RuntimeTypeAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

private val valueTypeAdapter: RuntimeTypeAdapterFactory<QuestionData> = RuntimeTypeAdapterFactory
    .of(QuestionData::class.java, "type")
    .registerSubtype(QuestionData.QuestionNormalData::class.java, "1")
    .registerSubtype(QuestionData.QuestionTableData::class.java, "2")
    .registerSubtype(QuestionData.QuestionListedData::class.java, "3")
    .registerSubtype(QuestionData.QuestionIATData::class.java, "4")
    .registerSubtype(QuestionData.QuestionAssayData::class.java, "5")

@InstallIn(SingletonComponent::class)
@Module
object RetrofitModule {

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder()
        .registerTypeAdapterFactory(valueTypeAdapter)
        .create()

    @Provides
    @Singleton
    fun provideGsonConverterFactory(gson: Gson): GsonConverterFactory =
        GsonConverterFactory.create(gson)

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(
        converterFactory: GsonConverterFactory,
        okHttpClient: OkHttpClient
    ): Retrofit = Retrofit.Builder()
        .addConverterFactory(converterFactory)
        .baseUrl(BuildConfig.BASE_URL)
        .client(okHttpClient)
        .build()

    @Singleton
    @Provides
    fun provideIATService(retrofit: Retrofit): IATService {
        return retrofit.create(IATService::class.java)
    }
}