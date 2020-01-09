package app.kazy.plugin.extension

import app.kazy.plugin.loadFileFromResources
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class FileExtensionsTest {

    @Test
    fun loadYaml_checkEmptyFileCase() {
        val file = loadFileFromResources("yaml/empty_file.yml")
        val libraryInfoList = file.loadYaml()
        assertThat(libraryInfoList.size).isEqualTo(0)
    }
}
