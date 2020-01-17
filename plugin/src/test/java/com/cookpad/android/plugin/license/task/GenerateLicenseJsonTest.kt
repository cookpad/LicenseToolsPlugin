package com.cookpad.android.plugin.license.task

import com.cookpad.android.plugin.license.loadYamlFromResources
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class GenerateLicenseJsonTest {
    @Test
    fun toJson_isSuccess() = GenerateLicenseJson.run {
        val yamlInfoList = loadYamlFromResources("yaml/example_apache2_license.yml")
        val htmlString = yamlInfoList.toJson()
        assertThat(htmlString).isEqualTo("""
            {"libraries":[{"artifactId":{"name":"example-common","group":"com.example","version":"+"},"copyrightHolder":"Example Author","copyrightStatement":"Copyright &copy; Example Author. All rights reserved.","license":"The Apache Software License, Version 2.0","licenseUrl":"http://www.apache.org/licenses/LICENSE-2.0.txt","normalizedLicense":"apache2","url":"https://example.com/example/example-common","libraryName":"example-common"}]}
        """.trimIndent())
    }
}
