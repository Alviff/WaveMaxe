package com.example.data.api

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class AIDeclaredPlaylistResponse(
    val commentary: String,
    val recommendedSongIds: List<String>,
    val aiPlaylistTitle: String,
    val recommendedVisualizerStyle: String
)

class AICopilotService {

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun generatePlaylistRecommendations(
        userPrompt: String,
        availableSongs: List<Song>
    ): AIDeclaredPlaylistResponse = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            Log.w("AICopilotService", "Gemini API Key is placeholder/empty; falling back to offline mood maps.")
            return@withContext getOfflineFallbackRecommendation(userPrompt, availableSongs)
        }

        val systemPrompt = """
            You are "Neuro-Copilot", the sentient organic-cyber intelligence powering the PulseWave futuristic music player.
            Your role is to analyze the user's music mood prompt and curate a custom playlist from the available songs.
            You must return a valid JSON object matching this schema exactly:
            {
               "commentary": "Immersive cyberpunk narrative explaining the mood match. Use sci-fi terminology (e.g., neural ports, cybergrid, terminal vectors). Be expressive and cool.",
               "recommendedSongIds": ["pulsewave_01", "pulsewave_03"],
               "aiPlaylistTitle": "Cybergrid Override",
               "recommendedVisualizerStyle": "Circular Wave"
            }
            
            Available Songs in the PulseWave Database:
            ${availableSongs.joinToString("\n") { "- ID: ${it.id}, Title: '${it.title}', Artist: '${it.artist}', Genre: '${it.genre}', Primary Mood: '${it.mood}', BPM: ${it.bpm}" }}
            
            Match the user mood as close as possible. Choose 1 to 4 songs from the list in recommendedSongIds.
            Return ONLY raw JSON, do NOT wrap it in markdown block tags of any kind.
        """.trimIndent()

        // Build direct HTTP POST Request Body for Gemini REST v1beta manually via JSONObject
        val requestJson = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", "User prompt: '$userPrompt'")
                        })
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("responseMimeType", "application/json")
                put("temperature", 0.7)
            })
            put("systemInstruction", JSONObject().apply {
                put("parts", JSONArray().apply {
                    put(JSONObject().apply {
                        put("text", systemPrompt)
                    })
                })
            })
        }

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = requestJson.toString().toRequestBody(mediaType)
        
        val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
        
        val request = Request.Builder()
            .url(endpoint)
            .post(requestBody)
            .build()

        try {
            okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw Exception("Unsuccessful API response code: ${response.code}")
                }
                val responseBodyText = response.body?.string() ?: throw Exception("Null response body from Gemini API")
                Log.d("AICopilotService", "Gemini Raw Response: $responseBodyText")

                val mainJsonObj = JSONObject(responseBodyText)
                val candidatesArray = mainJsonObj.getJSONArray("candidates")
                val candidateObj = candidatesArray.getJSONObject(0)
                val contentObj = candidateObj.getJSONObject("content")
                val partsArray = contentObj.getJSONArray("parts")
                val firstPartObj = partsArray.getJSONObject(0)
                val textResponse = firstPartObj.getString("text").trim()

                Log.d("AICopilotService", "Extracted JSON text: $textResponse")

                // Parse standard inner JSON
                val responseObj = JSONObject(textResponse)
                val commentary = responseObj.optString("commentary", "Synaptic audio linkages completed.")
                val title = responseObj.optString("aiPlaylistTitle", "Synthetic Vibe synthesis")
                val style = responseObj.optString("recommendedVisualizerStyle", "Circular Wave")
                
                val idsArray = responseObj.optJSONArray("recommendedSongIds")
                val idsList = mutableListOf<String>()
                if (idsArray != null) {
                    for (i in 0 until idsArray.length()) {
                        idsList.add(idsArray.getString(i))
                    }
                }
                
                if (idsList.isEmpty()) {
                    idsList.add("pulsewave_01")
                }

                AIDeclaredPlaylistResponse(
                    commentary = commentary,
                    recommendedSongIds = idsList,
                    aiPlaylistTitle = title,
                    recommendedVisualizerStyle = style
                )
            }
        } catch (e: Exception) {
            Log.e("AICopilotService", "Gemini HTTP / parsing call failed: ${e.message}. Using offline algorithm.", e)
            getOfflineFallbackRecommendation(userPrompt, availableSongs)
        }
    }

    private fun getOfflineFallbackRecommendation(
        userPrompt: String,
        availableSongs: List<Song>
    ): AIDeclaredPlaylistResponse {
        val prompt = userPrompt.lowercase()
        val recommendedSongs = mutableListOf<String>()
        val playlistTitle: String
        val visualizerStyle: String
        val commentary: String

        when {
            prompt.contains("run") || prompt.contains("speed") || prompt.contains("highway") || prompt.contains("fast") || prompt.contains("cyberpunk") -> {
                recommendedSongs.addAll(listOf("pulsewave_01", "pulsewave_03", "pulsewave_05"))
                playlistTitle = "Hypergrid Cyber-Overdrive"
                visualizerStyle = "Circular Wave"
                commentary = "Detected hyper-kinetic adrenaline triggers. Neural system buffers override completed. Re-routing Outrun soundscapes directly to your cyber-cortex to escape law forces in Neon Sector 12. Accelerating now."
            }
            prompt.contains("chill") || prompt.contains("lofi") || prompt.contains("cosmic") || prompt.contains("sunset") || prompt.contains("space") -> {
                recommendedSongs.addAll(listOf("pulsewave_02", "pulsewave_04"))
                playlistTitle = "Starlight Gravity Drift"
                visualizerStyle = "Neon Starburst"
                commentary = "Cabin pressure indicators stabilized. Launching deep orbital lofi resonance patterns to match serene stellar drifts. Relax your neural implants; the code is floating under the cosmic dust."
            }
            prompt.contains("dark") || prompt.contains("heavy") || prompt.contains("bass") || prompt.contains("hard") || prompt.contains("glitch") -> {
                recommendedSongs.addAll(listOf("pulsewave_03", "pulsewave_05"))
                playlistTitle = "Sub-grid Obsidian Void"
                visualizerStyle = "Spectrum Bars"
                commentary = "Intense sub-bass frequencies verified in your synaptic path. Initializing deep bass boost filters and injecting high-amplitude industrial glitch blocks. Brace for heavy cyber-compression."
            }
            prompt.contains("rain") || prompt.contains("monsoon") || prompt.contains("lounge") || prompt.contains("retro") -> {
                recommendedSongs.addAll(listOf("pulsewave_02", "pulsewave_06"))
                playlistTitle = "Cassette Retro Shibuya"
                visualizerStyle = "Line Wave"
                commentary = "Ambient Tokyo rain detected. Initializing analog vintage cassette configurations. Savor warm retro melodies drifting through flickering hologram billboards. Cozy & code-secure."
            }
            else -> {
                val shuffled = availableSongs.shuffled().take(3).map { it.id }
                recommendedSongs.addAll(shuffled)
                playlistTitle = "Synaptic Pulsewave Core"
                visualizerStyle = listOf("Circular Wave", "Spectrum Bars", "Neon Starburst", "Line Wave").random()
                commentary = "Synapses analyzed. Generating bespoke PulseWave vector stream at 60FPS. Synthesizing organic cybernetic wave models to harmonize with custom user moods."
            }
        }

        return AIDeclaredPlaylistResponse(
            commentary = commentary,
            recommendedSongIds = recommendedSongs,
            aiPlaylistTitle = playlistTitle,
            recommendedVisualizerStyle = visualizerStyle
        )
    }
}
