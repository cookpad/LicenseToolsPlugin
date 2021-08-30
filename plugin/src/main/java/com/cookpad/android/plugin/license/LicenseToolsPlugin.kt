// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2016 Cookpad Inc.

package com.cookpad.android.plugin.license

import com.cookpad.android.plugin.license.task.*
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
        UpdateMissingLicenses.register(project)
        GenerateLicensePage.register(project)
        GenerateLicenseJson.register(project)
    }
}
