package com.vesaa.mytv.ui.screens.leanback.video.player

import com.vesaa.mytv.ui.utils.SP

/** One playback session: a decoder that failed must not be selected again during recovery. */
internal class VideoDecoderPolicy(val mode: SP.VideoPlayerDecodeMode) {
    data class Candidate(val name: String, val softwareOnly: Boolean, val hardwareAccelerated: Boolean)

    // Codec queries run on ExoPlayer's playback thread; errors are delivered on the application thread.
    @Volatile
    private var rejectedNames: Set<String> = emptySet()

    val hasFailures: Boolean get() = rejectedNames.isNotEmpty()

    fun orderedCandidates(candidates: List<Candidate>): List<Candidate> {
        val rejected = rejectedNames
        return candidates.filter { candidate ->
            candidate.name !in rejected && when (mode) {
                SP.VideoPlayerDecodeMode.AUTO -> true
                SP.VideoPlayerDecodeMode.HARDWARE_ONLY -> candidate.hardwareAccelerated
                SP.VideoPlayerDecodeMode.SOFTWARE_ONLY -> candidate.softwareOnly
            }
        }.sortedBy { if (it.softwareOnly) 1 else 0 }
    }

    @Synchronized
    fun reject(names: Set<String>): Boolean {
        val additions = names.filter { it.isNotBlank() }.toSet() - rejectedNames
        if (additions.isEmpty()) return false
        rejectedNames = rejectedNames + additions
        return true
    }
}
