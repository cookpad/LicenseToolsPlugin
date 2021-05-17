// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2016 Cookpad Inc.

package com.cookpad.android.plugin.license.task

import com.cookpad.android.plugin.license.LicenseToolsPluginExtension
import com.cookpad.android.plugin.license.data.LibraryInfo
import com.cookpad.android.plugin.license.extension.generateLibraryInfoText
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.internal.impldep.com.google.common.annotations.VisibleForTesting

object UpdateLicenses {
    fun register(project: Project): Task {
        return project.task("updateLicenses").doLast {
            val ext = project.extensions.getByType(LicenseToolsPluginExtension::class.java)
            val resolvedArtifacts =
                CheckLicenses.resolveProjectDependencies(project, ext.ignoredProjects)
            val dependencyLicenses =
                CheckLicenses.loadDependencyLicenses(project, resolvedArtifacts, ext.ignoredGroups)
            updateLicensesYaml(project, ext.licensesYaml, dependencyLicenses)
        }
    }

    @VisibleForTesting
    internal fun updateLicensesYaml(
        project: Project,
        licensesYaml: String,
        dependencyLicenses: List<LibraryInfo>
    ) {
        // Dedup and sort dependencies
        val sortedDependencies = dependencyLicenses.associateBy { it.artifactId.withWildcardVersion() }
            .toSortedMap { o1, o2 ->
                o1.compareTo(o2, ignoreCase = true)
            }.values

        project.file(licensesYaml).apply {
            // Clean content
            writeText("")
            for (libraryInfo in sortedDependencies) {
                appendText("${libraryInfo.generateLibraryInfoText()}\n")
            }
        }
    }
}
