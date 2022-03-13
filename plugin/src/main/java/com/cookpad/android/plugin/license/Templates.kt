// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2016 Cookpad Inc.

package com.cookpad.android.plugin.license

import com.cookpad.android.plugin.license.data.LibraryInfo
import com.cookpad.android.plugin.license.exception.NotEnoughInformationException
import groovy.text.SimpleTemplateEngine
import org.codehaus.groovy.runtime.DefaultGroovyMethods
import org.codehaus.groovy.runtime.IOGroovyMethods
import org.gradle.api.Project
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.net.JarURLConnection
import java.net.URISyntaxException
import java.net.URL
import java.util.zip.ZipFile

object Templates {

    private val templateEngine = SimpleTemplateEngine()

    @Throws(
        IOException::class,
        URISyntaxException::class,
        ClassNotFoundException::class,
        NotEnoughInformationException::class
    )
    fun buildLicenseHtml(library: LibraryInfo, project: Project): String {
        assertLicenseAndStatement(
            library
        )

        val templateFile = "template/licenses/" + DefaultGroovyMethods.invokeMethod(
            String::class.java,
            "valueOf",
            arrayOf<Any>(library.normalizedLicense())
        ) + ".html"
        val map = LinkedHashMap<String, LibraryInfo>(1)
        map["library"] = library
        val templateText =
            readResourceContent(
                templateFile,
                project
            )
        return templateEngine.createTemplate(templateText).make(map).toString()
    }

    @Throws(NotEnoughInformationException::class)
    private fun assertLicenseAndStatement(library: LibraryInfo) {
        if (library.license.isNullOrBlank()) {
            throw NotEnoughInformationException(
                library,
                "Missing info in the \"license\" field"
            )
        }

        if (library.getCopyrightStatement() == null) {
            throw NotEnoughInformationException(
                library,
                "Could not generate the copyright statement. " +
                    "Please provide either the \"notice\" field or the \"copyrightHolder\" field."
            )
        }
    }

    fun wrapWithLayout(content: CharSequence, project: Project): String {
        val templateFile = "template/layout.html"
        val map = LinkedHashMap<String, String>(1)
        map["content"] =
            makeIndent(content, 4)
        return templateEngine.createTemplate(
            readResourceContent(
                templateFile,
                project
            )
        ).make(map).toString()
    }

    private fun makeIndent(content: CharSequence, level: Int): String {
        val s = StringBuilder()
        content.lines().forEach { line ->
            for (i in 0..level) {
                s.append(" ")
            }
            s.append(line)
            s.append("\n")
        }
        return s.toString()
    }

    private fun readResourceContent(filename: String, project: Project): String {
        var templateFileUrl: URL? = Templates::class.java.classLoader.getResource(filename)
        if (templateFileUrl == null) {
            project.extensions.getByType(LicenseToolsPluginExtension::class.java).originalLicenses.get(
                filename.replace("^template/licenses/".toRegex(), "")
                    .replace(".html$".toRegex(), "")
            )
                ?.let { urlString ->
                    return project.file("./${urlString}").readText()
                } ?: throw FileNotFoundException("File not found: $filename")
        }


        templateFileUrl = URL(templateFileUrl.toString())
        try {
            return IOGroovyMethods.getText(templateFileUrl.openStream(), "UTF-8")
        } catch (e: FileNotFoundException) {
            // fallback to read JAR directly
            val jarFile = DefaultGroovyMethods.asType(
                templateFileUrl.openConnection(),
                JarURLConnection::class.java
            ).jarFileURL.toURI()
            val zip: ZipFile
            try {
                zip = ZipFile(File(jarFile))
            } catch (ex: FileNotFoundException) {
                System.err.println("[plugin] no plugin.jar. run `./gradlew plugin:jar` first.")
                throw ex
            }
            return IOGroovyMethods.getText(zip.getInputStream(zip.getEntry(filename)), "UTF-8")
        }
    }
}
