package app.kazy.library


public class TinyYaml(options: Options = Options(prettyPrint = false)) {

    @Suppress("IMPLICIT_CAST_TO_ANY", "UNCHECKED_CAST")
    fun write(data: List<Map<String, Any>>): String {
        return data
            .map {
                it.toList().mapIndexed { index, (first, second) ->
                    when (second) {
                        is String -> {
                            "${first.printKey(index)}: ${second.printValue()}\n"
                        }
                        is List<*> -> {
                            var result = if (index == 0) {
                                "- ${first}:\n"
                            } else {
                                "  ${first}:\n"
                            }
                            (second as List<String>).map { value ->
                                result += "    - $value\n"
                            }
                            result
                        }
                        is Boolean -> {
                            "${first.printKey(index)}: ${second}\n"
                        }
                        else -> throw  Error("Unexpected type: ${second.javaClass}")
                    }

                }.joinToString("").trim()
            }
            .joinToString("")
    }

    private fun String.printKey(index: Int): String {
        return if (index == 0) {
            "- $this"
        } else {
            "  $this"
        }
    }

    private fun String.printValue(): String {
        return if (this.contains("\n")) {
            var result = "|\n"
            this.split("\n").forEach {
                result += "    $it\n"
            }
            return result.trim()
        } else {
            this
        }
    }

    public data class Options(
        val prettyPrint: Boolean
    )

}
