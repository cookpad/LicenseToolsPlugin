package app.kazy.plugin

import app.kazy.plugin.task.CheckLicenses
import app.kazy.plugin.task.GenerateLicenseJson
import app.kazy.plugin.task.GenerateLicensePage
import app.kazy.plugin.task.UpdateLicenses
import org.gradle.api.Plugin
import org.gradle.api.Project

class LicenseToolsPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.create(
            LicenseToolsPluginExtension.NAME,
            LicenseToolsPluginExtension::class.java
        )
        CheckLicenses.register(project)
        UpdateLicenses.register(project)
        GenerateLicensePage.register(project)
        GenerateLicenseJson.register(project)
    }
}
