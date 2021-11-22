package geoscript.layer.io

/**
 * A Utility for getting a list of all PyramidReaders.
 * @author Jared Erickson
 */
class PyramidReaders {

    /**
     * Get a List of all PyramidReaders
     * @return A List of PyramidReaders
     */
    static List<PyramidReader> list() {
        ServiceLoader.load(PyramidReader.class).iterator().collect()
    }

    /**
     * Find a PyramidReader by name (csv, geojson, kml)
     * @param name The name (csv, geojson, kml)
     * @return A PyramidReader or null
     */
    static PyramidReader find(String name) {
        list().find{ PyramidReader reader ->
            String readerName = reader.class.simpleName
            readerName.toLowerCase().startsWith(name.toLowerCase())
        }
    }

}
