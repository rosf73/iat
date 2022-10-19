package kumoh.iat.v2.data.dto

data class QuestionIATContentDto(
    val content: String,
    val words: List<QuestionIATWordDto>,
    val steps: List<QuestionIATStepDto>
)
