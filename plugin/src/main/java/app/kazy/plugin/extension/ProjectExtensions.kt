package app.kazy.plugin.extension

import app.kazy.plugin.LicenseToolsPluginExtension
import org.gradle.api.Project

fun Project.toFormattedText(): String {
    return "$group:$name:$version"
}

fun Project.writeLicenseHtml(html: String) {
    val ext = extensions.getByType(LicenseToolsPluginExtension::class.java)
    val assetsDir = file("src/main/assets")
    assetsDir.mkdirs()
    logger.info("render $assetsDir/${ext.outputHtml}")
    file("$assetsDir/${ext.outputHtml}").writeText(html)
}

fun Project.writeLicenseYaml(yaml: String) {
    val ext = extensions.getByType(LicenseToolsPluginExtension::class.java)
    logger.info("render ./${ext.licensesYaml}")
    file("./${ext.licensesYaml}").writeText(yaml)
}
