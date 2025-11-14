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
//        apiKey = apiKey,
//        sessionId = sessionId,
//        token = token,
//        captionsId = captionsId,
        apiKey = "8cf15bc5-d4f4-4e01-9aa6-f459a8b077a6",
        sessionId = "2_MX44Y2YxNWJjNS1kNGY0LTRlMDEtOWFhNi1mNDU5YThiMDc3YTZ-fjE3NjI1MTIwOTA3NjF-WmlGVEZTWDlUVG5qZUE4SDY1YzF6aFprfn5-",
        token = "eyJhbGciOiJSUzI1NiIsImprdSI6Imh0dHBzOi8vYW51YmlzLWNlcnRzLWMxLWV1dzEucHJvZC52MS52b25hZ2VuZXR3b3Jrcy5uZXQvandrcyIsImtpZCI6IkNOPVZvbmFnZSAxdmFwaWd3IEludGVybmFsIENBOjoyNzA4NTIzMjQwMjEyNjEyMzg4OTM4NDQwNTg2NjI4NzA4MzI4MzIiLCJ0eXAiOiJKV1QiLCJ4NXUiOiJodHRwczovL2FudWJpcy1jZXJ0cy1jMS1ldXcxLnByb2QudjEudm9uYWdlbmV0d29ya3MubmV0L3YxL2NlcnRzLzBlNjExN2ZjZjRmMTI3NTI2OTA2YWE2NWUwMzhjMWFjIn0.eyJwcmluY2lwYWwiOnsiYWNsIjp7InBhdGhzIjp7Ii8qKiI6e319fSwidmlhbUlkIjp7ImVtYWlsIjoibWFudWVsLnZlcmFuaWV0b0B2b25hZ2UuY29tIiwiZ2l2ZW5fbmFtZSI6Ik1hbnVlbCIsImZhbWlseV9uYW1lIjoiVmVyYSBOaWV0byIsInBob25lX251bWJlciI6IjM0NjQ1NDA2MTg3IiwicGhvbmVfbnVtYmVyX2NvdW50cnkiOiJFUyIsIm9yZ2FuaXphdGlvbl9pZCI6Ijk4MTQxNGE5LTJmZDQtNGQxOC1iMzdiLTQ4ZTFkOWNhMDA3YiIsImF1dGhlbnRpY2F0aW9uTWV0aG9kcyI6W3siY29tcGxldGVkX2F0IjoiMjAyNS0xMS0xNFQxNTo0Mjo0NS41MTAwNDU1NDRaIiwibWV0aG9kIjoiaW50ZXJuYWwifV0sImlwUmlzayI6eyJyaXNrX2xldmVsIjowfSwidG9rZW5UeXBlIjoidmlhbSIsImF1ZCI6InBvcnR1bnVzLmlkcC52b25hZ2UuY29tIiwiZXhwIjoxNzYzMTM1MjgzLCJqdGkiOiJmNDlmNjA2ZS1mZjVkLTQyOTgtYWUwYy1mMGVhYmI3MWQ1MDUiLCJpYXQiOjE3NjMxMzQ5ODMsImlzcyI6IlZJQU0tSUFQIiwibmJmIjoxNzYzMTM0OTY4LCJzdWIiOiIyMzY4OGU1NC1mYmY3LTQ0YjEtYjlhNC0yZTZlZWJhNmVhNzkifX0sImZlZGVyYXRlZEFzc2VydGlvbnMiOnsidmlkZW8tYXBpIjpbeyJhcGlLZXkiOiI1MzY0NjEyYSIsImFwcGxpY2F0aW9uSWQiOiI4Y2YxNWJjNS1kNGY0LTRlMDEtOWFhNi1mNDU5YThiMDc3YTYiLCJtYXN0ZXJBY2NvdW50SWQiOiI1MzY0NjEyYSIsImV4dHJhQ29uZmlnIjp7InZpZGVvLWFwaSI6eyJpbml0aWFsX2xheW91dF9jbGFzc19saXN0IjoiIiwicm9sZSI6Im1vZGVyYXRvciIsInNjb3BlIjoic2Vzc2lvbi5jb25uZWN0Iiwic2Vzc2lvbl9pZCI6IjJfTVg0NFkyWXhOV0pqTlMxa05HWTBMVFJsTURFdE9XRmhOaTFtTkRVNVlUaGlNRGMzWVRaLWZqRTNOakkxTVRJd09UQTNOakYtV21sR1ZFWlRXRGxVVkc1cVpVRTRTRFkxWXpGNmFGcHJmbjUtIn19fV19LCJhdWQiOiJwb3J0dW51cy5pZHAudm9uYWdlLmNvbSIsImV4cCI6MTc2MzEzNjc4MywianRpIjoiNDYwZGI0NjctMmEwMS00M2UyLWJkOWUtNTRhOWIzZTgyMTA4IiwiaWF0IjoxNzYzMTM0OTgzLCJpc3MiOiJWSUFNLUlBUCIsIm5iZiI6MTc2MzEzNDk2OCwic3ViIjoiMjM2ODhlNTQtZmJmNy00NGIxLWI5YTQtMmU2ZWViYTZlYTc5In0.dh61bHZsZqkQoKuhb-2ZhMPauczthxI2HcIjZRYZUvaxyQlMrv73FXeVS3XY3ujD0hFfE_y54b1NAY4XgFDKaLs3UMbLn8KMOb6iaJmAYq4wVE50LUGEt4mUeRLLTM0MbkYjDce6Zph2e2Ec77BCkSeQ2EodapRJB3UQhlr4WbaOqKio-TQDeqnszxIJ-VR3Hiv7S6-1VX4xeInL2D8ftCh9b9kIfOXJiGz-oG7-Bz0F-JTUV8jEF49lhuDfyL5hoUcsKhsGS4-asVyTDGNUnL0D3NKVLVdN4Stczjv4wvVtMLTcinGXJUdOQkrMsfLx5x7KB3QKh5YDG1jscRZItg",
        captionsId = null,
    )

data class SessionInfo(
    val apiKey: String,
    val sessionId: String,
    val token: String,
    val captionsId: String?,
)
