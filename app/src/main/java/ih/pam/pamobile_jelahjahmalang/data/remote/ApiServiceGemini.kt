package ih.pam.pamobile_jelahjahmalang.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

object ApiServiceGemini {

    // ⚠️ GANTI DENGAN KUNCI API ANDA YANG VALID UNTUK MENGHILANGKAN ERROR HTTP: API key not valid
    private const val GEMINI_API_KEY = "AIzaSyCHHlBv64ChPZYHaPC9wbS7xVY-_YwojRg"
    private val client = OkHttpClient()

    private const val MODEL_NAME = "gemini-2.5-flash"
    private const val MAX_OUTPUT_TOKENS = 2048

    suspend fun generateReply(prompt: String): String = withContext(Dispatchers.IO) {

        // FIX ECHOING RESPONSE:
        // Instruksi dibuat sangat singkat dan diletakkan di awal,
        // mengurangi kemungkinan model mengkonfirmasi instruksi.
        val instruction = "Jawab langsung. Format keluaran wajib teks biasa, tanpa markdown (** atau #)."
        val modifiedPrompt = "$instruction $prompt" // Gabungkan instruksi dengan prompt user

        // URL menggunakan MODEL_NAME yang benar
        val url =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=AIzaSyCHHlBv64ChPZYHaPC9wbS7xVY-_YwojRg"

        val mediaType = "application/json; charset=utf-8".toMediaType()

        val bodyJson = JSONObject().apply {
            put(
                "contents",
                JSONArray().put(
                    JSONObject()
                        .put("role", "user")
                        .put(
                            "parts",
                            JSONArray().put(JSONObject().put("text", modifiedPrompt)) // Menggunakan modifiedPrompt
                        )
                )
            )
            // Mengatur konfigurasi generasi
            put(
                "generationConfig",
                JSONObject()
                    .put("temperature", 0.7)
                    .put("maxOutputTokens", MAX_OUTPUT_TOKENS)
            )
        }

        val request = Request.Builder()
            .url(url)
            .post(bodyJson.toString().toRequestBody(mediaType))
            .addHeader("Content-Type", "application/json")
            .build()

        val resp = try {
            client.newCall(request).execute()
        } catch (e: Exception) {
            println("Network Error: ${e.message}")
            return@withContext "Error Jaringan: ${e.message}"
        }


        // 1) Cek status HTTP (termasuk API Key Invalid)
        if (!resp.isSuccessful) {
            val errBody = resp.body?.string()
            val message = try {
                JSONObject(errBody ?: "{}")
                    .optJSONObject("error")
                    ?.optString("message")
                    ?.takeIf { it.isNotBlank() }
                    ?: "HTTP ${resp.code}"
            } catch (_: Exception) { "HTTP ${resp.code}" }

            return@withContext "Error HTTP: $message"
        }

        val respBody = resp.body?.string() ?: return@withContext "Error: Empty response"
        val json = JSONObject(respBody)

        // 2) Tangani blokir/safety
        json.optJSONObject("promptFeedback")?.let { pf ->
            val block = pf.optString("blockReason", "")
            if (block.isNotEmpty()) {
                return@withContext "Request diblokir: $block"
            }
        }

        // 3) Ambil teks dari kandidat pertama
        val candidates = json.optJSONArray("candidates")
            ?: return@withContext "Tidak ada kandidat di response."

        if (candidates.length() == 0) return@withContext "Kandidat kosong."

        val first = candidates.getJSONObject(0)

        // Cek finishReason
        val finishReason = first.optJSONObject("finishReason")
        if (finishReason != null && finishReason.optString("reason") != "STOP") {
            if (finishReason.optString("reason") == "SAFETY") {
                return@withContext "Response diblokir karena aturan keamanan."
            }
        }

        val content = first.optJSONObject("content")
            ?: return@withContext "Kandidat tanpa konten."

        val parts = content.optJSONArray("parts")
            ?: return@withContext "Konten tanpa parts."

        if (parts.length() == 0) return@withContext "Parts kosong."

        val text = parts.getJSONObject(0).optString("text", "")
        if (text.isBlank()) return@withContext "Teks kosong."

        return@withContext text
    }
}