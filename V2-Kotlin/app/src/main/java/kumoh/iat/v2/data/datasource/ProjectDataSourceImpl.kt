package kumoh.iat.v2.data.datasource

import kumoh.iat.v2.data.IATService
import kumoh.iat.v2.data.response.project.ProjectInfoData
import javax.inject.Inject

class ProjectDataSourceImpl @Inject constructor(
    private val iatService: IATService
): ProjectDataSource {

    override suspend fun getProjectInfo(): Result<ProjectInfoData> {
        try {
            val response = iatService.getProjectInfo()
            if (response.isSuccessful) {
                val projectInfo = response.body() ?: return Result.failure(java.lang.Exception())
                return Result.success(projectInfo.data)
            } else {
                return Result.failure(java.lang.Exception())
            }
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }
}