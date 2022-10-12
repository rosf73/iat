package kumoh.iat.v2.data.datasource

import kumoh.iat.v2.data.response.project.ProjectInfoData

interface ProjectDataSource {

    suspend fun getProjectInfo(): Result<ProjectInfoData>
}