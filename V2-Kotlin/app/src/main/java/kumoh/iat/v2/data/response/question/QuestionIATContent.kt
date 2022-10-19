package kumoh.iat.v2.data.response.question

import com.google.gson.annotations.SerializedName
import kumoh.iat.v2.data.dto.QuestionIATContentDto
import kumoh.iat.v2.data.dto.QuestionIATStepDto
import kumoh.iat.v2.data.dto.QuestionIATWordDto

data class QuestionIATContent(
    @SerializedName("number") val number: Int,
    @SerializedName("content") val content: String,
    @SerializedName("words") val words: List<QuestionIATWord>,
    @SerializedName("steps") val steps: List<QuestionIATStep>
)

fun QuestionIATContent.toDto(): QuestionIATContentDto {
    val tempWords = mutableListOf<QuestionIATWordDto>()
    for (i in words.indices) {
        tempWords.add(
            QuestionIATWordDto(
                subject = words[i].subject,
                words = words[i].words.joinToString(", ")
            )
        )
    }

    val tempSteps = mutableListOf<QuestionIATStepDto>()
    for (i in steps.indices) {
        var leftTitle = steps[i].title[0]
        if (steps[i].title[1].isNotBlank()) leftTitle += " or ${steps[i].title[1]}"
        var rightTitle = steps[i].title[2]
        if (steps[i].title[3].isNotBlank()) rightTitle += " or ${steps[i].title[3]}"

        tempSteps.add(
            QuestionIATStepDto(
                trial = steps[i].trial,
                leftTitle = leftTitle,
                rightTitle = rightTitle,
                leftWords = steps[i].left,
                rightWords = steps[i].right
            )
        )
    }

    return QuestionIATContentDto(content, tempWords, tempSteps)
}
