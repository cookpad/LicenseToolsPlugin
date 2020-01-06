package app.kazy.plugin.extension

import app.kazy.plugin.data.ArtifactId
import app.kazy.plugin.data.LibraryInfo


fun List<LibraryInfo>.duplicatedArtifacts(): List<String> {
    return this
        .groupingBy { it.artifactId.withWildcardVersion() }
        .eachCount()
        .filter { it.value > 1 }
        .map { it.key }
}

fun List<LibraryInfo>.notListedIn(dependencySet: List<LibraryInfo>): List<LibraryInfo> {
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
}

fun List<LibraryInfo>.licensesUnMatched(librariesYaml: List<LibraryInfo>): List<LibraryInfo> {
    return librariesYaml
        .filter { it.skip ?: false || it.forceGenerate ?: false }
        .filter { it.license?.isNotBlank() ?: true }
        .filter { this.checkUnMatchedLicense(it) }
}

fun List<LibraryInfo>.checkUnMatchedLicense(libraryInfo: LibraryInfo): Boolean {
    return this.findAll(libraryInfo.artifactId)
        .filter { it.skip ?: false || it.forceGenerate ?: false }
        .filter { it.license?.isNotBlank() ?: true }
        .filterNot {
            it.normalizedLicense().equals(libraryInfo.normalizedLicense(), ignoreCase = true)
        }
        .isNotEmpty()
}


fun List<LibraryInfo>.findAll(artifactId: ArtifactId): List<LibraryInfo> {
    return this
        .filter { it.artifactId.matches(artifactId) }
}

fun List<LibraryInfo>.contains(artifactId: ArtifactId): Boolean {
    this.forEach {
        if (it.artifactId.matches(artifactId)) {
            return true
        }
    }
    return false
}
