package geoscript.workspace

import org.geotools.data.DataStore
import org.geotools.jdbc.JDBCDataStore
import org.geotools.jdbc.VirtualTable
import geoscript.feature.Field
import geoscript.layer.Layer
import org.geotools.jdbc.VirtualTableParameter

/**
 * A Workspace that is a Database.
 * <p>A Database subclass can add a SQL query as a Layer:</p>
 * <p><blockquote><pre>
 * Database db = new H2("acme", "target/h2")
 * Layer statesLayer = h2.add(shp, 'states')
 * String sql = """select st_centroid("the_geom") as "the_geom", "STATE_NAME" FROM "states""""
 * Layer statesCentroidLayer = h2.createView("states_centroids", sql, new Field("the_geom", "Point", "EPSG:4326"))
 * </pre></blockquote></p>
 * @author Jared Erickson
 */
class Database extends Workspace {

    /**
     * Create a new Database wrapping a GeoTools JDBCDataStore
     * @param The GeoTools DataStore
     */
    Database(DataStore ds) {
        super(ds)
    }

    /**
     * Create a Layer from a SQL View
     * @param options The named parameters
     * <ul>
     *     <li>params = The query parameters</li>
     *     <li>primaryKeyFields = The list of primary key fields</li>
     * </ul>
     * @param layerName The layer name
     * @param sql The SQL
     * @param geometryField The geometry Field
     * @return The new Layer
     */
    Layer createView(Map options = [:], String layerName, String sql, Field geometryField) {
        addVirtualTable(options, layerName, sql, geometryField)
        get(layerName)
    }

    /**
     * Delete a SQL View Layer
     * @param name The name of the SQL View Layer
     */
    void deleteView(String name) {
        (ds as JDBCDataStore).removeVirtualTable(name)
    }

    /**
     * Add a SQL Query as a Layer.  Deprecated, please use createView instead.
     * @param name The new Layer's name
     * @param sql The SQL Query that creates the new Layer
     * @param geometryFld The Geometry Field
     * @param primaryKeyFields A List of primary key Fields or Field names
     */
    @Deprecated
    Layer addSqlQuery(String layerName, String sql, Field geometryFld, List primaryKeyFields = []) {
        createView([
            primaryKeyFields: primaryKeyFields
        ], layerName, sql, geometryFld)
    }

    /**
     * Add a SQL Query as a Layer. Deprecated, please use createView instead.
     * @param name The new Layer's name
     * @param sql The SQL Query that creates the new Layer
     * @param geometryFieldName The Geometry Field name
     * @param geometryFieldType The Geometry Field type (Point, LineString, Polygon, ect...)
     * @param epsg The EPSG code (minus the EPSG: prefix)
     * @param primaryKeyFields A List of primary key Fields or Field names
     */
    @Deprecated
    Layer addSqlQuery(String layerName, String sql, String geometryFieldName, String geometryFieldType, int epsg, List primaryKeyFields = []) {
        createView([
                primaryKeyFields: primaryKeyFields
        ], layerName, sql, new Field(geometryFieldName, geometryFieldType, "EPSG:${epsg}"))
    }

    /**
     * Create and add a virtual table to the JDBC based DataStore
     * @param options The named parameters
     * <ul>
     *     <li>params = A Map of query parameters</li>
     *     <li>primaryKeyFields = A List of primary key fields</li>
     * </ul>
     * @param layerName The layer name
     * @param sql The SQL
     * @param geometryField The geometry Field
     */
    private void addVirtualTable(Map options = [:], String layerName, String sql, Field geometryField) {
        // Named parameters
        def params = options.get("params")
        List primaryKeyFields = options.get("primaryKeyFields")
        // Build the VirtualTable
        VirtualTable vt = new VirtualTable(layerName, sql)
        if (params) {
            if (!(params instanceof List)) {
                params = [params]
            }
            params.each {p ->
                if (p instanceof List) {
                    vt.addParameter(new VirtualTableParameter(p[0], p[1]))
                } else {
                    vt.addParameter(new VirtualTableParameter(p))
                }
            }
        }
        if (geometryField != null) {
            vt.addGeometryMetadatata(geometryField.name, getGeometryClass(geometryField.typ), geometryField.proj.id.replaceAll("EPSG:","") as int)
        }
        if (primaryKeyFields != null && primaryKeyFields.size() > 0) {
            vt.setPrimaryKeyColumns(primaryKeyFields[0] instanceof Field ? primaryKeyFields.collect{fld->fld.name} : primaryKeyFields)
        }
        (ds as JDBCDataStore).addVirtualTable(vt)
    }

    /**
     * Get the JTS Geometry Class from a String (Point, MultiPoint,
     * LineString, MultiLineString, LinearRing, Polygon, MultiPolygon,
     * and GeometryCollection are all valid).
     * @param geometry The geometry type
     * @return The JTS Geometry Class
     */
    private Class getGeometryClass(String geometry) {
        if (geometry.equalsIgnoreCase("point")) {
            return com.vividsolutions.jts.geom.Point.class
        } else if (geometry.equalsIgnoreCase("multipoint")) {
            return com.vividsolutions.jts.geom.MultiPoint.class
        } else if (geometry.equalsIgnoreCase("linestring")) {
            return com.vividsolutions.jts.geom.LineString.class
        } else if (geometry.equalsIgnoreCase("multilinestring")) {
            return com.vividsolutions.jts.geom.MultiLineString.class
        } else if (geometry.equalsIgnoreCase("linearring")) {
            return com.vividsolutions.jts.geom.LinearRing.class
        } else if (geometry.equalsIgnoreCase("polygon")) {
            return com.vividsolutions.jts.geom.Polygon.class
        } else if (geometry.equalsIgnoreCase("multipolygon")) {
            return com.vividsolutions.jts.geom.MultiPolygon.class
        } else if (geometry.equalsIgnoreCase("geometrycollection")) {
            return com.vividsolutions.jts.geom.GeometryCollection.class
        } else {
            return com.vividsolutions.jts.geom.Geometry.class
        }
    }
}

