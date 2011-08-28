package geoscript.feature

import org.opengis.feature.simple.SimpleFeature
import org.geotools.feature.simple.SimpleFeatureBuilder
import com.vividsolutions.jts.geom.Geometry as JtsGeometry
import geoscript.geom.*
import geoscript.layer.Layer

/**
 * A Feature contains a set of named attributes with values.
 * <p>A Feature is created from a Map of name value pairs and an identifier.</p>
 * <p><i>Without a Schema (see below) the data types are inferred).</i></p>
 * <code>
 * Feature f = new Feature(['name': 'anvil', 'price': 100.0], 'widgets.1')
 * </code>
 * <p>A Feature can also be created from a list of values, an identifier, and a {@link Schema}</p>
 * <code>
 * Schema s = new Schema('widgets', [['name','string'],['price','float']])
 * Feature f = new Feature(['anvil', 100.0], '1', s)
 * </code>
 * <p>A Feature can also be created from a Map of name value pairs, an identifier, and a {@link Schema}</p>
 * <code>
 * Schema s = new Schema('widgets', [['name','string'],['price','float']])
 * Feature f = new Feature(['name': 'anvil', 'price': 100.0], '1', s)
 * </code>
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
        new Bounds(b.west, b.south, b.east, b.north, schema.proj)
    }

    /**
     * Get a value by Field name
     * @param name The Field name
     * @return The attribute value
     */
    Object get(String name) {
        Field fld = schema.field(name)
        Object obj = f.getAttribute(name)
        if (obj instanceof JtsGeometry) {
            return Geometry.wrap((JtsGeometry)obj)
        }
        else {
            return obj
        }
    }

    /**
     * Get a value by Field name.  This method supports
     * a the following syntax:
     * <p><code>feature["name"]</code></p>
     * @param name The Field name
     * @return The Field value
     */
    Object getAt(String name) {
        get(name)
    }

    /**
     * Set a value for a Field
     * @param name The Field name
     * @param value The new attribute value
     */
    void set(String name, Object value) {
        Field fld = schema.field(name)
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
     * Build a SimpleFeature using the Map of data, the ID, and the Schema
     */
    private static SimpleFeature buildFeature(Map attributes, String id, Schema schema) {
        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(schema.featureType)
        attributes.each{
            if (it.value instanceof Geometry) {
                featureBuilder.set(it.key, it.value.g)
            }
            else {
                featureBuilder.set(it.key, it.value);
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
