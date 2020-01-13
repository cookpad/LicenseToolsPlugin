package app.kazy.library

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class TinyYamlTest {

    @Test
    fun write_supports_primitive() {
        val tinyYaml = TinyYaml()
        val data = mutableListOf<Map<String, Any>>().apply {
            mutableMapOf<String, Any>().apply {
                put("artifact", "com.android.support:+:+")
                put("name", "Android Support Libraries")
                put("copyrightHolder", "The Android Open Source Project")
                put("license", "apache2")
            }
                .let { add(it) }
        }
        val output = tinyYaml.write(data)
        assertThat(output).isEqualTo(
            """
- artifact: com.android.support:+:+
  name: Android Support Libraries
  copyrightHolder: The Android Open Source Project
  license: apache2
        """.trimIndent()
        )
    }

    @Test
    fun write_supports_arrayValue() {
        val tinyYaml = TinyYaml()
        val data = mutableListOf<Map<String, Any>>().apply {
            mutableMapOf<String, Any>().apply {
                put("artifact", "com.android.support:+:+")
                put("name", "Android Support Libraries")
                put("copyrightHolder", "The Android Open Source Project")
                put("authors", mutableListOf<String>().apply {
                    add("Terence Parr")
                    add("Sam Harwell")
                })
                put("license", "apache2")
            }
                .let { add(it) }
        }
        val output = tinyYaml.write(data)
        assertThat(output).isEqualTo(
            """
- artifact: com.android.support:+:+
  name: Android Support Libraries
  copyrightHolder: The Android Open Source Project
  authors:
    - Terence Parr
    - Sam Harwell
  license: apache2
        """.trimIndent()
        )
    }

    @Test
    fun write_supports_multiLine() {
        val tinyYaml = TinyYaml()
        val data = mutableListOf<Map<String, Any>>().apply {
            mutableMapOf<String, Any>().apply {
                put("artifact", "com.android.support:+:+")
                put("name", "Android Support Libraries")
                put("copyrightHolder", "The Android Open Source Project")
                put(
                    "notice", """
Copyright (c) 2015 FUJI Goro (gfx)
SQLite.g4 is: Copyright (c) 2014 by Bart Kiers
                """.trimIndent()
                )
                put("license", "apache2")
            }
                .let { add(it) }
        }
        val output = tinyYaml.write(data)
        assertThat(output).isEqualTo(
            """
- artifact: com.android.support:+:+
  name: Android Support Libraries
  copyrightHolder: The Android Open Source Project
  notice: |
    Copyright (c) 2015 FUJI Goro (gfx)
    SQLite.g4 is: Copyright (c) 2014 by Bart Kiers
  license: apache2
        """.trimIndent()
        )
    }
}
