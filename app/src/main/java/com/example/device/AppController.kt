package com.example.device

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings

data class AppInfo(
    val label: String,
    val packageName: String
)

class AppController(private val context: Context) {

    private val packageManager: PackageManager = context.packageManager

    // Known popular package mappings & aliases
    private val appAliases = mapOf(
        "simpmusic" to listOf("com.maxrave.simpmusic"),
        "music" to listOf("com.maxrave.simpmusic", "com.google.android.apps.youtube.music", "com.spotify.music"),
        "youtube" to listOf("com.google.android.youtube", "org.schabi.newpipe"),
        "videos" to listOf("com.google.android.youtube"),
        "chrome" to listOf("com.android.chrome"),
        "browser" to listOf("com.android.chrome", "org.mozilla.firefox"),
        "settings" to listOf("com.android.settings")
    )

    fun openApp(appNameOrAlias: String, preferredMusicPackage: String = "com.maxrave.simpmusic"): AppLaunchResult {
        val query = appNameOrAlias.lowercase().trim()

        if (query == "settings") {
            return try {
                val intent = Intent(Settings.ACTION_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
                AppLaunchResult.Success("Opening Settings.")
            } catch (e: Exception) {
                AppLaunchResult.Failure("Could not open Settings.")
            }
        }

        // Special handling for music / SimpMusic
        if (query.contains("simpmusic") || query == "music") {
            // Check preferred music package first
            val launchIntent = packageManager.getLaunchIntentForPackage(preferredMusicPackage)
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(launchIntent)
                return AppLaunchResult.Success("Opening SimpMusic.")
            }
        }

        // Check configured aliases
        val potentialPackages = appAliases[query] ?: emptyList()
        for (pkg in potentialPackages) {
            val intent = packageManager.getLaunchIntentForPackage(pkg)
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                val label = getAppLabel(pkg) ?: appNameOrAlias
                return AppLaunchResult.Success("Opening $label.")
            }
        }

        // If not found in known aliases, search installed launcher apps
        val installedApps = getInstalledApps()
        val match = installedApps.firstOrNull {
            it.label.lowercase().contains(query) || it.packageName.lowercase().contains(query)
        }

        if (match != null) {
            val intent = packageManager.getLaunchIntentForPackage(match.packageName)
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                return AppLaunchResult.Success("Opening ${match.label}.")
            }
        }

        val appDisplay = if (query.contains("simpmusic")) "SimpMusic" else appNameOrAlias.replaceFirstChar { it.uppercase() }
        return AppLaunchResult.Failure("$appDisplay isn't installed.")
    }

    fun isAppInstalled(packageName: String): Boolean {
        return try {
            packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    private fun getAppLabel(packageName: String): String? {
        return try {
            val appInfo = packageManager.getApplicationInfo(packageName, 0)
            packageManager.getApplicationLabel(appInfo).toString()
        } catch (e: Exception) {
            null
        }
    }

    fun getInstalledApps(): List<AppInfo> {
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val resolveInfos = packageManager.queryIntentActivities(intent, 0)
        return resolveInfos.mapNotNull { resolveInfo ->
            val pkg = resolveInfo.activityInfo.packageName
            val label = resolveInfo.loadLabel(packageManager).toString()
            if (pkg != context.packageName) {
                AppInfo(label = label, packageName = pkg)
            } else null
        }.sortedBy { it.label }
    }
}

sealed class AppLaunchResult {
    data class Success(val spokenFeedback: String) : AppLaunchResult()
    data class Failure(val spokenFeedback: String) : AppLaunchResult()
}
