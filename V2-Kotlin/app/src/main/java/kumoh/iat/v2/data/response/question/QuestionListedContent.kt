package kumoh.iat.v2.data.response.question

import com.google.gson.annotations.SerializedName

data class QuestionListedContent(
    @SerializedName("content") val content: String,
    @SerializedName("cases") val cases: List<String>
)
