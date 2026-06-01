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
import java.util.regex.Pattern
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.By
import com.google.common.truth.Truth.assertThat
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import com.google.maps.android.ktx.demo.execution.SnippetExecutionActivity
import kotlinx.coroutines.runBlocking
import androidx.test.rule.GrantPermissionRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented visual verification test leveraging Gemini Multimodal API to assert that
 * KTX snippets render map elements and animate camera clustering click events exactly as expected on-device.
 */
@RunWith(AndroidJUnit4::class)
public class SnippetVisualVerificationTest {

    // Scenario launched dynamically per test case to inject custom snippet intent extras!

    @get:Rule
    public val grantPermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION
    )

    private lateinit var uiDevice: UiDevice
    private lateinit var visualHelper: GeminiVisualTestHelper
    private lateinit var apiKey: String

    @Before
    public fun setUp() {
        uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        visualHelper = GeminiVisualTestHelper()
        
        // Fetch Gemini API Key passed via adb instrumentation arguments or build config environment
        val arguments = InstrumentationRegistry.getArguments()
        apiKey = arguments.getString("gemini_api_key") 
            ?: System.getenv("GEMINI_API_KEY")
            ?: ""
    }

    @Test
    public fun testMapInitVisualVerification(): Unit = runBlocking {
        val intent = Intent(ApplicationProvider.getApplicationContext(), SnippetExecutionActivity::class.java).apply {
            putExtra("EXTRA_SNIPPET_TITLE", "Map Initialization")
        }
        ActivityScenario.launch<SnippetExecutionActivity>(intent).use {
            // 1. Wait 5 seconds for basic map tiles and styled GeoJSON layers to fully render
            Thread.sleep(5000)

            // 2. Capture screenshot of the initial map centered at London
            val screenshot: Bitmap = InstrumentationRegistry.getInstrumentation().uiAutomation.takeScreenshot()
            screenshot.saveToDeviceStorage("map_init_golden")

            // If API Key is missing, skip semantic validation gracefully
            if (apiKey.isEmpty()) {
                android.util.Log.w("SnippetVisualTest", "GEMINI_API_KEY is missing. Skipping map initialization visual verification.")
                return@runBlocking
            }

            // 3. Perform Image-to-Image AI Semantic Regression Comparison against the golden reference!
            val golden = loadGoldenFromAssets("map_init_golden")
            val match = visualHelper.compareImages(screenshot, golden, apiKey)
            assertThat(match).isTrue()
        }
    }

    @Test
    public fun testCameraAnimationAndIdleBehavior(): Unit = runBlocking {
        val intent = Intent(ApplicationProvider.getApplicationContext(), SnippetExecutionActivity::class.java).apply {
            putExtra("EXTRA_SNIPPET_TITLE", "Animate Camera (Coroutines)")
        }
        ActivityScenario.launch<SnippetExecutionActivity>(intent).use {
            Thread.sleep(4000)
            
            // 1. Click on the 'ANIMATE CAMERA' UI button at the bottom of the screen to trigger awaitAnimateCamera()
            val animateButton = uiDevice.findObject(By.text("ANIMATE CAMERA"))
            if (animateButton != null) {
                animateButton.click()
            }
            
            // 2. Wait 4 seconds for the camera animation coroutine to complete and KTX cameraIdleEvents to fire
            Thread.sleep(4000)

            // 3. Capture screenshot showing the close-up view
            val screenshot1: Bitmap = InstrumentationRegistry.getInstrumentation().uiAutomation.takeScreenshot()
            screenshot1.saveToDeviceStorage("camera_animation_golden")

            // If API Key is missing, skip semantic validation gracefully
            if (apiKey.isEmpty()) {
                android.util.Log.w("SnippetVisualTest", "GEMINI_API_KEY is missing. Skipping camera animation and idle flow verification.")
                return@runBlocking
            }

            // 4. Perform Image-to-Image AI Semantic Regression Comparison against the golden close-up reference!
            val golden = loadGoldenFromAssets("camera_animation_golden")
            val match = visualHelper.compareImages(screenshot1, golden, apiKey)
            assertThat(match).isTrue()
        }
    }

    @Test
    public fun testClusteringFlowVisualVerification(): Unit = runBlocking {
        val intent = Intent(ApplicationProvider.getApplicationContext(), SnippetExecutionActivity::class.java).apply {
            putExtra("EXTRA_SNIPPET_TITLE", "Marker Cluster Click Flow")
        }
        ActivityScenario.launch<SnippetExecutionActivity>(intent).use {
            // 1. Wait for map layers and cluster badges to settle (5 seconds)
            Thread.sleep(5000)

            // 2. Capture initial screenshot of the map displaying London cluster badges
            val screenshot1: Bitmap = InstrumentationRegistry.getInstrumentation().uiAutomation.takeScreenshot()
            screenshot1.saveToDeviceStorage("clustering_clicks_before")
            
            // 3. Attempt to ask Gemini to dynamically locate the cluster badge coordinates
            val badgeDescription = "the circular marker cluster badge containing a number like '10' or '50' near the center of the map"
            val targetPoint = if (apiKey.isNotEmpty()) {
                visualHelper.findVisualCoordinates(screenshot1, badgeDescription, apiKey)
            } else null
            
            val clickX = targetPoint?.x ?: 540
            val clickY = targetPoint?.y ?: 1200
            android.util.Log.i("SnippetVisualTest", "Cluster Badge Click Coordinate: ($clickX, $clickY)")

            // 4. Trigger click at the cluster badge
            uiDevice.click(clickX, clickY)
            
            // 5. Wait 3 seconds for camera animation to settle
            Thread.sleep(3000)

            // 6. Capture final screenshot after cluster expansion
            val screenshot2: Bitmap = InstrumentationRegistry.getInstrumentation().uiAutomation.takeScreenshot()
            screenshot2.saveToDeviceStorage("clustering_clicks_after")

            // If API Key is missing, skip semantic validation gracefully
            if (apiKey.isEmpty()) {
                android.util.Log.w("SnippetVisualTest", "GEMINI_API_KEY is missing. Skipping semantic visual AI verification.")
                return@runBlocking
            }
            
            // 7. Perform Image-to-Image AI Semantic Regression Comparisons for before and after click states!
            val goldenBefore = loadGoldenFromAssets("clustering_clicks_before")
            val matchBefore = visualHelper.compareImages(screenshot1, goldenBefore, apiKey)
            assertThat(matchBefore).isTrue()

            val goldenAfter = loadGoldenFromAssets("clustering_clicks_after")
            val matchAfter = visualHelper.compareImages(screenshot2, goldenAfter, apiKey)
            assertThat(matchAfter).isTrue()
        }
    }

    @Test
    public fun testLocationFlowStreamingBehavior(): Unit = runBlocking {
        val intent = Intent(ApplicationProvider.getApplicationContext(), SnippetExecutionActivity::class.java).apply {
            putExtra("EXTRA_SNIPPET_TITLE", "Fine Location Flow")
        }
        ActivityScenario.launch<SnippetExecutionActivity>(intent).use {
            Thread.sleep(4000)

            // 1. Inject first mock GPS location (London: 51.5074, -0.1278) via shell commands
            val packageName = InstrumentationRegistry.getInstrumentation().targetContext.packageName
            uiDevice.executeShellCommand("appops set $packageName android:mock_location allow")
            uiDevice.executeShellCommand("cmd location providers set-test-provider-enabled gps true")
            uiDevice.executeShellCommand("cmd location providers set-test-provider-location gps --location 51.5074,-0.1278")
            
            // Wait 3 seconds for LocationManager.fineLocationEvents() to reactively collect coordinates and draw marker
            Thread.sleep(3000)

            // Capture screen for London mock location
            val screenshot1: Bitmap = InstrumentationRegistry.getInstrumentation().uiAutomation.takeScreenshot()
            screenshot1.saveToDeviceStorage("location_flow_london")

            // 2. Inject second mock GPS location (San Francisco: 37.7576, -122.4194) to test active Flow STREAMING!
            uiDevice.executeShellCommand("cmd location providers set-test-provider-location gps --location 37.7576,-122.4194")
            
            // Wait 3 seconds for flow to stream the second coordinate and update the user marker location on screen
            Thread.sleep(3000)

            // Capture screen for San Francisco mock location
            val screenshot2: Bitmap = InstrumentationRegistry.getInstrumentation().uiAutomation.takeScreenshot()
            screenshot2.saveToDeviceStorage("location_flow_sf")

            // Clean up and disable mock location provider on the device
            uiDevice.executeShellCommand("cmd location providers set-test-provider-enabled gps false")

            // If API Key is missing, skip semantic validation gracefully
            if (apiKey.isEmpty()) {
                android.util.Log.w("SnippetVisualTest", "GEMINI_API_KEY is missing. Skipping streaming location flow visual AI verification.")
                return@runBlocking
            }

            // 3. Perform Image-to-Image AI Semantic Regression Comparisons for both mock streaming locations!
            val goldenLondon = loadGoldenFromAssets("location_flow_london")
            val matchLondon = visualHelper.compareImages(screenshot1, goldenLondon, apiKey)
            assertThat(matchLondon).isTrue()

            val goldenSF = loadGoldenFromAssets("location_flow_sf")
            val matchSF = visualHelper.compareImages(screenshot2, goldenSF, apiKey)
            assertThat(matchSF).isTrue()
        }
    }

    @Test
    public fun testMarkerClicksFlowVisualVerification(): Unit = runBlocking {
        val intent = Intent(ApplicationProvider.getApplicationContext(), SnippetExecutionActivity::class.java).apply {
            putExtra("EXTRA_SNIPPET_TITLE", "Marker Collection Click Flow")
        }
        ActivityScenario.launch<SnippetExecutionActivity>(intent).use {
            Thread.sleep(4000)
            val screenshot1: Bitmap = InstrumentationRegistry.getInstrumentation().uiAutomation.takeScreenshot()

            // 1. Attempt to locate the unclustered marker dynamically
            val markerDescription = "the unclustered light-blue (azure) marker pin located slightly south of London center"
            val targetPoint = if (apiKey.isNotEmpty()) {
                visualHelper.findVisualCoordinates(screenshot1, markerDescription, apiKey)
            } else null

            val clickX = targetPoint?.x ?: 540
            val clickY = targetPoint?.y ?: 1650
            android.util.Log.i("SnippetVisualTest", "Marker Click Coordinate: ($clickX, $clickY)")

            // 2. Trigger a tap on the marker
            uiDevice.click(clickX, clickY)
            Thread.sleep(2000)

            // 3. Capture screenshot containing the click toast popup
            val screenshot2: Bitmap = InstrumentationRegistry.getInstrumentation().uiAutomation.takeScreenshot()
            screenshot2.saveToDeviceStorage("marker_clicks")

            // If API Key is missing, skip semantic validation gracefully
            if (apiKey.isEmpty()) {
                android.util.Log.w("SnippetVisualTest", "GEMINI_API_KEY is missing. Skipping marker click visual AI verification.")
                return@runBlocking
            }

            // 4. Perform Image-to-Image AI Semantic Regression Comparison against the golden clicked state reference!
            val golden = loadGoldenFromAssets("marker_clicks")
            val match = visualHelper.compareImages(screenshot2, golden, apiKey)
            assertThat(match).isTrue()
        }
    }

    private fun Bitmap.saveToDeviceStorage(fileName: String) {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val dir = context.getExternalFilesDir(null) ?: return
        val file = java.io.File(dir, "$fileName.png")
        try {
            java.io.FileOutputStream(file).use { out ->
                this.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            android.util.Log.i("SnippetVisualTest", "Screenshot successfully saved to app storage: ${file.absolutePath}")
        } catch (e: Exception) {
            android.util.Log.e("SnippetVisualTest", "Failed to save screenshot to app storage", e)
        }
    }

    private fun loadGoldenFromAssets(fileName: String): Bitmap {
        val testContext = InstrumentationRegistry.getInstrumentation().context
        val assetManager = testContext.assets
        assetManager.open("goldens/$fileName.png").use { stream ->
            return android.graphics.BitmapFactory.decodeStream(stream)
        }
    }
}
