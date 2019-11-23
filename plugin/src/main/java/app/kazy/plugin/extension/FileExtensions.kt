package app.kazy.plugin.extension

import org.yaml.snakeyaml.Yaml
import java.io.File

fun File.loadYaml(): List<LinkedHashMap<String, Any>> {
    val yaml = Yaml()
    val result: MutableList<LinkedHashMap<String, Any>> = yaml.load(readText())
    return result.toList()
}