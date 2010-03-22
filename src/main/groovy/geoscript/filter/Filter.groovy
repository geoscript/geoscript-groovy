package geoscript.filter

import org.opengis.filter.Filter as GTFilter
import org.geotools.filter.text.cql2.CQL
import org.geotools.xml.Parser
import org.geotools.xml.Encoder
import org.geotools.filter.v1_0.OGCConfiguration as OGCConfiguration10 
import org.geotools.filter.v1_0.OGC as OGC10 
import org.geotools.filter.v1_1.OGCConfiguration as OGCConfiguration11 
import org.geotools.filter.v1_1.OGC as OGC11 

/**
 * A Filter is a predicate or constraint used to match or filter Feature objects
 * <p>You can create Filters from CQL:</p>
 * <code>
 * Filter f = new Filter("name='foobar')
 * </code>
 * <p>Or you can create Filters from XML:</p>
 * <code>
 * Filter f = new Filter('&lt;Filter&gt;&lt;PropertyIsEqualTo&gt;&lt;PropertyName&gt;name&lt;/PropertyName&gt;&lt;Literal&gt;foobar&lt;/Literal&gt;&lt;/PropertyIsEqualTo&gt;&lt;/Filter&gt;')
 * </code>
 */
class Filter {
    
    /**
     * The wrapped GeoTools Filter
     */
    GTFilter filter
    
    /**
     * Create a new Filter wrapping a GeoTools Filter
     * @param filter The org.opengis.filter.Filter
     */
    Filter(GTFilter filter) {
        this.filter = filter
    }
    
    /**
     * Create a new Filter from an existing Filter
     * @param filter The exiting Filter
     */
    Filter(Filter filter) {
        this.filter = filter.filter
    }

    /**
     * Create a new Filter from a String (CQL or XML).
     * @param str The String CQL or XML
     * <p>Creating a Filter with CQL:</p>
     * <code>
     * Filter f = new Filter("name='foobar')
     * </code>
     * <p>Creating a Filter with XML:</p>
     * <code>
     * Filter f = new Filter('&lt;Filter&gt;&lt;PropertyIsEqualTo&gt;&lt;PropertyName&gt;name&lt;/PropertyName&gt;&lt;Literal&gt;foobar&lt;/Literal&gt;&lt;/PropertyIsEqualTo&gt;&lt;/Filter&gt;')
     * </code>
     */
    Filter(String str) {
        this(create(str))
    }

    /**
     * Create a GeoTools Filter from a CQL or XML string
     */
    private static GTFilter create(String str) {
        try {
            return fromCQL(str)
        }
        catch (e1) {
            try {
                return fromXML(str, 1.0)
            }
            catch (e2) {
                return fromXML(str, 1.1)
            }
        }
    }
    
    /**
     * Get the CQL string from the Filter.
     * @return The Filter as CQL
     */
    String getCql() {
        CQL.toCQL(filter)
    }
    
    /**
     * Get the XML string from the Filter.
     * @param pretty Whether the XML is pretty printed (defaults to true)
     * @param version The version (defaults to 1.0)
     * @return The Filter as XML
     */
    String getXml(boolean pretty = true, double version = 1.0) {
        Encoder e 
        if (version == 1.0) {
            e = new Encoder(new OGCConfiguration10())
        }
        else {
            e = new Encoder(new OGCConfiguration11())
        }
        e.indenting = pretty
        e.omitXMLDeclaration = true
        OutputStream out = new java.io.ByteArrayOutputStream()
        if (version == 1.0) {
            e.encode(filter, OGC10.getInstance().Filter, out)
        }
        else {
            e.encode(filter, OGC11.getInstance().Filter, out)
        }
        new String(out.toByteArray())
    }
   
    /**
     * The string representation
     * @return The string representation
     */
    String toString() {
        filter.toString()
    }
    
    /**
     * The PASS Filter wrapps the Geotools INCLUDE Filter
     */
    static Filter PASS = new Filter(GTFilter.INCLUDE)

    /**
     * The FAIL Filter wrapps the GeoTools EXCLUDE Filter
     */
    static Filter FAIL = new Filter(GTFilter.EXCLUDE)

    /**
     * Create a GeoTools Filter from a CQL String
     */
    private static GTFilter fromCQL(String cql) {
        CQL.toFilter(cql)
    }

    /**
     * Create a GeoTools Filter from an XML String
     */
    private static GTFilter fromXML(String xml, double version = 1.0) {
        Parser parser
        if (version == 1.0) {
            parser = new Parser(new OGCConfiguration10())
        }
        else {
            parser = new Parser(new OGCConfiguration11())
        }
        parser.parse(new java.io.StringReader(xml))
    }

}
