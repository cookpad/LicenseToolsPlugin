package com.cookpad.android.plugin.license.task

import com.cookpad.android.plugin.license.loadYamlFromResources
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class GenerateLicensePageTest {
    @Test
    fun toHtml_isSuccess() = GenerateLicensePage.run {
        val yamlInfoList = loadYamlFromResources("yaml/licenses.yml")
        val htmlString = yamlInfoList.toHtml()
        assertThat(htmlString).doesNotContain("null")
    }
}
