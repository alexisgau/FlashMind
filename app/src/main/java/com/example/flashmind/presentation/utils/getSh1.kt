package com.example.flashmind.presentation.utils

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import java.security.MessageDigest

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