package kumoh.iat.v2.data

import kumoh.iat.v2.data.request.ParticipantLoginRequest
import kumoh.iat.v2.data.request.ParticipantSingUpRequest
import kumoh.iat.v2.data.response.IATResponse
import kumoh.iat.v2.data.response.project.ProjectInfoResponse
import kumoh.iat.v2.data.response.question.QuestionResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Body

interface IATService {

    @GET("information/all")
    suspend fun getProjectInfo(): Response<ProjectInfoResponse>

    @POST("participant/signup")
    suspend fun signUp(
        @Body body: ParticipantSingUpRequest
    ): Response<IATResponse>

    @POST("participant/signin")
    suspend fun login(
        @Body body: ParticipantLoginRequest
    ): Response<IATResponse>

    @GET("question/all")
    suspend fun getAllQuestions(): Response<QuestionResponse>
}