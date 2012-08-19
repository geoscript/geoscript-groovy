package geoscript.render

/**
 * Render a {@link geoscript.render.Map Map} to a Type T or an OutputStream
 * @param < T > The type the Renderer can produce (like a BufferedImage or a SVG Document)
 */
abstract class Renderer<T>  {

    /**
     * Render the Map to a Type
     * @param map The Map
     * @return The Type
     */
    abstract T render(Map map)

    /**
     * Render the Map to the OutputStream
     * @param map The Map
     * @param out The OutputStream
     */
    abstract void render(Map map, OutputStream out)

}
