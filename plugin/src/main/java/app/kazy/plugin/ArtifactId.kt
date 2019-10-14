package app.kazy.plugin

data class ArtifactId(
    val group: String,
    val name: String, // "+" as a wild card
    val version: String  // "+" as a wild card
) {

    fun matches(artifactId: ArtifactId): Boolean {
        return (matchesWithWildcard(group, artifactId.group)
                && matchesWithWildcard(name, artifactId.name)
                && matchesWithWildcard(version, artifactId.version))
    }

    private fun matchesWithWildcard(a: String, b: String): Boolean {
        return a == "+" || b == "+" || a == b
    }

    fun withWildcardVersion(): String {
        return "$group:$name:+"
    }

    override fun toString(): String {
        return "$group:$name:$version"
    }

    companion object {

        fun parse(notation: String?): ArtifactId {
            if (notation == null) {
                return ArtifactId("", "", "")
            }
            val parts = notation.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (parts.size == 3) {
                return ArtifactId(parts[0], parts[1], parts[2])
            }
            throw IllegalArgumentException("Invalid arguments: $notation")
        }
    }
}

