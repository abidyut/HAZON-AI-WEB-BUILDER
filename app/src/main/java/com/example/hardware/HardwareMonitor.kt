package com.example.hardware

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.StatFs
import java.io.File

data class HardwareStats(
    val totalRamMb: Long,
    val availableRamMb: Long,
    val appUsedRamMb: Long,
    val cpuCores: Int,
    val totalStorageGb: Long,
    val availableStorageGb: Long,
    val gpuHardwareAccelerated: Boolean = true,
    val deviceModel: String
)

object HardwareMonitor {
    fun getHardwareStats(context: Context): HardwareStats {
        // RAM Metrics
        val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        actManager?.getMemoryInfo(memInfo)

        val totalRamMb = memInfo.totalMem / (1024 * 1024)
        val availRamMb = memInfo.availMem / (1024 * 1024)

        val runtime = Runtime.getRuntime()
        val appUsedRamMb = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)

        // CPU Metrics
        val cpuCores = runtime.availableProcessors()

        // ROM / Internal Storage Metrics
        val dataDir: File = Environment.getDataDirectory()
        val stat = StatFs(dataDir.path)
        val blockSize = stat.blockSizeLong
        val totalBlocks = stat.blockCountLong
        val availBlocks = stat.availableBlocksLong

        val totalStorageGb = (totalBlocks * blockSize) / (1024 * 1024 * 1024)
        val availStorageGb = (availBlocks * blockSize) / (1024 * 1024 * 1024)

        val deviceModel = "${Build.MANUFACTURER.replaceFirstChar { it.uppercase() }} ${Build.MODEL}"

        return HardwareStats(
            totalRamMb = totalRamMb,
            availableRamMb = availRamMb,
            appUsedRamMb = appUsedRamMb,
            cpuCores = cpuCores,
            totalStorageGb = totalStorageGb,
            availableStorageGb = availStorageGb,
            gpuHardwareAccelerated = true,
            deviceModel = deviceModel
        )
    }
}
