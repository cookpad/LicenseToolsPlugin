package app.kazy.plugin

import app.kazy.plugin.task.CheckLicenses
import app.kazy.plugin.task.GenerateLicensesPage
import org.gradle.api.Plugin
import org.gradle.api.Project

open class LicenseToolsPluginExtension {
    var outputHtml: String = "licenses.html"

    var outputJson: String = "licenses.json"

    var licensedYaml: String = "licenses.yml"

    var ignoredGroups = emptySet<String>()

    var ignoredProjects = emptySet<String>()
}

class LicenseToolsPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.create("licenses", LicenseToolsPluginExtension::class.java)
        CheckLicenses.create(project)
        GenerateLicensesPage.create(project)
    }
}
