// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2016 Cookpad Inc.

package com.cookpad.android.plugin.license.task

import com.cookpad.android.plugin.license.LicenseToolsPluginExtension
import com.cookpad.android.plugin.license.data.LibraryInfo
import com.cookpad.android.plugin.license.extension.generateLibraryInfoText
import com.cookpad.android.plugin.license.extension.notListedIn
import com.cookpad.android.plugin.license.util.YamlUtils
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.internal.impldep.com.google.common.annotations.VisibleForTesting

object UpdateMissingLicenses {
    fun register(project: Project): Task {
        return project.task("updateMissingLicenses").doLast {
            val ext = project.extensions.getByType(LicenseToolsPluginExtension::class.java)
            val resolvedArtifacts =
                CheckLicenses.resolveProjectDependencies(project, ext.ignoredProjects)
            val dependencyLicenses =
                CheckLicenses.loadDependencyLicenses(project, resolvedArtifacts, ext.ignoredGroups)
            updateMissingLicensesYaml(project, ext.licensesYaml, dependencyLicenses)
        }
    }

    @VisibleForTesting
    internal fun updateMissingLicensesYaml(
        project: Project,
        licensesYaml: String,
        dependencyLicenses: List<LibraryInfo>
    ) {
        // Dedup and sort dependencies
        val sortedDependencies = dependencyLicenses.associateBy { it.artifactId.withWildcardVersion() }
            .toSortedMap { o1, o2 ->
                o1.compareTo(o2, ignoreCase = true)
            }.values.toList()
        val yamlList = YamlUtils.loadToLibraryInfo(project.file(licensesYaml))
        val notInDependencies = sortedDependencies.notListedIn(yamlList)


        project.file(licensesYaml).apply {
            // Clean content
            writeText("")
            for (libraryInfo in yamlList) {
                sortedDependencies.find { it.artifactId.matches(libraryInfo.artifactId) }?.let {
                    appendText("${libraryInfo.generateLibraryInfoText(it)}\n")
                }
            }
            for (libraryInfo in notInDependencies) {
                appendText("${libraryInfo.generateLibraryInfoText()}\n")
            }
        }
    }
}
