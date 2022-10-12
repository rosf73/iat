package kumoh.iat.v2.data.response.question

import com.google.gson.annotations.SerializedName

data class QuestionIATWord(
    @SerializedName("subject") val subject: String,
    @SerializedName("words") val words: List<String>
)
