package com.cookpad.android.plugin.license.task

import com.cookpad.android.plugin.license.LicenseToolsPluginExtension
import com.cookpad.android.plugin.license.data.LibraryInfo
import com.cookpad.android.plugin.license.extension.writeLicenseYaml
import com.cookpad.android.plugin.license.util.YamlUtils
import org.gradle.api.Project
import org.gradle.api.Task
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml

object UpdateLicenses {
    fun register(project: Project): Task {
        return project.task("updateLicenses").doLast {
            val ext = project.extensions.getByType(LicenseToolsPluginExtension::class.java)
            val yamlInfoList = YamlUtils.loadToLibraryInfo(project.file(ext.licensesYaml))
            // TODO: 足りない依存をマージする
            // TODO: 余分な依存を削除する
            val sortedYamlInfoList = yamlInfoList.sortedBy { it.artifactId.withWildcardVersion() }
            project.writeLicenseYaml(sortedYamlInfoList.toYaml())
        }
    }

    private fun List<LibraryInfo>.toYaml(): String {
        return this.map {
            mutableMapOf<String, Any>().apply {
                put("artifact", it.artifactId.withWildcardVersion())
                put("name", it.name ?: "#NAME#")
                put("copyrightHolder", it.copyrightHolder ?: "#COPYRIGHT_HOLDER#")
                if (it.copyrightHolders?.isNotEmpty() == true) {
                    put("copyrightHolders", it.copyrightHolders)
                }
                put("license", it.license ?: "#LICENSE#")
                if (it.fileName != null) {
                    put("filename", it.fileName)
                }
                if (it.author != null) {
                    put("author", it.author)
                }
                if (it.authors?.isNotEmpty() == true) {
                    put("authors", it.authors)
                }
                if (it.year != null) {
                    put("year", it.year)
                }
                if (it.url != null) {
                    put("url", it.url)
                }
                if (it.licenseUrl != null) {
                    put("licenseUrl", it.licenseUrl)
                }
                if (it.notice != null) {
                    put("notice", it.notice)
                }
                if (it.skip == true) {
                    put("skip", true)
                }
                if (it.forceGenerate == true) {
                    put("forceGenerate", true)
                }
            }
        }.toList().let {
            Yaml(DumperOptions().apply {
                defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
            }).dump(it)
        }
    }
}
