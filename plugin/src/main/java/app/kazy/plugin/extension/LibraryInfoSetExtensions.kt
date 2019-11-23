package app.kazy.plugin.extension

import app.kazy.plugin.data.ArtifactId
import app.kazy.plugin.data.LibraryInfo

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

fun Set<LibraryInfo>.contains(artifactId: ArtifactId): Boolean {
    this.forEach {
        if (it.artifactId.matches(artifactId)) {
            return true
        }
    }
    return false
}
