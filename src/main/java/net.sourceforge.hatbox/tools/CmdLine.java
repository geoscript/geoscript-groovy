/*
 *    HatBox : A user-space spatial add-on for Java databases
 *    
 *    Copyright (C) 2007 - 2009 Peter Yuill
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package net.sourceforge.hatbox.tools;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.CallableStatement;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Types;
import java.io.InputStream;
import java.io.InputStreamReader;

import net.sourceforge.hatbox.MetaNode;
import net.sourceforge.hatbox.jts.Proc;

public class CmdLine {

    public static Logger LOGGER = Logger.getLogger("net.sourceforge.hatbox");
    static {
        if (LOGGER.getHandlers().length == 0) {
            ConsoleHandler h = new ConsoleHandler();
            SimpleFormatter formatter = new SimpleFormatter() {
                @Override
                public synchronized String format(LogRecord record) {
                    return record.getMessage() + "\n";
                }
            };
            h.setFormatter(formatter);
            LOGGER.addHandler(h);
            LOGGER.setUseParentHandlers(false);
        }
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        String command = "help";
        if (args.length > 0) {
            command = args[0].toLowerCase();
        }
        if (command.equals("help")) {
            displayHelp(args);
            return;
        }
        Map<String,String> argMap = populateOptions(args);
        String dbType = null;
        String driver = null;
        String url = argMap.get("url");
        String user = argMap.get("u");
        String password = argMap.get("p");
        String sql = null;
        if (url == null) {
            displayBasicHelp();
            return;
        }
        if (url.startsWith("jdbc:derby:")) {
            dbType = "derby";
            if (url.startsWith("//", 11)) {
                driver = "org.apache.derby.jdbc.ClientDriver";                
            } else {
                driver = "org.apache.derby.jdbc.EmbeddedDriver";
            }
        } else if (url.startsWith("jdbc:h2:")) {
            dbType = "h2";
            driver = "org.h2.Driver";
        } else {
            throw new IllegalArgumentException("Database URL not recognized: " + url);
        }
        Class.forName(driver);
        Connection con = null;
        if (user == null) {
            con = DriverManager.getConnection(url);
        } else {
            con = DriverManager.getConnection(url, user, password);
        }
        if (command.equals("list")){
            list(con, argMap);
        } else if (command.equals("meta")) {
            meta(con, argMap);
        } else if (command.equals("spatializedb")) {
            spatializeDb(con, dbType);
        } else if (command.equals("despatializedb")) {
            deSpatializeDb(con, dbType);
        } else if (command.equals("spatialize")) {
            spatialize(con, argMap);
        } else if (command.equals("despatialize")) {
            deSpatialize(con, argMap);
        } else if (command.equals("buildindex")) {
            buildIndex(con, argMap);
        }
    }
    
    public static void list(Connection con, Map<String,String> argMap) throws Exception {
        HashMap<String,String> tableMap = new HashMap<String,String>();
        HashSet<String> indexSet = new HashSet<String>();
        Statement stmt = con.createStatement();
        DatabaseMetaData dbMeta = con.getMetaData();
        String schema = dbMeta.getUserName();
        if (argMap.get("s") != null) {
            schema = argMap.get("s");
        }
        ResultSet rs = dbMeta.getTables(null, schema, null, new String[] {"TABLE"});
        while (rs.next()) {
            String table = rs.getString(3);
            if (table.endsWith("_HATBOX")) {
                indexSet.add(table);
            } else {
                //find PK column
                List<String> pkColList = new ArrayList<String>();
                ResultSet pkRs = dbMeta.getPrimaryKeys(null, schema, table);
                while(pkRs.next()) {
                    pkColList.add(pkRs.getString(4));
                }
                pkRs.close();
                if (pkColList.size() != 1) {
                    tableMap.put(table, "Incompatible PK - multi column");
                } else {
                    ResultSet colRs = dbMeta.getColumns(null, schema, table, pkColList.get(0));
                    int colType = Types.NULL;
                    if (colRs.next()) {
                        colType = colRs.getInt(5);
                    }
                    switch (colType) {
                    case Types.BIGINT :
                    case Types.INTEGER :
                    case Types.SMALLINT :
                    case Types.TINYINT :
                        tableMap.put(table, "Candidate");
                        break;
                    default :
                        tableMap.put(table, "Incompatible PK - not numeric");
                    }
                    
                }
            }
        }
        LOGGER.info("Listing tables for schema: " + schema);
        for (String table : tableMap.keySet()) {
            String status = tableMap.get(table);
            if (status.equals("Candidate")) {
                if (indexSet.contains(table + "_HATBOX")) {
                    ResultSet nodeRs = stmt.executeQuery("select NODE_DATA from \"" + schema + "\".\"" + table + "_HATBOX\" where id = 1");
                    if (nodeRs.next()) {
                        MetaNode indexMeta = new MetaNode(nodeRs.getBytes(1));
                        LOGGER.info(table + " : Spatialized " + indexMeta.getIndexStatus());
                    } else {
                        LOGGER.info(table + " : Spatial index table empty");
                    }
                } else {
                    LOGGER.info(table + " : " + status);
                }
            } else {
                LOGGER.info(table + " : " + status);
            }
        }
    }
    
    public static void meta(Connection con, Map<String,String> argMap) throws Exception {
        DatabaseMetaData dbMeta = con.getMetaData();
        String schema = dbMeta.getUserName();
        if (argMap.get("s") != null) {
            schema = argMap.get("s");
        }
        String table = argMap.get("t");
        if (table == null) {
            displayDespatializeHelp();
            return;
        }
        MetaNode meta = Proc.spatialMetaData(con, schema, table);
        LOGGER.info("Meta Data for table: " + schema + "." + table);
        LOGGER.info("Index Status     : " + meta.getIndexStatus());
        LOGGER.info("Metadata version : " + meta.getVersion());
        LOGGER.info("Index algorithm  : " + meta.getAlgorithm());
        LOGGER.info("PK Column        : " + meta.getPkColName());
        LOGGER.info("Geometry Column  : " + meta.getGeomColName());
        LOGGER.info("Geometry Type    : " + meta.getGeomType());
        LOGGER.info("SRID             : " + meta.getSrid());
        LOGGER.info("Expose PK        : " + meta.getExposePk());
        LOGGER.info("Entries per node : " + meta.getEntriesMax());
    }
    
    public static void spatializeDb(Connection con, String dbType) throws Exception {
        Statement stmt = con.createStatement();
        List<String> ddlList = getddl("create_" + dbType + ".sql");
        for (String ddl : ddlList) {
            stmt.execute(ddl);
        }
        stmt.close();
        LOGGER.info("Done");
    }
    
    public static void deSpatializeDb(Connection con, String dbType) throws Exception {
        Statement stmt = con.createStatement();
        List<String> ddlList = getddl("drop_" + dbType + ".sql");
        for (String ddl : ddlList) {
            try {
                stmt.execute(ddl);
            } catch (SQLException sqle) {} // ignore if not exists
        }
        stmt.close();
        LOGGER.info("Done");
    }
        
    public static void spatialize(Connection con, Map<String,String> argMap) throws Exception {
        DatabaseMetaData dbMeta = con.getMetaData();
        String schema = dbMeta.getUserName();
        if (argMap.get("s") != null) {
            schema = argMap.get("s");
        }
        String table = argMap.get("t");
        if (table == null) {
            displaySpatializeHelp();
            return;
        }
        if (argMap.get("geom") == null) {
            displaySpatializeHelp();
            return;
        }
        if (argMap.get("srid") == null) {
            displaySpatializeHelp();
            return;
        }
        Proc.spatialize(con, schema, table, argMap.get("geom"), argMap.get("type"), argMap.get("srid"), argMap.get("exposepk"), argMap.get("entries"));
        LOGGER.info("Spatialize complete");
    }
    
    public static void deSpatialize(Connection con, Map<String,String> argMap) throws Exception {
        DatabaseMetaData dbMeta = con.getMetaData();
        String schema = dbMeta.getUserName();
        if (argMap.get("s") != null) {
            schema = argMap.get("s");
        }
        String table = argMap.get("t");
        if (table == null) {
            displayDespatializeHelp();
            return;
        }
        Proc.deSpatialize(con, schema, table);
        LOGGER.info("DeSpatialize complete");
    }
    
    public static void buildIndex(Connection con, Map<String,String> argMap) throws Exception {
        DatabaseMetaData dbMeta = con.getMetaData();
        String schema = dbMeta.getUserName();
        if (argMap.get("s") != null) {
            schema = argMap.get("s");
        }
        String table = argMap.get("t");
        if (table == null) {
            displayBuildindexHelp();
            return;
        }
        int commitInterval = 0;
        if (argMap.get("commit") != null) {
            commitInterval = Integer.parseInt(argMap.get("commit"));
        }
        Proc.buildIndex(con, schema, table, commitInterval, null);
        LOGGER.info("Index build complete");
    }
    
    public static void displayHelp(String[] args) {
        if (args.length < 2) {
            displayBasicHelp();
        } else {
            String command = args[1].toLowerCase();
            if (command.equals("list")) {
                displayListHelp();
            } else if (command.equals("meta")) {
                displayMetaHelp();
            } else if (command.equals("spatialize")) {
                displaySpatializeHelp();
            } else if (command.equals("despatialize")) {
                displayDespatializeHelp();
            } else if (command.equals("buildindex")) {
                displayBuildindexHelp();
            } else {
                displayBasicHelp();
            }
        }
    }
    
    public static void displayBasicHelp() {
        LOGGER.info("usage: hatbox <command> [common options] [command options]");
        LOGGER.info("'hatbox help <command>' for help on specific commands");
        LOGGER.info("Commands:");
        LOGGER.info(" list           - lists all tables in a schema with their hatbox status");
        LOGGER.info(" meta           - displays meta data for a spatialized table");
        LOGGER.info(" spatializedb   - spatialize the database (create common procedures)");
        LOGGER.info(" despatializedb - despatialize the database (remove common procedures)");
        LOGGER.info(" spatialize     - spatialize a table, without building an index");
        LOGGER.info(" despatialize   - remove all spatial meta data");
        LOGGER.info(" buildindex     - build an index for a spatialized table");
        LOGGER.info("Common Options:");
        LOGGER.info(" -url <string> : jdbc url to connect (required)");
        LOGGER.info(" -u <string>   : database user (optional)");
        LOGGER.info(" -p <string>   : database password (optional)");
        LOGGER.info(" -s <string>   : database schema (optional)");
    }
    
    public static void displayListHelp() {
        LOGGER.info("usage: hatbox list [common options]");
        LOGGER.info("List tables in a schema with spatial status.");
    }
    
    public static void displayMetaHelp() {
        LOGGER.info("usage: hatbox meta [common options]");
        LOGGER.info("Display meta data for a spatialized table.");
        LOGGER.info("Command Options:");
        LOGGER.info(" -t <string>            : database table (required)");
    }
    
    public static void displaySpatializeDbHelp() {
        LOGGER.info("usage: hatbox spatializedb [common options]");
        LOGGER.info("Spatialize the database by creating common procedures.");
    }
    
    public static void displayDespatializeDbHelp() {
        LOGGER.info("usage: hatbox despatializedb [common options]");
        LOGGER.info("Depatialize the database by removing common procedures.");
    }
    
    public static void displaySpatializeHelp() {
        LOGGER.info("usage: hatbox spatialize [common options] [command options]");
        LOGGER.info("Create spatial meta data for a table. Does not create index.");
        LOGGER.info("Command Options:");
        LOGGER.info(" -t <string>            : database table (required)");
        LOGGER.info(" -geom <string>         : geometry column (required)");
        LOGGER.info(" -type <string>         : geometry type (optional)");
        LOGGER.info(" -srid <int>            : epsg spatial ref id (required)");
        LOGGER.info(" -exposepk <true|false> : expose pk as business property (optional)");
        LOGGER.info(" -entries <int>         : maximum entries per index page (optional)");
    }
    
    public static void displayDespatializeHelp() {
        LOGGER.info("usage: hatbox despatialize [common options] [command options]");
        LOGGER.info("Remove all spatial meta data for a table");
        LOGGER.info("Command Options:");
        LOGGER.info(" -t <string>            : database table (required)");
    }
    
    public static void displayBuildindexHelp() {
        LOGGER.info("usage: hatbox buildindex [common options] [command options]");
        LOGGER.info("Build (or rebuild) a spatial index on a spatialized table");
        LOGGER.info("Command Options:");
        LOGGER.info(" -t <string>            : database table (required)");
        LOGGER.info(" -commit <number>       : commit every <number> of entries (optional)");
    }

    public static Map<String,String> populateOptions(String[] args) {
        Map<String,String> argMap = new HashMap<String,String>();
        String option = null;
        for (int i = 1; i < args.length; i++) {
            if (args[i].startsWith("-")) {
                option = args[i].substring(1);
            } else if (option != null) {
                argMap.put(option, args[i]);
                option = null;
            }
        }
        return argMap;
    }
    
    public static List<String> getddl(String fileName) throws Exception {
        List<String> ddl = new ArrayList<String>();
        InputStream in =  CmdLine.class.getResourceAsStream("/" + fileName);
        InputStreamReader reader = new InputStreamReader(in, "UTF-8");
        StringBuilder builder = new StringBuilder();
        char last = ' ';
        int ch = reader.read();
        while(ch >= 0) {
            if (ch == ';') {
                ddl.add(builder.toString());
                builder.setLength(0);
                last = ' ';
            } else if (Character.isWhitespace(ch)) {
                if (last == ' ') {
                    // ignore
                } else {
                    last = ' ';
                    builder.append(last);
                }
            } else {
                last = (char)ch;
                builder.append(last);
            }
            ch = reader.read();
        }
        LOGGER.info(ddl.toString());
        return ddl;
    }
}
