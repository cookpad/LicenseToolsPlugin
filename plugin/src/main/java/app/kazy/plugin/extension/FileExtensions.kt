package app.kazy.plugin.extension

import java.io.File
import org.yaml.snakeyaml.Yaml

fun File.loadYaml(): List<LinkedHashMap<String, Any>> {
    val yaml = Yaml()
    val result: MutableList<LinkedHashMap<String, Any>> = yaml.load(readText())
    return result.toList()
}
