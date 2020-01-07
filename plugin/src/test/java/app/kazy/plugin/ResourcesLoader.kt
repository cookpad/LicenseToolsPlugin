package app.kazy.plugin

import app.kazy.plugin.data.LibraryInfo
import app.kazy.plugin.util.YamlUtils
import java.io.File

private class Irrelevant

private val classLoader: ClassLoader = Irrelevant::class.java.classLoader!!

fun loadYamlFromResources(path: String): List<LibraryInfo> =
    YamlUtils.loadToLibraryInfo(loadFileFromResources(path))

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
fun loadFileFromResources(path: String): File =
    File(requireNotNull(classLoader.getResource(path).file))

@Suppress("UNUSED")
private fun loadTextFromResources(path: String): String =
    requireNotNull(classLoader.getResource(path)) { "File not found in resources folder. path: $path" }
        .readText()
