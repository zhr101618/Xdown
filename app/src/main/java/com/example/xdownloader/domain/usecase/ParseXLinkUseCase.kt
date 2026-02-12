package com.example.xdownloader.domain.usecase

import javax.inject.Inject

/**
 * 解析X链接获取推文ID
 */
class ParseXLinkUseCase @Inject constructor() {

    operator fun invoke(url: String): String? {
        // 标准X链接: https://x.com/username/status/1234567890
        // 标准Twitter链接: https://twitter.com/username/status/1234567890
        // 短链接: https://x.com/i/status/1234567890

        val pattern = """(?:x\.com|twitter\.com)/[^/]+/status/(\d+)""".toRegex()
        val match = pattern.find(url)
        return match?.groupValues?.get(1)
    }

    /**
     * 验证是否为有效的X视频链接
     */
    fun isValidXLink(url: String): Boolean {
        return url.matches(Regex("""https?://(?:x\.com|twitter\.com)/[^/]+/status/\d+.*"""))
    }
}
