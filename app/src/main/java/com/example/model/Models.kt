package com.example.model

data class TokenItem(
    val id: String,
    val label: String,
    val url: String,
    val isCustom: Boolean = false,
    val addedTimestamp: Long = System.currentTimeMillis()
) {
    val shortToken: String
        get() {
            val tokenRegex = Regex("e_token=([^&]+)")
            val match = tokenRegex.find(url)
            val fullToken = match?.groupValues?.get(1) ?: ""
            return if (fullToken.length > 12) {
                "${fullToken.take(6)}...${fullToken.takeLast(4)}"
            } else if (fullToken.isNotEmpty()) {
                fullToken
            } else {
                "No e_token"
            }
        }
}

data class UserAgentItem(
    val id: String,
    val name: String,
    val userAgent: String,
    val description: String,
    val iconType: String = "mobile" // "mobile", "fb", "ios", "firefox", "desktop", "custom"
)
