/*
 * Copyright 2026 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.maps.android.ktx.demo.visual

import android.graphics.Bitmap
import android.graphics.Point
import android.util.Base64
import android.util.Log
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.ByteArrayOutputStream

/**
 * Helper class to interact with the Gemini API for visual verification and action.
 */
class GeminiVisualTestHelper {
    private val json =
        Json {
            ignoreUnknownKeys = true
            prettyPrint = true
        }

    private val client =
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(json)
            }
        }

    /**
     * Executes a UI action based on a natural language prompt by analyzing UI XML hierarchies.
     */
    suspend fun performActionFromPrompt(
        prompt: String,
        uiDevice: UiDevice,
        apiKey: String,
    ) {
        val hierarchyStream = ByteArrayOutputStream()
        uiDevice.dumpWindowHierarchy(hierarchyStream)
        val hierarchyXml = hierarchyStream.toString("UTF-8")

        val systemPrompt =
            """
            You are an expert Android QA automaton. Your task is to translate a natural language command 
            into a specific action to be performed on a UI. Given a UI hierarchy (in XML format), 
            determine the correct action and selector.

            The available actions are: "click", "longClick", "setText".

            Your response MUST be a single, well-formed JSON object with "action" and "selector" keys.
            The "selector" object must contain exactly one of "text", "contentDescription", or "resourceId".
            If the action is "setText", you must also include a "textValue" field at the top level.

            Example for a click:
            { "action": "click", "selector": { "text": "Login" } }

            Example for setting text:
            { "action": "setText", "selector": { "resourceId": "com.example.app:id/email_input" }, "textValue": "test@example.com" }
            """.trimIndent()

        val fullPrompt = "$systemPrompt\n\nCommand: \"$prompt\"\n\nUI Hierarchy:\n$hierarchyXml"
        val modelName = "gemini-2.5-flash"
        val request = GeminiRequest(contents = listOf(Content(parts = listOf(Part(text = fullPrompt)))))

        val response: HttpResponse =
            client.post("https://generativelanguage.googleapis.com/v1/models/$modelName:generateContent?key=$apiKey") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }

        if (response.status != HttpStatusCode.OK) {
            val errorBody = response.bodyAsText()
            Log.e("GeminiVisualTestHelper", "Action API Error: ${response.status} $errorBody")
            throw Exception("Gemini Action API returned an error: ${response.status}\n$errorBody")
        }

        val geminiResponse: GeminiResponse = response.body()
        val actionJson =
            geminiResponse.candidates
                .firstOrNull()
                ?.content
                ?.parts
                ?.firstOrNull()
                ?.text
                ?: throw Exception("Gemini returned no action JSON.")

        val cleanedActionJson = actionJson.removePrefix("```json\n").removeSuffix("\n```")
        Log.d("GeminiVisualTestHelper", "Received Action JSON: $cleanedActionJson")

        try {
            val aiAction = json.decodeFromString<AiAction>(cleanedActionJson)
            val selector =
                aiAction.selector.let {
                    when {
                        it.text != null -> By.text(it.text)
                        it.contentDescription != null -> By.desc(it.contentDescription)
                        it.resourceId != null -> By.res(it.resourceId)
                        else -> throw IllegalArgumentException("Selector must have text, contentDescription, or resourceId.")
                    }
                }

            val uiObject =
                uiDevice.wait(Until.findObject(selector), 10000)
                    ?: throw Exception("Could not find UI element for selector: $selector")

            when (aiAction.action.lowercase()) {
                "click" -> uiObject.click()
                "longclick" -> uiObject.longClick()
                "settext" -> {
                    val textToSet = aiAction.textValue ?: throw Exception("Action 'setText' requires a 'textValue' field.")
                    uiObject.text = textToSet
                }
                else -> throw UnsupportedOperationException("Action '${aiAction.action}' is not supported.")
            }
        } catch (e: Exception) {
            Log.e("GeminiVisualTestHelper", "Failed to parse or execute AI action", e)
            throw e
        }
    }

    /**
     * Analyzes an image (screenshot) with a given prompt using the Gemini multimodal API.
     */
    suspend fun analyzeImage(
        bitmap: Bitmap,
        prompt: String,
        apiKey: String,
    ): String? {
        val base64Image = bitmap.toBase64EncodedJpeg()
        val request =
            GeminiRequest(
                contents =
                    listOf(
                        Content(
                            parts =
                                listOf(
                                    Part(text = prompt),
                                    Part(
                                        inlineData =
                                            InlineData(
                                                mimeType = "image/jpeg",
                                                data = base64Image,
                                              ),
                                    ),
                                ),
                        ),
                    ),
            )

        // Using gemini-2.5-flash for fast, efficient multimodal image analysis
        val response: HttpResponse =
            client.post("https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent?key=$apiKey") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }

        if (response.status != HttpStatusCode.OK) {
            val errorBody = response.bodyAsText()
            Log.e("GeminiVisualTestHelper", "API Error: ${response.status} $errorBody")
            throw Exception("Gemini API returned an error: ${response.status}\n$errorBody")
        }

        val geminiResponse: GeminiResponse = response.body()
        if (geminiResponse.candidates.isEmpty()) {
            val rawBody = response.bodyAsText()
            Log.w("GeminiVisualTestHelper", "Gemini API returned empty candidates. Full response: $rawBody")
            throw Exception("Gemini API returned no candidates.")
        }

        return geminiResponse.candidates
            .firstOrNull()
            ?.content
            ?.parts
            ?.firstOrNull()
            ?.text
    }

    /**
     * Dynamically locates the coordinates of a visual element described in natural language.
     * Returns the center pixel Point on the screen, or null if not found/parse error.
     */
    suspend fun findVisualCoordinates(
        bitmap: Bitmap,
        elementDescription: String,
        apiKey: String,
    ): Point? {
        val spatialPrompt =
            """
            You are an expert spatial coordinate regression assistant. Look at this Android map screen.
            Locate the visual element matching this description: "$elementDescription".
            
            Return its exact center pixel coordinate (x and y) on the screen as a single JSON object:
            { "x": <int>, "y": <int> }
            
            Ensure the coordinates match the screen dimensions, and do not return any additional text, code delimiters, or markdown formatting. Reply ONLY with the JSON.
            """.trimIndent()

        val responseText = analyzeImage(bitmap, spatialPrompt, apiKey) ?: return null
        val cleanedResponse = responseText.removePrefix("```json\n").removeSuffix("\n```").trim()
        Log.d("GeminiVisualTestHelper", "Received Spatial Coordinate JSON: $cleanedResponse")

        return try {
            val aiCoord = json.decodeFromString<AiCoordinate>(cleanedResponse)
            Point(aiCoord.x, aiCoord.y)
        } catch (e: Exception) {
            Log.e("GeminiVisualTestHelper", "Failed to parse visual coordinates from AI response: $cleanedResponse", e)
            null
        }
    }

    /**
     * Performs an AI-driven semantic comparison between a golden reference screenshot and the current run.
     * Returns true if they match visually/layout-wise, false otherwise.
     */
    suspend fun compareImages(
        currentBitmap: Bitmap,
        goldenBitmap: Bitmap,
        apiKey: String,
    ): Boolean {
        val base64Current = currentBitmap.toBase64EncodedJpeg()
        val base64Golden = goldenBitmap.toBase64EncodedJpeg()

        val regressionPrompt =
            """
            You are an expert visual regression QA model. Compare the following two Android Google Map screenshots:
            - Image A: Golden Reference Screenshot (First Image attached)
            - Image B: Current Test Run Screenshot (Second Image attached)
            
            Do they display the exact same map layout, coordinates, styled vector polygons, custom marker pins, or clicked Toast feedbacks in the same positions, matching styling and design semantically?
            
            Ignore minor rendering variances, scale bar differences, and map label text font differences.
            
            Reply exactly with 'YES' or 'NO' only.
            """.trimIndent()

        val request =
            GeminiRequest(
                contents =
                    listOf(
                        Content(
                            parts =
                                listOf(
                                    Part(text = regressionPrompt),
                                    Part(
                                        inlineData =
                                            InlineData(
                                                mimeType = "image/jpeg",
                                                data = base64Golden
                                            )
                                    ),
                                    Part(
                                        inlineData =
                                            InlineData(
                                                mimeType = "image/jpeg",
                                                data = base64Current
                                            )
                                    ),
                                )
                        )
                    )
            )

        val response: HttpResponse =
            client.post("https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent?key=$apiKey") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }

        if (response.status != HttpStatusCode.OK) {
            val errorBody = response.bodyAsText()
            Log.e("GeminiVisualTestHelper", "API Error during image comparison: ${response.status} $errorBody")
            throw Exception("Gemini API returned an error during image comparison: ${response.status}\n$errorBody")
        }

        val geminiResponse: GeminiResponse = response.body()
        val answer = geminiResponse.candidates
            .firstOrNull()
            ?.content
            ?.parts
            ?.firstOrNull()
            ?.text
            ?.trim()
            ?.uppercase()

        Log.i("GeminiVisualTestHelper", "AI Visual Regression Comparison Result: $answer")
        return answer == "YES"
    }

    private fun Bitmap.toBase64EncodedJpeg(): String {
        val outputStream = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }
}

@Serializable
data class AiCoordinate(
    val x: Int,
    val y: Int,
)

@Serializable
data class AiAction(
    val action: String,
    val selector: AiSelector,
    val textValue: String? = null,
)

@Serializable
data class AiSelector(
    val text: String? = null,
    val contentDescription: String? = null,
    val resourceId: String? = null,
)

@Serializable
data class GeminiRequest(
    val contents: List<Content>,
)

@Serializable
data class Content(
    val parts: List<Part>,
)

@Serializable
data class Part(
    val text: String? = null,
    @SerialName("inline_data")
    val inlineData: InlineData? = null,
)

@Serializable
data class InlineData(
    @SerialName("mime_type")
    val mimeType: String,
    val data: String,
)

@Serializable
data class GeminiResponse(
    val candidates: List<Candidate> = emptyList(),
)

@Serializable
data class Candidate(
    val content: Content,
)
