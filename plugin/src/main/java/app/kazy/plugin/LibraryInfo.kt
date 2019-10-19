package app.kazy.plugin

data class LibraryInfo(
    val artifactId: ArtifactId,
    val name: String,
    val libraryName: String,
    val url: String = "",
    val fileName: String = "",
    val license: String = "",
    val licenseUrl: String? = "",
    val notice: String? = "",
    val copyrightHolder: String = "",
    val year: String = "",
    val skip: Boolean = false
) {

    fun normalizedLicense(): String {
        return when {
            apache1_0.matches(license) -> "apache1_0"
            apache1_1.matches(license) -> "apache1_1"
            apache2.matches(license) -> "apache2"
            else -> throw IllegalStateException()
        }
    }

    fun getCopyrightStatement(): String? {
        return when {
            !notice.isNullOrEmpty() -> notice
            copyrightHolder.isEmpty() -> null
            else -> buildCopyrightStatement(copyrightHolder)
        }
    }

    private fun buildCopyrightStatement(copyrightHolder: String): String? {
        val dot = if (copyrightHolder.endsWith(".")) "" else "."
        return if (year.isNotEmpty()) {
            "Copyright &copy; ${year}, ${copyrightHolder}${dot} All rights reserved."
        } else {
            "Copyright &copy; ${copyrightHolder}${dot} All rights reserved."
        }
    }

    companion object {
        val apache1_0 = """(?i).*\bapache.*1\.0.*""".toRegex()
        val apache1_1 = """(?i).*\bapache.*1.*""".toRegex()
        val apache2 = """(?i).*\bapache.*2.*""".toRegex()
    }

}
