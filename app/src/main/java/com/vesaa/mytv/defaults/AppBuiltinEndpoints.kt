package com.vesaa.mytv.defaults

/** 首装默认端点；发版构建可写入与仓库默认不同的取值。 */
object AppBuiltinEndpoints {
    const val GIT_RELEASE_LATEST_API =
        "https://api.github.com/repos/sobranie2406/vstv/releases/latest"

    const val IPTV_DEFAULT_SUBSCRIPTION_URL =
        ""

    const val IPTV_DEFAULT_REQUEST_HEADERS = ""

    const val EPG_XML_PRIMARY =
        ""

    const val EPG_XML_SECONDARY =
        ""

    val EPG_BUILTIN_ORDERED: List<String> =
        listOf(EPG_XML_PRIMARY, EPG_XML_SECONDARY).filter { it.isNotBlank() }

    /** Base64 HMAC 密钥；空表示出站不改写 UA。 */
    const val REQUEST_SIGNING_KEY_B64: String =
        ""
}
