package geoscript.render

/**
 * A utility for getting Renderers
 * @author Jared Erickson
 */
class Renderers {

    /**
     * Get a List of all Renderers
     * @return A List of Renderers
     */
    public static List<Renderer> list() {
        ServiceLoader.load(Renderer.class).iterator().collect()
    }

    /**
     * Find a Renderer by name (png, svg, gif)
     * @param name The name (csv, geojson, kml)
     * @return A Renderer or null
     */
    public static Renderer find(String name) {
        list().find{ Renderer renderer ->
            String rendererName = renderer.class.simpleName
            rendererName.toLowerCase().startsWith(name.toLowerCase())
        }
    }

}
