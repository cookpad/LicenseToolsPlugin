// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2016 Cookpad Inc.

package com.cookpad.android.plugin.license.extension

import com.cookpad.android.plugin.license.data.ArtifactId
import com.cookpad.android.plugin.license.data.LibraryInfo

fun List<LibraryInfo>.duplicatedArtifacts(): List<String> {
    return this
        .groupingBy { it.artifactId.withWildcardVersion() }
        .eachCount()
        .filter { it.value > 1 }
        .map { it.key }
}

fun List<LibraryInfo>.notListedIn(dependencySet: List<LibraryInfo>): List<LibraryInfo> {
    return this
        .filterNot { dependencySet.contains(it.artifactId) }
        .filterNot { it.skip ?: false }
        .filterNot { it.forceGenerate ?: false }
}

fun List<LibraryInfo>.licensesUnMatched(librariesYaml: List<LibraryInfo>): List<LibraryInfo> {
    return librariesYaml
        .filterNot { it.skip ?: false }
        .filterNot { it.forceGenerate ?: false }
        .filter { it.license?.isNotBlank() ?: false }
        .filter { this.checkUnMatchedLicense(it) }
}

private fun List<LibraryInfo>.checkUnMatchedLicense(libraryInfo: LibraryInfo): Boolean {
    return this
        .filter { it.artifactId.matches(libraryInfo.artifactId) }
        .filterNot { it.skip ?: false }
        .filterNot { it.forceGenerate ?: false }
        .filter { it.license?.isNotBlank() ?: false }
        .filterNot {
            it.normalizedLicense().equals(libraryInfo.normalizedLicense(), ignoreCase = true)
        }
        .isNotEmpty()
}

private fun List<LibraryInfo>.contains(artifactId: ArtifactId): Boolean {
    this.forEach {
        if (it.artifactId.matches(artifactId)) {
            return true
        }
    }
    return false
}
