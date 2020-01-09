package app.kazy.plugin.task

import app.kazy.plugin.loadYamlFromResources
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
