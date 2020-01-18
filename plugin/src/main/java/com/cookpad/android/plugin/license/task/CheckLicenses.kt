package com.cookpad.android.plugin.license.task

import com.cookpad.android.plugin.license.LicenseToolsPluginExtension
import com.cookpad.android.plugin.license.data.ArtifactId
import com.cookpad.android.plugin.license.data.LibraryInfo
import com.cookpad.android.plugin.license.data.LibraryPom
import com.cookpad.android.plugin.license.extension.duplicatedArtifacts
import com.cookpad.android.plugin.license.extension.licensesUnMatched
import com.cookpad.android.plugin.license.extension.notListedIn
import com.cookpad.android.plugin.license.extension.resolvedArtifacts
import com.cookpad.android.plugin.license.extension.toFormattedText
import com.cookpad.android.plugin.license.util.YamlUtils
import java.io.File
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.ResolvedArtifact
import org.gradle.internal.impldep.com.google.common.annotations.VisibleForTesting
import org.simpleframework.xml.Serializer
import org.simpleframework.xml.core.Persister

object CheckLicenses {
    fun register(project: Project): Task {
        return project.task("checkLicenses").doLast {
            val ext = project.extensions.getByType(LicenseToolsPluginExtension::class.java)
            // based on license plugin's dependency-license.xml
            val resolvedArtifacts =
                resolveProjectDependencies(
                    project,
                    ext.ignoredProjects
                )
            val dependencyLicenses =
                loadDependencyLicenses(
                    project,
                    resolvedArtifacts,
                    ext.ignoredGroups
                )
            // based on libraries.yml
            val librariesYaml = YamlUtils.loadToLibraryInfo(project.file(ext.licensesYaml))

            val notDocumented = dependencyLicenses.notListedIn(librariesYaml)
            val notInDependencies = librariesYaml.notListedIn(dependencyLicenses)
            val licensesUnMatched = dependencyLicenses.licensesUnMatched(librariesYaml)
            val duplicatedArtifactIds = librariesYaml.duplicatedArtifacts()

            if (
                notDocumented.isEmpty() &&
                notInDependencies.isEmpty() &&
                licensesUnMatched.isEmpty() &&
                duplicatedArtifactIds.isEmpty()
            ) {
                project.logger.info("checkLicenses: ok")
                return@doLast
            }

            if (notDocumented.isNotEmpty()) {
                project.logger.warn("# Libraries not listed in ${ext.licensesYaml}:")
                notDocumented
                    .distinctBy { it.artifactId.withWildcardVersion() }
                    .sortedBy { it.artifactId.withWildcardVersion() }
                    .forEach { libraryInfo ->
                        val text =
                            generateLibraryInfoText(
                                libraryInfo
                            )
                        project.logger.warn(text)
                    }
            }

            if (notInDependencies.isNotEmpty()) {
                project.logger.warn("# Libraries listed in ${ext.licensesYaml} but not in dependencies:")
                notInDependencies
                    .sortedBy { it.artifactId.withWildcardVersion() }
                    .forEach { libraryInfo ->
                        project.logger.warn("- artifact: ${libraryInfo.artifactId}\n")
                    }
            }
            if (licensesUnMatched.isNotEmpty()) {
                project.logger.warn("# Licenses not matched with pom.xml in dependencies:")
                licensesUnMatched
                    .sortedBy { it.artifactId.withWildcardVersion() }
                    .forEach { libraryInfo ->
                        project.logger.warn("- artifact: ${libraryInfo.artifactId}\n  license: ${libraryInfo.license}")
                    }
            }
            if (duplicatedArtifactIds.isNotEmpty()) {
                project.logger.warn("# Libraries is duplicated listed in ${ext.licensesYaml}:")
                duplicatedArtifactIds
                    .sorted()
                    .forEach { artifactId ->
                        project.logger.warn("- artifact: $artifactId\n")
                    }
            }
            throw GradleException("checkLicenses: missing libraries in ${ext.licensesYaml}")
        }.also {
            it.group = "Verification"
            it.description = "Check whether dependency licenses are listed in licenses.yml"
        }
    }

    @VisibleForTesting
    fun generateLibraryInfoText(libraryInfo: LibraryInfo): String {
        val text = StringBuffer()
        text.append("- artifact: ${libraryInfo.artifactId.withWildcardVersion()}\n")
        text.append("  name: ${libraryInfo.name ?: "#NAME#"}\n")
        text.append("  copyrightHolder: ${libraryInfo.copyrightHolder ?: "#COPYRIGHT_HOLDER#"}\n")
        text.append("  license: ${libraryInfo.license ?: "#LICENSE#"}\n")
        if (libraryInfo.licenseUrl?.isNotBlank() == true) {
            text.append("  licenseUrl: ${libraryInfo.licenseUrl}\n")
        }
        if (libraryInfo.url?.isNotBlank() == true) {
            text.append("  url: ${libraryInfo.url}\n")
        }
        return text.toString().trim()
    }

    @VisibleForTesting
    fun loadDependencyLicenses(
        project: Project,
        resolvedArtifacts: Set<ResolvedArtifact>,
        ignoredGroups: Set<String>
    ): List<LibraryInfo> {
        return resolvedArtifacts
            .filterNot { it.moduleVersion.id.version == "unspecified" }
            .filterNot { ignoredGroups.contains(it.moduleVersion.id.group) }
            .mapNotNull {
                resolvedArtifactToLibraryInfo(
                    it,
                    project
                )
            }
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

    @VisibleForTesting
    fun resolvedArtifactToLibraryInfo(artifact: ResolvedArtifact, project: Project): LibraryInfo? {
        val dependencyDesc =
            "${artifact.moduleVersion.id.group}:${artifact.moduleVersion.id.name}:${artifact.moduleVersion.id.version}"
        val artifactId: ArtifactId
        try {
            artifactId = ArtifactId.parse(dependencyDesc)
        } catch (e: IllegalArgumentException) {
            project.logger.info("UnSupport dependency: $dependencyDesc")
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
            license = licenseName,
            licenseUrl = licenseUrl
        )
    }

    fun resolveProjectDependencies(
            project: Project,
            ignoredProjects: Set<String>
    ): Set<ResolvedArtifact> {
        val projectNamesToSkip = ignoredProjects.toMutableSet()
        return getProjectDependencies(project, projectNamesToSkip)
            .distinctBy { it.toFormattedText() }
            .toSet()
    }

    private fun getProjectDependencies(
            project: Project?,
            projectNamesToSkip: MutableSet<String>
    ): Sequence<ResolvedArtifact> {
        project ?: return emptySequence()
        if (project.name in projectNamesToSkip) return emptySequence()
        projectNamesToSkip.add(project.name)
        val subProjects = targetSubProjects(project, projectNamesToSkip).associateBy {
            it.toFormattedText()
        }
        return (sequenceOf(project) + subProjects.values)
            .map { it.configurations }
            .flatten()
            .filter { isConfigForDependencies(it.name) }
            .map { it.resolvedArtifacts() }
            .flatten()
            .distinctBy { it.toFormattedText() }
            .flatMap {
                val subProject = subProjects[it.toFormattedText()]
                sequenceOf(it) + getProjectDependencies(subProject, projectNamesToSkip)
            }
    }

    private val dependencyKeywordPattern =
        """^(?!releaseUnitTest)(?:release\w*)?([cC]ompile|[cC]ompileOnly|[iI]mplementation|[aA]pi)$""".toRegex()
}
