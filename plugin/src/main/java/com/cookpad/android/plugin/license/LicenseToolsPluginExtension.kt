// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2016 Cookpad Inc.

package com.cookpad.android.plugin.license

open class LicenseToolsPluginExtension {
    var outputHtml: String = "licenses.html"

    var outputJson: String = "licenses.json"

    var licensesYaml: String = "licenses.yml"

    var ignoredGroups = emptySet<String>()

    var ignoredProjects = emptySet<String>()

    var customStyles = ""

    companion object {
        const val NAME = "licenseTools"
    }
}
