package app.kazy.plugin.task

import app.kazy.plugin.LicenseToolsPluginExtension
import app.kazy.plugin.Templates
import app.kazy.plugin.data.LibraryInfo
import app.kazy.plugin.extension.writeLicenseFile
import app.kazy.plugin.util.YamlUtils
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.internal.impldep.com.google.common.annotations.VisibleForTesting

object GenerateLicensePage {
    fun register(project: Project): Task {
        return project.task("generateLicensePage").doLast {
            val ext = project.extensions.getByType(LicenseToolsPluginExtension::class.java)
            // based on libraries.yml
            val yamlInfoList = YamlUtils.loadToLibraryInfo(project.file(ext.licensesYaml))
            project.writeLicenseFile(yamlInfoList.toHtml())
        }
    }

    @VisibleForTesting
    fun Set<LibraryInfo>.toHtml(): String {
        val licenseHtml = StringBuffer()
        this.filterNot { it.skip ?: false }
            .forEach { licenseHtml.append(Templates.buildLicenseHtml(it)) }
        return Templates.wrapWithLayout(licenseHtml)
    }
}