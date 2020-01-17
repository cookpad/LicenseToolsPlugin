package com.cookpad.android.plugin.license.task

import com.cookpad.android.plugin.license.LicenseToolsPluginExtension
import com.cookpad.android.plugin.license.data.LibraryInfo
import com.cookpad.android.plugin.license.extension.writeLicenseJson
import com.cookpad.android.plugin.license.util.YamlUtils
import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.internal.impldep.com.google.common.annotations.VisibleForTesting

object GenerateLicenseJson {
    fun register(project: Project): Task {
        return project.task("generateLicenseJson").doLast {
            val ext = project.extensions.getByType(LicenseToolsPluginExtension::class.java)
            val yamlInfoList = YamlUtils.loadToLibraryInfo(project.file(ext.licensesYaml))
            project.writeLicenseJson(yamlInfoList.toJson())
        }
    }

    @VisibleForTesting
    fun List<LibraryInfo>.toJson(): String {
        val adapter: JsonAdapter<LibraryInfoWrapper> =
            Moshi.Builder().build().adapter(LibraryInfoWrapper::class.java)
        return this
            .filterNot { it.skip ?: false }
            .map {
                LibraryInfoWrapper.LibraryInfoJson(
                    artifactId = LibraryInfoWrapper.LibraryInfoJson.ArtifactId(
                        name = it.artifactId.name,
                        group = it.artifactId.group,
                        version = it.artifactId.version
                    ),
                    notice = it.notice,
                    copyrightHolder = it.copyrightHolder,
                    copyrightStatement = it.getCopyrightStatement(),
                    license = it.license,
                    licenseUrl = it.licenseUrl,
                    normalizedLicense = it.normalizedLicense(),
                    year = it.year,
                    url = it.url,
                    libraryName = it.libraryName
                )
            }
            .toList()
            .let { adapter.toJson(LibraryInfoWrapper(libraries = it)) }
    }

    @JsonClass(generateAdapter = true)
    data class LibraryInfoWrapper(
        @Json(name = "libraries")
        val libraries: List<LibraryInfoJson>
    ) {
        @JsonClass(generateAdapter = true)
        data class LibraryInfoJson(
            @Json(name = "artifactId")
            val artifactId: ArtifactId,
            @Json(name = "notice")
            val notice: String?,
            @Json(name = "copyrightHolder")
            val copyrightHolder: String?,
            @Json(name = "copyrightStatement")
            val copyrightStatement: String?,
            @Json(name = "license")
            val license: String?,
            @Json(name = "licenseUrl")
            val licenseUrl: String?,
            @Json(name = "normalizedLicense")
            val normalizedLicense: String?,
            @Json(name = "year")
            val year: String?,
            @Json(name = "url")
            val url: String?,
            @Json(name = "libraryName")
            val libraryName: String?
        ) {
            @JsonClass(generateAdapter = true)
            data class ArtifactId(
                @Json(name = "name")
                val name: String,
                @Json(name = "group")
                val group: String,
                @Json(name = "version")
                val version: String
            )
        }
    }

}
