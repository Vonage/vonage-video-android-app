package com.vonage.android.data

import com.vonage.android.data.network.APIService
import com.vonage.android.data.network.GetSessionResponse
import javax.inject.Inject

class SessionRepository @Inject constructor(
    private val apiService: APIService,
) {

    suspend fun getSession(roomName: String): Result<SessionInfo> =
        runCatching {
            val response = apiService.getSession(roomName)
            return if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it.toSessionInfo())
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Failed getting session"))
            }
        }
}

fun GetSessionResponse.toSessionInfo() =
    SessionInfo(
        apiKey = "8cf15bc5-d4f4-4e01-9aa6-f459a8b077a6",
        sessionId = "2_MX44Y2YxNWJjNS1kNGY0LTRlMDEtOWFhNi1mNDU5YThiMDc3YTZ-fjE3NjIyNTcyNjU1NjZ-RjdES05lK2Z5WUtvZlo0cHArcy9OL25Sfn5-",
        token = "eyJhbGciOiJSUzI1NiIsImprdSI6Imh0dHBzOi8vYW51YmlzLWNlcnRzLWMxLXVzZTEucHJvZC52MS52b25hZ2VuZXR3b3Jrcy5uZXQvandrcyIsImtpZCI6IkNOPVZvbmFnZSAxdmFwaWd3IEludGVybmFsIENBOjoxMjMyNzQ0MDA2MDgxNjU1MTIzMTUzNDc3NDU1OTM5MjcwMDIyNzkiLCJ0eXAiOiJKV1QiLCJ4NXUiOiJodHRwczovL2FudWJpcy1jZXJ0cy1jMS11c2UxLnByb2QudjEudm9uYWdlbmV0d29ya3MubmV0L3YxL2NlcnRzL2RkMjE1MGM1OTQ2OGJjODU1ZWI1YjI1YjI4YTNjNTk1In0.eyJwcmluY2lwYWwiOnsiYWNsIjp7InBhdGhzIjp7Ii8qKiI6e319fSwidmlhbUlkIjp7ImVtYWlsIjoibWFudWVsLnZlcmFuaWV0b0B2b25hZ2UuY29tIiwiZ2l2ZW5fbmFtZSI6Ik1hbnVlbCIsImZhbWlseV9uYW1lIjoiVmVyYSBOaWV0byIsInBob25lX251bWJlciI6IjM0NjQ1NDA2MTg3IiwicGhvbmVfbnVtYmVyX2NvdW50cnkiOiJFUyIsIm9yZ2FuaXphdGlvbl9pZCI6Ijk4MTQxNGE5LTJmZDQtNGQxOC1iMzdiLTQ4ZTFkOWNhMDA3YiIsImF1dGhlbnRpY2F0aW9uTWV0aG9kcyI6W3siY29tcGxldGVkX2F0IjoiMjAyNS0xMS0wNVQwOTo0Nzo1Ni4yOTE1NDI1OVoiLCJtZXRob2QiOiJpbnRlcm5hbCJ9XSwiaXBSaXNrIjp7ImlzX3Byb3h5Ijp0cnVlLCJyaXNrX2xldmVsIjowfSwidG9rZW5UeXBlIjoidmlhbSIsImF1ZCI6InBvcnR1bnVzLmlkcC52b25hZ2UuY29tIiwiZXhwIjoxNzYyMzU3NDU1LCJqdGkiOiI5M2M4MjY5ZS0zODBhLTRjMTgtYjEzMi0xMWE0MDlmZTI1M2MiLCJpYXQiOjE3NjIzNTcxNTUsImlzcyI6IlZJQU0tSUFQIiwibmJmIjoxNzYyMzU3MTQwLCJzdWIiOiIyMzY4OGU1NC1mYmY3LTQ0YjEtYjlhNC0yZTZlZWJhNmVhNzkifX0sImZlZGVyYXRlZEFzc2VydGlvbnMiOnsidmlkZW8tYXBpIjpbeyJhcGlLZXkiOiI1MzY0NjEyYSIsImFwcGxpY2F0aW9uSWQiOiI4Y2YxNWJjNS1kNGY0LTRlMDEtOWFhNi1mNDU5YThiMDc3YTYiLCJtYXN0ZXJBY2NvdW50SWQiOiI1MzY0NjEyYSIsImV4dHJhQ29uZmlnIjp7InZpZGVvLWFwaSI6eyJpbml0aWFsX2xheW91dF9jbGFzc19saXN0IjoiIiwicm9sZSI6Im1vZGVyYXRvciIsInNjb3BlIjoic2Vzc2lvbi5jb25uZWN0Iiwic2Vzc2lvbl9pZCI6IjJfTVg0NFkyWXhOV0pqTlMxa05HWTBMVFJsTURFdE9XRmhOaTFtTkRVNVlUaGlNRGMzWVRaLWZqRTNOakl5TlRjeU5qVTFOalotUmpkRVMwNWxLMlo1V1V0dlpsbzBjSEFyY3k5T0wyNVNmbjUtIn19fV19LCJhdWQiOiJwb3J0dW51cy5pZHAudm9uYWdlLmNvbSIsImV4cCI6MTc2MjM2MDc1NSwianRpIjoiMzViNzg3NzEtNjI0OC00MWU4LWE0MjQtNzc2MWFiYjRiNzVhIiwiaWF0IjoxNzYyMzU3MTU1LCJpc3MiOiJWSUFNLUlBUCIsIm5iZiI6MTc2MjM1NzE0MCwic3ViIjoiMjM2ODhlNTQtZmJmNy00NGIxLWI5YTQtMmU2ZWViYTZlYTc5In0.O9qTxxg-0ekupyJ9n5KQFfPbkRud0UqMXpk60vISl6FFkRv47DjD0izkjPsFkg9GPGhSX9Vg8dkREYdGfUIydcFNVQUY5cHDrnUFdQwtk3m3zQe3Nm4sOFhSJGycRkO0Teosww4EO6XClOeC5fdB0aYGNn1PDLBWq4WP1s5HbaKOhewzV1FxBQBP0S_fLCYDEo-PMazdJENrO147fQ8SkB_sNAXcNkJZXUzPQ_xCyRrjX_dGzRpNSLG97quF-WTWupJeb-XEDJt7OBcZd4f3RBFcyLlc8-eUMPwSdlhyn6MOatsemJZSMA_5ye6CsoMhqoDqjlkurmDe3uOdEr-dyg",
        captionsId = null,
    )

data class SessionInfo(
    val apiKey: String,
    val sessionId: String,
    val token: String,
    val captionsId: String?,
)
