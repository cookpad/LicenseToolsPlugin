// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2016 Cookpad Inc.

package com.cookpad.android.plugin.license.extension

import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ResolvedArtifact

fun Configuration.resolvedArtifacts(): Set<ResolvedArtifact> {
    val copyConfiguration = copyRecursive()
    copyConfiguration.isCanBeResolved = true
    return copyConfiguration.resolvedConfiguration.lenientConfiguration.artifacts
}
