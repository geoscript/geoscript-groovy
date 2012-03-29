package geoscript.workspace

import org.geotools.data.DataStore
import org.geotools.jdbc.VirtualTable
import geoscript.feature.Field
import geoscript.layer.Layer

/**
 * A Workspace that is a Database
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
     * Add a SQL Query as a Layer
     * @param name The new Layer's name
     * @param sql The SQL Query that creates the new Layer
     * @param geometryFld The Geometry Field
     * @param primaryKeyFields A List of primary key Fields or Field names
     */
    Layer addSqlQuery(String layerName, String sql, Field geometryFld, List primaryKeyFields = []) {
        addSqlQuery(layerName, sql, geometryFld.name, geometryFld.typ, geometryFld.proj.id.replaceAll("EPSG:","") as int, primaryKeyFields)
    }

    /**
     * Add a SQL Query as a Layer
     * @param name The new Layer's name
     * @param sql The SQL Query that creates the new Layer
     * @param geometryFieldName The Geometry Field name
     * @param geometryFieldType The Geometry Field type (Point, LineString, Polygon, ect...)
     * @param epsg The EPSG code (minus the EPSG: prefix)
     * @param primaryKeyFields A List of primary key Fields or Field names
     */
    Layer addSqlQuery(String layerName, String sql, String geometryFieldName, String geometryFieldType, int epsg, List primaryKeyFields = []) {
        VirtualTable vt = new VirtualTable(layerName, sql)
        vt.addGeometryMetadatata(geometryFieldName, getGeometryClass(geometryFieldType), epsg)
        if (primaryKeyFields.size() > 0) {
            vt.setPrimaryKeyColumns(primaryKeyFields[0] instanceof Field ? primaryKeyFields.collect{fld->fld.name} : primaryKeyFields)
        }
        ds.addVirtualTable(vt)
        super.get(layerName)
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

