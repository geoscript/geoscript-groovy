package geoscript.workspace

import org.apache.commons.lang3.StringUtils
import org.geotools.util.logging.Logging
import org.h2gis.functions.io.utility.IOMethods

import java.sql.SQLException
import java.util.logging.Logger

/**
 * A Groovy Extension Module that adds static methods to H2GIS workspace.
 * @author Erwan Bocher (CNRS)
 */
class H2GISDatabaseExtensionModule {
    /**
     * The Logger
     */
    private static final Logger LOGGER = Logging.getLogger("geoscript.workspace.H2GISDatabaseExtensionModule")

    private static IOMethods ioMethods = null;

    /**
     * Create a dynamic link from a file to the H2GIS database.
     *
     * @param sql connection to the database.
     * @param path The path of the file.
     * @param table The name of the table created to store the file.
     * @param delete True to delete the table if exists. Default to true.
     * @throws java.sql.SQLException Exception throw on database error.
     */
    static String link(H2GIS h2GIS, String path, String table, boolean delete = false) throws SQLException {
        return IOMethods.linkedFile(h2GIS.getDataSource().getConnection(), path, table, delete)
    }

    /**
     * Create a dynamic link from a file to the H2GIS database.
     *
     * @param sql connection to the database.
     * @param path The path of the file.
     * @param table The name of the table created to store the file.
     * @param delete True to delete the table if exists. Default to true.
     * @throws java.sql.SQLException Exception throw on database error.
     */
    static String link(H2GIS h2GIS, String path, boolean delete = false) throws SQLException {
        File pathFile = new File(path)
        String fileName = pathFile.name
        String name = fileName.substring(0, fileName.lastIndexOf('.'))
        return link(h2GIS, path, StringUtils.deleteWhitespace(name), delete)
    }

    /**
     * Link a table from an external database to a H2GIS database.
     * Databases supported are H2 [H2GIS], PostgreSQL [PostGIS]
     * Note :  the jdb driver of the external database must be in the classpath
     *
     * @param sql connection to the database.
     * @param path The path of the file.
     * @param table The name of the table created to store the file.
     * @param delete True to delete the table if exists. Default to true.
     * @throws java.sql.SQLException Exception throw on database error.
     */
    static String link(H2GIS h2GIS, String dbname, String host, String port, String user, String password, String schema = "",
                       String table, String newTable = "", boolean delete = false,
                       int batch_size = 100) throws SQLException {
        def properties = [:]
        properties.put("user", user)
        properties.put("password": password)
        def url = "jdbc:$host"
        if (port) {
            url += "/$port"
        }
        properties.put("url": "$url/$dbname")
        def sourceTable = table

        if (schema) {
            sourceTable = "(select * from $schema.$table)"
        }
        return IOMethods.linkedTable(h2GIS.getDataSource().getConnection(), properties, sourceTable, newTable ? newTable : table, delete, batch_size)
    }

    /**
     * Save a PostGIS table into a file
     * @param database PostGIS database
     * @param table the name of the table
     * @param filePath the path of the file
     * @param delete the file is exist
     * @param encoding encode the file is supported
     * @return true if success
     */
    static boolean save(PostGIS database, String table, String filePath, boolean delete = false, String encoding = null) {
        def con = database.getDataSource().getConnection()
        if (con == null) {
            LOGGER.severe("No connection, cannot save.")
            return false
        }
        try {
            if (ioMethods == null) {
                ioMethods = new IOMethods()
            }
            ioMethods.exportToFile(con, "\"$table\"", filePath, encoding, delete)
            return true;
        } catch (SQLException e) {
            LOGGER.severe("Cannot import the file : " + filePath)
        }
        return false;
    }

    /**
     * Save an H2GIS table into a file
     * @param database PostGIS database
     * @param table the name of the table
     * @param filePath the path of the file
     * @param delete the file is exist
     * @param encoding encode the file is supported
     * @return true if success
     */
    static boolean save(H2GIS database, String table, String filePath, boolean delete = false, String encoding = null) {
        def con = database.getDataSource().getConnection()
        if (con == null) {
            LOGGER.severe("No connection, cannot save.");
            return false;
        }
        try {
            if (ioMethods == null) {
                ioMethods = new IOMethods();
            }
            ioMethods.exportToFile(con, "\"$table\"", filePath, encoding, delete)
            return true;
        } catch (SQLException e) {
            LOGGER.severe("Cannot import the file : " + filePath)
        }
        return false;
    }


    /**
     * Load a file to H2GIS database
     * @param database H2GIS database
     * @param filePath the input file path
     * @param delete remove the table if exists
     * @return the names of the created tables
     */
    static String[] load(H2GIS database, String filePath,
                         boolean delete = false) {
        File pathFile = new File(filePath)
        String fileName = pathFile.name
        String name = fileName.substring(0, fileName.lastIndexOf('.'))
        return load(database, filePath, StringUtils.deleteWhitespace(name), null, delete)
    }

    /**
     * Load a file to PostGIS database
     * @param database PostGIS database
     * @param filePath the input file path
     * @param delete remove the table if exists
     * @return the names of the created tables
     */
    static String[] load(PostGIS database, String filePath,
                         boolean delete = false) {
        File pathFile = new File(filePath)
        String fileName = pathFile.name
        String name = fileName.substring(0, fileName.lastIndexOf('.'))
        return load(database, filePath, StringUtils.deleteWhitespace(name), null, delete)
    }

    /**
     * Load a file to PostGIS database
     * @param database PostGIS database
     * @param filePath the input file path
     * @param table a name for the created table
     * @param encoding input file encoding if supported
     * @param delete remove the table if exists
     * @return the names of the created tables
     */
    static String[] load(PostGIS database, String filePath, String table, String encoding = null,
                         boolean delete = false) {
        def con = database.getDataSource().getConnection()
        if (con == null) {
            LOGGER.severe("No connection, cannot load the file $filePath.");
            return false;
        }
        try {
            if (ioMethods == null) {
                ioMethods = new IOMethods();
            }
            return ioMethods.importFile(con, filePath, table, encoding, delete)
        } catch (SQLException e) {
            LOGGER.severe("Cannot import the file : " + filePath);
        }
        return null
    }

    /**
     * Load a file to H2GIS database
     * @param database H2GIS database
     * @param filePath the input file path
     * @param table a name for the created table
     * @param encoding input file encoding if supported
     * @param delete remove the table if exists
     * @return the names of the created tables
     */
    static String[] load(H2GIS database, String filePath, String table, String encoding = null,
                         boolean delete = false) {
        def con = database.getDataSource().getConnection()
        if (con == null) {
            LOGGER.severe("No connection, cannot load the file $filePath.");
            return false;
        }
        try {
            if (ioMethods == null) {
                ioMethods = new IOMethods();
            }
            return ioMethods.importFile(con, filePath, table, encoding, delete)
        } catch (SQLException e) {
            LOGGER.severe("Cannot import the file : " + filePath);
        }
        return null
    }

    /**
     * Load a table from an external database to H2GIS
     *
     * @param h2GIS database
     * @param database destination database connection
     * @param table the name of the table to export or a select query
     * @param newTable target table name
     * @param mode -1 delete the target table if exists and create a new table,
     *                         0 create a new table, 1 update the target table if exists
     * @param batch_size batch size value before sending the data
     * @return the name of the created table
     */
    static String load(H2GIS h2GIS, Database database, String table, int mode = 0, int batch_size = 100) {
        return IOMethods.exportToDataBase(database.getDataSource().getConnection(),
                "\"$table\"", h2GIS.getDataSource().getConnection(),  "\"$table\"", mode, batch_size)
    }
    /**
     * Load a table from an external database to H2GIS
     *
     * @param h2GIS database
     * @param database destination database connection
     * @param table the name of the table to export or a select query
     * @param newTable target table name
     * @param mode -1 delete the target table if exists and create a new table,
     *                         0 create a new table, 1 update the target table if exists
     * @param batch_size batch size value before sending the data
     * @return the name of the created table
     */
    static String load(H2GIS h2GIS, Database database, String table, String newTable , int mode = 0, int batch_size = 100) {
        return IOMethods.exportToDataBase(database.getDataSource().getConnection(),
                table, h2GIS.getDataSource().getConnection(), newTable ? newTable : table, mode, batch_size)
    }


    /**
     * Save a table to an external database
     * @param h2GIS database
     * @param database destination database connection
     * @param table the name of the table to export or a select query
     * @param newTable target table name
     * @param mode -1 delete the target table if exists and create a new table,
     *                         0 create a new table, 1 update the target table if exists
     * @param batch_size batch size value before sending the data
     * @return the name of the created table
     */
    static String save(H2GIS h2GIS, Database database, String table, String newTable,  boolean delete=false, int batch_size =100) {
        //The save method support select when the user set the table with '(select * from mytable)'
        if(!table.startsWith("(")){
            //Escapte the table name because it's not a query
            table = "\"$table\""
        }
        return IOMethods.exportToDataBase(h2GIS.getDataSource().getConnection(),
                table, database.getDataSource().getConnection(), "\"$newTable\"", delete?-1:0, batch_size)
    }

    /**
     * Save a table to an external database
     * @param h2GIS database
     * @param database destination database connection
     * @param table the name of the table to export
     * @param newTable target table name
     * @param delete true to delete the existing table
     * @param batch_size batch size value before sending the data
     * @return the name of the created table
     */
    static String save(H2GIS h2GIS, Database database, String table, boolean delete=false, int batch_size =100) {
        return IOMethods.exportToDataBase(h2GIS.getDataSource().getConnection(),
                "\"$table\"", database.getDataSource().getConnection(),  "\"$table\"", delete?-1:0, batch_size)
    }

    /**
     * Insert to an existing table stored in an external database
     * @param h2GIS database
     * @param database destination database connection
     * @param table the name of the table to export
     * @param newTable target table name
     * @param batch_size batch size value before sending the data
     * @return the name of the created table
     */
    static String insert(H2GIS h2GIS, Database database, String table, int batch_size =100) {
        return IOMethods.exportToDataBase(h2GIS.getDataSource().getConnection(),
                "\"$table\"", database.getDataSource().getConnection(),  "\"$table\"", 1, batch_size)
    }

}
