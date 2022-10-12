package kumoh.iat.v2.data.repository

import kumoh.iat.v2.data.datasource.QuestionDataSource
import kumoh.iat.v2.data.dto.QuestionDto
import kumoh.iat.v2.data.response.question.*
import kumoh.iat.v2.data.response.question.QuestionData.*
import javax.inject.Inject

class QuestionRepositoryImpl @Inject constructor(
    private val questionDataSource: QuestionDataSource
): QuestionRepository {

    override suspend fun getAllQuestions(): Result<List<QuestionDto>> {
        val questionResponse = questionDataSource.getAllQuestions().getOrElse {
            return Result.failure(it)
        }.map {
            return@map when (it) {
                is QuestionNormalData -> it.toNormalDto()
                is QuestionTableData -> it.toTableDto()
                is QuestionListedData -> it.toListedDto()
                is QuestionIATData -> it.toIATDto()
                is QuestionAssayData -> it.toAssayDto()
            }
        }

        return Result.success(questionResponse)
    }
}