package geoscript.render.io

/**
 * A Utility for finding all registered MapReaders.
 * @author Jared Erickson
 */
class MapReaders {

    /**
     * Get a List of all MapReaders
     * @return A List of MapReaders
     */
    static List<Reader> list() {
        ServiceLoader.load(MapReader).iterator().collect()
    }

    /**
     * Find a MapReader by name (json, xml)
     * @param name The name (json, xml)
     * @return A MapReader or null
     */
    static MapReader find(String name) {
        list().find{ MapReader reader ->
            String readerName = reader.class.simpleName
            readerName.toLowerCase().startsWith(name.toLowerCase())
        }
    }

}
