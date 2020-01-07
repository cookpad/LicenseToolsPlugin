package app.kazy.plugin.data

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Namespace
import org.simpleframework.xml.Root

@Root(name = "project", strict = false)
class LibraryPom {

    @set:Attribute(name = "schemaLocation", required = false)
    @get:Attribute(name = "schemaLocation", required = false)
    @Namespace(reference = "http://www.w3.org/2001/XMLSchema-instance", prefix = "xsi")
    var mSchemaLocation: String = ""

    @set:ElementList(name = "licenses", required = false)
    @get:ElementList(name = "licenses", required = false)
    var licenses: ArrayList<LicensePom> = ArrayList()

    @set:Element(name = "name", required = false)
    @get:Element(name = "name", required = false)
    var name: String = ""

    @set:Element(name = "url", required = false)
    @get:Element(name = "url", required = false)
    var url: String = ""

    @Root(strict = false)
    class LicensePom {
        @set:Element(name = "name", required = false)
        @get:Element(name = "name", required = false)
        var name: String? = ""

        @set:Element(name = "url", required = false)
        @get:Element(name = "url", required = false)
        var url: String? = ""
    }
}
