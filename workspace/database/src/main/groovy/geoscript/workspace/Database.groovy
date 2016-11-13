package geoscript.workspace

import geoscript.feature.Field
import geoscript.layer.Layer
import groovy.sql.Sql
import org.geotools.data.DataStore
import org.geotools.jdbc.JDBCDataStore
import org.geotools.jdbc.VirtualTable
import org.geotools.jdbc.VirtualTableParameter
import javax.sql.DataSource

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
     * Get the javax.sql.DataSource
     * @return The javax.sql.DataSource
     */
    DataSource getDataSource() {
        (ds as JDBCDataStore).dataSource
    }

    /**
     * Get a groovy.sql.Sql object that provides direct access
     * to the underlying database
     * @return A groovy.sql.Sql object
     */
    Sql getSql() {
        new Sql((ds as JDBCDataStore).dataSource)
    }

    /**
     * Remove the layer from the database
     * @param layerName The layer name
     */
    void remove(String layerName) {
        (ds as JDBCDataStore).removeSchema(layerName)
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
     * Create an index
     * @param layerName The layer or table name
     * @param indexName The index name
     * @param fieldName The field name
     * @param unique Whether the index is unique or not
     */
    void createIndex(String layerName, String indexName, String fieldName, boolean unique) {
        createIndex(layerName, indexName, [fieldName], unique)
    }

    /**
     * Create an index
     * @param layerName The layer or table name
     * @param indexName The index name
     * @param fieldNames A List of field names
     * @param unique Whether the index is unique or not
     */
    void createIndex(String layerName, String indexName, List<String> fieldNames, boolean unique) {
        (ds as JDBCDataStore).createIndex(new org.geotools.jdbc.Index(layerName, indexName, unique, fieldNames as String[]))
    }

    /**
     * Delete an index
     * @param layerName The layer or table name
     * @param indexName The index name
     */
    void deleteIndex(String layerName, String indexName) {
        (ds as JDBCDataStore).dropIndex(layerName, indexName)
    }

    /**
     * Get a List of indexes for a layer or table by name
     * @param layerName The layer or table name
     * @return A List of indexes
     */
    List getIndexes(String layerName) {
        (ds as JDBCDataStore).getIndexes(layerName).collect{ [
            name: it.indexName,
            unique: it.unique,
            attributes: it.attributes
        ]}
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

