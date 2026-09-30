package com.vesaa.mytv.ui.screens.leanback.video.player

import com.vesaa.mytv.ui.utils.SP
import org.junit.Assert.*
import org.junit.Test

class VideoDecoderPolicyTest {
    private val xring = VideoDecoderPolicy.Candidate("c2.xring.avc.decoder", false, true)
    private val otherHardware = VideoDecoderPolicy.Candidate("vendor.avc.decoder", false, true)
    private val android = VideoDecoderPolicy.Candidate("c2.android.avc.decoder", true, false)

    @Test
    fun runtimeFailureTriesAnotherHardwareBeforeSoftwareAndTerminates() {
        val policy = VideoDecoderPolicy(SP.VideoPlayerDecodeMode.AUTO)
        val advertised = listOf(android, xring, otherHardware)
        assertEquals(listOf(xring, otherHardware, android), policy.orderedCandidates(advertised))
        assertTrue(policy.reject(setOf(xring.name)))
        assertEquals(listOf(otherHardware, android), policy.orderedCandidates(advertised))
        assertTrue(policy.reject(setOf(otherHardware.name)))
        assertEquals(listOf(android), policy.orderedCandidates(advertised))
        assertTrue(policy.reject(setOf(android.name)))
        assertTrue(policy.orderedCandidates(advertised).isEmpty())
        assertFalse(policy.reject(setOf(android.name)))
    }

    @Test
    fun hardwareOnlyNeverFallsBackToSoftware() {
        val policy = VideoDecoderPolicy(SP.VideoPlayerDecodeMode.HARDWARE_ONLY)
        policy.reject(setOf(xring.name))
        assertEquals(listOf(otherHardware), policy.orderedCandidates(listOf(xring, android, otherHardware)))
        policy.reject(setOf(otherHardware.name))
        assertTrue(policy.orderedCandidates(listOf(xring, android, otherHardware)).isEmpty())
    }

    @Test
    fun softwareOnlyNeverReturnsFailedHardwareAndNewChannelResetsRejections() {
        val policy = VideoDecoderPolicy(SP.VideoPlayerDecodeMode.SOFTWARE_ONLY)
        assertEquals(listOf(android), policy.orderedCandidates(listOf(xring, android)))
        policy.reject(setOf(android.name))
        assertTrue(policy.orderedCandidates(listOf(xring, android)).isEmpty())
        val nextChannel = VideoDecoderPolicy(SP.VideoPlayerDecodeMode.AUTO)
        assertEquals(listOf(xring, android), nextChannel.orderedCandidates(listOf(xring, android)))
    }

    @Test
    fun initializationFailureChainExcludesAllFailedCandidatesAndUnknownModeIsSafe() {
        val policy = VideoDecoderPolicy(SP.VideoPlayerDecodeMode.AUTO)
        assertTrue(policy.reject(setOf(xring.name, otherHardware.name, "")))
        assertEquals(listOf(android), policy.orderedCandidates(listOf(xring, otherHardware, android)))
        assertEquals(SP.VideoPlayerDecodeMode.AUTO, SP.VideoPlayerDecodeMode.fromValue(999))
    }
}
