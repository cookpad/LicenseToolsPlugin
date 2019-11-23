package app.kazy.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ResolvedArtifact
import org.gradle.internal.impldep.com.google.common.annotations.VisibleForTesting
import org.simpleframework.xml.Serializer
import org.simpleframework.xml.core.Persister
import org.yaml.snakeyaml.Yaml
import java.io.File

open class LicenseToolsPluginExtension {
    var outputHtml: String = "licenses.html"

    var outputJson: String = "licenses.json"

    var licensedYaml: String = "licenses.yml"

    var ignoredGroups = emptySet<String>()

    var ignoredProjects = emptySet<String>()
}

@VisibleForTesting
fun Set<LibraryInfo>.notListedIn(dependencySet: Set<LibraryInfo>): Set<LibraryInfo> {
    return this
        .filterNot {
            dependencySet.contains(it.artifactId)
        }
        .filterNot {
            it.skip ?: false
        }
        .filterNot {
            it.forceGenerate ?: false
        }
        .toSet()
}

@VisibleForTesting
fun Set<LibraryInfo>.contains(artifactId: ArtifactId): Boolean {
    this.forEach {
        if (it.artifactId.matches(artifactId)) {
            return true
        }
    }
    return false

}

@VisibleForTesting
fun ResolvedArtifact.toFormattedText(): String {
    return "${moduleVersion.id.group}:${moduleVersion.id.name}:${moduleVersion.id.version}"
}

@VisibleForTesting
fun Project.toFormattedText(): String {
    return "${group}:${name}:${version}"
}

class LicenseToolsPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.create("licenses", LicenseToolsPluginExtension::class.java)
        val ext = project.extensions.getByType(LicenseToolsPluginExtension::class.java)

        val checkLicenses = project.task("checkLicenses").doLast {
            // based on license plugin's dependency-license.xml
            val resolvedArtifacts = resolveProjectDependencies(project, ext.ignoredProjects)
            val dependencyLicenses =
                loadDependencyLicenses(project, resolvedArtifacts, ext.ignoredGroups)
            // based on libraries.yml
            val yamlData = loadYaml(project.file(ext.licensedYaml))
            val librariesYaml = yamlToLibraryInfo(yamlData)

            val notDocumented = dependencyLicenses.notListedIn(librariesYaml)
            val notInDependencies = librariesYaml.notListedIn(dependencyLicenses)
            //TODO: impl license not matched

            if (notDocumented.isEmpty() && notInDependencies.isEmpty()) {
                project.logger.info("checkLicenses: ok")
                return@doLast
            }
            //TODO: output error message
        }
        checkLicenses.group = "Verification"
        checkLicenses.description = "Check whether dependency licenses are listed in licenses.yml"


        project.task("generateLicensesPage").doLast {
            // based on libraries.yml
            val yamlData = loadYaml(project.file(ext.licensedYaml))
            val yamlInfoList = yamlToLibraryInfo(yamlData)
            generateHTML(project, yamlInfoList)
        }
    }

    private fun generateHTML(project: Project, yamlInfoList: Set<LibraryInfo>) {
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

    @VisibleForTesting
    fun yamlToLibraryInfo(
        yamlData: List<Map<String, Any>>
    ): Set<LibraryInfo> {
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
                    skip = it.getOrDefault("skip", "false").toString().toBoolean(),
                    forceGenerate = it.getOrDefault("forceGenerate", "false").toString().toBoolean()
                )
            }.toSet()
    }

    private fun loadYaml(
        file: File
    ): List<LinkedHashMap<String, Any>> {
        val yaml = Yaml()
        val result: MutableList<LinkedHashMap<String, Any>> = yaml.load(file.readText())
        return result.toList()
    }

    private fun loadDependencyLicenses(
        project: Project,
        resolvedArtifacts: Set<ResolvedArtifact>,
        ignoredGroups: Set<String>
    ): Set<LibraryInfo> {
        return resolvedArtifacts
            .filterNot { it.moduleVersion.id.version == "unspecified" }
            .filterNot { ignoredGroups.contains(it.moduleVersion.id.group) }
            .mapNotNull { resolvedArtifactToLibraryInfo(it, project) }
            .toSet()
    }

    @VisibleForTesting
    fun resolvedArtifactToLibraryInfo(artifact: ResolvedArtifact, project: Project): LibraryInfo? {
        val dependencyDesc =
            "${artifact.moduleVersion.id.group}:${artifact.moduleVersion.id.name}:${artifact.moduleVersion.id.version}"
        val artifactId: ArtifactId
        try {
            artifactId = ArtifactId.parse(dependencyDesc)
        } catch (e: IllegalArgumentException) {
            project.logger.info("Unsupport dependency: $dependencyDesc")
            return null
        }
        val pomDependency = project.dependencies.create("$dependencyDesc@pom")
        val pomConfiguration = project.configurations.detachedConfiguration(pomDependency)
        pomConfiguration.resolve().forEach { file ->
            project.logger.info("POM: $file")
        }
        val pomStream: File
        try {
            pomStream = pomConfiguration.resolve().toList().first()
        } catch (e: Exception) {
            project.logger.warn("Unable to retrieve license for $dependencyDesc")
            return null
        }
        val persister: Serializer = Persister()
        val result = persister.read(LibraryPom::class.java, pomStream)
        val licenseName = result.licenses.firstOrNull()?.name
        val licenseUrl = result.licenses.firstOrNull()?.url
        val libraryName = result.name
        val libraryUrl = result.url
        return LibraryInfo(
            artifactId = artifactId,
            name = artifact.name,
            libraryName = libraryName,
            url = libraryUrl,
            fileName = artifact.file.name,
            license = licenseName.toString(),
            licenseUrl = licenseUrl,
            copyrightHolder = null,
            notice = null
        )
    }

    private fun resolveProjectDependencies(
        project: Project?,
        ignoredProjects: Set<String> = emptySet()
    ): Set<ResolvedArtifact> {
        project ?: return emptySet()
        val subProjects = targetSubProjects(project, ignoredProjects)
        val subProjectIndex = subProjects.groupBy { it.toFormattedText() }
        return subProjects
            .map { it.configurations }
            .flatten()
            .filter { isConfigForDependencies(it.name) }
            .map { resolvedArtifacts(it) }
            .flatten()
            .distinctBy { it.toFormattedText() }
            .flatMap {
                val dependencyDesc = it.toFormattedText()
                val subProject = subProjectIndex[dependencyDesc]?.first()
                setOf(it, *resolveProjectDependencies(subProject).toTypedArray())
            }.toSet()
    }

    @VisibleForTesting
    fun targetSubProjects(project: Project, ignoredProjects: Set<String>): List<Project> {
        return project.rootProject.subprojects
            .filter { !ignoredProjects.contains(it.name) }
    }

    @VisibleForTesting
    fun isConfigForDependencies(name: String): Boolean {
        return name.matches(dependencyKeywordPattern)
    }

    private fun resolvedArtifacts(configuration: Configuration): Set<ResolvedArtifact> {
        val copyConfiguration = configuration.copyRecursive()
        copyConfiguration.isCanBeResolved = true
        return copyConfiguration.resolvedConfiguration.lenientConfiguration.artifacts
    }

    companion object {
        val dependencyKeywordPattern =
            """^(?!releaseUnitTest)(?:release\w*)?([cC]ompile|[cC]ompileOnly|[iI]mplementation|[aA]pi)$""".toRegex()
    }
}

