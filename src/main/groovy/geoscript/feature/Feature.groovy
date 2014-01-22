package geoscript.feature

import org.opengis.feature.simple.SimpleFeature
import org.geotools.feature.simple.SimpleFeatureBuilder
import com.vividsolutions.jts.geom.Geometry as JtsGeometry
import geoscript.geom.*
import geoscript.layer.Layer

/**
 * A Feature contains a set of named attributes with values.
 * <p>A Feature is created from a Map of name value pairs and an identifier.</p>
 * <p><i>Without a {@link Schema} (see below) the data types are inferred).</i></p>
 * <p><blockquote><pre>
 * Feature f = new Feature(['name': 'anvil', 'price': 100.0], 'widgets.1')
 * </pre></blockquote></p>
 * <p>A Feature can also be created from a list of values, an identifier, and a {@link Schema}</p>
 * <p><blockquote><pre>
 * {@link Schema} s = new {@link Schema}('widgets', [['name','string'],['price','float']])
 * Feature f = new Feature(['anvil', 100.0], '1', s)
 * </pre></blockquote></p>
 * <p>A Feature can also be created from a Map of name value pairs, an identifier, and a {@link Schema}</p>
 * <p><blockquote><pre>
 * {@link Schema} s = new {@link Schema}('widgets', [['name','string'],['price','float']])
 * Feature f = new Feature(['name': 'anvil', 'price': 100.0], '1', s)
 * </pre></blockquote></p>
 * @author Jared Erickson
 */
class Feature {

    /**
     * The wrapped GeoTools SimpleFeature
     */
    SimpleFeature f

    /**
     * The Schema
     */
    Schema schema

    /**
     * The Layer the Feature was read from
     */
    Layer layer

    /**
     * Create a new Feature by wrapping a GeoTools SimpleFeature
     * @param f The GeoTools SimpleFeature
     */
    Feature(SimpleFeature f) {
        this.f = f
        this.schema = new Schema(this.f.featureType)
    }

    /**
     * Create a new Feature with a Map of attributes, an id, and a Schema.
     * <p><code>
     * Schema s = new Schema('widgets', [['name','string'],['price','float']])
     * Feature f = new Feature(['name': 'anvil', 'price': 100.0], '1', s)
     * </code></p>
     * @param attributes A Map of name value pairs
     * @param id A String identifier
     * @param schema The Schema
     */
    Feature(Map attributes, String id, Schema schema) {
        this(buildFeature(attributes, id, schema))
    }

    /**
     * Create a new Feature with a List of values, an id, and a Schema.  The
     * List of values must be in the same order as the Schema's fields.
     * <p><code>
     * Schema s = new Schema('widgets', [['name','string'],['price','float']])
     * Feature f = new Feature(['anvil', 100.0], '1', s)
     * </code></p>
     * @param attributes A List of attribute values
     * @param id A String identifier
     * @param schema The Schema
     */
    Feature(List attributes, String id, Schema schema) {
        this(buildFeature(attributes, id, schema))
    }

    /**
     * Create a new Feature with a Map of Attributes and an Id.  The Schema is
     * inferred from the attribute values.
     * <p><code>
     * Feature f = new Feature(['name': 'anvil', 'price': 100.0], 'widgets.1')
     * </code></p>
     * @param atributes A Map of name value pairs
     * @param id The string identifer
     */
    Feature(Map attributes, String id) {
        this(buildFeature(attributes, id))
    }

    /**
     * Get the Feature's ID
     * @return The Feature's ID
     */
    String getId() {
        f.identifier.toString()
    }

    /**
     * Get the Feature's Geometry
     * @return The Feature's Geometry
     */
    Geometry getGeom() {
        Geometry.wrap((JtsGeometry) f.defaultGeometry)
    }

    /**
     * Set the Feature's Geometry
     * @param geom The new Geometry
     */
    void setGeom(Geometry geom) {
        f.defaultGeometry = geom.g
    }

    /**
     * The Bounds of the Feature's Geometry.
     * @param The Bounds of the Feature's Geometry
     */
    Bounds getBounds() {
        Bounds b = getGeom().bounds
        new Bounds(b.minX, b.minY, b.maxX, b.maxY, schema.proj)
    }

    /**
     * Get a value by Field name.
     * <p><code>String name = feature.get("name")</code></p>
     * @param name The Field name
     * @return The attribute value
     */
    Object get(String name) {
        if (name != null && !schema.has(name) && name.length() >= 10) {
            name = name.substring(0,10)
        }
        Object obj = f.getAttribute(name)
        if (obj instanceof JtsGeometry) {
            return Geometry.wrap((JtsGeometry)obj)
        }
        else {
            return obj
        }
    }

    /**
     * Get a value by a Field.
     * <p><code>Field fld = feature.schema.get("name")</code></p>
     * <p><code>String name = feature.get(fld)</code></p>
     * @param name The Field name
     * @return The attribute value
     */
    Object get(Field field) {
        get(field.name)
    }

    /**
     * Get a value by Field name.  This method supports
     * a the following syntax:
     * <p><code>String name = feature["name"]</code></p>
     * @param name The Field name
     * @return The attribute value
     */
    Object getAt(String name) {
        get(name)
    }

    /**
     * Get a value by Field.  This method supports
     * a the following syntax:
     * <p><code>Field fld = feature.schema.get("name")</code></p>
     * <p><code>String name = feature[fld]</code></p>
     * @param name The Field
     * @return The attribute value
     */
    Object getAt(Field field) {
        get(field)
    }

    /**
     * Set a value.
     * <p><code>feature.set("name") = "lighthouse"</code></p>
     * @param name The Field name
     * @param value The new attribute value
     */
    void set(String name, Object value) {
        if (name != null && !schema.has(name) && name.length() >= 10) {
            name = name.substring(0,10)
        }
        if (name.equalsIgnoreCase(schema.geom.name)) {
            f.defaultGeometry = ((Geometry)value).g
        } else {
            f.setAttribute(name, value)
        }
        if (layer) {
            layer.queueModified(this, name)
        }
    }

    /**
     * Set a value.
     * <p><code>Field fld = feature.schema.get("name")</code></p>
     * <p><code>feature.set(fld) = "lighthouse"</code></p>
     * @param name The Field
     * @param value The new attribute value
     */
    void set(Field field, Object value) {
        set(field.name, value)
    }

    /**
     * Another way of setting a value. This method supports
     * the following syntax:
     * <p><code>feature["name"] = "lighthouse"</code></p>
     * @param name The Field name
     * @param value The new value
     */
    void putAt(String name, Object value) {
        set(name, value)
    }

    /**
     * Set a value
     * <p><code>Field fld = feature.schema.get("name")</code></p>
     * <p><code>feature[fld] = "lighthouse"</code></p>
     * @param name The Field
     * @param value The new attribute value
     */
    void putAt(Field field, Object value) {
        set(field, value)
    }

    /**
     * Set the values of this Feature based on values from another Feature
     * @param f The other Feature whose values we want
     */
    void set(Feature f) {
        f.attributes.each {k,v ->
            this.set(k,v)
        }
    }

    /**
     * Set the values of this Feature based on values from a Map
     * @param attributes A Map of values
     */
    void set(Map attributes) {
        attributes.each {k,v ->
            this.set(k,v)
        }
    }

    /**
     * Get a Map of all attributes
     * @return A Map of all attributes
     */
    Map getAttributes() {
        Map atts = [:]
        schema.fields.each{fld ->
            String name = fld.name
            Object value = f.getAttribute(name)
            atts[name] = (value instanceof JtsGeometry) ? Geometry.wrap((JtsGeometry) value) : value
        }
        atts
    }

    /**
     * The string representation
     * @return The string representation
     */
    String toString() {
        String atts = schema.fields.collect{fld -> "${fld.name}: ${get(fld.name)}"}.join(", ")
        String id = (id.startsWith(schema.name)) ? id : "${schema.name}.${id}"
        "${id} ${atts}"
    }

    /**
     * Get this Feature as a GeoJSON String
     * @return The GeoJSON String
     */
    String getGeoJSON() {
        def writer = new geoscript.feature.io.GeoJSONWriter()
        writer.write(this)
    }

    /**
     * Get this Feature as a GeoRSS String
     * @param options The named parameters
     * <ul>
     *      <li>feedType = The feed type (atom or rss)</li>
     *      <li>geometryType = The geometry type (simple, gml, w3c)</li>
     *      <li>includeAttributes = Whether to include all attributes</li>
     *      <li>attributeNamespace = The namespace for attributes (ogr=http://www.gdal/ogr/)</li>
     *      <li>itemTitle = The item title (Closure, Expression, or String)</li>
     *      <li>itemId = The item id (Closure, Expression, or String)</li>
     *      <li>itemDescription = The item description (Closure, Expression, or String)</li>
     *      <li>itemDate = The item date (Closure, Expression, or String)</li>
     *      <li>itemGeometry = The item geometry (Closure, Expression, or String)</li>
     * </ul>
     * @return A GeoRSS String
     */
    String getGeoRSS(Map options = [:]) {
        def writer = new geoscript.feature.io.GeoRSSWriter(options)
        writer.write(this)
    }

    /**
     * Get this Feature as a GML String
     * @param options The named parameters
     * <ul>
     *     <li>version = The version 2, 3, or 3.2</li>
     *     <li>format = Whether to pretty print or not</li>
     *     <li>bounds = Whether to include Feature Bounds or not</li>
     *     <li>xmldecl = Whether to include XML declaration or not</li>
     *     <li>nsprefix = The XML namespace prefix</li>
     * </ul>
     * @return A GML String
     */
    String getGml(Map options = [:]) {
        double version = options.get("version", 2)
        boolean format = options.get("format", true)
        boolean bounds = options.get("bounds", false)
        boolean xmldecl = options.get("xmldecl", false)
        String nsprefix = options.get("nsprefix", "gsf")
        def writer = new geoscript.feature.io.GmlWriter()
        writer.write(this, version, format, bounds, xmldecl, nsprefix)
    }

    /**
     * Get this Feature as a KML Placemark
     * @param options The named parameters
     * <ul>
     *     <li>format = Whether to format the KML or not (default = false)</li>
     *     <li>xmldecl = Whether to include the XML declaration (default = false)</li>
     * </ul>
     * @return A KML Placemark String
     */
    String getKml(Map options = [:]) {
        def writer = new geoscript.feature.io.KmlWriter()
        writer.write(options, this)
    }

    /**
     * Get this Feature as a GPX String
     * @param options The named parameters
     * <ul>
     *     <li>version = The GPX version (defaults to 1.1)</li>
     *     <li>includeAttributes = Whether to include attributes (defaults to false)</li>
     *     <li>attributeNamespace = The attribute namespace (prefix=url)</li>
     *     <li>elevation = The elevation filter, closure, or value</li>
     *     <li>time = The time elevation filter, closure, or value</li>
     *     <li>name = The name elevation filter, closure, or value</li>
     *     <li>description = The description elevation filter, closure, or value</li>
     *     <li>type = The type elevation filter, closure, or value</li>
     * </ul>
     * @return A KML Placemark String
     */
    String getGpx(Map options = [:]) {
        def writer = new geoscript.feature.io.GpxWriter(options)
        writer.write(this)
    }


    /**
     * Build a SimpleFeature using the Map of data, the ID, and the Schema
     */
    private static SimpleFeature buildFeature(Map attributes, String id, Schema schema) {
        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(schema.featureType)
        attributes.each{
            String name = it.key
            Object value = it.value
            // Shapefiles can only have field names of 10 characters
            // or less, so if the schema doesn't contain a Field by the
            // original name, try truncating it to 10 characters
            if (!schema.has(name) && name.length() >= 10) {
                name = name.substring(0,10)
            }
            if (schema.has(name)) {
                if (value instanceof Geometry) {
                    featureBuilder.set(name, value.g)
                }
                else {
                    featureBuilder.set(name, value);
                }
            }
            // Shapefiles always have a geometry field named the_geom,
            // so always set the Geometry using the schema's geom field name
            else if (value instanceof Geometry) {
                featureBuilder.set(schema.geom.name, value.g)
            }
        }
        featureBuilder.buildFeature(id)
    }

    /**
     * Build a SimpleFeature using the List of data, the ID, and the Schema
     */
    private static SimpleFeature buildFeature(List attributes, String id, Schema schema) {
        Map map = [:]
        schema.fields.eachWithIndex{fld, i ->
            map.put(fld.name, attributes[i])
        }
        buildFeature(map, id, schema)
    }

    /**
     * Build a SimpleFeature using the Map of data and the ID.  The Schema is inferred
     * from the values of the Map
     */
    private static SimpleFeature buildFeature(Map attributes, String id) {
        List<Field> fields = attributes.collect{at ->
            String name = at.key.toString()
            Object value = at.value
            String type = Schema.lookUpBinding(value.class.name)
            new Field(name, type)
        }
        Schema schema = new Schema("feature", fields)
        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(schema.featureType)
        attributes.each{at ->
            String name = at.key.toString()
            Object value = (at.value instanceof Geometry) ? ((Geometry)at.value).g : at.value
            featureBuilder.set(name, value)
        }
        featureBuilder.buildFeature(id)
    }

}
