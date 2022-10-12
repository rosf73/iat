package kumoh.iat.v2.data.response.question

import com.google.gson.annotations.SerializedName

data class QuestionIATStep(
    @SerializedName("trial") val trial: Int,
    @SerializedName("title") val title: List<String>,
    @SerializedName("left") val left: List<String>,
    @SerializedName("right") val right: List<String>
)
