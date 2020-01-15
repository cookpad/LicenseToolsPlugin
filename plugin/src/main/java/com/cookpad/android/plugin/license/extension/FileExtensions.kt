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
