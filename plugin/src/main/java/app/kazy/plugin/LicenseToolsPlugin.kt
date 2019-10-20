package app.kazy.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.ResolvedArtifact
import org.simpleframework.xml.Serializer
import org.simpleframework.xml.core.Persister
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.lang.IllegalStateException

open class LicenseToolsPluginExtension {
    var outputHtml: String = "licenses.html"
}

class LicenseToolsPlugin : Plugin<Project> {
    val yaml = Yaml()
    override fun apply(project: Project) {
        // yamlから情報を引き出す
        // yaml情報からhtmlを生成する
        project.extensions.create("licenses", LicenseToolsPluginExtension::class.java)
        project.task("checkLicenses").doLast {
            val ext = project.extensions.getByType(LicenseToolsPluginExtension::class.java)

            val yamlData = loadYaml(project)
            val yamlInfoList = yamlToLibraryInfo(yamlData)
            val licenseHtml = StringBuffer()

            // generate HTML
            yamlInfoList
                .filter { !it.skip }
                .forEach { libraryInfo ->
                    licenseHtml.append(Templates.buildLicenseHtml(libraryInfo))
                    val assetsDir = project.file("src/main/assets")
                    if (!assetsDir.exists()) {
                        assetsDir.mkdirs()
                    }
                    project.logger.info("render ${assetsDir}/${ext.outputHtml}")
                    project.file("${assetsDir}/${ext.outputHtml}")
                        .writeText(Templates.wrapWithLayout(licenseHtml))
                }

//            val deps = resolveProjectDependencies(project, setOf(""))
//            val depsInfoList = depToLibraryInfo(project, deps)
        }
    }

    private fun yamlToLibraryInfo(
        yamlData: List<Map<String, String>>
    ): List<LibraryInfo> {
        return yamlData
            .map {
                LibraryInfo(
                    artifactId = ArtifactId.parse(it["artifact"]),
                    name = it["name"].toString(),
                    libraryName = it["name"].toString(),
                    url = it["url"].toString(),
                    fileName = it["name"].toString(),
                    license = it["license"].toString(),
                    licenseUrl = it["licenseUrl"].toString(),
                    copyrightHolder = it["copyrightHolder"].toString()
                )
            }
    }

    private fun loadYaml(project: Project): List<LinkedHashMap<String, String>> {
        val result: MutableList<LinkedHashMap<String, String>> =
            yaml.load(project.file("licenses.yml").readText())
        return result
    }

    private fun depToLibraryInfo(
        project: Project,
        deps: Set<ResolvedArtifact>
    ): List<LibraryInfo> {
        return deps
            .filter {
                it.moduleVersion.id.version != "undefined"
            }
            .filter {
                true //ignore-list
            }
            .map {
                val dependencyDesc =
                    "${it.moduleVersion.id.group}:${it.moduleVersion.id.name}:${it.moduleVersion.id.version}"

                val artifactId = ArtifactId.parse(dependencyDesc)

                val pomDependency = project.dependencies.create("$dependencyDesc@pom")
                val pomConfiguration = project.configurations.detachedConfiguration(pomDependency)

                pomConfiguration.resolve().forEach { file ->
                    project.logger.info("POM: $file")
                }

                val pStream: File?
                try {
                    pStream = pomConfiguration.resolve().first()
                } catch (e: Exception) {
                    project.logger.warn("Unable to retrieve license for $dependencyDesc")
                    throw IllegalStateException(e)
                }
                val persister: Serializer = Persister()
                val result = persister.read(LibraryPom::class.java, pStream)
                val licenseName = result.licenses.firstOrNull()?.name
                val licenseUrl = result.licenses.firstOrNull()?.url
                val libraryName = result.name
                val libraryUrl = result.url
                LibraryInfo(
                    artifactId = artifactId,
                    name = it.name,
                    libraryName = libraryName,
                    url = libraryUrl,
                    fileName = it.file.name,
                    license = licenseName.toString(),
                    licenseUrl = licenseUrl
                )
            }

    }

    private fun resolveProjectDependencies(
        project: Project,
        ignoredProjects: Set<String>
    ): Set<ResolvedArtifact> {
        val subProjects = project.rootProject.subprojects.filter {
            !ignoredProjects.contains(it.name)
        }
        val subProjectMap = subProjects.groupBy { "${it.group}:${it.name}:${it.version}" }

        val runtimeDependencies: MutableList<ResolvedArtifact> = ArrayList()

        subProjects.forEach { subProject ->
            val configs = subProject.configurations
                .filter {
                    val regex =
                        """^(?!releaseUnitTest)(?:release\w*)?([cC]ompile|[cC]ompileOnly|[iI]mplementation|[aA]pi)$""".toRegex()
                    it.name.matches(regex)
                }
                .map {
                    val copyConfiguration = it.copyRecursive()
                    copyConfiguration.isCanBeResolved = true
                    copyConfiguration.resolvedConfiguration.lenientConfiguration.artifacts
                }
                .flatten()
            runtimeDependencies.addAll(configs)
        }
        return runtimeDependencies.toSet()
    }
}

