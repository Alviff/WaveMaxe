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
        availableSongs: List<Song>,
        likedSongs: List<Song> = emptyList(),
        recentlyPlayed: List<Song> = emptyList(),
        selectedTheme: String? = null,
        prefGenre: String? = null,
        tempoFilter: String? = null, // "Slow", "Medium", "Fast", "All"
        isAdventurous: Boolean = false
    ): AIDeclaredPlaylistResponse = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            Log.w("AICopilotService", "Gemini API Key is placeholder/empty; falling back to offline mood maps.")
            return@withContext getOfflineFallbackRecommendation(
                userPrompt, availableSongs, likedSongs, recentlyPlayed, selectedTheme, prefGenre, tempoFilter, isAdventurous
            )
        }

        val basePromptStr = if (userPrompt.isNotBlank()) userPrompt else {
            when (selectedTheme) {
                "workout" -> "High BPM workout pump soundtrack"
                "chill" -> "Serene atmospheric slow space drift chillout"
                "focus" -> "Deep cyberdeck focusing ambient melodies"
                else -> "Perfect vibe synthesis"
            }
        }

        val systemPrompt = """
            You are "Neuro-Copilot", the sentient organic-cyber intelligence powering the PulseWave futuristic music player.
            Your role is to analyze the user's music mood prompt/theme, incorporate their current taste signature, and curate a custom playlist from the available songs.
            You must return a valid JSON object matching this schema exactly:
            {
               "commentary": "Immersive cyberpunk narrative explaining the mood match. Use sci-fi terminology (e.g., neural ports, cybergrid, terminal vectors). Mention their user taste signature (favorites/recents) if relevant. Be expressive and cool.",
               "recommendedSongIds": ["pulsewave_01", "pulsewave_03"],
               "aiPlaylistTitle": "Cybergrid Override",
               "recommendedVisualizerStyle": "Circular Wave"
            }
            
            Available Songs in the PulseWave Database:
            ${availableSongs.joinToString("\n") { "- ID: ${it.id}, Title: '${it.title}', Artist: '${it.artist}', Genre: '${it.genre}', Primary Mood: '${it.mood}', BPM: ${it.bpm}" }}
            
            User's Current Taste Signature:
            ${if (likedSongs.isNotEmpty()) "- Liked Tracks Space: ${likedSongs.joinToString(", ") { "'${it.title}' (${it.genre})" }}" else "- Liked Tracks Space: [None registered yet]"}
            ${if (recentlyPlayed.isNotEmpty()) "- Synaptic Memory (Recent Tracks): ${recentlyPlayed.joinToString(", ") { "'${it.title}'" }}" else "- Synaptic Memory (Recent Tracks): [Memory buffer clear]"}
            
            Selected Theme Preset: ${selectedTheme ?: "None"}
            Preferred Genre Filter: ${prefGenre ?: "Any"}
            Tempo Constraint: ${tempoFilter ?: "Any"}
            Cybernetic Adventure Level: ${if (isAdventurous) "High (introduce unexpected genre shifts)" else "Strict (precision matching)"}

            MATCH INSTRUCTIONS:
            1. Formulate a curated playlist (1 to 5 songs) drawing from the database matching the criteria.
            2. If Tempo Constraint is "Slow", filter/prioritize songs with BPM < 100.
               If "Medium", filter/prioritize songs with BPM 100 to 125.
               If "Fast", filter/prioritize songs with BPM > 125.
            3. If Preferred Genre Filter is specified and not "All" or "Any", prioritize and include songs matching that genre if possible.
            4. Integrate user's favorite genres/artists derived from their Taste Signature (Favorites and Recents) to bias recommendations.
            5. Return ONLY raw JSON. Do NOT wrap in markdown block tags of any kind.
        """.trimIndent()

        // Build direct HTTP POST Request Body for Gemini REST v1beta manually via JSONObject
        val requestJson = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", "User prompt: '$basePromptStr'")
                        })
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("responseMimeType", "application/json")
                put("temperature", if (isAdventurous) 0.9 else 0.4)
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
            getOfflineFallbackRecommendation(
                userPrompt, availableSongs, likedSongs, recentlyPlayed, selectedTheme, prefGenre, tempoFilter, isAdventurous
            )
        }
    }

    private fun getOfflineFallbackRecommendation(
        userPrompt: String,
        availableSongs: List<Song>,
        likedSongs: List<Song>,
        recentlyPlayed: List<Song>,
        selectedTheme: String?,
        prefGenre: String?,
        tempoFilter: String?,
        isAdventurous: Boolean
    ): AIDeclaredPlaylistResponse {
        var playlistTitle = "Synaptic Pulsewave Core"
        var visualizerStyle = "Circular Wave"
        var commentary = "Offline synapse backup loaded successfully. Deep-scanned your neural matrix database."

        // Analyze Taste Signature
        val signatureFacts = mutableListOf<String>()
        if (likedSongs.isNotEmpty()) {
            val topGenre = likedSongs.groupingBy { it.genre }.eachCount().maxByOrNull { it.value }?.key
            signatureFacts.add("your interest in $topGenre melodies")
        }
        if (recentlyPlayed.isNotEmpty()) {
            signatureFacts.add("your recently audited nodes")
        }

        val habitsNotes = if (signatureFacts.isNotEmpty()) {
            " Correlating patterns to match ${signatureFacts.joinToString(" and ")}."
        } else {
            " Neural buffers are clean; initializing default synthwave seed sequences."
        }

        // Start with all available songs
        var filtered = availableSongs.toList()

        // Apply Genre tuning
        if (!prefGenre.isNullOrBlank() && prefGenre != "All" && prefGenre != "Any") {
            val gFiltered = filtered.filter { it.genre.equals(prefGenre, ignoreCase = true) }
            if (gFiltered.isNotEmpty() || !isAdventurous) {
                filtered = gFiltered.ifEmpty { filtered }
            }
        }

        // Apply Tempo tuning
        if (!tempoFilter.isNullOrBlank() && tempoFilter != "All" && tempoFilter != "Any") {
            val tFiltered = when (tempoFilter) {
                "Slow" -> filtered.filter { it.bpm < 100 }
                "Medium" -> filtered.filter { it.bpm in 100..125 }
                "Fast" -> filtered.filter { it.bpm > 125 }
                else -> filtered
            }
            if (tFiltered.isNotEmpty() || !isAdventurous) {
                filtered = tFiltered.ifEmpty { filtered }
            }
        }

        // Apply theme/prompt override
        val prompt = (userPrompt + " " + (selectedTheme ?: "")).lowercase()
        when {
            selectedTheme == "workout" || prompt.contains("workout") || prompt.contains("run") || prompt.contains("speed") || prompt.contains("fast") -> {
                // Workout theme
                val workoutSongs = filtered.filter { it.bpm >= 120 || it.genre == "Outrun" || it.genre == "Dark Synth" || it.genre == "Synthwave" }
                filtered = workoutSongs.ifEmpty { filtered }
                playlistTitle = "Hypergrid Cyber-Overdrive"
                visualizerStyle = "Circular Wave"
                commentary = "Detected high-velocity adrenal prompts for Theme: Workout.$habitsNotes Overclocking cybergrid neural cores to feed high-tempo Outrun & Dark Synth frequencies straight to your audio deck."
            }
            selectedTheme == "chill" || prompt.contains("chill") || prompt.contains("lofi") || prompt.contains("atmosphere") || prompt.contains("relax") -> {
                // Chill theme
                val chillSongs = filtered.filter { it.bpm < 110 || it.genre == "Lofi" || it.genre == "Vaporwave" }
                filtered = chillSongs.ifEmpty { filtered }
                playlistTitle = "Starlight Gravity Drift"
                visualizerStyle = "Neon Starburst"
                commentary = "Detected solar calm triggers for Theme: Chill.$habitsNotes Routing atmospheric space-lofi feeds into your neural port. Relax your shields and float on synthetic cassette currents."
            }
            selectedTheme == "focus" || prompt.contains("focus") || prompt.contains("study") || prompt.contains("ambient") || prompt.contains("concentration") -> {
                // Focus theme
                val focusSongs = filtered.filter { it.genre == "Lofi" || it.genre == "Retrowave" || it.genre == "Synthwave" }
                filtered = focusSongs.ifEmpty { filtered }
                playlistTitle = "Deep Cyberdeck Focus Grid"
                visualizerStyle = "Line Wave"
                commentary = "Detected focusing terminal logs for Theme: Focus.$habitsNotes Engaging high-concentration sound wave mapping. Filtering out high-frequency noise elements to elevate mental productivity."
            }
            else -> {
                playlistTitle = "Bespoke Taste Alignment"
                visualizerStyle = "Spectrum Bars"
                commentary = "Analyzed customized fine-tuning filters.$habitsNotes Generating tailored frequencies matching your unique spectrum filter. Secure connection complete."
            }
        }

        // Return up to 4 song IDs
        val recommendedSongIds = filtered.shuffled().take(4).map { it.id }.ifEmpty { listOf("pulsewave_01") }

        return AIDeclaredPlaylistResponse(
            commentary = commentary,
            recommendedSongIds = recommendedSongIds,
            aiPlaylistTitle = playlistTitle,
            recommendedVisualizerStyle = visualizerStyle
        )
    }
}
