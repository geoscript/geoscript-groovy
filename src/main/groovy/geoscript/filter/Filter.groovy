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
 * A Filter
 */
class Filter {
    
    /**
     * The wrapped GeoTools Filter
     */
    GTFilter filter
    
    /**
     * Create a new Filter wrapping a GeoTools Filter
     */
    Filter(GTFilter filter) {
        this.filter = filter
    }
    
    /**
     * Create a new Filter from a String (CQL or XML)
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
     * Get the CQL string from the Filter
     */
    String cql() {
        CQL.toCQL(filter)
    }
    
    /**
     * Get the XML string from the Filter
     */
    String xml(boolean pretty = true, double version = 1.0) {
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
