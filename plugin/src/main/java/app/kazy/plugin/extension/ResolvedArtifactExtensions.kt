package app.kazy.plugin.extension

import org.gradle.api.artifacts.ResolvedArtifact
import org.gradle.internal.impldep.com.google.common.annotations.VisibleForTesting

@VisibleForTesting
fun ResolvedArtifact.toFormattedText(): String {
    return "${moduleVersion.id.group}:${moduleVersion.id.name}:${moduleVersion.id.version}"
}
