package geoscript.filter

import geoscript.geom.Bounds
import geoscript.geom.Geometry
import geoscript.feature.Feature
import org.geotools.filter.visitor.SimplifyingFilterVisitor
import org.opengis.filter.Filter as GTFilter
import org.geotools.filter.text.ecql.ECQL
import org.geotools.xml.Parser
import org.geotools.xml.Encoder
import org.geotools.filter.v1_0.OGCConfiguration as OGCConfiguration10 
import org.geotools.filter.v1_0.OGC as OGC10 
import org.geotools.filter.v1_1.OGCConfiguration as OGCConfiguration11 
import org.geotools.filter.v1_1.OGC as OGC11 
import org.geotools.factory.CommonFactoryFinder
import org.geotools.factory.GeoTools
import org.opengis.filter.FilterFactory2

/**
 * A Filter is a predicate or constraint used to match or filter {@link geoscript.feature.Feature Feature} objects.
 * <p>You can create Filters from CQL:</p>
 * <p><blockquote><pre>
 * Filter f = new Filter("name='foobar')
 * </pre></blockquote></p>
 * <p>Or you can create Filters from XML:</p>
 * <p><blockquote><pre>
 * Filter f = new Filter('&lt;Filter&gt;&lt;PropertyIsEqualTo&gt;&lt;PropertyName&gt;name&lt;/PropertyName&gt;&lt;Literal&gt;foobar&lt;/Literal&gt;&lt;/PropertyIsEqualTo&gt;&lt;/Filter&gt;')
 * </pre></blockquote></p>
 * @author Jared Erickson
 */
class Filter {
    
    /**
     * The wrapped GeoTools Filter
     */
    GTFilter filter

    /**
     * The GeoTools FilterFactory
     */
    FilterFactory2 factory = CommonFactoryFinder.getFilterFactory2(GeoTools.defaultHints)

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
        ECQL.toCQL(filter)
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
     * Evaluate the Filter against a Feature
     */
    boolean evaluate(Feature f) {
        filter.evaluate(f.f)
    }

    /**
     * Simplify this Filter
     * @return The new simplified Filter
     */
    Filter simplify() {
        def simplifier = new SimplifyingFilterVisitor()
        new Filter(filter.accept(simplifier, null))
    }

    /**
     * Get a new Filter that is the negation of the current Filter
     * @return A new Filter that is the negation of the current Filter
     */
    Filter getNot() {
        new Filter(factory.not(filter))
    }

    /**
     * Get a new Filter that is the negation of the current Filter
     * @return A new Filter that is the negation of the current Filter
     */
    Filter negative() {
        not
    }

    /**
     * The string representation
     * @return The string representation
     */
    String toString() {
        filter.toString()
    }

    /**
     * Does this Filter equal another Filter?
     * @return Whether this and the other Filter are equal
     */
    boolean equals(Object obj) {
        filter.equals(obj.filter)
    }

    /**
     * Returns the hash code value of the Filter
     * @return The hash code value of the Filter
     */
    int hashCode() {
        filter.hashCode()
    }

    /**
     * Combine the current Filter with another Filter.
     * @param other Another Filter or a CQL String
     * @return A combined Filter
     */
    Filter plus(def other) {
        and(other)
    }

    /**
     * Combine the current Filter with another Filter.
     * @param other Another Filter or a CQL String
     * @return A combined Filter
     */
    Filter and(def other) {
        if (filter == GTFilter.INCLUDE) {
            return new Filter(other )
        } else {
            return new Filter(factory.and(filter, new Filter(other).filter))
        }
    }

    /**
     * Combine the current Filter with another Filter in an OR relationship.
     * @param other Another Filter or a CQL String
     * @return A new Filter
     */
    Filter or(def other) {
        new Filter(factory.or(filter, new Filter(other).filter))
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
     * Create a Spatial Bounding Box Filter
     * @param fieldName The geometry field name (defaults to the_geom)
     * @param bounds The Bounds
     * @return A Filter
     */
    static Filter bbox(String fieldName = "the_geom", Bounds bounds) {
        new Filter("BBOX(${fieldName}, ${bounds.minX},${bounds.minY},${bounds.maxX},${bounds.maxY})")
    }

    /**
     * Create a Spatial Filter that contains the given Geometry
     * @param fieldName The geometry field name (defaults to the_geom)
     * @param geometry The Geometry
     * @return A Filter
     */
    static Filter contains(String fieldName = "the_geom", Geometry geometry) {
        new Filter("CONTAINS(${fieldName}, ${geometry.wkt})")
    }

    /**
     * Create a Spatial Filter that is within a certain distance of the given Geometry.
     * There are some serious limitations to DWITHIN in Geotools.  It does not work with
     * projection EPSG:4326!
     * @param fieldName The geometry field name (defaults to the_geom)
     * @param geometry The Geometry
     * @param distance The distance
     * @param units The units (kilometers, meters, feet, ect...)
     * @return A Filter
     */
    static Filter dwithin(String fieldName = "the_geom", Geometry geometry, double distance, String units) {
        new Filter("DWITHIN(${fieldName}, ${geometry.wkt}, ${distance}, ${units})")
    }

    /**
     * Create a Spatial Filter that crosses the given Geometry
     * @param fieldName The geometry field name (defaults to the_geom)
     * @param geometry The Geometry
     * @return A Filter
     */
    static Filter crosses(String fieldName = "the_geom", Geometry geometry) {
        new Filter("CROSSES(${fieldName}, ${geometry.wkt})")
    }

    /**
     * Create a Spatial Filter that intersects the given Geometry
     * @param fieldName The geometry field name (defaults to the_geom)
     * @param bounds The Bounds
     * @return A Filter
     */
    static Filter intersects(String fieldName = "the_geom", Geometry geometry) {
        new Filter("INTERSECTS(${fieldName}, ${geometry.wkt})")
    }

    /**
     * Create a GeoTools Filter from a CQL String
     */
    private static GTFilter fromCQL(String cql) {
        return ECQL.toFilter(cql)
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
