// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2016 Cookpad Inc.

package com.cookpad.android.plugin.license.task

import com.cookpad.android.plugin.license.LicenseToolsPluginExtension
import com.cookpad.android.plugin.license.Templates
import com.cookpad.android.plugin.license.data.LibraryInfo
import com.cookpad.android.plugin.license.exception.NotEnoughInformationException
import com.cookpad.android.plugin.license.extension.writeLicenseHtml
import com.cookpad.android.plugin.license.util.YamlUtils
import org.gradle.api.Action
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.internal.impldep.com.google.common.annotations.VisibleForTesting

object GenerateLicensePage {
    fun register(project: Project): Task {
        return project.task("generateLicensePage").doLast(GenerateLicensePageAction())
    }

    // can't use lambdas to define the action if you want to allow this to be used as a cacheable task
    class GenerateLicensePageAction : Action<Task> {
        override fun execute(task: Task) {
            val ext = task.project.extensions.getByType(LicenseToolsPluginExtension::class.java)
            val yamlInfoList = YamlUtils.loadToLibraryInfo(task.project.file(ext.licensesYaml))
            task.project.writeLicenseHtml(yamlInfoList.toHtml(task.project))
        }
    }

    @VisibleForTesting
    fun List<LibraryInfo>.toHtml(project: Project): String {
        val licenseHtml = StringBuffer()
        var hasError = false
        this.filterNot { it.skip ?: false }
            .forEach {
                try {
                    licenseHtml.append(Templates.buildLicenseHtml(it))
                } catch (exception: NotEnoughInformationException) {
                    // Print all libraries aren't enough information for develops to fix them.
                    hasError = true
                    project.logger.warn(exception.message)
                }
            }
        if (hasError) {
            throw GradleException("generateLicensePage: more than one library isn't enough information")
        }

        return Templates.wrapWithLayout(licenseHtml)
    }
}
