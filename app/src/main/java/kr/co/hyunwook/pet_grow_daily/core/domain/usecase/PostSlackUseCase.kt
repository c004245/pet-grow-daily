package kr.co.hyunwook.pet_grow_daily.core.domain.usecase

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kr.co.hyunwook.pet_grow_daily.core.database.entity.SlackNotificationRequest
import kr.co.hyunwook.pet_grow_daily.BuildConfig
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class PostSlackUseCase @Inject constructor() {

    suspend operator fun invoke(request: SlackNotificationRequest) {
        try {
            val slackWebhookUrl = runCatching {
                BuildConfig::class.java.getField("SLACK_WEBHOOK").get(null) as String
            }.getOrElse {
                ""
            }

            Log.d("HWO", "slackWebHook -> $slackWebhookUrl")

            val message = if (request.isSuccess) {
                """
                🎉 *새로운 앨범 주문이 완료되었습니다!*
                
                • 주문 ID: `${request.orderId}`
                • 사용자 ID: `${request.userId}`
                • ZIP 파일: ${request.zipUrl}
                
                제작을 시작해주세요! 📸
                """.trimIndent()
            } else {
                """
                ❌ *앨범 주문 처리 실패*
                
                • 주문 ID: `${request.orderId}`
                • 사용자 ID: `${request.userId}`
                • 오류: ${request.errorMessage}
                """.trimIndent()
            }

            val slackPayload = """
            {
                "attachments": [
                    {
                        "color": "${if (request.isSuccess) "good" else "danger"}",
                        "text": "$message",
                        "footer": "Pet Grow Daily",
                        "ts": ${System.currentTimeMillis() / 1000}
                    }
                ]
            }
            """.trimIndent()

            sendHttpPostRequest(slackWebhookUrl, slackPayload)

        } catch (e: Exception) {
            Log.e("PostSlackUseCase", "슬랙 알림 전송 실패: ${e.message}")
        }
    }

    private suspend fun sendHttpPostRequest(url: String, jsonPayload: String) {
        try {
            val client = OkHttpClient()
            val mediaType = "application/json".toMediaType()
            val requestBody = jsonPayload.toRequestBody(mediaType)
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            withContext(Dispatchers.IO) {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    Log.d("PostSlackUseCase", "슬랙 알림 전송 성공")
                } else {
                    Log.e("PostSlackUseCase", "슬랙 알림 전송 실패: ${response.code}")
                }
            }
        } catch (e: Exception) {
            Log.e("PostSlackUseCase", "HTTP 요청 실패: ${e.message}")
        }
    }
}