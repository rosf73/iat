package kumoh.iat.v2.data.request

import com.google.gson.annotations.SerializedName

data class ParticipantLoginRequest(
    @SerializedName("phone_num") val phoneNum: String
)
