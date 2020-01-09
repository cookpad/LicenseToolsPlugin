package app.kazy.plugin.task

import app.kazy.plugin.LicenseToolsPluginExtension
import app.kazy.plugin.Templates
import app.kazy.plugin.data.LibraryInfo
import app.kazy.plugin.extension.writeLicenseHtml
import app.kazy.plugin.util.YamlUtils
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.internal.impldep.com.google.common.annotations.VisibleForTesting

object GenerateLicensePage {
    fun register(project: Project): Task {
        return project.task("generateLicensePage").doLast {
            val ext = project.extensions.getByType(LicenseToolsPluginExtension::class.java)
            val yamlInfoList = YamlUtils.loadToLibraryInfo(project.file(ext.licensesYaml))
            project.writeLicenseHtml(yamlInfoList.toHtml())
        }
    }

    @VisibleForTesting
    fun List<LibraryInfo>.toHtml(): String {
        val licenseHtml = StringBuffer()
        this.filterNot { it.skip ?: false }
            .forEach { licenseHtml.append(Templates.buildLicenseHtml(it)) }
        return Templates.wrapWithLayout(licenseHtml)
    }
}