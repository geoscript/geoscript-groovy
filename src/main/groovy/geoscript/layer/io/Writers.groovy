package geoscript.layer.io

/**
 * A Utility for getting a List of all Layer Writers.
 * @author Jared Erickson
 */
class Writers {

    /**
     * Get a List of all Layer Writers
     * @return A List of Layer Writers
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
