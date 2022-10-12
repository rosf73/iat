package kumoh.iat.v2.data.response.project

import com.google.gson.annotations.SerializedName
import kumoh.iat.v2.data.dto.ProjectInfoDto

data class ProjectInfoData(
    @SerializedName("project_name") val projectName: String,
    @SerializedName("description") val description: String,
    @SerializedName("agreement") val agreement: String,
    @SerializedName("giftable") val gift: Int
)

fun ProjectInfoData.toDto(): ProjectInfoDto
    = ProjectInfoDto(
        this.projectName,
        this.description,
        this.agreement,
        if (this.gift == 1) "참여가 끝나면\n스타벅스(아메리카노) 기프티콘이 제공됩니다\n(전화번호 당 최초 1회 참여시에만 제공)"
        else "기프티콘 보상이벤트가 종료되었습니다\n보상 없이 참여하실 분들만\n아래 약관 읽기를 눌러주세요"
    )