package com.vesaa.mytv.ui.screens.leanback.quickpanel

import org.junit.Assert.assertEquals
import org.junit.Test

class QuickPanelMetadataInfoTest {
    @Test
    fun androidSoftwareCodecsAreDisplayedAsSoftware() {
        listOf(
            "c2.android.avc.decoder",
            "c2.google.avc.decoder",
            "OMX.google.h264.decoder",
            "ffmpeg-avc",
            " C2.Android.avc.decoder ",
        ).forEach { name ->
            assertEquals("软解($name)", videoDecoderPathHint(name))
        }
    }

    @Test
    fun vendorCodecsRemainHardwareAndMissingNamesAreUnknown() {
        listOf("c2.xring.avc.decoder", "c2.qti.avc.decoder").forEach { name ->
            assertEquals("硬解($name)", videoDecoderPathHint(name))
        }
        assertEquals("未知", videoDecoderPathHint(""))
        assertEquals("未知", videoDecoderPathHint("  "))
    }
}
