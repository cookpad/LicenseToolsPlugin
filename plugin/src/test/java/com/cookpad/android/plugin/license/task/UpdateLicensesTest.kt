package com.cookpad.android.plugin.license.task

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
class UpdateLicensesTest {

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
    fun updateLicensesYaml_duplicatedDependencies_dedupsAndSortsDependencies() {
        val licenses = loadYamlFromResources("yaml/duplicated_unsorted_licenses.yml")
        assertThat(licenses.size).isEqualTo(4)
        UpdateLicenses.updateLicensesYaml(project, FILENAME_LICENSES_YAML, licenses)

        val result = YamlUtils.loadToLibraryInfo(licensesYamlFile)

        val expected = loadYamlFromResources("yaml/dedup_sorted_licenses.yml")
        assertThat(result.size).isEqualTo(expected.size)
        for ((index, info) in result.withIndex()) {
            assertThat(info).isEqualTo(expected[index])
        }
    }

    companion object {
        private const val FILENAME_LICENSES_YAML = "licenses.yml"
    }
}
