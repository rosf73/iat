package kumoh.iat.v2.data.response.question

import com.google.gson.annotations.SerializedName
import kumoh.iat.v2.data.dto.QuestionDto

sealed class QuestionData {
    data class QuestionNormalData(
        @SerializedName("type") val type: Int,
        @SerializedName("number") val number: Int,
        @SerializedName("question") val question: String,
        @SerializedName("cases") val cases: List<String>,
        @SerializedName("is_assay") val isAssay: List<Int>
    ): QuestionData() {
        fun toNormalDto(): QuestionDto.QuestionNormalDto
            = QuestionDto.QuestionNormalDto(
                number, question, cases, isAssay
            )
    }

    data class QuestionTableData(
        @SerializedName("type") val type: Int,
        @SerializedName("number") val number: Int,
        @SerializedName("question") val question: String,
        @SerializedName("sub_contents") val subContents: List<String>,
        @SerializedName("cases") val cases: List<String>
    ): QuestionData() {
        fun toTableDto(): QuestionDto.QuestionTableDto
            = QuestionDto.QuestionTableDto(
                number, question, subContents, cases
            )
    }

    data class QuestionListedData(
        @SerializedName("type") val type: Int,
        @SerializedName("number") val number: Int,
        @SerializedName("question") val question: String,
        @SerializedName("sub_questions_count") val count: Int,
        @SerializedName("sub_contents") val subContents: List<QuestionListedContent>
    ): QuestionData() {
        fun toListedDto(): QuestionDto.QuestionListedDto
            = QuestionDto.QuestionListedDto(
                number, question, subContents
            )
    }

    data class QuestionIATData(
        @SerializedName("type") val type: Int,
        @SerializedName("number") val number: Int,
        @SerializedName("question") val question: String,
        @SerializedName("sub_contents") val subContents: List<QuestionIATContent>
    ): QuestionData() {
        fun toIATDto(): QuestionDto.QuestionIATDto
            = QuestionDto.QuestionIATDto(
                number, question, subContents.map { it.toDto() }
            )
    }

    data class QuestionAssayData(
        @SerializedName("type") val type: Int,
        @SerializedName("number") val number: Int,
        @SerializedName("question") val question: String
    ): QuestionData() {
        fun toAssayDto(): QuestionDto.QuestionAssayDto
                = QuestionDto.QuestionAssayDto(
            number, question
        )
    }
}
