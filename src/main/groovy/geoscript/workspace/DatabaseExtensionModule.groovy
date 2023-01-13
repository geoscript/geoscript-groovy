package geoscript.workspace

import org.apache.commons.lang3.StringUtils
import org.H2GIS.functions.io.utility.IOMethods
import org.geotools.util.logging.Logging
import java.sql.SQLException
import java.util.logging.Logger

/**
 * A Groovy Extension Module that adds static methods to some Database drivers.
 * @author Erwan Bocher (CNRS)
 */
class DatabaseExtensionModule {
    /**
     * The Logger
     */
    private static final Logger LOGGER = Logging.getLogger("geoscript.workspace.DatabaseExtensionModule")

    private static IOMethods ioMethods = null;

    /**
     * Create a dynamic link from a file to a H2GIS database.
     *
     * @param sql connection to the database.
     * @param path The path of the file.
     * @param table The name of the table created to store the file.
     * @param delete True to delete the table if exists. Default to true.
     * @throws java.sql.SQLException Exception throw on database error.
     */
    static String linkedFile(H2GIS h2GIS, String path, String table, boolean delete = false) throws SQLException {
        return IOMethods.linkedFile(h2GIS.getDataSource().getConnection(), path, table, delete)
    }

    /**
     * Create a dynamic link from a file to a H2GIS database.
     *
     * @param sql connection to the database.
     * @param path The path of the file.
     * @param table The name of the table created to store the file.
     * @param delete True to delete the table if exists. Default to true.
     * @throws java.sql.SQLException Exception throw on database error.
     */
    static String linkedFile(H2GIS h2GIS, String path, boolean delete = false) throws SQLException {
        File pathFile = new File(path)
        String fileName = pathFile.name
        String name = fileName.substring(0, fileName.lastIndexOf('.'))
        return linkedFile(h2GIS, path, StringUtils.deleteWhitespace(name), delete)
    }


    /**
     * Save a table into a file
     * @param database
     * @param tableName
     * @param filePath
     * @param delete
     * @return
     */
    static boolean save(Database database, String tableName, String filePath, boolean delete = false) {
        if (database instanceof H2GIS || database instanceof PostGIS) {
            def con = database.getDataSource().getConnection()
            if (con == null) {
                LOGGER.warning("No connection, cannot save.");
                return false;
            }
            try {
                if (ioMethods == null) {
                    ioMethods = new IOMethods();
                }
                ioMethods.exportToFile(con, tableName, filePath, null, delete);
                return true;
            } catch (SQLException e) {
                LOGGER.severe("Cannot import the file : " + filePath);
            }
        }
        LOGGER.warning("Unsuported method for this Database ")
        return false;
    }

    /**
     * Save a table into a file
     * @param database
     * @param tableName
     * @param filePath
     * @param delete
     * @return
     */
    static boolean save(Database database, String tableName, String filePath, String encoding) {
        if (database instanceof H2GIS || database instanceof PostGIS) {
            def con = database.getDataSource().getConnection()
            if (con == null) {
                LOGGER.severe("No connection, cannot save.");
                return false;
            }
            try {
                if (ioMethods == null) {
                    ioMethods = new IOMethods();
                }
                ioMethods.exportToFile(con, tableName, filePath, encoding, false);
                return true;
            } catch (SQLException e) {
                LOGGER.severe("Cannot import the file : " + filePath);
            }
        }
        LOGGER.warning("Unsuported method for this Database ")
        return false;
    }

    /**
     * Load a file to the database
     * @param database
     * @param filePath
     * @param encoding
     * @param delete
     * @return
     */
    static String load(Database database, String filePath,
                       boolean delete = false) {
        File pathFile = new File(filePath)
        String fileName = pathFile.name
        String name = fileName.substring(0, fileName.lastIndexOf('.'))
        return load(database, filePath, StringUtils.deleteWhitespace(name), null, delete)
    }


    /**
     * Load a file to the database
     * @param database
     * @param filePath
     * @param tableName
     * @param encoding
     * @param delete
     * @return
     */
    static String load(Database database, String filePath, String tableName) {
        return load(database, filePath, tableName, null,
                false)
    }

    /**
     * Load a file to the database
     * @param database
     * @param filePath
     * @param tableName
     * @param encoding
     * @param delete
     * @return
     */
    static String load(Database database, String filePath, String tableName,
                       delete) {
        return load(database, filePath, tableName, null,
                delete)
    }

    /**
     * Load a file to the database
     * @param database
     * @param filePath
     * @param tableName
     * @param encoding
     * @param delete
     * @return
     */
    static String load(Database database, String filePath, String tableName, String encoding,
                       boolean delete = false) {
        if (database instanceof H2GIS || database instanceof PostGIS) {
            def con = database.getDataSource().getConnection()
            if (con == null) {
                LOGGER.severe("No connection, cannot load the file $filePath.");
                return false;
            }
            try {
                if (ioMethods == null) {
                    ioMethods = new IOMethods();
                }
                return ioMethods.importFile(con, filePath, tableName, encoding, delete)
            } catch (SQLException e) {
                LOGGER.severe("Cannot import the file : " + filePath);
            }
        }
        return null
    }
}
