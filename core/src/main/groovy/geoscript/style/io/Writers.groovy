package geoscript.style.io

/**
 * A utility for getting Style Writers
 * @author Jared Erickson
 */
class Writers {

    /**
     * Get a List of all Style Writers
     * @return A List of Style Writers
     */
    public static List<Writer> list() {
        ServiceLoader.load(Writer.class).iterator().collect()
    }

    /**
     * Find a Style Writer by name (sld, css)
     * @param name The name (sld, css)
     * @return A Writer or null
     */
    public static Writer find(String name) {
        list().find{ Writer writer ->
            String writerName = writer.class.simpleName
            writerName.toLowerCase().startsWith(name.toLowerCase())
        }
    }

}
