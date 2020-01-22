package com.cookpad.android.plugin.license.util

import com.cookpad.android.plugin.license.data.ArtifactId
import com.cookpad.android.plugin.license.loadFileFromResources
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class YamlUtilsTest {
    @Test
    fun loadToLibraryInfo_isSuccess() {
        val file = loadFileFromResources("yaml/okhttp3_license.yml")
        val yamlData = YamlUtils.loadToLibraryInfo(file).first()
        assertThat(yamlData.artifactId).isEqualTo(ArtifactId.parse("com.squareup.okhttp3:okhttp:+"))
        assertThat(yamlData.name).isEqualTo("OkHttp")
        assertThat(yamlData.libraryName).isEqualTo("OkHttp")
        assertThat(yamlData.year).isEqualTo("2019")
        assertThat(yamlData.url).isEqualTo("https://github.com/square/okhttp")
        assertThat(yamlData.license).isEqualTo("The Apache Software License, Version 2.0")
        assertThat(yamlData.licenseUrl).isEqualTo("http://www.apache.org/licenses/LICENSE-2.0.txt")
    }
}
