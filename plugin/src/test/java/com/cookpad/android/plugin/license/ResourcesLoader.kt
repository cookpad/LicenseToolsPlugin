package com.cookpad.android.plugin.license

import com.cookpad.android.plugin.license.data.LibraryInfo
import com.cookpad.android.plugin.license.util.YamlUtils
import java.io.File

private class Irrelevant

private val classLoader: ClassLoader = Irrelevant::class.java.classLoader!!

fun loadYamlFromResources(path: String): List<LibraryInfo> =
    YamlUtils.loadToLibraryInfo(
        loadFileFromResources(
            path
        )
    )

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
fun loadFileFromResources(path: String): File =
    File(requireNotNull(classLoader.getResource(path).file))

@Suppress("UNUSED")
private fun loadTextFromResources(path: String): String =
    requireNotNull(classLoader.getResource(path)) { "File not found in resources folder. path: $path" }
        .readText()
