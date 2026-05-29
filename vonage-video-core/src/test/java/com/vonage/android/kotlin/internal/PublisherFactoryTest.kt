package com.vonage.android.kotlin.internal

import android.content.Context
import com.vonage.android.kotlin.model.PublisherConfig
import com.vonage.android.kotlin.sdk.VonagePublisher
import com.vonage.android.kotlin.sdk.VonageSdkFactory
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class PublisherFactoryTest {

    private lateinit var mockSdkFactory: VonageSdkFactory
    private lateinit var mockPublisher: VonagePublisher
    private lateinit var mockContext: Context
    private lateinit var publisherFactory: PublisherFactory

    @Before
    fun setup() {
        mockContext = mockk(relaxed = true)
        mockPublisher = mockk(relaxed = true)
        mockSdkFactory = mockk(relaxed = true) {
            every { createPublisher(any(), any()) } returns mockPublisher
            every { getOptimalResolution(any()) } returns com.vonage.android.kotlin.sdk.VonageCaptureResolution.HIGH
        }
        publisherFactory = PublisherFactory(sdkFactory = mockSdkFactory)
    }

    @Test
    fun `createPublisherState sets publishCaptions to true when config has publishCaptions true`() {
        publisherFactory.init(
            PublisherConfig(
                name = "Test User",
                publishVideo = true,
                publishAudio = true,
                cameraIndex = 1,
                publishCaptions = true,
            ),
        )

        publisherFactory.createPublisherState(mockContext)

        verify { mockPublisher.publishCaptions = true }
    }

    @Test
    fun `createPublisherState sets publishCaptions to false when config has publishCaptions false`() {
        publisherFactory.init(
            PublisherConfig(
                name = "Test User",
                publishVideo = true,
                publishAudio = true,
                cameraIndex = 1,
                publishCaptions = false,
            ),
        )

        publisherFactory.createPublisherState(mockContext)

        verify { mockPublisher.publishCaptions = false }
    }

    @Test
    fun `createPublisherState sets publishCaptions to false when no config is set`() {
        // publisherFactory.init() not called — currentConfig is null
        publisherFactory.createPublisherState(mockContext)

        verify { mockPublisher.publishCaptions = false }
    }
}
