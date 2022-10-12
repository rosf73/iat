package kumoh.iat.v2.data.repository

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kumoh.iat.v2.data.datasource.ProjectDataSource
import kumoh.iat.v2.data.response.project.ProjectInfoData
import kumoh.iat.v2.data.response.project.toDto
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class ProjectRepositoryTest {

    private lateinit var dataSource: ProjectDataSource
    private lateinit var repository: ProjectRepository

    @Before
    fun setUp() {
        dataSource = mockk()
        repository = ProjectRepositoryImpl(dataSource)
    }

    @Test
    fun getProjectInfo_isCorrect() = runTest {
        val expect = ProjectInfoData(
            projectName = "연구 과제1",
            description = "무언갈 연구합니다",
            agreement = "아무튼 동의하십시오",
            gift = 0
        )
        coEvery { dataSource.getProjectInfo() } returns Result.success(expect)

        val actual = repository.getProjectInfo().getOrNull()

        Assert.assertEquals(expect.toDto(), actual)
    }
}