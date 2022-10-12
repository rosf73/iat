package kumoh.iat.v2.data.datasource

import kumoh.iat.v2.data.response.question.QuestionData

interface QuestionDataSource {

    suspend fun getAllQuestions(): Result<List<QuestionData>>
}