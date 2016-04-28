package geoscript.style.io

import geoscript.style.Style

/**
 * A utility for getting Style Readers
 * @author Jared Erickson
 */
class Readers {

    /**
     * Get a List of all Style Readers
     * @return A List of Style Readers
     */
    public static List<Reader> list() {
        ServiceLoader.load(Reader.class).iterator().collect()
    }

    /**
     * Find a Style by name (sld, css)
     * @param name The name (sld, css)
     * @return A Reader or null
     */
    public static Reader find(String name) {
        list().find{ Reader reader ->
            String readerName = reader.class.simpleName
            readerName.toLowerCase().startsWith(name.toLowerCase())
        }
    }

    /**
     * Read a Style from a String.
     * @param str The String
     * @return A Style or null
     */
    static Style read(String str) {
        Style style = null
        for(Reader reader : list()) {
            try {
                style = reader.read(str)
                if (style) {
                    break
                }
            } catch (Exception ex) {
                // Just try the next reader
            }
        }
        style
    }
}
