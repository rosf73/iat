package kumoh.iat.v2.data.datasource

import kumoh.iat.v2.data.IATService
import kumoh.iat.v2.data.response.question.QuestionData
import javax.inject.Inject

class QuestionDataSourceImpl @Inject constructor(
    private val iatService: IATService
): QuestionDataSource {

    override suspend fun getAllQuestions(): Result<List<QuestionData>> {
        try {
            val response = iatService.getAllQuestions()
            if (response.isSuccessful) {
                val iatResponse = response.body() ?: return Result.failure(java.lang.Exception())
                return Result.success(iatResponse.data)
            } else {
                return Result.failure(java.lang.Exception())
            }
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }
}