package app.kazy.plugin.extension

import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ResolvedArtifact

fun Configuration.resolvedArtifacts(): Set<ResolvedArtifact> {
    val copyConfiguration = copyRecursive()
    copyConfiguration.isCanBeResolved = true
    return copyConfiguration.resolvedConfiguration.lenientConfiguration.artifacts
}
