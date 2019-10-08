package app.kazy.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class LicenseToolsPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        println("Hello ${project.name}!")
    }
}