# 小米平板解码适配

本分支恢复并修改公开的 VsTV **2.1.26** 源码，基线来自
[hongjingeng/vstv](https://github.com/hongjingeng/vstv/tree/afe829374cadde9c79c9149915ca329a07c281f7)，
修订 `afe829374cadde9c79c9149915ca329a07c281f7`。
上游 `vesaaa/vstv` 当前主分支和发行标签只包含文档，没有公开 2.5.20 的完整源码。
本适配版不是对 2.5.20 二进制的修改。

## 设置和行为

设置 → 播放器 → 视频解码方式，切换后下次换台生效：

- **自动（硬解优先）**：解码初始化或运行时失败后，排除本次会话已经失败的组件，先尝试其他硬解，再尝试系统软解。
- **仅硬解**：只允许硬件加速的视频解码器，候选用尽后明确报错。
- **仅软解**：只允许系统软件视频解码器。

同一会话中失败组件不会重复尝试。音频继续使用 Media3/FFmpeg 音频解码。
重建播放器保留静音、暂停、播放速度、回看位置、音轨和字幕选择、请求头、Surface 和 HTTP/UDP 缓冲策略。
视频解码错误不会触发无关的 RTSP TCP/UDP 网络切换，仍保留 HLS 兼容变体回退。
快捷面板正确将 Android/Google 软件视频解码器标记为软解。

当前发布版本为 **VSTV 2.1.27**。独立包名为 `com.vesaa.mytv.xiaomipad`，桌面名称 **VSTV**，可与原版并存。
保留原测试包的包名和签名，以便覆盖升级并保留频道配置；版本号不再带 `-xiaomi-pad` 后缀。
首次安装需配置直播源；仓库不包含用户的频道地址、订阅、请求头或账户数据。

## 实机验证（2026-09-30）

小米平板 `25053RP5CC / violin`，玄戒 O1，Android 16。

| 内容 | 设置 | 实际解码器 | 结果 |
|---|---|---|---|
| 原版失败的第一财经 HD，同一条 1080 隔行 H.264 流 | 自动 | Xring 失败后切到 `c2.android.avc.decoder` | 首帧成功并持续播放 |
| 同一隔行流 | 仅软解 | `c2.android.avc.decoder` | 直接出画面 |
| 同一隔行流 | 仅硬解 | `c2.xring.avc.decoder` | 报错并终止，不使用软解 |
| 自制 1920×1080、25 fps、High Level 4.1 逐行 H.264/AAC 测试片 | 仅硬解 | `c2.xring.avc.decoder` | 首帧成功，保留硬解能力 |

原隔行流的 SPS 为 High、Level 5.1、8-bit 4:2:0、MBAFF。
驱动读取 SPS 后报 `INTERLACED!!! Not supported in baseline or High10 progressive decoder`。
这里的 High10 是错误文案中的能力描述，源实际为 8-bit。

设备 `MediaCodecList.ALL_CODECS` 实际枚举：

```text
c2.xring.avc.decoder             hardware=true,  software=false, alias=false
OMX.xring.video.decoder.avc      canonical=c2.xring.avc.decoder, alias=true
c2.xring.avc.decoder.secure      hardware=true, secure-only component
c2.android.avc.decoder           hardware=false, software=true, alias=false
OMX.google.h264.decoder          canonical=c2.android.avc.decoder, alias=true
```

`OMX.xring...` 是同一个组件的别名。受保护内容的 secure 解码器不构成这条普通直播流的独立备用硬解路径。
当前固件没有第二条独立的普通 AVC 硬解路径。
更换别名或只修改 SPS、profile/level、Surface、同步模式不能安全地把 MBAFF 输入转换为逐行输入。
原源若要使用平板硬解，需要源端先解码、去隔行并重编码成逐行 H.264/HEVC，或由厂商更新解码驱动。
软件解码不等于高质量去隔行，运动画面仍可能出现梳齿；尚未做长时间耗电/温度测试。

## 构建和诊断

需要 JDK 17、Android SDK 35。依赖来自公开仓库，无需上游私有 GitHub Packages 凭据。

```sh
bash ./gradlew :app:testXiaomiPadArmDebugUnitTest :app:assembleXiaomiPadArmDebug
adb install -r app/build/outputs/apk/xiaomiPadArm/debug/app-xiaomiPad-arm-debug.apk
adb logcat -s VsTVDecoder EventLogger
```

6 项 JVM 回归测试通过，覆盖软硬解标签、有界回退、硬解模式不会使用软解、
软解模式不会重新进入硬解，以及换台后的会话重置。
`VsTVDecoder` 日志记录解码器列表、别名和回退候选，不记录完整播放地址或请求头。

参考：

- [AOSP AVC 软件解码器及隔行支持](https://android.googlesource.com/platform/frameworks/av/+/master/media/codec2/components/avc/C2SoftAvcDec.cpp)
- [Media3 1.8.0 解码器选择接口](https://github.com/androidx/media/blob/1.8.0/libraries/exoplayer/src/main/java/androidx/media3/exoplayer/mediacodec/MediaCodecSelector.java)
- [Android 解码器能力与别名](https://developer.android.com/media/optimize/performance/codec)

第三方许可和源码见 [THIRD_PARTY_NOTICES.md](../THIRD_PARTY_NOTICES.md)。
