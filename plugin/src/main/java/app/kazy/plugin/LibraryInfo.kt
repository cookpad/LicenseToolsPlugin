package app.kazy.plugin

data class LibraryInfo(
    val artifactId: ArtifactId,
    val name: String,
    val libraryName: String,
    val url: String,
    val fileName: String?,
    val license: String?,
    val licenseUrl: String?
)
