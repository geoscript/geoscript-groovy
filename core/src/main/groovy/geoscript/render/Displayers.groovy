package geoscript.render

/**
 * A utility for getting Displayers
 * @author Jared Erickson
 */
class Displayers {

    /**
     * Get a List of all Displayers
     * @return A List of Displayers
     */
    public static List<Displayer> list() {
        ServiceLoader.load(Displayer.class).iterator().collect()
    }

    /**
     * Find a Displayer by name (window, mapwindow)
     * @param name The name (window, mapwindow)
     * @return A Displayer or null
     */
    public static Displayer find(String name) {
        list().find{ Displayer displayer ->
            String rendererName = displayer.class.simpleName
            rendererName.toLowerCase().startsWith(name.toLowerCase())
        }
    }

}
