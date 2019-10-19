package app.kazy.plugin

data class LibraryInfo(
    val artifactId: ArtifactId,
    val name: String,
    val libraryName: String,
    val url: String = "",
    val fileName: String = "",
    val license: String? = "",
    val licenseUrl: String? = "",
    val notice: String? = "",
    val copyrightHolder: String = "",
    val year: String = ""
) {

    fun normalizedLicense(): String {
        return ""
    }

    fun getCopyrightStatement(): String? {
        if (notice.isNotEmpty()) {
            return notice;
        } else if (copyrightHolder.isEmpty()) {
            return null;
        } else {
            return buildCopyrightStatement(copyrightHolder)
        }
    }

    private fun buildCopyrightStatement(copyrightHolder: String): String? {
        val dot = if (copyrightHolder.endsWith(".")) "" else "."
        if (year.isNotEmpty()) {
            return "Copyright &copy; ${year}, ${copyrightHolder}${dot} All rights reserved."
        } else {
            return "Copyright &copy; ${copyrightHolder}${dot} All rights reserved."
        }
    }

}
