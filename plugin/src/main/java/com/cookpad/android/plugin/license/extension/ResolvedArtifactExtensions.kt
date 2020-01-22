// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2016 Cookpad Inc.

package com.cookpad.android.plugin.license.extension

import org.gradle.api.artifacts.ResolvedArtifact
import org.gradle.internal.impldep.com.google.common.annotations.VisibleForTesting

@VisibleForTesting
fun ResolvedArtifact.toFormattedText(): String {
    return "${moduleVersion.id.group}:${moduleVersion.id.name}:${moduleVersion.id.version}"
}
