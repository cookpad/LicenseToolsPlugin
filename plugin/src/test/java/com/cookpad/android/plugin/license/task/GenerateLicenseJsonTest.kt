package com.cookpad.android.plugin.license.task

import com.cookpad.android.plugin.license.loadYamlFromResources
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class GenerateLicenseJsonTest {
    @Test
    fun toJson_isSuccess() = GenerateLicenseJson.run {
        val yamlInfoList = loadYamlFromResources("yaml/licenses.yml")
        val htmlString = yamlInfoList.toJson()
        assertThat(htmlString).isNotEmpty()
    }
}
