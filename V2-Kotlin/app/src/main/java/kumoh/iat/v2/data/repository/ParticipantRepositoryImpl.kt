package kumoh.iat.v2.data.repository

import kumoh.iat.v2.data.datasource.ParticipantDataSource
import javax.inject.Inject

class ParticipantRepositoryImpl @Inject constructor(
    private val participantDataSource: ParticipantDataSource
): ParticipantRepository {

    override suspend fun login(num: String): Result<String> {
        val res = participantDataSource.login(num).getOrElse {
            return Result.failure(it)
        }
        return Result.success(res)
    }

    override suspend fun signUp(
        num: String,
        age: Int,
        gender: Int,
        grade: Int,
        major: Int
    ): Result<String> {
        val res = participantDataSource.signUp(
            num, age, gender, grade, major
        ).getOrElse {
            return Result.failure(it)
        }
        return Result.success(res)
    }
}