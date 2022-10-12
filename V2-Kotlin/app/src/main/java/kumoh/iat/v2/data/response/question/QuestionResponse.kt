package kumoh.iat.v2.data.response.question

import com.google.gson.annotations.SerializedName

data class QuestionResponse(
    @SerializedName("msg") val msg: String,
    @SerializedName("data") val data: List<QuestionData>
)
