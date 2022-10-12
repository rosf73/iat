package kumoh.iat.v2.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kumoh.iat.v2.data.IATService
import kumoh.iat.v2.data.datasource.*
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DataSourceModule {

    @Provides
    @Singleton
    fun provideProjectDataSource(service: IATService): ProjectDataSource
        = ProjectDataSourceImpl(service)

    @Provides
    @Singleton
    fun provideParticipantDataSource(service: IATService): ParticipantDataSource
        = ParticipantDataSourceImpl(service)

    @Provides
    @Singleton
    fun provideQuestionDataSource(service: IATService): QuestionDataSource
        = QuestionDataSourceImpl(service)
}