package kumoh.iat.v2.data.response

import com.google.gson.annotations.SerializedName

data class IATResponse(
    @SerializedName("msg") val msg: String
)
