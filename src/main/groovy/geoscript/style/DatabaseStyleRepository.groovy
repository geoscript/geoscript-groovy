package geoscript.style

import geoscript.style.io.Readers
import groovy.sql.GroovyRowResult
import groovy.sql.Sql

/**
 * A StyleRepository that stores styles in a database table called layer_styles.
 * H2, Postgres and SQLite are supported.
 * @author Jared Erickson
 */
class DatabaseStyleRepository implements StyleRepository {

    private final Sql sql

    private final Dialect dialect = Dialect.H2

    private enum Dialect {
        H2, POSTGRES, SQLITE
    }

    private DatabaseStyleRepository(Dialect dialect, Sql sql) {
        this.dialect = dialect
        this.sql = sql
        this.createTable()
    }

    static DatabaseStyleRepository forPostgres(Sql sql) {
        new DatabaseStyleRepository(Dialect.POSTGRES, sql)
    }

    static DatabaseStyleRepository forH2(Sql sql) {
        new DatabaseStyleRepository(Dialect.H2, sql)
    }

    static DatabaseStyleRepository forSqlite(Sql sql) {
        new DatabaseStyleRepository(Dialect.SQLITE, sql)
    }

    @Override
    String getDefaultForLayer(String layerName) {
        getForLayer(layerName, layerName)
    }

    @Override
    Style getDefaultStyleForLayer(String layerName) {
        getStyleForLayer(layerName, layerName)
    }

    @Override
    String getForLayer(String layerName, String styleName) {
        List results = sql.rows(
            "SELECT stylesld FROM layer_styles WHERE f_table_name = :name AND styleName = :styleName",
            [name: layerName, styleName: styleName]
        )
        if (results) {
            getText(results[0].stylesld)
        } else {
            null
        }
    }

    @Override
    Style getStyleForLayer(String layerName, String styleName) {
        String styleString = getForLayer(layerName, styleName)
        Readers.find("sld").read(styleString as String)
    }

    @Override
    List<Map<String, Object>> getForLayer(String layerName) {
        sql.rows(
            """SELECT 
                    id,
                    f_table_catalog,
                    f_table_schema,
                    f_table_name,
                    f_geometry_column,
                    styleName,
                    styleQML,
                    styleSLD,
                    useAsDefault,
                    description,
                    owner,
                    ui,
                    update_time 
                FROM layer_styles WHERE f_table_name = :name""",
            [name: layerName]
        ).collect { GroovyRowResult result ->
            mapRow(result)
        }
    }

    @Override
    List<Map<String, Object>> getAll() {
        sql.rows(
        """SELECT
                id,
                f_table_catalog,
                f_table_schema,
                f_table_name,
                f_geometry_column,
                styleName,
                styleQML,
                styleSLD,
                useAsDefault,
                description,
                owner,
                ui,
                update_time 
            FROM layer_styles"""
        ).collect { GroovyRowResult result ->
            mapRow(result)
        }
    }

    @Override
    void save(String layerName, String styleName, String style, Map options = [:]) {
        sql.execute("""INSERT INTO layer_styles (       
                f_table_catalog, 
                f_table_schema, 
                f_table_name, 
                f_geometry_column, 
                styleName, 
                styleSLD,
                styleQML,
                useAsDefault, 
                description, 
                owner,
                ui
            ) VALUES (:catalog, :schema, :table, :geometryColumn, :styleName, :sld, :qml, :default, :description, :owner, :ui)""", [
                catalog: options.get("catalog", "") as String,
                schema: options.get("schema", "public") as String,
                table: options.get("table", layerName) as String,
                geometryColumn: options.get("geometryColumn", "") as String,
                styleName: options.get("stylename", styleName) as String,
                sld: options.get("sld", style),
                qml: options.get("qml", options.get("qml","")),
                default: options.get("useAsDefault", layerName.equalsIgnoreCase(styleName)),
                description: options.get("description", "Style ${styleName} for Layer ${layerName}") as String,
                owner: options.get("owner", System.getProperty("user.name")) as String,
                ui: options.get("ui", options.get("ui","")),
        ])
    }

    @Override
    void delete(String layerName, String styleName) {
        sql.execute(
            "delete from layer_styles where f_table_name = :layerName AND stylename = :styleName",
            [layerName: layerName, styleName: styleName]
        )
    }

    private void createTable() {
        sql.execute('''CREATE TABLE IF NOT EXISTS layer_styles ( 
            id  ''' + getPrimaryKey() + ''',
            f_table_catalog VARCHAR(256),
            f_table_schema VARCHAR(256),
            f_table_name VARCHAR(256),
            f_geometry_column VARCHAR(256),
            styleName VARCHAR(30),
            styleQML TEXT,
            styleSLD TEXT,
            useAsDefault BOOLEAN,
            description VARCHAR,
            owner VARCHAR(30),
            ui VARCHAR(30),
            update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        )''')
    }

    private Map mapRow(GroovyRowResult result) {
        String styleStr = getText(result.stylesld)
        [
            layerName: result.f_table_name,
            styleName: result.stylename,
            style: Readers.find("sld").read(styleStr as String),
            styleStr: styleStr,
            id: result.id,
            f_table_catalog: result.f_table_catalog,
            f_table_schema: result.f_table_schema,
            f_table_name: result.f_table_name,
            f_geometry_column: result.f_geometry_column,
            styleQML: getText(result.styleQML),
            styleSLD: getText(result.styleSLD),
            useAsDefault: result.useAsDefault,
            description: result.description,
            owner: result.owner,
            ui: result.ui,
            update_time: result.update_time
        ]
    }

    private String getPrimaryKey() {
        if (dialect == Dialect.H2) {
            "INTEGER PRIMARY KEY AUTO_INCREMENT NOT NULL"
        } else if (dialect == Dialect.POSTGRES) {
            "SERIAL PRIMARY KEY"
        } else if (dialect == Dialect.SQLITE) {
            "INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL"
        } else {
            "INTEGER PRIMARY KEY AUTO INCREMENT NOT NULL"
        }
    }

    private String getText(Object result) {
        if (dialect == Dialect.H2) {
            result.characterStream.text
        } else if (dialect == Dialect.POSTGRES) {
            result
        } else if (dialect == Dialect.SQLITE) {
            result.toString()
        } else {
            null
        }
    }

}
