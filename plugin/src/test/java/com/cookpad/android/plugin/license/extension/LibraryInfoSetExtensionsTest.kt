package com.cookpad.android.plugin.license.extension

import com.cookpad.android.plugin.license.loadYamlFromResources
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class LibraryInfoSetExtensionsTest {

    @Test
    fun duplicatedArtifacts_isSuccess() {
        val libraryInfoSet =
            loadYamlFromResources("yaml/valid_licenses.yml")
        val duplicatedArtifacts = libraryInfoSet.duplicatedArtifacts()
        assertThat(duplicatedArtifacts.size).isEqualTo(0)
    }

    @Test
    fun duplicatedArtifacts_foundDuplicated() {
        val libraryInfoSet =
            loadYamlFromResources("yaml/duplicated_licenses.yml")
        val duplicatedArtifacts = libraryInfoSet.duplicatedArtifacts()
        assertThat(duplicatedArtifacts.size).isEqualTo(1)
    }

    @Test
    fun notListedIn_isSuccess() {
        val librariesYaml =
            loadYamlFromResources("yaml/valid_licenses.yml")
        val lackedLicenses =
            loadYamlFromResources("yaml/lacked_licenses.yml")
        val notInDependencies = librariesYaml.notListedIn(lackedLicenses)
        assertThat(notInDependencies.size).isEqualTo(1)
    }

    @Test
    fun licensesUnMatched_isSuccess() {
        val dependencyLicenses =
            loadYamlFromResources("yaml/valid_licenses.yml")
        val libraryInfoSet =
            loadYamlFromResources("yaml/invalid_licenses.yml")
        val licensesUnMatched = dependencyLicenses.licensesUnMatched(libraryInfoSet)
        assertThat(licensesUnMatched.size).isEqualTo(1)
    }
}
