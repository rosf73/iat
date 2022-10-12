package kumoh.iat.v2.data.repository

import kumoh.iat.v2.data.dto.ProjectInfoDto

interface ProjectRepository {

    suspend fun getProjectInfo(): Result<ProjectInfoDto>
}