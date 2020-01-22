// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2016 Cookpad Inc.

package com.cookpad.android.plugin.license

import com.cookpad.android.plugin.license.task.CheckLicenses
import com.cookpad.android.plugin.license.task.GenerateLicenseJson
import com.cookpad.android.plugin.license.task.GenerateLicensePage
import com.cookpad.android.plugin.license.task.UpdateLicenses
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
