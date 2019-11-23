package app.kazy.plugin

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.mock
import org.gradle.api.Project
import org.junit.Test


class LicenseToolsPluginTest {

    @Test
    fun isConfigForDependencies_isCorrect() = LicenseToolsPlugin().run {
        assertThat(isConfigForDependencies("api")).isTrue()
        assertThat(isConfigForDependencies("compile")).isTrue()
        assertThat(isConfigForDependencies("compileOnly")).isTrue()
        assertThat(isConfigForDependencies("releaseCompileOnly")).isTrue()
        assertThat(isConfigForDependencies("implementation")).isTrue()

        assertThat(isConfigForDependencies("releaseUnitTestApi")).isFalse()
        assertThat(isConfigForDependencies("androidTestCompile")).isFalse()
        assertThat(isConfigForDependencies("debugImplementation")).isFalse()
        assertThat(isConfigForDependencies("testCompileOnly")).isFalse()
    }
}
