package geoscript.workspace

import com.vividsolutions.jts.geom.Envelope
import com.vividsolutions.jts.geom.Geometry
import com.vividsolutions.jts.geom.GeometryCollection
import com.vividsolutions.jts.geom.GeometryFactory
import com.vividsolutions.jts.geom.LineString
import com.vividsolutions.jts.geom.MultiLineString
import com.vividsolutions.jts.geom.MultiPoint
import com.vividsolutions.jts.geom.MultiPolygon
import com.vividsolutions.jts.geom.Point
import com.vividsolutions.jts.geom.Polygon
import com.vividsolutions.jts.io.WKBReader
import com.vividsolutions.jts.io.WKBWriter
import com.vividsolutions.jts.io.WKTReader
import com.vividsolutions.jts.io.WKTWriter
import geoscript.proj.Projection
import groovy.sql.Sql
import org.apache.commons.dbcp.BasicDataSource
import org.geotools.data.DataAccessFactory
import org.geotools.data.DataStore
import org.geotools.factory.Hints
import org.geotools.geometry.jts.Geometries
import org.geotools.jdbc.JDBCDataStore
import org.geotools.jdbc.JDBCDataStoreFactory
import org.geotools.jdbc.PreparedStatementSQLDialect
import org.geotools.jdbc.SQLDialect
import org.geotools.referencing.CRS
import org.opengis.feature.simple.SimpleFeatureType
import org.opengis.feature.type.GeometryDescriptor
import org.opengis.referencing.FactoryException
import org.opengis.referencing.crs.CoordinateReferenceSystem

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Types

/**
 * A Sqlite Workspace based on the GDAL Sqlite format.
 * @author Jared Erickson
 */
class Sqlite extends Database {

    /**
     * Create a new Sqlite Workspace form a File
     * @param file The File
     */
    Sqlite(File file) {
        super(createDataStore(file, "WKB"))
    }

    /**
     * Create a new Sqlite Workspace form a File and a Geometry Format (WKB or WKT)
     * @param file The File
     * @param geometryFormat The geometry encoding format (WKB or WKT)
     */
    Sqlite(File file, String format) {
        super(createDataStore(file, format))
    }

    /**
     * Create a new Sqlite Workspace from a GeoTools JDBCDataStore
     * @param ds The GeoTools JDBCDataStore
     */
    Sqlite(JDBCDataStore ds) {
        super(ds)
    }

    /**
     * Get the format
     * @return The workspace format name
     */
    @Override
    String getFormat() {
        return "Sqlite"
    }

    /**
     * Create a new Sqlite Workspace with a name and directory
     */
    private static DataStore createDataStore(File file, String geometryFormat) {
        Map params = new java.util.HashMap()
        params.put("database", file.absolutePath)
        params.put("dbtype", "sqlite")
        SqliteDataStoreFactory factory = new SqliteDataStoreFactory(GeometryFormat.valueOf(geometryFormat))
        factory.createDataStore(params)
    }

    /**
     * The Sqlite WorkspaceFactory
     */
    static class Factory extends WorkspaceFactory<Sqlite> {

        @Override
        Map getParametersFromString(String str) {
            Map params = [:]
            if (!str.contains("=") && str.endsWith(".sqlite")) {
                params.put("dbtype", "sqlite")
                params.put("database", new File(str).absolutePath)
            } else {
                params = super.getParametersFromString(str)
            }
            params
        }

        @Override
        Sqlite create(String type, Map params) {
            if (type.equalsIgnoreCase('sqlite')) {
                params['dbtype'] = 'sqlite'
                if (params.containsKey('file')) {
                    params['database'] = params['file']
                }
                super.create(params)
            } else {
                null
            }
        }

        @Override
        Sqlite create(DataStore dataStore) {
            Sqlite sqlite = null
            if (dataStore instanceof org.geotools.jdbc.JDBCDataStore) {
                def jdbcds = dataStore as org.geotools.jdbc.JDBCDataStore
                if (jdbcds.dataStoreFactory instanceof SqliteDataStoreFactory) {
                    sqlite = new Sqlite(dataStore)
                }
            }
            sqlite
        }
    }

    final static class SqliteDataStoreFactory extends JDBCDataStoreFactory {

        static final DataAccessFactory.Param DBTYPE = new DataAccessFactory.Param("dbtype", String.class, "Type", true, "sqlite")

        static final DataAccessFactory.Param DATABASE = new DataAccessFactory.Param("database", File.class, "Database", true)

        private final GeometryFormat geometryFormat

        SqliteDataStoreFactory() {
            this(GeometryFormat.WKB)
        }

        SqliteDataStoreFactory(GeometryFormat geometryFormat) {
            this.geometryFormat = geometryFormat
        }

        @Override
        protected String getDatabaseID() {
            "sqlite"
        }

        @Override
        protected String getDriverClassName() {
            "org.sqlite.JDBC"
        }

        @Override
        protected SQLDialect createSQLDialect(JDBCDataStore jdbcDataStore) {
            new SqliteDialect(jdbcDataStore, geometryFormat)
        }

        @Override
        protected String getValidationQuery() {
            "SELECT 1"
        }

        @Override
        String getDescription() {
            "SQLite"
        }

        @Override
        protected String getJDBCUrl(Map params) throws IOException {
            File db = (File) DATABASE.lookUp(params)
            if (db.getPath().startsWith("file:")) {
                db = new File(db.getPath().substring(5))
            }
            "jdbc:sqlite:${db}"
        }

        @Override
        protected void setupParameters(Map parameters) {
            super.setupParameters(parameters);
            parameters.remove(HOST.key)
            parameters.remove(PORT.key)
            parameters.remove(SCHEMA.key)
            parameters.remove(PASSWD.key)
            parameters.remove(USER.key)
            parameters.put(DATABASE.key, DATABASE)
            parameters.put(DBTYPE.key, DBTYPE)
        }

        @Override
        BasicDataSource createDataSource(Map params) throws IOException {
            BasicDataSource dataSource = new BasicDataSource()
            dataSource.setDriverClassName(getDriverClassName())
            dataSource.setUrl(getJDBCUrl(params))
            dataSource.setAccessToUnderlyingConnectionAllowed(true)
            dataSource
        }

        @Override
        protected JDBCDataStore createDataStoreInternal(JDBCDataStore dataStore, Map params) throws IOException {
            dataStore.setDatabaseSchema(null)
            dataStore
        }

    }

    final static class SqliteDialect extends PreparedStatementSQLDialect {

        private final GeometryFormat geometryFormat

        SqliteDialect(JDBCDataStore dataStore, GeometryFormat geometryFormat) {
            super(dataStore)
            this.geometryFormat = geometryFormat
        }

        protected void createTables(Connection connection) {
            Sql sql = new Sql(connection)
            sql.execute 'create table if not exists geometry_columns (f_table_name VARCHAR, f_geometry_column VARCHAR, geometry_type INT, coord_dimension INT, srid INT, geometry_format VARCHAR)'
            sql.execute 'create table if not exists spatial_ref_sys (srid INTEGER, auth_name TEXT, auth_srid TEXT, srtext TEST)'
        }

        protected void addGeometryColumn(Connection connection, String table, String geometryColumn, GeometryType geometryType, int coordinateDimension, int srid, GeometryFormat geometryFormat) {
            Sql sql = new Sql(connection)
            sql.execute 'INSERT INTO geometry_columns (f_table_name, f_geometry_column, geometry_type, coord_dimension, srid, geometry_format) VALUES (?,?,?,?,?,?)', [
                    table, geometryColumn, geometryType.code, coordinateDimension, srid, geometryFormat.toString()
            ]
        }

        protected void addSpatialReferenceSystem(Connection connection, int srid, String authName, int authSrid, String srText) {
            Sql sql = new Sql(connection)
            List srsRows = sql.rows("select srid from spatial_ref_sys WHERE srid = ?", [srid])
            if (srsRows.size() == 0) {
                sql.execute 'INSERT INTO spatial_ref_sys (srid, auth_name, auth_srid, srtext) VALUES (?,?,?,?)', [
                        srid, authName, authSrid, srText
                ]
            }
        }

        protected void deleteGeometryColumn(Connection connection, String table) {
            Sql sql = new Sql(connection)
            sql.execute 'DELETE FROM geometry_columns WHERE f_table_name = ?', [table]
        }

        protected GeometryFormat getGeometryFormat(Connection connection, String table) {
            Sql sql = new Sql(connection)
            List rows = sql.rows("select geometry_format from geometry_columns where f_table_name = ?", [table])
            if (!rows.isEmpty()) {
                GeometryFormat.valueOf(rows[0].get("geometry_format").toString().toUpperCase())
            } else {
                null
            }
        }

        protected GeometryType getGeometryType(Connection connection, String table, String geometryColumn) {
            Sql sql = new Sql(connection)
            List rows = sql.rows("select geometry_type from geometry_columns where f_table_name = ? and f_geometry_column = ?", [table, geometryColumn])
            if (!rows.isEmpty()) {
                GeometryType.get(rows[0].get("geometry_type") as int)
            } else {
                null
            }
        }

        protected Geometry readGeometry(byte[] bytes, GeometryFactory geometryFactory, GeometryFormat geometryFormat) {
            if (geometryFormat == GeometryFormat.WKB) {
                WKBReader reader = new WKBReader(geometryFactory)
                reader.read(bytes)
            } else /*(geometryFormat == GeometryFormat.WKT)*/ {
                WKTReader reader = new WKTReader(geometryFactory)
                reader.read(new String(bytes))
            }
        }

        protected byte[] writeGeometry(Geometry geometry, GeometryFormat geometryFormat) {
            if (geometryFormat == GeometryFormat.WKB) {
                WKBWriter writer = new WKBWriter()
                writer.write(geometry)
            } else /*(geometryFormat == GeometryFormat.WKT)*/ {
                WKTWriter writer = new WKTWriter()
                writer.write(geometry).bytes
            }
        }

        protected int getSrid(CoordinateReferenceSystem crs) {
            int srid = -1
            if (crs != null) {
                try {
                    srid = CRS.lookupEpsgCode(crs, true)
                } catch (FactoryException e) {
                    e.printStackTrace()
                }
            }
            srid
        }

        @Override
        void initializeConnection(Connection cx) throws SQLException {
            createTables(cx)
        }

        @Override
        boolean includeTable(String schemaName, String tableName, Connection cx) throws SQLException {
            Sql sql = new Sql(cx)
            List results = sql.rows("select * from geometry_columns where f_table_name = ?", [tableName])
            !results.isEmpty()
        }

        @Override
        void encodePrimaryKey(String column, StringBuffer sql) {
            super.encodePrimaryKey(column, sql)
            sql.append(" AUTOINCREMENT")
        }

        @Override
        void encodeGeometryEnvelope(String tableName, String geometryColumn, StringBuffer sql) {
            encodeColumnName(null, geometryColumn, sql);
        }

        @Override
        Envelope decodeGeometryEnvelope(ResultSet rs, int column, Connection cx) throws SQLException, IOException {
            GeometryFormat geometryFormat = getGeometryFormat(cx, rs.metaData.getTableName(1))
            Geometry g = readGeometry(rs.getBytes(column), new GeometryFactory(), geometryFormat)
            g != null ? g.getEnvelopeInternal() : null
        }
        
        @Override
        Geometry decodeGeometryValue(GeometryDescriptor descriptor, ResultSet rs, String column, GeometryFactory factory, Connection cx, Hints hints) throws IOException, SQLException {
            GeometryFormat geometryFormat = getGeometryFormat(cx, rs.metaData.getTableName(1))
            return readGeometry(rs.getBytes(column), factory, geometryFormat)
        }

        @Override
        void setGeometryValue(Geometry g, int dimension, int srid, Class binding, PreparedStatement ps, int column) throws SQLException {
            if (g == null || g.isEmpty()) {
                ps.setNull(column, Types.BLOB)
            }
            else {
                g.setSRID(srid)
                try {
                    byte[] bytes = writeGeometry(g, geometryFormat)
                    ps.setBytes(column, bytes)
                } catch (IOException e) {
                    throw new RuntimeException(e)
                }
            }
        }

        @Override
        String getGeometryTypeName(Integer type) {
            Geometries.getForSQLType(type).getName()
        }

        @Override
        void registerSqlTypeNameToClassMappings( Map<String, Class<?>> mappings) {
            super.registerSqlTypeNameToClassMappings(mappings)
            mappings.put("DOUBLE", Double)
            mappings.put("BOOLEAN", Boolean)
            mappings.put("DATE", java.sql.Date)
            mappings.put("TIMESTAMP", java.sql.Timestamp)
            mappings.put("TIME", java.sql.Time)
        }

        @Override
        void registerClassToSqlMappings(Map<Class<?>, Integer> mappings) {
            super.registerClassToSqlMappings(mappings)
            for (Geometries g : Geometries.values()) {
                mappings.put(g.getBinding(), g.getSQLType())
            }
            mappings.put(Long, Types.INTEGER)
            mappings.put(Double, Types.REAL)
            mappings.put(Boolean, Types.INTEGER)
        }

        @Override
        void registerSqlTypeToSqlTypeNameOverrides(Map<Integer, String> overrides) {
            super.registerSqlTypeToSqlTypeNameOverrides(overrides)
            overrides.put(Types.BOOLEAN, "BOOLEAN")
            overrides.put(Types.SMALLINT, "SMALLINT")
            overrides.put(Types.BIGINT, "BIGINT")
            overrides.put(Types.DOUBLE, "DOUBLE")
            overrides.put(Types.NUMERIC, "NUMERIC")
            overrides.put(Types.DATE, "DATE")
            overrides.put(Types.TIME, "TIME")
            overrides.put(Types.TIMESTAMP, "TIMESTAMP")
        }

        @Override
        Class<?> getMapping(ResultSet columns, Connection cx) throws SQLException {
            String tbl = columns.getString("TABLE_NAME")
            String col = columns.getString("COLUMN_NAME")
            GeometryType geometryType = getGeometryType(cx, tbl, col)
            geometryType ? geometryType.type : null
        }

        @Override
        void postCreateTable(String schemaName, SimpleFeatureType featureType, Connection cx) throws SQLException, IOException {
            String table = featureType.typeName
            String geometryColumn = featureType.geometryDescriptor.localName
            GeometryType geometryType = GeometryType.get(featureType.geometryDescriptor.type.binding)
            int coordinateDimension = 2
            CoordinateReferenceSystem crs = featureType.coordinateReferenceSystem
            Projection projection
            if (crs) {
                projection = new Projection(featureType.coordinateReferenceSystem)
            }
            int srid = projection?.epsg ?: -1
            addGeometryColumn(cx, table, geometryColumn, geometryType, coordinateDimension, srid, this.geometryFormat)

            if (srid > -1) {
                String authName = "EPSG"
                int authSrid = projection.epsg
                String srText = projection.wkt
                addSpatialReferenceSystem(cx, srid, authName, authSrid, srText)
            }
        }

        @Override
        void postDropTable(String schemaName, SimpleFeatureType featureType, Connection cx) throws SQLException {
            super.postDropTable(schemaName, featureType, cx)
            deleteGeometryColumn(cx, featureType.typeName)
        }

    }

    private static enum GeometryType {
        GEOMETRY(0, Geometry),
        POINT(1, Point),
        LINESTRING(2, LineString),
        POLYGON(3, Polygon),
        MULTIPOINT(4, MultiPoint),
        MULTILINESTRING(5, MultiLineString),
        MULTIPOLYGON(6, MultiPolygon),
        GEOMETRYCOLLECTION(7, GeometryCollection)

        final int code
        final Class type

        GeometryType(int code, Class type) {
            this.code = code
            this.type = type
        }

        static GeometryType get(int code) {
            values().find { GeometryType geometryType ->
                geometryType.code == code
            }
        }

        static GeometryType get(Class type) {
            values().find { GeometryType geometryType ->
                geometryType.type == type
            }
        }

    }

    private static enum GeometryFormat {
        WKT, WKB
    }

}
