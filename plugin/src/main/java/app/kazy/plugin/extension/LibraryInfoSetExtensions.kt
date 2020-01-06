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

fun Set<LibraryInfo>.licensesUnMatched(librariesYaml: Set<LibraryInfo>): Set<LibraryInfo> {
    return librariesYaml
        .filter { it.skip ?: false || it.forceGenerate ?: false }
        .filter { it.license?.isNotBlank() ?: true }
        .filter { this.checkUnMatchedLicense(it) }
        .toSet()
}

fun Set<LibraryInfo>.checkUnMatchedLicense(libraryInfo: LibraryInfo): Boolean {
    return this.findAll(libraryInfo.artifactId)
        .filter { it.skip ?: false || it.forceGenerate ?: false }
        .filter { it.license?.isNotBlank() ?: true }
        .filterNot {
            it.normalizedLicense().equals(libraryInfo.normalizedLicense(), ignoreCase = true)
        }
        .isNotEmpty()
}


fun Set<LibraryInfo>.findAll(artifactId: ArtifactId): Set<LibraryInfo> {
    return this
        .filter { it.artifactId.matches(artifactId) }
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
