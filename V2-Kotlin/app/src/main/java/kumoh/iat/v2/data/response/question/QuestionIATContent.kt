package kumoh.iat.v2.data.response.question

import com.google.gson.annotations.SerializedName

data class QuestionIATContent(
    @SerializedName("number") val number: Int,
    @SerializedName("content") val content: String,
    @SerializedName("words") val words: List<QuestionIATWord>,
    @SerializedName("steps") val steps: List<QuestionIATStep>
)
