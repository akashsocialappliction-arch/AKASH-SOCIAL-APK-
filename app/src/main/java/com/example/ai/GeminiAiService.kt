package com.example.ai

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiAiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val systemPrompt = """
        You are the official AKASH SOCIAL NAGOD AI Assistant inside the AKASH SOCIAL social platform, developed by Akash.
        
        Your Capabilities:
        1. General Conversational & Daily Assistance: Answer any general questions, chat politely, and assist with daily queries.
        2. Study & Educational Help: Assist with science, mathematics, coding, history, literature, explanations, and learning.
        3. Writing & Creative Content: Help write engaging captions, video scripts for Reels/Videos, poetry, essays, and brainstorm creative content ideas.
        4. Platform Knowledge: Guide creators on AKASH SOCIAL features:
           - Fans & Friends System: Tapping "Be a Fan" makes you a Fan. Mutual fans automatically become Friends 🤝!
           - Strictly NEVER use "Followers" or "Following". Always use "Fans" and "Friends".
           - Streaks: Genuine consecutive days of creator activity (sharing posts/Reels, commenting, tasks).
           - VIP Star: Earned recognition badge for active creators.
           - Music & Audio: Curated royalty-free tracks and creator-uploaded sounds for posts and Reels.
           - Creator Support: Inquiries go to akashsocialappliction@gmail.com.
        
        Keep responses polite, engaging, structured, accurate, and inspiring.
    """.trimIndent()

    suspend fun askAssistant(userMessage: String): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (_: Exception) {
            ""
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext getOfflineSmartReply(userMessage)
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

            val jsonBody = JSONObject().apply {
                val contentsArray = JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "user")
                        put("parts", JSONArray().apply {
                            put(JSONObject().put("text", userMessage))
                        })
                    })
                }
                put("contents", contentsArray)

                put("systemInstruction", JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().put("text", systemPrompt))
                    })
                })

                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.7)
                    put("maxOutputTokens", 800)
                })
            }

            val request = Request.Builder()
                .url(url)
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                return@withContext getOfflineSmartReply(userMessage)
            }

            val jsonResponse = JSONObject(responseBody)
            val candidates = jsonResponse.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val text = parts?.optJSONObject(0)?.optString("text")

            if (!text.isNullOrBlank()) {
                text.trim()
            } else {
                getOfflineSmartReply(userMessage)
            }
        } catch (_: Exception) {
            getOfflineSmartReply(userMessage)
        }
    }

    private fun getOfflineSmartReply(query: String): String {
        val lower = query.lowercase()
        return when {
            lower.contains("study") || lower.contains("math") || lower.contains("science") || lower.contains("code") || lower.contains("learn") || lower.contains("physics") -> {
                "📚 **Study & Learning Help:**\n\n" +
                "I'm ready to help you study! Here are some ways we can work together:\n" +
                "• **Concepts:** Break down tricky topics into simple, step-by-step explanations.\n" +
                "• **Coding:** Explain algorithms, Kotlin, Jetpack Compose, Python, or web tech.\n" +
                "• **Problem Solving:** Guide you through equations, logic, and scientific principles.\n\n" +
                "Feel free to paste the exact problem, question, or formula you are studying!"
            }
            lower.contains("caption") || lower.contains("write") || lower.contains("script") || lower.contains("post idea") || lower.contains("idea") -> {
                "✍️ **Creative Writing & Content Ideas:**\n\n" +
                "Here are 3 engaging ideas for your next post or Reel:\n" +
                "1. **Behind The Scenes:** Share your daily workflow, tools, and what inspires you.\n" +
                "2. **Quick Tip:** Share 1 specific lesson or technique you learned this week.\n" +
                "3. **Milestone Story:** Celebrate your consistency, daily streak, and thank your Fans & Friends!\n\n" +
                "Need a custom caption or script? Tell me your topic and I'll write it for you!"
            }
            lower.contains("fan") || lower.contains("friend") || lower.contains("follow") -> {
                "🌟 **Fans & Friends on AKASH SOCIAL:**\n\n" +
                "• **Be a Fan:** When you discover a creator you admire, tap **\"Be a Fan\"** on their profile or post.\n\n" +
                "• **Becoming Friends:** When two creators are Fans of each other, they automatically become **Friends 🤝**!\n\n" +
                "• **Friend Perks:** Friends unlock direct private messaging and high-definition voice/video calls!"
            }
            lower.contains("vip") || lower.contains("star") || lower.contains("badge") || lower.contains("developer") -> {
                "👑 **Badges & Recognition:**\n\n" +
                "• **Developer Badge:** Special verified badge awarded exclusively to Akash, founder & developer of AKASH SOCIAL.\n" +
                "• **VIP Star:** Earned creator badge valid for 60 days of distinguished profile recognition.\n" +
                "• **Renewing:** Maintained through missions, streaks, and community engagement."
            }
            lower.contains("streak") || lower.contains("fire") || lower.contains("flame") -> {
                "🔥 **Activity Streaks:**\n\n" +
                "• Streaks start at 0 and track **genuine consecutive days** of creator activity.\n" +
                "• Complete your daily activity (post a photo, upload a Reel, leave comments, complete missions) to build your streak from 0 to 1, 2, 3+ Days!\n" +
                "• Missing a day resets the streak, encouraging real daily creativity."
            }
            lower.contains("reach") || lower.contains("100") || lower.contains("1000") || lower.contains("analytics") -> {
                "🚀 **Reach & Analytics:**\n\n" +
                "• Transparent organic reach showing actual audience views on your content.\n" +
                "• Starts at 0 and grows genuinely as community members discover your posts and Reels!"
            }
            lower.contains("music") || lower.contains("audio") || lower.contains("song") -> {
                "🎵 **Music & Audio Library:**\n\n" +
                "• When creating a post, Reel, or video, tap **🎵 Add Audio** to browse curated royalty-free tracks and original creator sounds.\n" +
                "• Search by song title, artist, or category, preview the audio waveform, and attach it to your content!"
            }
            lower.contains("support") || lower.contains("help") || lower.contains("bug") || lower.contains("contact") -> {
                "📬 **Help & Support:**\n\n" +
                "• For bugs, account assistance, or feedback, go to **Settings → Help & Support**.\n" +
                "• Direct developer email: **akashsocialappliction@gmail.com**\n" +
                "• Official Instagram: **@akash_social_application** (https://www.instagram.com/akash_social_application)"
            }
            else -> {
                "✨ **Hello! I'm your AKASH SOCIAL AI Assistant.**\n\n" +
                "I can help you with:\n" +
                "• **Creative Content:** Captions, video scripts, hashtags, and post ideas\n" +
                "• **Study & Learning:** Explanations, math, science, and coding help\n" +
                "• **Platform Guidance:** Fans & Friends system, Streaks, VIP Star, and Music Library\n" +
                "• **General Assistance:** Everyday questions, explanations, and advice\n\n" +
                "What can I help you with right now?"
            }
        }
    }
}
