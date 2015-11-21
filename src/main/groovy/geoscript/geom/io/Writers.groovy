package geoscript.geom.io

/**
 * A Utility for finding all registered Geometry Writers.
 * @author Jared Erickson
 */
class Writers {

    /**
     * Get a List of all Geometry Writers
     * @return A List of Geometry Writers
     */
    public static List<Writer> list() {
        ServiceLoader.load(Writer.class).iterator().collect()
    }

    /**
     * Find a Writer by name (csv, geojson, kml)
     * @param name The name (csv, geojson, kml)
     * @return A Writer or null
     */
    public static Writer find(String name) {
        list().find{ Writer writer ->
            String writerName = writer.class.simpleName
            writerName.toLowerCase().startsWith(name.toLowerCase())
        }
    }
}
