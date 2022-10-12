package kumoh.iat.v2.data.request

import com.google.gson.annotations.SerializedName

data class ParticipantSingUpRequest(
    @SerializedName("phone_num") val phoneNum: String,
    @SerializedName("age") val age: Int,
    @SerializedName("gender") val gender: Int,
    @SerializedName("grade") val grade: Int,
    @SerializedName("major") val major: Int
)
