package app.kazy.plugin.extension

import app.kazy.plugin.loadFileFromResources
import app.kazy.plugin.util.YamlUtils
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class LibraryInfoSetExtensionsTest {

    @Test
    fun duplicatedArtifacts_isSuccess() {
        val file = loadFileFromResources("yaml/valid_licenses.yml")
        val libraryInfoSet = YamlUtils.loadToLibraryInfo(file)
        val duplicatedArtifacts = libraryInfoSet.duplicatedArtifacts()
        assertThat(duplicatedArtifacts.size).isEqualTo(0)
    }

    @Test
    fun duplicatedArtifactsTest_foundDuplicated() {
        val file = loadFileFromResources("yaml/duplicated_licenses.yml")
        val libraryInfoSet = YamlUtils.loadToLibraryInfo(file)
        val duplicatedArtifacts = libraryInfoSet.duplicatedArtifacts()
        assertThat(duplicatedArtifacts.size).isEqualTo(1)
    }
}