package kumoh.iat.v2.data.dto

data class QuestionIATStepDto(
    val trial: Int,
    val leftTitle: String,
    val rightTitle: String,
    val leftWords: List<String>,
    val rightWords: List<String>
)
