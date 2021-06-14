package com.cookpad.android.plugin.license.task

import com.cookpad.android.plugin.license.loadYamlFromResources
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class GenerateLicensePageTest {

    @Mock
    lateinit var project: Project

    @Mock
    lateinit var logger: Logger

    @Before
    fun setUp() {
        whenever(project.logger) doReturn logger
    }

    @Test
    fun toHtml_isSuccess() = GenerateLicensePage.run {
        val yamlInfoList = loadYamlFromResources("yaml/licenses.yml")
        val htmlString = yamlInfoList.toHtml(project)
        assertThat(htmlString).doesNotContain("null")
    }

    @Test
    fun toHtml_notEnoughInfo_printsAllErrorAndThrowsGradleException() = GenerateLicensePage.run {
        val yamlInfoList = loadYamlFromResources("yaml/not_enough_info_licenses.yaml")
        try {
            yamlInfoList.toHtml(project)
            fail()
        } catch (expected: GradleException) {
        }

        verify(logger, times(2)).warn(any())
    }
}
