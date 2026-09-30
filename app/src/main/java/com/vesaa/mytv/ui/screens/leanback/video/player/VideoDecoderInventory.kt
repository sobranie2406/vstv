package com.vesaa.mytv.ui.screens.leanback.video.player

import android.media.MediaCodecList
import android.os.Build
import android.util.Log

/** Local diagnostics. No channel URLs, headers or subscription credentials are logged. */
internal object VideoDecoderInventory {
    const val TAG = "VsTVDecoder"

    fun logAvcDecoders() {
        runCatching {
            val codecs = MediaCodecList(MediaCodecList.ALL_CODECS).codecInfos.filter {
                !it.isEncoder && it.supportedTypes.any { type -> type.equals("video/avc", true) }
            }
            codecs.forEach { codec ->
                val details = if (Build.VERSION.SDK_INT >= 29) {
                    "canonical=${codec.canonicalName} alias=${codec.isAlias} " +
                        "hardware=${codec.isHardwareAccelerated} software=${codec.isSoftwareOnly}"
                } else {
                    "classification=unavailable_on_api_${Build.VERSION.SDK_INT}"
                }
                Log.i(TAG, "avc_inventory name=${codec.name} $details")
            }
        }.onFailure { Log.w(TAG, "avc_inventory_unavailable", it) }
    }
}
