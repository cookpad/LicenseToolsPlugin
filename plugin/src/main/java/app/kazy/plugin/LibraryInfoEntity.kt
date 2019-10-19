package app.kazy.plugin

data class LibraryInfoEntity(
    val artifact: String,
    val name: String,
    val url: String?,
    val copyrightHolder: String,
    val license: String,
    val licenseUrl: String?
)
