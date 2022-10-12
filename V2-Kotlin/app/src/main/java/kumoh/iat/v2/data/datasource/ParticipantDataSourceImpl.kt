package kumoh.iat.v2.data.datasource

import kumoh.iat.v2.data.IATService
import kumoh.iat.v2.data.request.ParticipantLoginRequest
import kumoh.iat.v2.data.request.ParticipantSingUpRequest
import javax.inject.Inject

class ParticipantDataSourceImpl @Inject constructor(
    private val iatService: IATService
): ParticipantDataSource {

    override suspend fun login(num: String): Result<String> {
        try {
            val response = iatService.login(ParticipantLoginRequest(num))
            if (response.isSuccessful) {
                val iatResponse = response.body() ?: return Result.failure(java.lang.Exception())
                return Result.success(iatResponse.msg)
            } else {
                return Result.failure(java.lang.Exception())
            }
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override suspend fun signUp(
        num: String,
        age: Int,
        gender: Int,
        grade: Int,
        major: Int
    ): Result<String> {
        try {
            val response = iatService.signUp(ParticipantSingUpRequest(num, age, gender, grade, major))
            if (response.isSuccessful) {
                val iatResponse = response.body() ?: return Result.failure(java.lang.Exception())
                return Result.success(iatResponse.msg)
            } else {
                return Result.failure(java.lang.Exception())
            }
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }
}