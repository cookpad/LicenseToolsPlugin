package com.cookpad.android.plugin.license.task

import com.cookpad.android.plugin.license.LicenseToolsPluginExtension
import com.cookpad.android.plugin.license.data.LibraryInfo
import com.cookpad.android.plugin.license.extension.notListedIn
import com.cookpad.android.plugin.license.extension.writeLicenseYaml
import com.cookpad.android.plugin.license.util.YamlUtils
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.internal.impldep.com.google.common.annotations.VisibleForTesting
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import java.nio.charset.Charset

object UpdateLicenses {
    fun register(project: Project): Task {
        return project.task("updateLicenses").doLast {
            val ext = project.extensions.getByType(LicenseToolsPluginExtension::class.java)
            val resolvedArtifacts =
                CheckLicenses.resolveProjectDependencies(project, ext.ignoredProjects)
            val dependencyLicenses =
                CheckLicenses.loadDependencyLicenses(project, resolvedArtifacts, ext.ignoredGroups)
            val librariesYaml = YamlUtils.loadToLibraryInfo(project.file(ext.licensesYaml))
            val notDocumented = dependencyLicenses.notListedIn(librariesYaml)

            notDocumented.forEach {
                val text = generateLibraryInfoText(it)
                project.file(ext.licensesYaml).appendText("${text}\n")
            }
        }
    }

    @VisibleForTesting
    fun generateLibraryInfoText(libraryInfo: LibraryInfo): String {
        val text = StringBuffer()
        text.append("- artifact: ${libraryInfo.artifactId.withWildcardVersion()}\n")
        text.append("  name: ${libraryInfo.name ?: "#NAME#"}\n")
        text.append("  copyrightHolder: ${libraryInfo.copyrightHolder ?: "#COPYRIGHT_HOLDER#"}\n")
        text.append("  license: ${libraryInfo.license ?: "#LICENSE#"}\n")
        if (libraryInfo.licenseUrl?.isNotBlank() == true) {
            text.append("  licenseUrl: ${libraryInfo.licenseUrl}\n")
        }
        if (libraryInfo.url?.isNotBlank() == true) {
            text.append("  url: ${libraryInfo.url}\n")
        }
        return text.toString().trim()
    }

}
