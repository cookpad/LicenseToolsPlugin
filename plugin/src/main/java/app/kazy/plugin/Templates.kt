package app.kazy.plugin

import org.codehaus.groovy.runtime.DefaultGroovyMethods
import org.codehaus.groovy.runtime.IOGroovyMethods

import java.net.JarURLConnection
import java.net.URISyntaxException
import java.net.URL
import java.util.LinkedHashMap
import java.util.zip.ZipFile

import groovy.text.SimpleTemplateEngine
import java.io.*

object Templates {

    private val templateEngine = SimpleTemplateEngine()

    @Throws(IOException::class, URISyntaxException::class, ClassNotFoundException::class)
    fun buildLicenseHtml(library: LibraryInfo): String {
        assertLicenseAndStatement(library)

        val templateFile = "template/licenses/" + DefaultGroovyMethods.invokeMethod(
            String::class.java,
            "valueOf",
            arrayOf<Any>(library.normalizedLicense())
        ) + ".html"
        val map = LinkedHashMap<String, LibraryInfo>(1)
        map["library"] = library
        val templateText = readResourceContent(templateFile)
        return templateEngine.createTemplate(templateText).make(map).toString()
    }

    private fun assertLicenseAndStatement(library: LibraryInfo) {
        if (library.license.isBlank()) {
            throw NotEnoughInformationException(library)
        }

        if (library.getCopyrightStatement() == null) {
            throw NotEnoughInformationException(library)
        }

    }

    fun wrapWithLayout(content: CharSequence): String {
        val templateFile = "template/layout.html"
        val map = LinkedHashMap<String, String>(1)
        map["content"] = makeIndent(content, 4)
        return templateEngine.createTemplate(readResourceContent(templateFile)).make(map).toString()
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

    private fun readResourceContent(filename: String): String {
        var templateFileUrl: URL? = Templates::class.java.classLoader.getResource(filename)
            ?: throw FileNotFoundException("File not found: $filename")

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
