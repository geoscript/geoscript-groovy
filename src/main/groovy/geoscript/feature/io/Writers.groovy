package geoscript.feature.io

/**
 * A Utility for finding all registered Feature Writers.
 * @author Jared Erickson
 */
class Writers {

    /**
     * Get a List of all Feature Writers
     * @return A List of Feature Writers
     */
    static List<Writer> list() {
        ServiceLoader.load(Writer.class).iterator().collect()
    }

    /**
     * Find a Writer by name (csv, geojson, kml)
     * @param name The name (csv, geojson, kml)
     * @return A Writer or null
     */
    static Writer find(String name) {
        list().find{ Writer writer ->
            String writerName = writer.class.simpleName
            writerName.toLowerCase().startsWith(name.toLowerCase())
        }
    }

}
