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
        token = "eyJhbGciOiJSUzI1NiIsImprdSI6Imh0dHBzOi8vYW51YmlzLWNlcnRzLWMxLWV1dzEucHJvZC52MS52b25hZ2VuZXR3b3Jrcy5uZXQvandrcyIsImtpZCI6IkNOPVZvbmFnZSAxdmFwaWd3IEludGVybmFsIENBOjoyNzA4NTIzMjQwMjEyNjEyMzg4OTM4NDQwNTg2NjI4NzA4MzI4MzIiLCJ0eXAiOiJKV1QiLCJ4NXUiOiJodHRwczovL2FudWJpcy1jZXJ0cy1jMS1ldXcxLnByb2QudjEudm9uYWdlbmV0d29ya3MubmV0L3YxL2NlcnRzLzBlNjExN2ZjZjRmMTI3NTI2OTA2YWE2NWUwMzhjMWFjIn0.eyJwcmluY2lwYWwiOnsiYWNsIjp7InBhdGhzIjp7Ii8qKiI6e319fSwidmlhbUlkIjp7ImVtYWlsIjoibWFudWVsLnZlcmFuaWV0b0B2b25hZ2UuY29tIiwiZ2l2ZW5fbmFtZSI6Ik1hbnVlbCIsImZhbWlseV9uYW1lIjoiVmVyYSBOaWV0byIsInBob25lX251bWJlciI6IjM0NjQ1NDA2MTg3IiwicGhvbmVfbnVtYmVyX2NvdW50cnkiOiJFUyIsIm9yZ2FuaXphdGlvbl9pZCI6Ijk4MTQxNGE5LTJmZDQtNGQxOC1iMzdiLTQ4ZTFkOWNhMDA3YiIsImF1dGhlbnRpY2F0aW9uTWV0aG9kcyI6W3siY29tcGxldGVkX2F0IjoiMjAyNS0xMS0xNFQxNTo0Mjo0NS41MTAwNDU1NDRaIiwibWV0aG9kIjoiaW50ZXJuYWwifV0sImlwUmlzayI6eyJyaXNrX2xldmVsIjowfSwidG9rZW5UeXBlIjoidmlhbSIsImF1ZCI6InBvcnR1bnVzLmlkcC52b25hZ2UuY29tIiwiZXhwIjoxNzYzMTQxNjk4LCJqdGkiOiJmYWFmYjFmZS02YzljLTQzNDctOGY5YS1mMjUyZjJhMzAwZjYiLCJpYXQiOjE3NjMxNDEzOTgsImlzcyI6IlZJQU0tSUFQIiwibmJmIjoxNzYzMTQxMzgzLCJzdWIiOiIyMzY4OGU1NC1mYmY3LTQ0YjEtYjlhNC0yZTZlZWJhNmVhNzkifX0sImZlZGVyYXRlZEFzc2VydGlvbnMiOnsidmlkZW8tYXBpIjpbeyJhcGlLZXkiOiI1MzY0NjEyYSIsImFwcGxpY2F0aW9uSWQiOiI4Y2YxNWJjNS1kNGY0LTRlMDEtOWFhNi1mNDU5YThiMDc3YTYiLCJtYXN0ZXJBY2NvdW50SWQiOiI1MzY0NjEyYSIsImV4dHJhQ29uZmlnIjp7InZpZGVvLWFwaSI6eyJpbml0aWFsX2xheW91dF9jbGFzc19saXN0IjoiIiwicm9sZSI6Im1vZGVyYXRvciIsInNjb3BlIjoic2Vzc2lvbi5jb25uZWN0Iiwic2Vzc2lvbl9pZCI6IjJfTVg0NFkyWXhOV0pqTlMxa05HWTBMVFJsTURFdE9XRmhOaTFtTkRVNVlUaGlNRGMzWVRaLWZqRTNOakkxTVRJd09UQTNOakYtV21sR1ZFWlRXRGxVVkc1cVpVRTRTRFkxWXpGNmFGcHJmbjUtIn19fV19LCJhdWQiOiJwb3J0dW51cy5pZHAudm9uYWdlLmNvbSIsImV4cCI6MTc2MzE0MzE5OSwianRpIjoiMzk2NWE3NGEtZTJkOS00MDcyLWIyYmMtMDY2ODQwM2ZjYTAwIiwiaWF0IjoxNzYzMTQxMzk5LCJpc3MiOiJWSUFNLUlBUCIsIm5iZiI6MTc2MzE0MTM4NCwic3ViIjoiMjM2ODhlNTQtZmJmNy00NGIxLWI5YTQtMmU2ZWViYTZlYTc5In0.UGTLROAQPo6_500VWj9Gi4Zf4sBy1lck02Hwl0vmwRKyqOnj6c2VyLJtrHK2jxnpNZfhaONtDUg2c_t5yWUGqwDejxi0JcrNY_xp2THjGsh6CtOOcuAYoGsTuLSnzsU4ATL5loZ52cHZPeK17aEtOkXd-7Gks8vuK4S1HI2XPdzhGvUxsIR_R_d6MMdRFxi9nhk-YnXC_Yw_G_Dab3rTwwEAZJm6tvGImDOqcy_UhoPndhXdan8adY5eOwPkMU_1SeA4qE_FSAELSpfRxhUjZ-pCfBVgcR2MolURngasQiEVRZVTWs-zeTJ6k2yE7Q0RRTZHXIXlVBTyE83a3TCgmQ",
        captionsId = null,
    )

data class SessionInfo(
    val apiKey: String,
    val sessionId: String,
    val token: String,
    val captionsId: String?,
)
