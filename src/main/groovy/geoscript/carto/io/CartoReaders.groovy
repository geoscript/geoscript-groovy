package geoscript.carto.io

import geoscript.render.io.MapReader

/**
 * A Utility for finding all registered CartoReaders.
 * @author Jared Erickson
 */
class CartoReaders {

    /**
     * Get a List of all CartoReaders
     * @return A List of CartoReaders
     */
    static List<Reader> list() {
        ServiceLoader.load(CartoReader).iterator().collect()
    }

    /**
     * Find a CartoReader by name (json, xml)
     * @param name The name (json, xml)
     * @return A CartoReader or null
     */
    static CartoReader find(String name) {
        list().find{ CartoReader reader ->
            String readerName = reader.class.simpleName
            readerName.toLowerCase().startsWith(name.toLowerCase())
        }
    }

}
