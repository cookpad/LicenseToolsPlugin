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
    override fun apply(project: Project) {
        project.extensions.create("licenses", LicenseToolsPluginExtension::class.java)
        project.task("checkLicenses").doLast {
            val deps = resolveProjectDependencies(project)
            val depsInfoList = depToLibraryInfo(project, deps)
            println(depsInfoList.joinToString("\n"))
        }

        project.task("generateLicensesPage").doLast {
            val yamlData = loadYaml(project)
            val yamlInfoList = yamlToLibraryInfo(yamlData)
            generateHTML(project, yamlInfoList)
        }
    }

    private fun generateHTML(project: Project, yamlInfoList: List<LibraryInfo>) {
        val licenseHtml = StringBuffer()
        val ext = project.extensions.getByType(LicenseToolsPluginExtension::class.java)
        yamlInfoList
            .filterNot { it.skip ?: false }
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
    }

    private fun yamlToLibraryInfo(
        yamlData: List<Map<String, Any>>
    ): List<LibraryInfo> {
        return yamlData
            .filterNot {
                it.getOrDefault("skip", "false").toString().toBoolean()
            }
            .map {
                LibraryInfo(
                    artifactId = ArtifactId.parse(it["artifact"] as String?),
                    name = it["name"] as String,
                    libraryName = it["name"] as String,
                    fileName = it["name"] as String,
                    license = it["license"] as String,
                    copyrightHolder = it["copyrightHolder"] as String?,
                    notice = it["notice"] as String?,
                    url = it.getOrDefault("url", "") as String,
                    licenseUrl = it["licenseUrl"] as String?,
                    skip = it.getOrDefault("skip", "false").toString().toBoolean()
                )
            }
    }

    private fun loadYaml(project: Project): List<LinkedHashMap<String, Any>> {
        val yaml = Yaml()
        val result: MutableList<LinkedHashMap<String, Any>> =
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
                    licenseUrl = licenseUrl,
                    copyrightHolder = null,
                    notice = null,
                    skip = false
                )
            }
    }

    private fun resolveProjectDependencies(
        project: Project,
        ignoredProjects: Set<String> = emptySet()
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


        val seen = HashSet<String>()
        val dependenciesToHandle = HashSet<ResolvedArtifact>()

        runtimeDependencies.forEach { d ->
            val dependencyDesc =
                "$d.moduleVersion.id.group:$d.moduleVersion.id.name:$d.moduleVersion.id.version"
            if (!seen.contains(dependencyDesc)) {
                dependenciesToHandle.add(d)
                val subProject = subProjectMap[dependencyDesc]?.first()
                if (subProject != null) {
                    dependenciesToHandle.addAll(resolveProjectDependencies(subProject))
                }
            }
        }
        return dependenciesToHandle
    }
}

