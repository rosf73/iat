package kumoh.iat.v2.data.response.project

import com.google.gson.annotations.SerializedName

data class ProjectInfoResponse(
    @SerializedName("msg") val msg: String,
    @SerializedName("data") val data: ProjectInfoData
)