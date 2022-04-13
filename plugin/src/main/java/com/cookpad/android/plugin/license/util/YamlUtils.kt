// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2016 Cookpad Inc.

package com.cookpad.android.plugin.license.util

import com.cookpad.android.plugin.license.data.ArtifactId
import com.cookpad.android.plugin.license.data.LibraryInfo
import com.cookpad.android.plugin.license.extension.loadYaml
import java.io.File

object YamlUtils {
    fun loadToLibraryInfo(file: File): List<LibraryInfo> {
        if (!file.exists()) {
            return emptyList()
        }
        return file.loadYaml()
            .map {
                LibraryInfo(
                    artifactId = ArtifactId.parse(
                        it["artifact"] as String?
                    ),
                    name = it["name"] as String?,
                    libraryName = it["name"] as String?,
                    license = it["license"] as String?,
                    year = (it["year"] as Int?)?.toString(),
                    copyrightHolder = makeCopyRightHolder(
                        it
                    ),
                    notice = it["notice"] as String?,
                    url = it["url"] as String?,
                    licenseUrl = it["licenseUrl"] as String?,
                    skip = it.getOrDefault("skip", "false").toString().toBoolean(),
                    forceGenerate = it.getOrDefault(
                        "forceGenerate",
                        "false"
                    ).toString().toBoolean(),
                    customLicenseName = it["customLicenseName"] as String?,
                    customLicenseContent = it["customLicenseContent"] as String?
                )
            }
    }

    private fun makeCopyRightHolder(map: Map<String, Any>): String? {
        return when {
            map["copyrightHolder"] != null -> {
                map["copyrightHolder"] as String
            }
            map["copyrightHolders"] != null -> {
                @Suppress("UNCHECKED_CAST")
                (joinWords(map["copyrightHolders"] as List<String>))
            }
            map["authors"] != null -> {
                @Suppress("UNCHECKED_CAST")
                (joinWords(map["authors"] as List<String>))
            }
            map["author"] != null -> {
                map["author"] as String
            }
            else -> null
        }
    }

    private fun joinWords(words: List<String>): String {
        return when (words.size) {
            0 -> ""
            1 -> words.first()
            2 -> "${words.first()} and ${words.last()}"
            else -> {
                val last: String = words.last()
                "${words.subList(0, words.size - 1).joinToString(",")}, and $last"
            }
        }
    }
}
