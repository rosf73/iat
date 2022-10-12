package kumoh.iat.v2.data.repository

import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kumoh.iat.v2.data.datasource.QuestionDataSource
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class QuestionRepositoryTest {

    private lateinit var dataSource: QuestionDataSource
    private lateinit var repository: QuestionRepository

    @Before
    fun setUp() {
        dataSource = mockk()
        repository = QuestionRepositoryImpl(dataSource)
    }

    @Test
    fun getQuestions_isCorrect() = runTest {

    }
}