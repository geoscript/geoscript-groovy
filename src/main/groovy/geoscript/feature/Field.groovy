package geoscript.feature

import geoscript.proj.Projection

/**
 * A Field is composed of a name and a type
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
     * Create a new Field with a name and type
     */
    Field(String name, String type){
        this.name = name
        this.typ = type
    }

    /**
     * Create a new Field with a name, type, and Projection
     */
    Field(String name, String type, def proj){
        this.name = name
        this.typ = type
        if (proj != null) this.proj = new Projection(proj)
    }

    /**
     * Create a Field from a List of parts.  The first item is the name.  The
     * second part of the type. The optional third item is the Projection
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
     */
    Field(Map parts) {
        this.name = parts['name']
        this.typ = parts['type']
        if (parts.containsKey('proj')) {
            this.proj = new Projection(parts['proj'])
        }
    }

    /**
     * Is the Field spatial?
     */
    boolean isGeometry() {
	List geometryNames = ["point","linestring","polygon","linearring"]
        geometryNames.any{geomName -> typ.toLowerCase().endsWith(geomName)}
    }

    /**
     * The string representation
     */
    String toString() {
        "${name}: ${typ}${(proj!=null?'(' + proj + ')' : '')}".toString()
    }

}
