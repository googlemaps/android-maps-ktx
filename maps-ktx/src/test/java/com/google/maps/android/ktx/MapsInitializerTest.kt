/*
 * Copyright 2026 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.maps.android.ktx

import android.content.Context
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapsSdkInitializedCallback
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.Mockito.mockStatic
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
public class MapsInitializerTest {

    @Mock
    private lateinit var context: Context

    private lateinit var mapsInitializerMock: MockedStatic<MapsInitializer>

    @Before
    public fun setUp() {
        mapsInitializerMock = mockStatic(MapsInitializer::class.java)
    }

    @After
    public fun tearDown() {
        mapsInitializerMock.close()
    }

    @Suppress("DEPRECATION")
    @Test
    public fun testAwaitMapsSdkInitializedReturnsActualRenderer(): Unit = runTest {
        mapsInitializerMock.`when`<Int> {
            MapsInitializer.initialize(
                eq(context),
                eq(MapsInitializer.Renderer.LATEST),
                any(OnMapsSdkInitializedCallback::class.java)
            )
        }.thenAnswer { invocation ->
            invocation.getArgument<OnMapsSdkInitializedCallback>(2)
                .onMapsSdkInitialized(MapsInitializer.Renderer.LEGACY)
            ConnectionResult.SUCCESS
        }

        val renderer = context.awaitMapsSdkInitialized(MapsInitializer.Renderer.LATEST)

        assertThat(renderer).isEqualTo(MapsInitializer.Renderer.LEGACY)
    }

    @Test
    public fun testAwaitMapsSdkInitializedThrowsForNonSuccessStatus(): Unit = runTest {
        mapsInitializerMock.`when`<Int> {
            MapsInitializer.initialize(
                eq(context),
                eq(MapsInitializer.Renderer.LATEST),
                any(OnMapsSdkInitializedCallback::class.java)
            )
        }.thenReturn(ConnectionResult.SERVICE_MISSING)

        val exception = runCatching {
            context.awaitMapsSdkInitialized(MapsInitializer.Renderer.LATEST)
        }.exceptionOrNull()

        assertThat(exception).isInstanceOf(GooglePlayServicesNotAvailableException::class.java)
        assertThat((exception as GooglePlayServicesNotAvailableException).errorCode)
            .isEqualTo(ConnectionResult.SERVICE_MISSING)
    }
}
