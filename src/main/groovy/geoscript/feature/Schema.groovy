package geoscript.feature

import org.opengis.feature.simple.SimpleFeatureType
import org.opengis.feature.type.AttributeDescriptor
import org.opengis.feature.type.GeometryDescriptor
import org.geotools.feature.NameImpl
import org.geotools.feature.simple.SimpleFeatureTypeBuilder
import org.geotools.data.DataUtilities
import com.vividsolutions.jts.geom.Geometry as JtsGeometry
import geoscript.geom.*
import geoscript.proj.Projection

/**
 * A Schema describes the structure of a Feature.  It is contains a name, and
 * a set of Fields.
 * <p> You can create a Schema from a name and a List of Fields</p>
 * <code>
 * Schema s1 = new Schema("widgets", [new Field("geom","Point"), new Field("name","string"), new Field("price","float")])
 * </code>
 * <p> You can create a Schema from a name and a List of Lists which contain a name and type</p>
 * <code>
 * Schema s2 = new Schema("widgets", [["geom","Point"], ["name","string"], ["price","float"]])
 * </code>
 * <p> You can create a Schema from a name and a List of Maps containing name, type, and optionally proj keys</p>
 * <code>
 * Schema s3 = new Schema("widgets", [[name: "geom",type: "Point"], [name: "name", type: "string"], [name: "price", type: "float"]])
 * </code>
 * <p> You can create a Schema from a name and a type spec:</p>
 * <code>
 * Schema s4 = new Schema("widgets","geom:Point:srid=4326,name:String,price:float")
 * </code>
 * @author Jared Erickson
 */
class Schema {

    /**
     * The wrapped GeoTools SimpleFeatureType
     */
    SimpleFeatureType featureType

    /**
     * Create a new Schema wrapping a GeoTools SimpleFeatureType
     * @param The wrapped GeoTools SimpleFeatureType
     */
    Schema(SimpleFeatureType featureType) {
        this.featureType = featureType
    }

    /**
     * Create a new Schema with a name and a String containing a comma delimited
     * list of fields.
     * <p><code>
     * Schema s = new Schema("widgets","geom:Point:srid=4326,name:String,price:float")
     * </code></p>
     * @param name The Schema name
     * @param typeSpec The Schema String.
     */
    Schema(String name, String typeSpec) {
        this(DataUtilities.createType(name, typeSpec))
    }

    /**
     * Create a new Schema with a name and a List of Fields.
     * <p><code>
     * Schema s1 = new Schema("widgets", [new Field("geom","Point"), new Field("name","string"), new Field("price","float")])
     * </code></p>
     * <p><code>
     * Schema s2 = new Schema("widgets", [["geom","Point"], ["name","string"], ["price","float"]])
     * </code></p>
     * <p><code>
     * Schema s3 = new Schema("widgets", [[name: "geom",type: "Point"], [name: "name", type: "string"], [name: "price", type: "float"]])
     * </code></p>
     * @param name The Schema's name
     * @param fields A List of Fields, a List of Lists, or a List of Maps
     */
    Schema(String name, def fields) {
        this(buildFeatureType(name, fields))
    }

    /**
     * Get the Schema's name
     * @return The of the Schema
     */
    String getName() {
        featureType.name.localPart
    }

    /**
     * Get the Schema's geometry Field
     * @return The Schema's Geometry Field
     */
    Field getGeom() {
        field(featureType.geometryDescriptor.localName)
    }

    /**
     * Get a Field by name
     * @param name The name of the Field
     * @return The Field
     */
    Field field(String name) {
        AttributeDescriptor ad = featureType.getDescriptor(name)
        if (ad != null) {
            if (ad instanceof GeometryDescriptor) {
                GeometryDescriptor gd = (GeometryDescriptor) ad
                Projection p = null
                if (gd.coordinateReferenceSystem != null) {
                    p = new Projection(gd.coordinateReferenceSystem)
                }
                else if (featureType.coordinateReferenceSystem != null) {
                    p = new Projection(featureType.coordinateReferenceSystem)
                }
                Field fld = new Field(gd.localName, Schema.lookUpAlias(gd.type.binding.name), p)
                return fld
            }
            else {
                return new Field(ad.localName, Schema.lookUpAlias(ad.type.binding.name))
            }
        }
        throw new Exception("No such field ${name}".toString())
    }

    /**
     * Get a Field by name
     * @param The name of the Field
     * @return The Field
     */
    Field get(String name) {
        field(name)
    }

    /**
     * Get the List of Fields
     * @return The List of Fields
     */
    List<Field> getFields() {
        List<Field> fields = []
        Iterator<AttributeDescriptor> it = featureType.attributeDescriptors.iterator()
        while(it.hasNext()) {
            AttributeDescriptor ad = it.next()
            fields.add(field(ad.localName))
        }
        return fields
    }

    /**
     * Create a Feature from this Schema with a List of values and the id.
     * @param The List of values
     * @param id The Feature ID (defaults to feature)
     * @return A new Feature
     */
    Feature feature(List values, String id = "feature") {
        new Feature(values, id, this)
    }

    /**
     * Create a Feature from this Schema with a Map of values and the id.
     * @param The Map of key value pairs
     * @param id The Feature ID (defaults to feature)
     * @return A new Feature
     */
    Feature feature(Map values, String id = "feature") {
        new Feature(values, id, this)
    }

    /**
     * Reproject the Schema.
     * @param prj A Projection or a String
     * @return The reprojected Schema
     */
    Schema reproject(def prj) {
        reproject(prj, name)
    }

    /**
     * Reproject the schema with a new name
     * @param prj A Projection or a String
     * @param name The new name of the reprojected Schema
     * @return The reprojected Schema
     */
    Schema reproject(def prj, String name) {
        Projection proj = new Projection(prj)
        Field geom = geom
        List<Field> flds = fields.collect{
            fld -> (fld.name == geom.name) ? new Field(fld.name, fld.typ, proj) : new Field(fld.name, fld.typ)
        }
        new Schema(name, flds)
    }

    /**
     * Create a new Schema with the same fields as this Schema but with a different
     * Geometry type
     * @param geometryType The new type of Geometry
     * @param name The new Schema name
     */
    Schema changeGeometryType(String geometryType, String name) {
        List flds = []
        fields.each{fld ->
            if (fld.isGeometry()) {
                flds.add(new Field(fld.name, geometryType, fld.proj))
            }
            else {
                flds.add(new Field(fld.name, fld.typ))
            }
        }
        new Schema(name, flds)
    }

    /**
     * The string reprentation
     * @return The string representation
     */
    String toString() {
        "${name} ${fields.join(', ')}"
    }

    /**
     * Look up the alias of a GeoTools binding.
     * For example: "com.vividsolutions.jts.geom.LinearRing" => "LinearRing"
     * @param The Class name
     * @return The alias
     */
    static String lookUpAlias(String binding) {
        Map map = [
            ("com.vividsolutions.jts.geom.LinearRing".toLowerCase()) : "LinearRing",
            ("com.vividsolutions.jts.geom.LineString".toLowerCase()) : "LineString",
            ("com.vividsolutions.jts.geom.MultiLineString".toLowerCase()) : "MultiLineString",
            ("com.vividsolutions.jts.geom.MultiPoint".toLowerCase()) : "MultiPoint",
            ("com.vividsolutions.jts.geom.MultiPolygon".toLowerCase()) : "MultiPolygon",
            ("com.vividsolutions.jts.geom.Point".toLowerCase()) : "Point",
            ("com.vividsolutions.jts.geom.Polygon".toLowerCase()) : "Polygon",
            ("com.vividsolutions.jts.geom.Geometry".toLowerCase()) : "Geometry",
            ("java.lang.String".toLowerCase()) : "String",
            ("java.lang.Float".toLowerCase()) : "Float",
            ("java.lang.Integer".toLowerCase()) : "Integer",
            ("java.lang.Long".toLowerCase()) : "Long",
            ("java.lang.Double".toLowerCase()) : "Double"
        ]
        map.get(binding.toLowerCase(), binding)
    }

    /**
     * Look up a GeoTools binding for the alias.
     * For example: "geoscript.geom.LinearRing" => "com.vividsolutions.jts.geom.LinearRing"
     * @param alias The alias
     * @return The GeoTools binding class name
     */
    static lookUpBinding(String alias) {
        Map map = [
            ("geoscript.geom.LinearRing".toLowerCase()) : "com.vividsolutions.jts.geom.LinearRing",
            ("LinearRing".toLowerCase()) : "com.vividsolutions.jts.geom.LinearRing",
            ("geoscript.geom.LineString".toLowerCase()) : "com.vividsolutions.jts.geom.LineString",
            ("LineString".toLowerCase()) : "com.vividsolutions.jts.geom.LineString",
            ("geoscript.geom.MultiLineString".toLowerCase()) : "com.vividsolutions.jts.geom.MultiLineString",
            ("MultiLineString".toLowerCase()) : "com.vividsolutions.jts.geom.MultiLineString",
            ("geoscript.geom.MultiPoint".toLowerCase()) : "com.vividsolutions.jts.geom.MultiPoint",
            ("MultiPoint".toLowerCase()) : "com.vividsolutions.jts.geom.MultiPoint",
            ("geoscript.geom.MultiPolygon".toLowerCase()) : "com.vividsolutions.jts.geom.MultiPolygon",
            ("MultiPolygon".toLowerCase()) : "com.vividsolutions.jts.geom.MultiPolygon",
            ("geoscript.geom.Point".toLowerCase()) : "com.vividsolutions.jts.geom.Point",
            ("Point".toLowerCase()) : "com.vividsolutions.jts.geom.Point",
            ("geoscript.geom.Polygon".toLowerCase()) : "com.vividsolutions.jts.geom.Polygon",
            ("Polygon".toLowerCase()) : "com.vividsolutions.jts.geom.Polygon",
            ("geoscript.geom.Geometry".toLowerCase()) : "com.vividsolutions.jts.geom.Geometry",
            ("Geometry".toLowerCase()) : "com.vividsolutions.jts.geom.Geometry",
            ("String".toLowerCase()) : "java.lang.String",
            ("Str".toLowerCase()) : "java.lang.String",
            ("Float".toLowerCase()) : "java.lang.Float",
            ("Int".toLowerCase()) : "java.lang.Integer",
            ("Integer".toLowerCase()) : "java.lang.Integer",
            ("Short".toLowerCase()) : "java.lang.Integer",
            ("Long".toLowerCase()) : "java.lang.Long",
            ("Double".toLowerCase()) : "java.lang.Double"
        ]
        map.get(alias.toLowerCase(), alias)
    }

    /**
     * Build a SimpleFeatureType from the name and a List of Fields
     */
    private static SimpleFeatureType buildFeatureType(String name, def fields) {
        SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder()
        builder.setName(new NameImpl(name))
        fields.each{field ->
            if (!(field instanceof Field)) {
                field = new Field(field)
            }
            Class c = Class.forName(lookUpBinding(field.typ))
            if (field.proj != null && field.proj.crs != null) {
                builder.setCRS(field.proj.crs)
                builder.add(field.name, c, field.proj.crs)
            }
            else {
                builder.add(field.name, c)
            }
        }
        builder.buildFeatureType()
    }
}