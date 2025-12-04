package com.example.flashmind.presentation.utils

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun getSignatureSha1(context: Context): String? {
    try {
        val packageName = context.packageName
        val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.packageManager.getPackageInfo(
                packageName,
                PackageManager.PackageInfoFlags.of(PackageManager.GET_SIGNING_CERTIFICATES.toLong())
            )
        } else {
            @Suppress("DEPRECATION")
            context.packageManager.getPackageInfo(
                packageName,
                PackageManager.GET_SIGNATURES
            )
        }

        val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageInfo.signingInfo?.apkContentsSigners
        } else {
            @Suppress("DEPRECATION")
            packageInfo.signatures
        }

        val signature = signatures?.first()
        val md = MessageDigest.getInstance("SHA1")
        md.update(signature?.toByteArray())
        val digest = md.digest()
        return digest.joinToString(separator = ":") { "%02X".format(it) }
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}

fun Long.toFormattedDateString(): String {
    val date = Date(this)


    val pattern = "MM/dd/yyyy"


    val sdf = SimpleDateFormat(pattern, Locale.US)

    return sdf.format(date)
}

fun Long.formatTime(): String {
    val hours = this / 3600
    val minutes = (this % 3600) / 60
    val seconds = this % 60

    return if (hours > 0) {
        String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format(Locale.US, "%02d:%02d", minutes, seconds)
    }
}

fun cleanMarkdownForPreview(text: String): String {
    return text.lines() // Divide el texto en líneas
        .map { line ->
            // Elimina prefijos comunes de Markdown y espacios
            line.trim()
                .removePrefix("## ")
                .removePrefix("### ")
                .removePrefix("* ")
                .removePrefix("- ")
                .replace("**", "") // Quita negritas
        }
        .filter { it.isNotEmpty() } // Elimina líneas que quedaron vacías
        .take(3) // Toma solo las primeras 3 líneas para la vista previa
        .joinToString(" ") // Une las líneas con un espacio
        .trim() // Limpia espacios al inicio/final
}

