package geoscript.geom.io

/**
 * A Utility for finding all registered Geometry Readers.
 * @author Jared Erickson
 */
class Readers {

    /**
     * Get a List of all Geometry Readers
     * @return A List of Geometry Readers
     */
    public static List<Reader> list() {
        ServiceLoader.load(Reader.class).iterator().collect()
    }

    /**
     * Find a Reader by name (csv, geojson, kml)
     * @param name The name (csv, geojson, kml)
     * @return A Reader or null
     */
    public static Reader find(String name) {
        list().find{ Reader reader ->
            String readerName = reader.class.simpleName
            readerName.toLowerCase().startsWith(name.toLowerCase())
        }
    }

}
