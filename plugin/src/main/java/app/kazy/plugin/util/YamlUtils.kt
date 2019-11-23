package app.kazy.plugin.util

import app.kazy.plugin.data.ArtifactId
import app.kazy.plugin.data.LibraryInfo
import app.kazy.plugin.extension.loadYaml
import java.io.File

object YamlUtils {
    fun loadToLibraryInfo(file: File): Set<LibraryInfo> {
        return file.loadYaml()
            .filterNot {
                it.getOrDefault("skip", "false").toString().toBoolean()
            }
            .map {
                LibraryInfo(
                    artifactId = ArtifactId.parse(it["artifact"] as String?),
                    name = it["name"] as String,
                    libraryName = it["name"] as String,
                    fileName = it["name"] as String,
                    license = it["license"] as String,
                    copyrightHolder = it["copyrightHolder"] as String?,
                    notice = it["notice"] as String?,
                    url = it.getOrDefault("url", "") as String,
                    licenseUrl = it["licenseUrl"] as String?,
                    skip = it.getOrDefault("skip", "false").toString().toBoolean(),
                    forceGenerate = it.getOrDefault(
                        "forceGenerate",
                        "false"
                    ).toString().toBoolean()
                )
            }.toSet()
    }
}