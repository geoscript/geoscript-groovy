package geoscript.feature.io

/**
 * A Utility for finding all registered SchemaReaders.
 * @author Jared Erickson
 */
class SchemaReaders {

    /**
     * Get a List of all SchemaReaders
     * @return A List of SchemaReaders
     */
    static List<SchemaReader> list() {
        ServiceLoader.load(SchemaReader.class).iterator().collect()
    }

    /**
     * Find a SchemaReader by name (string, json)
     * @param name The name (string, json)
     * @return A SchemaReader or null
     */
    static SchemaReader find(String name) {
        list().find{ SchemaReader reader ->
            String readerName = reader.class.simpleName
            readerName.toLowerCase().startsWith(name.toLowerCase())
        }
    }

}
