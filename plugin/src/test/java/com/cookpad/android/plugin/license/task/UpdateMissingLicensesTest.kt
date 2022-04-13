package com.cookpad.android.plugin.license.task

import com.cookpad.android.plugin.license.data.ArtifactId
import com.cookpad.android.plugin.license.loadYamlFromResources
import com.cookpad.android.plugin.license.util.YamlUtils
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import org.gradle.api.Project
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import java.io.File

@RunWith(MockitoJUnitRunner::class)
class UpdateMissingLicensesTest {

    @get:Rule
    val tempFolder = TemporaryFolder()
    private lateinit var licensesYamlFile: File

    @Mock
    lateinit var project: Project

    @Before
    fun setUp() {
        licensesYamlFile = tempFolder.newFile()
        whenever(project.file(FILENAME_LICENSES_YAML)) doReturn licensesYamlFile
    }

    @After
    fun tearDown() {
        licensesYamlFile.delete()
    }

    @Test
    fun updateLicensesYaml_addsMissingDependencies() {
        val licenses = loadYamlFromResources("yaml/missing_licenses.yml")
        assertThat(licenses.size).isEqualTo(3)
        UpdateMissingLicenses.updateMissingLicensesYaml(project, FILENAME_LICENSES_YAML, licenses)

        var result = YamlUtils.loadToLibraryInfo(licensesYamlFile)
        assertThat(result.size).isEqualTo(3)

        val update = loadYamlFromResources("yaml/missing_licenses_update.yml")
        UpdateMissingLicenses.updateMissingLicensesYaml(project, FILENAME_LICENSES_YAML, update)
        result = YamlUtils.loadToLibraryInfo(licensesYamlFile)
        assertThat(result.size).isEqualTo(4)

        assertThat(result.size).isEqualTo(update.size)
        for (info in update) {
            val finalInfo = result.find { it.artifactId.matches(info.artifactId) }
            assertThat(finalInfo).isNotNull()
            if (info.artifactId.matches(ArtifactId("com.android.support", "+", "+"))) {
                assertThat(info).isNotEqualTo(finalInfo)
                assertThat(finalInfo!!.license).isEqualTo("apache2")
            } else {
                assertThat(info).isEqualTo(finalInfo)
            }
        }
    }

    @Test
    fun updateLicensesYaml_removeMissingDependencies() {
        val licenses = loadYamlFromResources("yaml/missing_licenses_update.yml")
        assertThat(licenses.size).isEqualTo(4)
        UpdateMissingLicenses.updateMissingLicensesYaml(project, FILENAME_LICENSES_YAML, licenses)

        var result = YamlUtils.loadToLibraryInfo(licensesYamlFile)
        assertThat(result.size).isEqualTo(4)

        val update = loadYamlFromResources("yaml/removed_licenses.yml")
        UpdateMissingLicenses.updateMissingLicensesYaml(project, FILENAME_LICENSES_YAML, update)
        result = YamlUtils.loadToLibraryInfo(licensesYamlFile)
        assertThat(result.size).isEqualTo(3)
    }

    companion object {
        private const val FILENAME_LICENSES_YAML = "licenses.yml"
    }
}
