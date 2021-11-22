package geoscript.layer.io

/**
 * A Utility for getting a List of all PyramidWriters.
 * @author Jared Erickson
 */
class PyramidWriters {

    /**
     * Get a List of PyramidWriters
     * @return A List of PyramidWriters
     */
    static List<PyramidWriter> list() {
        ServiceLoader.load(PyramidWriter.class).iterator().collect()
    }

    /**
     * Find a PyramidWriter by name (csv, geojson, kml)
     * @param name The name (csv, geojson, kml)
     * @return A PyramidWriter or null
     */
    static PyramidWriter find(String name) {
        list().find{ PyramidWriter writer ->
            String writerName = writer.class.simpleName
            writerName.toLowerCase().startsWith(name.toLowerCase())
        }
    }

}
