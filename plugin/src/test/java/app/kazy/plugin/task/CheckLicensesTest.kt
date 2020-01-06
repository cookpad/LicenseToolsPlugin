package app.kazy.plugin.task

import com.google.common.truth.Truth.assertThat
import org.junit.Test


class CheckLicensesTest {

    @Test
    fun isConfigForDependencies_isCorrect() = CheckLicenses.run {
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
