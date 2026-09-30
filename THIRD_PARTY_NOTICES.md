# Third-party dependencies

Application source retains upstream MIT copyright notices in `LICENSE`. Bundled dependencies retain
their own licenses; the application notice does not relicense those libraries.

## Jellyfin Media3 FFmpeg decoder

- Artifact: `org.jellyfin.media3:media3-ffmpeg-decoder:1.8.0+1`, unmodified Maven Central binary.
- License: GPL-3.0, declared in the published POM.
- Versioned source: https://github.com/jellyfin/jellyfin-androidx-media/tree/v1.8.0%2B1
- Release and source archives: https://github.com/jellyfin/jellyfin-androidx-media/releases/tag/v1.8.0%2B1
- License text: https://github.com/jellyfin/jellyfin-androidx-media/blob/v1.8.0%2B1/LICENSE

The dependency provides FFmpeg audio decoding. Its experimental video renderer is a stub; this
adaptation uses Android system software decoding for video. APK distributions must preserve the
dependency's applicable GPL/FFmpeg licensing and corresponding-source obligations.

## AndroidX Media3

- Version: 1.8.0; Apache-2.0.
- Source: https://github.com/androidx/media/tree/1.8.0
- License: https://github.com/androidx/media/blob/1.8.0/LICENSE

Other inherited dependencies are declared in `gradle/libs.versions.toml` and `app/build.gradle.kts`.
The legacy FFmpeg AAR in `app/libs` is excluded from the adaptation build to prevent duplicate classes.
