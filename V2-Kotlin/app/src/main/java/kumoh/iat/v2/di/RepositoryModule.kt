package kumoh.iat.v2.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kumoh.iat.v2.data.datasource.ParticipantDataSource
import kumoh.iat.v2.data.datasource.ProjectDataSource
import kumoh.iat.v2.data.datasource.QuestionDataSource
import kumoh.iat.v2.data.repository.*
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RepositoryModule {

    @Provides
    @Singleton
    fun provideProjectRepository(dataSource: ProjectDataSource): ProjectRepository
        = ProjectRepositoryImpl(dataSource)

    @Provides
    @Singleton
    fun provideParticipantRepository(dataSource: ParticipantDataSource): ParticipantRepository
        = ParticipantRepositoryImpl(dataSource)

    @Provides
    @Singleton
    fun provideQuestionRepository(dataSource: QuestionDataSource): QuestionRepository
        = QuestionRepositoryImpl(dataSource)
}