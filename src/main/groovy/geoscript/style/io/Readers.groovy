package geoscript.style.io

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
}
