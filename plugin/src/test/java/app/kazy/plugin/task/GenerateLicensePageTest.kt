package app.kazy.plugin.task

import app.kazy.plugin.loadYamlFromResources
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