// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2016 Cookpad Inc.

package com.cookpad.android.plugin.license.extension

import java.io.File
import org.yaml.snakeyaml.Yaml

fun File.loadYaml(): List<LinkedHashMap<String, Any>> {
    val yaml = Yaml()
    val text = readText()
    if (text.isBlank()) {
        return emptyList()
    }
    val result: MutableList<LinkedHashMap<String, Any>> = yaml.load(text)
    return result.toList()
}
