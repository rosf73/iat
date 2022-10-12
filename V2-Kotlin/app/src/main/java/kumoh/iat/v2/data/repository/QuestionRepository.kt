package kumoh.iat.v2.data.repository

import kumoh.iat.v2.data.dto.QuestionDto

interface QuestionRepository {

    suspend fun getAllQuestions(): Result<List<QuestionDto>>
}