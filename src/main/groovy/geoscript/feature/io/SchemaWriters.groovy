package geoscript.feature.io

/**
 * A Utility for finding all registered SchemaWriters.
 * @author Jared Erickson
 */
class SchemaWriters {

    /**
     * Get a List of all SchemaWriters
     * @return A List of SchemaWriters
     */
    static List<SchemaWriter> list() {
        ServiceLoader.load(SchemaWriter.class).iterator().collect()
    }

    /**
     * Find a SchemaWriter by name (csv, geojson, kml)
     * @param name The name (csv, geojson, kml)
     * @return A SchemaWriter or null
     */
    static SchemaWriter find(String name) {
        list().find{ SchemaWriter writer ->
            String writerName = writer.class.simpleName
            writerName.toLowerCase().startsWith(name.toLowerCase())
        }
    }

}
