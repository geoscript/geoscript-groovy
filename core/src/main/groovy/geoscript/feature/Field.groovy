package geoscript.feature

import geoscript.proj.Projection

/**
 * A Field is composed of a name and a type.  A Field with a {@link geoscript.geom.Geometry Geometry} type
 * can also contain a {@link geoscript.proj.Projection Projection}.
 * <p><blockquote><pre>
 * Field f1 = new Field("name","String")
 * Field f2 = new Field("geom","Point", "EPSG:2927")
 * </pre></blockquote></p>
 * @author Jared Erickson
 */
class Field {
    
    /**
     * The name
     */
    String name

    /**
     * The type
     */
    String typ

    /**
     * The Projection
     */
    Projection proj

    /**
     * Create a new Field with a name and type.
     * <p><code>
     * Field f = new Field("name","String")
     * </code></p>
     * @param name The name of the Field
     * @param type The type of the Field
     */
    Field(String name, String type){
        this.name = name
        this.typ = type
    }

    /**
     * Create a new Field with a name, type, and Projection.
     * <p><code>
     * Field f = new Field("geom","Point", "EPSG:2927")
     * </code></p>
     * @param name The name of the Field
     * @param type The type of the Field
     * @param proj The Projection can either be a Projection object or a String
     */
    Field(String name, String type, def proj){
        this.name = name
        this.typ = type
        if (proj != null) this.proj = new Projection(proj)
    }

    /**
     * Create a Field from a List of parts.  The first item is the name.  The
     * second part of the type. The optional third item is the Projection.
     * <p><code>
     * Field f = new Field(["geom","Point", "EPSG:2927"])
     * </code></p>
     * @param parts The List of parts
     */
    Field(List parts) {
        this.name = parts[0]
        this.typ = parts[1]
        if (parts.size() > 2) {
            this.proj = new Projection(parts[2])
        }
    }

    /**
     * Create a Field from a Map. The keys are name, type, proj.
     * <p><code>
     * Field f = new Field(["name": "geom", "type": "Point", "proj": new Projection("EPSG:2927")])
     * </code></p>
     * @param parts A Map with name, type, and optionally proj keys.
     */
    Field(Map parts) {
        this.name = parts['name']
        this.typ = parts['type']
        if (parts.containsKey('proj')) {
            this.proj = new Projection(parts['proj'])
        }
    }

    /**
     * Create a new Field based on an existing Field
     * @param fld The existing Field
     */
    Field(Field fld) {
        this(fld.name, fld.typ, fld.proj)
    }

    /**
     * Is the Field spatial?
     * @return Is the Field spatial?
     */
    boolean isGeometry() {
        List geometryNames = [
                "point","linestring","polygon","linearring","geometry","geometrycollection",
                "circularstring","circularring","compoundring","compoundcurve"
        ]
        geometryNames.any{geomName -> typ.toLowerCase().endsWith(geomName)}
    }

    /**
     * The string representation
     * @return The string representation
     */
    @Override
    String toString() {
        "${name}: ${typ}${(proj!=null?'(' + proj + ')' : '')}".toString()
    }

    @Override
    boolean equals(Object o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false
        Field field = (Field) o
        if (name != field.name) return false
        if (proj != field.proj) return false
        if (typ != field.typ) return false
        return true
    }

    @Override
    int hashCode() {
        int result
        result = name.hashCode()
        result = 31 * result + typ.hashCode()
        result = 31 * result + (proj != null ? proj.hashCode() : 0)
        return result
    }
}
