package kumoh.iat.v2.data.repository

interface ParticipantRepository {

    suspend fun login(num: String): Result<String>

    suspend fun signUp(num: String, age: Int, gender: Int, grade: Int, major: Int): Result<String>
}