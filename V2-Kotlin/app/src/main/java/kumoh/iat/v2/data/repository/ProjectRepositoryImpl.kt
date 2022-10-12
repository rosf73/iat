package kumoh.iat.v2.data.repository

import kumoh.iat.v2.data.datasource.ProjectDataSource
import kumoh.iat.v2.data.dto.ProjectInfoDto
import kumoh.iat.v2.data.response.project.toDto
import javax.inject.Inject

class ProjectRepositoryImpl @Inject constructor(
    private val projectDataSource: ProjectDataSource
): ProjectRepository {

    override suspend fun getProjectInfo(): Result<ProjectInfoDto> {
        val projectInfoResponse = projectDataSource.getProjectInfo().getOrElse {
            return Result.failure(it)
        }
        return Result.success(projectInfoResponse.toDto())
    }
}