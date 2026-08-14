package com.example.data.model

enum class AppType(val displayName: String) {
    LEANBACK("TV App"),
    SIDELOADED("Sideloaded"),
    SYSTEM("System")
}

data class StorageInfo(
    val totalBytes: Long,
    val freeBytes: Long,
    val usedBytes: Long,
    val usedPercentage: Float
) {
    fun formatFreeGb(): String {
        val gb = freeBytes.toDouble() / (1024 * 1024 * 1024)
        return String.format("%.1f GB free", gb)
    }

    fun formatTotalGb(): String {
        val gb = totalBytes.toDouble() / (1024 * 1024 * 1024)
        return String.format("%.1f GB total", gb)
    }
}
