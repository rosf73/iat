package kumoh.iat.v2.data.dto

import kumoh.iat.v2.data.response.question.QuestionIATContent
import kumoh.iat.v2.data.response.question.QuestionListedContent

sealed class QuestionDto {
    data class QuestionNormalDto(
        val number: Int,
        val question: String,
        val cases: List<String>,
        val isAssay: List<Int>
    ): QuestionDto()

    data class QuestionTableDto(
        val number: Int,
        val question: String,
        val subContents: List<String>,
        val cases: List<String>
    ): QuestionDto()

    data class QuestionListedDto(
        val number: Int,
        val question: String,
        val subContents: List<QuestionListedContent>
    ): QuestionDto()

    data class QuestionIATDto(
        val number: Int,
        val question: String,
        val subContents: List<QuestionIATContent>
    ): QuestionDto()

    data class QuestionAssayDto(
        val number: Int,
        val question: String
    ): QuestionDto()
}
